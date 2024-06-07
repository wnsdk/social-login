import styles from './Expiration.module.css';
import { useLoginStore } from '../store/LoginStore';
import { useEffect, useState } from 'react';
import { Buffer } from 'buffer';
import { authAxios } from '../apis/authAxios';

export default function LoginPage() {
    const { accessToken } = useLoginStore();

    const [expOfAccessToken, setExpOfAccessToken] = useState();
    const [expOfRefreshToken, setExpOfRefreshToken] = useState();

    const [timeOfAccessToken, setTimeOfAccessToken] = useState();
    const [timeOfRefreshToken, setTimeOfRefreshToken] = useState();

    // 카운트다운
    useEffect(() => {
        let id = setInterval(() => {
            const now = Math.floor(Date.now() / 1000, 3);
            setTimeOfAccessToken(expOfAccessToken - now);
            setTimeOfRefreshToken(expOfRefreshToken - now);
        }, 1000);
        return () => clearInterval(id);
    });

    useEffect(() => {
        if (accessToken != null) {
            const base64Payload = accessToken.split('.')[1];
            const payload = Buffer.from(base64Payload, 'base64');
            const result = JSON.parse(payload.toString());
            setExpOfAccessToken(parseInt(result.exp));

            // RT는 httpOnly 쿠키에 저장돼 있어서 client에서 직접 구할 수 없다.
            // server에 요청해서 RT 값을 구할 수 있다.
            authAxios.get(`/test/exp/rt`).then((res) => {
                setExpOfRefreshToken(Math.floor(res.data / 1000, 3));
            });
        }
    }, [accessToken]);

    return (
        <>
            {accessToken != null && (
                <table className={styles.body}>
                    <tbody>
                        <tr>
                            <th>AccessToken</th>
                            <td>{timeOfAccessToken > 0 ? timeOfAccessToken : 0}초</td>
                        </tr>
                        <tr>
                            <th>RefreshToken</th>
                            <td>{timeOfRefreshToken > 0 ? timeOfRefreshToken : 0}초</td>
                        </tr>
                    </tbody>
                </table>
            )}
        </>
    );
}
