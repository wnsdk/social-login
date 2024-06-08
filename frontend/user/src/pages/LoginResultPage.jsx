import styles from "./LoginResultPage.module.css";
import { useSearchParams } from "react-router-dom";
import { Buffer } from "buffer";
import { useEffect } from "react";
import { useLoginStore } from "../store/LoginStore";

export default function LoginResultPage() {
  const [searchParams] = useSearchParams();

  const { email, name, profile, role, accessToken } = useLoginStore();

  useEffect(() => {
    const base64Payload = searchParams.get("accessToken").split(".")[1];
    const payload = Buffer.from(base64Payload, "base64");
    const result = JSON.parse(payload.toString());

    useLoginStore.setState({
      email: result.email,
      name: result.name,
      profile: result.profile,
      role: result.auth,
      accessToken: searchParams.get("accessToken"),
    });
  }, []);

  return (
    <div className={styles.body}>
      <h2>환영합니다</h2>
      <img className={styles.profile} src={profile} />
      <h3>{name} 님</h3>

      <table>
        <tbody>
          <tr>
            <th>이메일</th>
            <td>{email}</td>
          </tr>
          <tr>
            <th>권한</th>
            <td>{role}</td>
          </tr>

          <tr>
            <th>access token</th>
            <td>{accessToken}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}
