import styles from './WelcomePage.module.css';
import { $ } from '../apis/axios';
import { useNavigate } from 'react-router-dom';

export default function WelcomePage() {
    const navigate = useNavigate();

    // 소셜 로그인
    function socialLogin(thirdPartyId) {
        window.location.href = `http://localhost:8080/oauth2/authorization/${thirdPartyId}?redirect_uri=http://localhost:5173/oauth/redirect`;
    }

    // 테스트용 로그인 (실제 서비스에서는 사용하면 안 됨!)
    function testLogin(role) {
        $.get(`/login/${role}`).then((res) => {
            navigate(`/oauth/redirect?accessToken=${res.data}`);
        });
    }

    return (
        <div className={styles.body}>
            <div className={styles.btn_box}>
                <button className={styles.btn} onClick={() => socialLogin('google')}>
                    구글 로그인
                </button>
                <button className={styles.btn} onClick={() => testLogin('user')}>
                    USER 테스트 로그인
                </button>
                <button className={styles.btn} onClick={() => testLogin('admin')}>
                    ADMIN 테스트 로그인
                </button>
            </div>
        </div>
    );
}
