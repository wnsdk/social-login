import axios from 'axios';
import { useLoginStore } from '../store/LoginStore';

export const authAxios = axios.create({
    baseURL: 'http://localhost:8080/',
    withCredentials: true,
});

authAxios.interceptors.request.use((config) => {
    const { accessToken } = useLoginStore.getState();
    // console.log('요청 : ', accessToken);
    config.headers['Authorization'] = 'Bearer ' + accessToken;
    return config;
});

authAxios.interceptors.response.use(
    (response) => {
        const { setAccessToken } = useLoginStore.getState();
        const accessToken = response.headers.get('Access-Token');

        if (accessToken) {
            setAccessToken(accessToken);
        }
        console.log('응답 : ', accessToken);
        return response;
    },
    (error) => {
        console.log(error);
        return Promise.reject(error);
    }
);
