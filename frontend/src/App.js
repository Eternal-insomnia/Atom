import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import DocumentVerification from './pages/documentVerification/DocumentVerification';

function App() {
  return (
    <div className="App">
      {/* BrowserRouter is used if multiple pages are needed */}
      <BrowserRouter>
        <Routes>
          {/* Home page */}
          <Route path="*" element={<DocumentVerification />} />
  
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
