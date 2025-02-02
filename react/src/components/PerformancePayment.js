import React, { useState, useCallback } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import config from '../config';
import useAuthStore from '../store';

function PerformancePayment() {
  const { performanceId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { seatId, seatCode } = location.state || {};
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const { accessToken } = useAuthStore(state => ({ accessToken: state.accessToken }));

  const seatInfo = seatCode ? `${seatCode}` : '좌석 정보 없음';

  const buttonStyle = {
    padding: '10px 20px',
    fontSize: '16px',
    color: 'white',
    backgroundColor: '#4CAF50',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    textDecoration: 'none',
    display: 'inline-block',
    margin: '0 10px',
  };

  const releaseSeat = useCallback(async () => {
    if (!seatId) return;

    try {
      const dbResponse = await fetch(`${config.API_URL}/api/seats/release`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`,
          'performanceId': `${performanceId}`
        },
        body: JSON.stringify({
          seatId: seatId
        })
      });

      if (!dbResponse.ok) {
        console.error('DB 좌석 해제 중 오류가 발생했습니다.');
      }
    } catch (err) {
      console.error('Seat release error:', err);
    }
  }, [seatId, accessToken, performanceId]);

  const handlePayment = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch(`${config.API_URL}/api/tickets`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${accessToken}`,
          'performanceId': `${performanceId}`
        },
        body: JSON.stringify({
          seatId: seatId
        })
      });

      if (!response.ok) {
        throw new Error('결제 처리 중 오류가 발생했습니다.');
      }

      alert('결제가 완료되었습니다!');
      navigate('/my-tickets');
    } catch (err) {
      setError(err.message);
      console.error('Payment error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackToSeatSelection = async () => {
    await releaseSeat();
    navigate(`/performances/${performanceId}/select`);
  };

  const handleGoHome = async () => {
    await releaseSeat();
    navigate('/');
  };

  return (
      <div className="content" style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
        <h2 style={{ textAlign: 'center' }}>공연 결제</h2>
        <p>공연 ID: {performanceId}</p>
        <p>선택한 좌석: {seatInfo}</p>
        <p>결제 금액: 50,000원</p>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
          <button
              style={{ ...buttonStyle, opacity: isLoading ? 0.5 : 1 }}
              onClick={handlePayment}
              disabled={isLoading}
          >
            {isLoading ? '처리 중...' : '결제하기'}
          </button>
          <button
              onClick={handleBackToSeatSelection}
              style={{ ...buttonStyle, backgroundColor: '#2196F3' }}
          >
            좌석 선택으로 돌아가기
          </button>
          <button
              onClick={handleGoHome}
              style={{ ...buttonStyle, backgroundColor: '#f44336' }}
          >
            홈
          </button>
        </div>
      </div>
  );
}

export default PerformancePayment;
