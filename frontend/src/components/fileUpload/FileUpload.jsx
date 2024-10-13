import React, { useState } from 'react';
import axios from 'axios';
import { Button } from "antd";
import cl from './FileUpload.module.css';
import { FileTextOutlined } from '@ant-design/icons';
import { DeleteOutlined } from '@ant-design/icons';
import spinner from '../../images/spinner.jpeg'

const FileUpload = ({ setCheckResult }) => {
    const [useCaseFiles, setUseCaseFiles] = useState([]);
    const [regulationFiles, setRegulationFiles] = useState([]);
    const [loading, setLoading] = useState(false);

    // Handler for selecting use case files
    const handleUseCaseFileChange = (event) => {
        const newFiles = Array.from(event.target.files);

        setUseCaseFiles((prevFiles) => {
            // Keep only .docx files
            const updatedFiles = prevFiles.filter(file => file.name.endsWith('.docx'));

            newFiles.forEach((newFile) => {
                if (newFile.name.endsWith('.docx')) {
                    // Find index of existing file with same name
                    const index = updatedFiles.findIndex((file) => file.name === newFile.name);

                    if (index !== -1) {
                        // Replace old file with new one          
                        updatedFiles[index] = newFile;
                    } else {
                        // If file doesn't exist, add it
                        updatedFiles.push(newFile);
                    }
                }
            });

            return updatedFiles;
        });
    };

    // Handler for selecting regulation files
    const handleRegulationFileChange = (event) => {
        const newFiles = Array.from(event.target.files);

        setRegulationFiles((prevFiles) => {
            // Keep only .pdf files
            const updatedFiles = prevFiles.filter(file => file.name.endsWith('.pdf'));

            newFiles.forEach((newFile) => {
                if (newFile.name.endsWith('.pdf')) {
                    // Find index of existing file with same name
                    const index = updatedFiles.findIndex((file) => file.name === newFile.name);

                    if (index !== -1) {
                        // Replace old file with new one
                        updatedFiles[index] = newFile;
                    } else {
                        // If file doesn't exist, add it
                        updatedFiles.push(newFile);
                    }
                }
            });

            return updatedFiles;
        });
    };

    // Form submission
    const handleSubmit = async (event) => {
        event.preventDefault();

        // Show loading
        setLoading(true)

        const formData = new FormData();

        // Append use case files
        useCaseFiles.forEach((file) => {
            formData.append('useCases', file);
        });

        // Append regulation files
        regulationFiles.forEach((file) => {
            formData.append('regulations', file);
        });

        try {
            // Send POST request to the server with files
            const response = await axios.post('https://andrey.hack-szfo-2024.gros.pro/api/check-uc', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            // Hide loading indicator
            setLoading(false)
            if (response.status === 200) {
                // If response is successful, update check result
                setCheckResult(response.data);
                console.log('successful response', response);
            } else {
                alert(`Ошибка загрузки: ${response.statusText}`);
            }
        } catch (error) {
            // Hide loading indicator
            setLoading(false)
            console.error('Ошибка при загрузке файлов:', error);
            alert('Произошла ошибка при загрузке файлов.');
        }
    };

    // Handler for deleting use case file
    const handleDeleteUseCaseFile = (indexToDelete) => {
        setUseCaseFiles((prevFiles) =>
            prevFiles.filter((_, index) => index !== indexToDelete)
        );

        // Reset input fields for use case files
        document.getElementById('useCaseFileInput').value = '';
        document.getElementById('useCaseFolderInput').value = '';
    };

    // Handler for deleting Regulation file
    const handleDeleteRegulationFile = (indexToDelete) => {
        setRegulationFiles((prevFiles) =>
            prevFiles.filter((_, index) => index !== indexToDelete)
        );

        // Reset input fields for regulation files
        document.getElementById('regulationFileInput').value = '';
        document.getElementById('regulationFolderInput').value = '';
    };

    // Determine if submit button should be disabled
    const isDisabled = useCaseFiles.length === 0 || regulationFiles.length === 0;

    return (
        <form onSubmit={handleSubmit} className={cl.form}>
            <div className={cl.container}>
                <div className={cl.fileUploadContainer}>
                    <div className={cl.btnContainer}>
                        {/* Input for selecting use case file */}
                        <input
                            type="file"
                            accept=".docx"
                            multiple
                            onChange={handleUseCaseFileChange}
                            style={{ display: 'none' }}
                            id="useCaseFileInput"
                        />
                        <Button
                            className={`${cl.btn} ${cl.fisrtBtn}`}
                            type="primary"
                            onClick={() => document.getElementById('useCaseFileInput').click()}
                        >
                            Load use case file
                        </Button>


                        {/* Input for selecting use case folders */}
                        <input
                            type="file"
                            accept=".docx"
                            multiple
                            webkitdirectory="true"
                            onChange={handleUseCaseFileChange}
                            style={{ display: 'none' }}
                            id="useCaseFolderInput"
                        />
                        <Button
                            className={cl.btn}
                            type="primary"
                            onClick={() => document.getElementById('useCaseFolderInput').click()}
                        >
                            Load use case folder
                        </Button>
                    </div>

                    {/* Display list of uploaded use case files */}
                    {useCaseFiles.length > 0 &&
                        <div className={cl.useCaseFilesContainer}>
                            {useCaseFiles.map((file, index) => (
                                <div className={cl.fileItem} key={index}>
                                    <FileTextOutlined />
                                    <p className={cl.fileName} >{file.name}</p>
                                    <DeleteOutlined
                                        className={cl.deleteBtn}
                                        onClick={() => handleDeleteUseCaseFile(index)}
                                    />
                                </div>
                            ))}
                        </div>
                    }
                </div>

                {/* Submit button for checking results */}
                <Button
                    className={`${cl.btn} ${isDisabled ? cl.disabled : ''} ${cl.btnCheck}`}
                    type="primary"
                    htmlType="submit"
                    disabled={isDisabled}
                >
                    Сheck the result
                </Button>

                <div className={cl.fileUploadContainer}>
                    <div className={cl.btnContainer}>
                        {/* Input for selecting regulation file */}
                        <input
                            type="file"
                            accept=".pdf"
                            multiple
                            onChange={handleRegulationFileChange}
                            style={{ display: 'none' }}
                            id="regulationFileInput"
                        />
                        <Button
                            className={`${cl.btn} ${cl.fisrtBtn}`}
                            type="primary"
                            onClick={() => document.getElementById('regulationFileInput').click()}
                        >
                            Load regulation file
                        </Button>

                        {/* Input for selecting use case folder */}
                        <input
                            type="file"
                            accept=".pdf"
                            multiple
                            webkitdirectory="true"
                            onChange={handleRegulationFileChange}
                            style={{ display: 'none' }}
                            id="regulationFolderInput"
                        />
                        <Button
                            className={cl.btn}
                            type="primary"
                            onClick={() => document.getElementById('regulationFolderInput').click()}
                        >
                            Load regulation folder
                        </Button>

                    </div>

                    {/* Display list of uploaded regulation files */}
                    {regulationFiles.length > 0 &&
                        <div className={cl.useCaseFilesContainer}>
                            {regulationFiles.map((file, index) => (
                                <div className={cl.fileItem} key={index}>
                                    <FileTextOutlined />
                                    <p className={cl.fileName}>{file.name}</p>
                                    <DeleteOutlined
                                        className={cl.deleteBtn}
                                        onClick={() => handleDeleteRegulationFile(index)}
                                    />
                                </div>
                            ))}
                        </div>
                    }

                </div>

                {loading &&
                    <div className={cl.overlay}>
                        <img
                            src={spinner}
                            alt="Загрузка..."
                            className={cl.spinner}
                        />
                    </div>
                }
            </div>
        </form>
    );
};

export default FileUpload;
