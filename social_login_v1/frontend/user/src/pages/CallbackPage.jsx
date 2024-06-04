import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useLoginStore } from '../store/UserStore';

export default function MainPage() {
    const navigate = useNavigate();

    const login = (email, name, profile, role, accessToken, refreshToken) => {
        useLoginStore.setState({
            email: email,
            name: name,
            profile: profile,
            role: role,
            accessToken: accessToken,
            refreshToken: refreshToken,
        });
    };

    useEffect(() => {
        console.log('어 형이야');
        login('a', 'b', 'c', 'd', 'e', 'f');
        navigate('../../');
    });
}
