import axios from 'axios';
import { useLoginStore } from '../store/UserStore';

export const authAxios = axios.create({
    baseURL: 'http://localhost:8080/',
    withCredentials: true,
});

authAxios.interceptors.request.use((config) => {
    config.headers['Authorization'] = 'Bearer ' + useLoginStore.getState().accessToken;
    return config;
});
