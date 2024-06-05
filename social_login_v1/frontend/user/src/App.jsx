import styles from './App.module.css';
import { Routes, Route } from 'react-router-dom';
import WelcomePage from './pages/WelcomePage';
import LoginResultPage from './pages/LoginResultPage';
import { authAxios } from './apis/authAxios';

function App() {
    function request() {
        authAxios
            .get(`/test/2`)
            .then((res) => {
                console.log(res);
                alert(res.data);
            })
            .catch((err) => {
                if (err.response) {
                    console.log(err.response.data);
                }
                //TODO : store 비우고 로그인 페이지로 이동(로그아웃 처리)
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
