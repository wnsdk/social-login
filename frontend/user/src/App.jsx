import { Routes, Route } from 'react-router-dom';
import WelcomePage from './pages/WelcomePage';
import LoginResultPage from './pages/LoginResultPage';
import RequestButtons from './components/RequestButtons';
import Expiration from './components/Expiration';

export default function App() {
    return (
        <>
            <Routes>
                <Route path="/" element={<WelcomePage />} />
                <Route path="/oauth/redirect" element={<LoginResultPage />} />
            </Routes>
            <RequestButtons />
            <Expiration />
        </>
    );
}
