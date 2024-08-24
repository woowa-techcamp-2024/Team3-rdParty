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

        if (data.remainingCount <= 0) {
          navigate(`/performances/${performanceId}/select`);
        }
      } catch (error) {
        console.error('Error fetching remaining count:', error);
      }
    };

    const intervalId = setInterval(fetchRemainingCount, 5000);

    return () => clearInterval(intervalId);
  }, [performanceId, accessToken, setRemainingCount, navigate]);

  const handleLeaveQueue = async () => {
    try {
      const response = await fetch(`${config.API_URL}/api/performances/${performanceId}/wait`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`,
          'performanceId': `${performanceId}`
        },
      });

      if (!response.ok) {
        throw new Error('Failed to leave queue');
      }

      // 홈으로 이동
      navigate('/');
    } catch (error) {
      console.error('Error leaving queue:', error);
    }
  };

  return (
    <div className="content">
      <h2>공연 대기열</h2>
      <p>공연 ID: {performanceId}</p>
      <p>현재 대기 순번: {remainingCount}번</p>
      <button onClick={handleLeaveQueue}>대기열 나가기</button>
    </div>
  );
}

export default PerformanceWaiting;
