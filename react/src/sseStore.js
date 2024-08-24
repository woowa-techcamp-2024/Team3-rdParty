import { create } from 'zustand';
import config from './config';
import { EventSourcePolyfill, NativeEventSource } from "event-source-polyfill";
const EventSource = EventSourcePolyfill || NativeEventSource;

const useSseStore = create((set, get) => ({
  eventSource: null,
  currentPerformanceId: null,

  connectSse: (performanceId, accessToken) => {
    const { eventSource, currentPerformanceId } = get();
    
    // 이미 같은 공연에 대한 연결이 있다면 새로 연결하지 않음
    if (currentPerformanceId === performanceId && eventSource) {
      console.log(`이미 공연 ID ${performanceId}에 대한 SSE 연결이 존재합니다.`);
      return;
    }

    // 이전 연결이 있다면 먼저 종료
    if (eventSource) {
      eventSource.close();
    }

    const newEventSource = new EventSource(`${config.API_URL}/api/subscribe/performances/${performanceId}`, {
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'performanceId': `${performanceId}`
      }
    });

    newEventSource.onopen = () => {
      console.log(`SSE 연결됨 (공연 ID: ${performanceId})`);
    };

    newEventSource.onerror = (error) => {
      console.error(`SSE 에러 (공연 ID: ${performanceId}):`, error);
    };

    set({ eventSource: newEventSource, currentPerformanceId: performanceId });
  },

  disconnectSse: () => {
    const { eventSource } = get();
    if (eventSource) {
      eventSource.close();
      console.log("SSE 연결 해제됨");
    }
    set({ eventSource: null, currentPerformanceId: null });
  },

  addEventListenerToSse: (eventName, callback) => {
    const { eventSource } = get();
    if (eventSource) {
      eventSource.addEventListener(eventName, callback);
    }
  },

  removeEventListenerFromSse: (eventName, callback) => {
    const { eventSource } = get();
    if (eventSource) {
      eventSource.removeEventListener(eventName, callback);
    }
  }
}));

export default useSseStore;