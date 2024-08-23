import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import useAuthStore from '../store';
import useCounterStore from '../counterStore';
import config from '../config';

function PerformanceWaiting() {
  const { performanceId } = useParams();
  const { accessToken } = useAuthStore(state => ({ accessToken: state.accessToken }));
  const { remainingCount, setRemainingCount } = useCounterStore(state => ({
    remainingCount: state.remainingCount,
    setRemainingCount: state.setRemainingCount
  }));
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRemainingCount = async () => {
      try {
        const response = await fetch(`${config.API_URL}/api/performances/${performanceId}/wait`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
            'performanceId': `${performanceId}`
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch remaining count');
        }

        const data = await response.json();
        setRemainingCount(data.remainingCount);

        // 대기 순번이 0 이하일 때 좌석 선택 화면으로 이동
        if (data.remainingCount <= 0) {
          navigate(`/performances/${performanceId}/select`);
        }

      } catch (error) {
        console.error('Error fetching remaining count:', error);
      }
    };

    // 초기 fetch
    // fetchRemainingCount();

    // 5초마다 fetch
    const intervalId = setInterval(fetchRemainingCount, 5000);

    // Clean up
    return () => clearInterval(intervalId);
  }, [performanceId, accessToken, setRemainingCount, navigate]);

  return (
    <div className="content">
      <h2>공연 대기열</h2>
      <p>공연 ID: {performanceId}</p>
      <p>현재 대기 순번: {remainingCount}번</p>
    </div>
  );
}

export default PerformanceWaiting;
