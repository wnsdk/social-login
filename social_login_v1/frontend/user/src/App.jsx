import styles from './App.module.css';
import { Routes, Route } from 'react-router-dom';
import WelcomePage from './pages/WelcomePage';
import LoginResultPage from './pages/LoginResultPage';
import { authAxios } from './apis/authAxios';

function App() {
    function request() {
        authAxios.get(`/test/2`).then((res) => {
            alert(res.data);
        });
    }

    return (
        <>
            <Routes>
                <Route path="/" element={<WelcomePage />} />
                <Route path="/oauth/redirect" element={<LoginResultPage />} />
            </Routes>
            <button className={styles.floating_btn} onClick={() => request()}>
                서버에
                <br /> 요청 보내기
            </button>
        </>
    );
}

export default App;
