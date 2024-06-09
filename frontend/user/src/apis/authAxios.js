import axios from 'axios';
import { useLoginStore } from '../store/LoginStore';

export const authAxios = axios.create({
    baseURL: 'http://localhost:8080/',
    withCredentials: true,
});

// 요청을 보내기 전, 인증 정보를 담아서 서버에 전송
authAxios.interceptors.request.use((config) => {
    const { accessToken } = useLoginStore.getState();
    config.headers['Authorization'] = 'Bearer ' + accessToken;
    return config;
});

authAxios.interceptors.response.use(
    // 응답 헤더에 AccessToken이 포함되어 있다면, 로그인 정보 갱신하기
    (response) => {
        const { setAccessToken } = useLoginStore.getState();
        const accessToken = response.headers.get('Access-Token');

        if (accessToken) {
            setAccessToken(accessToken);
        }
        return response;
    },

    // 인증되지 않은 유저라면 로그인 페이지로 보내기
    (error) => {
        const { accessToken, setLogout } = useLoginStore.getState();

        if (error.response.status == 401) {
            alert('로그인이 필요합니다.');
            window.location.href = '/';

            // 로그인 한 유저였다면 로그아웃 시키기
            if (accessToken != null) {
                authAxios.post(`/logout`).then(() => {
                    setLogout();
                });
            }
        } else if (error.response.status == 403) {
            alert('권한이 부족합니다.');
        }
        return Promise.reject(error);
    }
);
