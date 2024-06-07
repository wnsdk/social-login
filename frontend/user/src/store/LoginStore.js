import { create } from 'zustand';

const useLoginStore = create((set) => ({
    email: null,
    name: null,
    profile: null,
    role: null,
    accessToken: null,
    setAccessToken: (accessToken) => set({ accessToken: accessToken }),
    // setLogin: (email, name, profile, role, accessToken) =>
    //     set({ email: email, name: name, profile: profile, role: role, accessToken: accessToken }),
    setLogout: () => set({ email: null, name: null, profile: null, role: null, accessToken: null }),
}));

export { useLoginStore };
