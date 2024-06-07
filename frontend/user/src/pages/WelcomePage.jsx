import styles from './WelcomePage.module.css';
import { useLoginStore } from '../store/LoginStore';
import { $ } from '../apis/axios';
import { useNavigate } from 'react-router-dom';

export default function LoginPage() {
    const navigate = useNavigate();

    // 소셜 로그인
    function socialLogin(thirdPartyId) {
        window.location.href = `http://localhost:8080/oauth2/authorization/${thirdPartyId}?redirect_uri=http://localhost:5173/oauth/redirect`;
    }

    // 테스트용 로그인 (실제 서비스에서는 사용하면 안 됨!)
    function testLogin(role) {
        $.get(`/test/login/${role}`).then((res) => {
            // TODO : 액세스 토큰을 쿼리 파라미터에 담아서 바로 리다이렉트
            navigate(`/oauth/redirect?accessToken=${res.data}`);
        });
    }

    return (
        <div className={styles.body}>
            <div className={styles.btn_box}>
                <button className={styles.btn} onClick={() => socialLogin('google')}>
                    구글 로그인
                </button>

                {/* <button className={styles.btn} onClick={() => login('kakao')}>
                        카카오 로그인
                    </button>
                    <button className={styles.btn} onClick={() => login('naver')}>
                        네이버 로그인
                    </button> */}
                <button className={styles.btn} onClick={() => testLogin('admin')}>
                    USER 테스트 로그인
                </button>
                <button className={styles.btn} onClick={() => testLogin('user')}>
                    ADMIN 테스트 로그인
                </button>
            </div>
        </div>
    );
}
