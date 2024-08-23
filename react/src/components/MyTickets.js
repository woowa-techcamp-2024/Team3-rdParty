import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import config from '../config';
import useAuthStore from '../store';

function MyTickets() {
  const [tickets, setTickets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const { accessToken } = useAuthStore(state => ({ accessToken: state.accessToken }));

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const response = await fetch(`${config.API_URL}/api/members/tickets`, {
          headers: {
            'Authorization': `Bearer ${accessToken}`,
          },
        });

        if (!response.ok) {
          throw new Error('티켓 정보를 불러오는데 실패했습니다.');
        }

        const data = await response.json();
        setTickets(data.items);
      } catch (err) {
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTickets();
  }, [accessToken]);

  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>에러: {error}</div>;

  return (
    <div className="my-tickets">
      <h2>내 티켓</h2>
      {tickets.length === 0 ? (
        <p>구매한 티켓이 없습니다.</p>
      ) : (
        <ul>
          {tickets.map((ticket) => (
            <li key={ticket.serialNumber}>
              <h3>{ticket.performance.performanceName}</h3>
              <p>장소: {ticket.performance.performancePlace}</p>
              <p>날짜: {new Date(ticket.performance.performanceShowtime).toLocaleDateString()}</p>
              <p>좌석: {ticket.seat.seatCode}</p>
            </li>
          ))}
        </ul>
      )}
      <button onClick={() => navigate('/')}>홈으로</button>
    </div>
  );
}

export default MyTickets;
