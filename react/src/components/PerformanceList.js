import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import config from '../config';
import useAuthStore from '../store';

function PerformanceList() {
  const [performances, setPerformances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { isLoggedIn, accessToken } = useAuthStore(state => ({
    isLoggedIn: state.isLoggedIn,
    accessToken: state.accessToken
  }));

  useEffect(() => {
    const fetchPerformances = async () => {
      if (!isLoggedIn) {
        setError('로그인이 필요합니다. 로그인 후 다시 시도해 주세요.');
        setLoading(false);
        return;
      }

      try {
        const response = await fetch(`${config.API_URL}/api/performances`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
          },
        });

        if (!response.ok) {
          throw new Error('공연 정보를 불러오는 데 실패했습니다.');
        }

        const data = await response.json();
        setPerformances(data.items);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchPerformances();
  }, [isLoggedIn, accessToken]);

  if (loading) return <div className="content">로딩 중...</div>;

  return (
    <div className="content">
      <h2>공연 목록</h2>
      {error && <p className="error">{error}</p>}
      {!error && (
        <ul>
          {performances.map((performance) => (
            <li key={performance.performanceId}>
              <h3>{performance.performanceName}</h3>
              <p>장소: {performance.performancePlace}</p>
              <p>일시: {new Date(performance.performanceShowtime).toLocaleString()}</p>
              <Link to={`/performances/${performance.performanceId}/select`}>좌석 구매</Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default PerformanceList;
