import { create } from 'zustand';
import Cookies from 'js-cookie';

const COOKIE_NAME = 'auth_token';
const COOKIE_EXPIRY_MINUTES = 30;

const getInitialState = () => {
  const token = Cookies.get(COOKIE_NAME);
  return {
    isLoggedIn: !!token,
    memberId: null,
    accessToken: token || null,
  };
};

const useAuthStore = create((set) => ({
  ...getInitialState(),
  login: (memberId, accessToken) => {
    Cookies.set(COOKIE_NAME, accessToken, { expires: COOKIE_EXPIRY_MINUTES / (24 * 60) });
    set({ 
      isLoggedIn: true, 
      memberId, 
      accessToken 
    });
  },
  logout: () => {
    Cookies.remove(COOKIE_NAME);
    set({ 
      isLoggedIn: false, 
      memberId: null, 
      accessToken: null 
    });
  },
  checkAuth: () => {
    const state = getInitialState();
    set(state);
    return state.isLoggedIn;
  },
}));

export default useAuthStore;
