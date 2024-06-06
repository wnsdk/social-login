import styles from './LoginResultPage.module.css';
import { Buffer } from 'buffer';
import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useLoginStore } from '../store/LoginStore';
import { authAxios } from '../apis/authAxios';

export default function LoginResultPage() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    // const email = useLoginStore(state => state.email);
    // const name = useLoginStore(state => state.name);
    // const profile = useLoginStore(state => state.profile);
    // const role = useLoginStore(state => state.role);
    // const accessToken = useLoginStore(state => state.accessToken);
    // const setAccessToken = useLoginStore(state => state.setAccessToken);

    const { email, name, profile, role, accessToken, setAccessToken, setLogout } = useLoginStore();

    const logout = () => {
        authAxios.post(`/logout`).then(() => {
            setLogout(); // store에 저장된 로그인 정보 없애기
            navigate('/');
        });
    };

    useEffect(() => {
        const base64Payload = searchParams.get('accessToken').split('.')[1];
        const payload = Buffer.from(base64Payload, 'base64');
        const result = JSON.parse(payload.toString());

        useLoginStore.setState({
            email: result.email,
            name: result.name,
            profile: result.profile,
            role: result.auth,
            accessToken: searchParams.get('accessToken'),
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

            <button className={styles.btn} onClick={() => logout()}>
                로그아웃
            </button>
        </div>
    );
}
