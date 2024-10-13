import cl from './DocumentVerification.module.css'
import FileUpload from '../../components/fileUpload/FileUpload';
import { useState } from 'react';
import CheckResult from '../../components/checkResult/CheckResult';

const DocumentVerification = () => {
    const [checkResult, setCheckResult] = useState([]);

    return (
        <div className={cl.container}>

            {/* uploading and displaying files */}
            <FileUpload setCheckResult={setCheckResult} />

            {/* displaying test result */}
            {checkResult.length !== 0 &&
                <CheckResult checkResult={checkResult} />
            }

        </div>
    )
}

export default DocumentVerification