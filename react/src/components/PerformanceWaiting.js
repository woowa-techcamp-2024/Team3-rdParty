import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import useAuthStore from '../store';
import useCounterStore from '../counterStore';
import config from '../config';
import { ClipLoader } from 'react-spinners';

function PerformanceWaiting() {
  const { performanceId } = useParams();
  const { accessToken } = useAuthStore(state => ({ accessToken: state.accessToken }));
  const { remainingCount, setRemainingCount } = useCounterStore(state => ({
    remainingCount: state.remainingCount,
    setRemainingCount: state.setRemainingCount
  }));
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchRemainingCount = async () => {
      setIsLoading(true);
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
      } finally {
        setIsLoading(false);
      }
    };

    //fetchRemainingCount();
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

      navigate('/');
    } catch (error) {
      console.error('Error leaving queue:', error);
    }
  };

  return (
      <div className="content" style={{ textAlign: 'center', padding: '20px' }}>
        <h2>공연 대기열</h2>
        <p>공연 ID: {performanceId}</p>
        <div style={{ margin: '20px 0' }}>
          <ClipLoader color="#36D7B7" loading={isLoading} size={50} />
        </div>
        <p style={{ fontSize: '18px', fontWeight: 'bold' }}>
          현재 대기 순번: {remainingCount}번
        </p>
        <p style={{ fontSize: '14px', color: '#666', margin: '10px 0' }}>
          대기열 정보를 5초마다 갱신 중입니다...
        </p>
        <button
            onClick={handleLeaveQueue}
            style={{
              padding: '10px 20px',
              fontSize: '16px',
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer'
            }}
        >
          대기열 나가기
        </button>
      </div>
  );
}

export default PerformanceWaiting;
