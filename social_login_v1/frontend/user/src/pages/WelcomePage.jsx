import styles from './WelcomePage.module.css';
import { useLoginStore } from '../store/LoginStore';

export default function LoginPage() {
    // 로그인 정보
    const email = useLoginStore.getState().email;
    const name = useLoginStore.getState().name;
    const profile = useLoginStore.getState().profile;

    function login(thirdPartyId) {
        window.location.href = `http://localhost:8080/oauth2/authorization/${thirdPartyId}?redirect_uri=http://localhost:5173/oauth/redirect`;
    }

    return (
        <div className={styles.body}>
            {email == null ? (
                <div className={styles.btn_box}>
                    <button className={styles.btn} onClick={() => login('google')}>
                        구글 로그인
                    </button>
                    <button className={styles.btn} onClick={() => login('kakao')}>
                        카카오 로그인
                    </button>
                    <button className={styles.btn} onClick={() => login('naver')}>
                        네이버 로그인
                    </button>
                </div>
            ) : (
                email
            )}
        </div>
    );
}
