import cl from './CheckResult.module.css'
import React from 'react'

const CheckResult = ({ checkResult }) => {
    
    
    return (
        <div className={cl.container}>
            {/* Iterate through list of files */}
            {checkResult.map((file, fileIndex) => (
                <div key={fileIndex} className={cl.fileSection}>
                    {/* Display the name of the file */}
                    <p className={cl.fileName}>{file.fileName}</p>

                    {/* Iterate through regulations related to each file */}
                    {file.regulations.map((regulation, regulationIndex) => (
                        <div key={regulationIndex} className={cl.regulationItem}>
                            {/* Display regulation name with its index */}
                            <p className={cl.regulationName}>
                                {`${regulationIndex + 1}. ${regulation.name}:`}
                            </p>

                            {/* Display comment of the regulation */}
                            <p className={cl.comment}>
                                {regulation.comment}
                            </p>
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
};

export default CheckResult;
