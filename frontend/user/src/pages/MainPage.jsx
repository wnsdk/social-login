import { useLoginStore } from '../store/UserStore';

export default function MainPage() {
    // 로그인 정보
    const email = useLoginStore.getState().email;
    const name = useLoginStore.getState().name;
    const profile = useLoginStore.getState().profile;

    function login(thirdPartyId) {
        window.location.href = `http://localhost:8080/oauth2/authorization/${thirdPartyId}?redirect_uri=http://localhost:5173/oauth/redirect`;
    }

    return (
        <>
            {email == null ? (
                <>
                    <button onClick={() => login('google')}>구글 로그인</button>
                    <button onClick={() => login('kakao')}>카카오 로그인</button>
                    <button onClick={() => login('naver')}>네이버 로그인</button>
                </>
            ) : (
                email
            )}
        </>
    );
}
