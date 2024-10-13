from flask import Flask, request, jsonify
import numpy as np
from openai import OpenAI
import os
app = Flask(__name__)
import numpy as np  # Assuming numpy is used for the cosine_similarity function

# Function to get a single embedding for a given text
def get_single_embedding(text, client):
    # Sends the text to the embeddings model (text-embedding-3-large) to get its embedding
    response = client.embeddings.create(
        input=text,
        model="text-embedding-3-large"
    )
    # Returns the embedding from the response (it's located in response.data[0].embedding)
    return response.data[0].embedding

# Function to get embeddings for multiple texts at once

def get_embeddings(texts, client):
    # Sends multiple texts to the embeddings model to retrieve embeddings for each
    response = client.embeddings.create(
        input=texts,
        model="text-embedding-3-large"
    )
    # Extracts and returns embeddings for each text in the input list
    return [item.embedding for item in response.data]

# Function to calculate cosine similarity between two embeddings (vectors)
# a: First embedding (vector)
# b: Second embedding (vector)
# Returns a value between -1 and 1 that represents how similar the two embeddings are
def cosine_similarity(a, b):
    # Cosine similarity formula: dot product of vectors a and b divided by the product of their magnitudes
    return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))

def parse_paragraphs(paragraph_string):
    # Initialize an empty dictionary to store the point numbers and texts
    parsed_dict = {}
    
    # Check if the input is valid (a string) and not empty
    if not isinstance(paragraph_string, str) or not paragraph_string.strip():
        print("Invalid input: Input must be a non-empty string.")
        return parsed_dict
    
    try:
        # Split the string into paragraphs using the separator $$$$$
        paragraphs = paragraph_string.split("$$$$$")[1:]
        
        for paragraph in paragraphs:
            # Safeguard: Skip empty or invalid paragraphs
            if not paragraph.strip():
                continue
            
            # Find the point number enclosed in '&' (e.g., &5.14&)
            start = paragraph.find('&')
            end = paragraph.find('&', start + 1)
            
            # Check if the paragraph contains a valid point number enclosed in '&'
            if start != -1 and end != -1 and start < end:
                point_number = paragraph[start+1:end].strip()
                
                # Extract the paragraph text after the point number
                point_text = paragraph[end+1:].strip()
                
                # Handle cases where point number or text is missing
                if not point_number:
                    point_number = "Unknown"
                if not point_text:
                    point_text = "No description available."
                
                # Add the point number and text to the dictionary
                parsed_dict[point_number] = point_text
            else:
                # If the paragraph doesn't have a valid point number, assign a default key
                parsed_dict["Document"] = paragraph.strip() if paragraph.strip() else "Empty paragraph."
    
    except Exception as e:
        # Handle unexpected errors and print a message for debugging
        print(f"An error occurred: {e}")
    
    return parsed_dict

def split_paragraph_by_query(paragraph_string, client):
    # Define the system message
    system_message = {
        "role": "system",
        "content": (
            "You are a document parsing assistant. Your task is to analyze the provided document and identify distinct paragraph points. "
            "Output the same document, but insert '$$$$$' as separators between separate points. "
            "Enclose each point number within '&' characters. "
            "For example:\n\n"
            "\"&5.1.2& Point $$$$$ &5.1.3& Another Point $$$$$\".\n\n"
            "Output ONLY the formatted document text without any additional comments or explanations."
        )
    }
    
    # Prepare the user message
    user_message = {
        "role": "user",
        "content": f"Document:\n{paragraph_string}"
    }
    
    # Create the chat completion
    chat_completion = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[system_message, user_message],
        max_tokens=1000
    )
    
    # Get and process the model's reply
    reply = chat_completion.choices[0].message.content.strip()
    
    # Process the reply as needed
    requirements = parse_paragraphs(reply)
    
    return requirements

client = OpenAI(
    api_key=f"{os.getenv('PROXY_API_KEY')}",
    base_url="https://api.proxyapi.ru/openai/v1",
)



@app.route('/check-uc', methods=['POST'])
def process_data():
    # Taking JSON from query
    data = request.get_json()

    # Extract data
    use_case = data.get('useCase')
    regulations = data.get('regulation', [])
    requirements = {}
    for reg in regulations:
        requirements.update(split_paragraph_by_query(reg.get("requirement"), client))

    # requirements = {}
    # for reg in regulations:
    #     requirements[reg.get('section')] = reg.get('requirement')
    
    # Creating embeddings for requirement
    requirements_list = []
    for i in requirements:
        requirements_list.append(requirements[i])

    if not("".join(requirements_list)):
        return jsonify("Requirements are met")
    
    requirements_embeddings = get_embeddings(requirements_list, client)

    usecase_embedding = get_single_embedding(use_case, client)

    # Calculating similarities of usecase embedding with requirements embeddings
    similarities = [
        (idx, cosine_similarity(usecase_embedding, req_emb))
        for idx, req_emb in enumerate(requirements_embeddings)
    ]

    similarities.sort(key=lambda x: x[1], reverse=True)
    requirements_keys = {}

    for idx, key in enumerate(requirements):
        requirements_keys[idx] = key

    # Taking n most similar to use case requirements
    n_requirements = int(os.getenv('N_REQUIREMENTS'))

    final_requirements = {}
    for i in range(n_requirements):
        req_idx = similarities[i][0]
        req_key = requirements_keys[req_idx]
        final_requirements[req_key] = requirements[req_key]

    # Prompting model for each requirement and adding to answer
    answer = ""

    # Define the system message
    system_message = {
        "role": "system",
        "content": (
            "You are a compliance assistant. Your task is to determine if a given use case "
            "violates a specific requirement. Only consider the provided requirement and nothing else. "
            "If you find a violation, output a concise one-sentence description of it. "
            "If there is no violation, respond only with '[]' and nothing else."
        )
    }



    for requirement_key in final_requirements:
        user_message = {
            "role": "user",
            "content": (
                f"Requirement:\n{final_requirements[requirement_key]}\n\n"
                f"Use Case:\n{use_case}"
            )
        }

        chat_completion = client.chat.completions.create(
            model="gpt-4o",
            messages=[system_message, user_message],
            max_tokens=60
        )

        model_reply = chat_completion.choices[0].message.content.strip()

        if model_reply == "[]":
            continue  # No violation found; move to the next requirement
        else:
            answer += f"According to {requirement_key}: {model_reply}\n"

    if not answer:
        answer = "The regulations objects were found. Requirements are met"
    # Return answer with results
    return jsonify(answer)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)