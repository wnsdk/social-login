import axios from 'axios';

export const $ = axios.create({
    baseURL: 'http://localhost:8080/',
    withCredentials: true,
});
