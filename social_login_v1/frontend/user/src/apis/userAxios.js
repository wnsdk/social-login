import axios from 'axios';

export const $ = axios.create({
    baseURL: 'http://localhost:8080/',
});

// $.interceptors.request.use((config) => {
//     config.headers['Access-Token'] = sessionStorage.getItem('access-token');
//     return config;
// });
