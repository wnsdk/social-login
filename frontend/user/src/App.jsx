import { Routes, Route } from 'react-router-dom';
import MainPage from './pages/MainPage';
import CallbackPage from './pages/CallbackPage';

function App() {
    return (
        <>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/oauth/redirect" element={<CallbackPage />} />
            </Routes>
        </>
    );
}

export default App;
