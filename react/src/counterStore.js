import { create } from 'zustand';
import Cookies from 'js-cookie';

const COOKIE_NAME = 'remaining_count';
const COOKIE_EXPIRY_MINUTES = 30;

const getInitialState = () => {
  const count = Cookies.get(COOKIE_NAME);
  return {
    remainingCount: count ? parseInt(count, 10) : null,
  };
};

const useCounterStore = create((set) => ({
  ...getInitialState(),
  setRemainingCount: (count) => {
    Cookies.set(COOKIE_NAME, count.toString(), { expires: COOKIE_EXPIRY_MINUTES / (24 * 60) });
    set({ remainingCount: count });
  },
  clearRemainingCount: () => {
    Cookies.remove(COOKIE_NAME);
    set({ remainingCount: null });
  },
  checkRemainingCount: () => {
    const state = getInitialState();
    set(state);
    return state.remainingCount;
  },
}));

export default useCounterStore;
