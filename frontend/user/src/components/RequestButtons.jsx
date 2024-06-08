import styles from "./RequestButtons.module.css";
import { authAxios } from "../apis/authAxios";

export default function RequestButtons() {
  function request(role) {
    authAxios.get(`/test/${role}`).then((res) => {
      alert(res.data);
    });
  }
  return (
    <>
      <div className={styles.btns}>
        <button className={styles.btn} onClick={() => request("all")}>
          all
        </button>
        <button className={styles.btn} onClick={() => request("user")}>
          user
        </button>
        <button className={styles.btn} onClick={() => request("admin")}>
          admin
        </button>
      </div>
    </>
  );
}
