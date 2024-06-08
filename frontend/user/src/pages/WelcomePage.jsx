import styles from "./WelcomePage.module.css";

export default function LoginPage() {
  // 소셜 로그인
  function socialLogin(thirdPartyId) {
    window.location.href = `http://localhost:8080/oauth2/authorization/${thirdPartyId}?redirect_uri=http://localhost:5173/oauth/redirect`;
  }

  return (
    <div className={styles.body}>
      <div className={styles.btn_box}>
        <button className={styles.btn} onClick={() => socialLogin("google")}>
          구글 로그인
        </button>
      </div>
    </div>
  );
}
