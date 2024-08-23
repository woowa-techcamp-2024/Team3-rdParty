import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import config from '../config';

function Signup() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password.length < 8 || password.length > 20) {
      setError('비밀번호는 8자 이상, 20자 이하여야 합니다.');
      return;
    }

    try {
      const response = await fetch(`${config.API_URL}/api/members`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || '회원가입에 실패했습니다.');
      }

      const data = await response.json();
      console.log('회원가입 성공:', data);
      alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
      navigate('/login');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="content">
      <h2>회원가입</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">이메일:</label>
          <input 
            type="email" 
            id="email" 
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required 
          />
        </div>
        <div>
          <label htmlFor="password">비밀번호:</label>
          <input 
            type="password" 
            id="password" 
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required 
            minLength="8"
            maxLength="20"
          />
        </div>
        <button type="submit">가입하기</button>
      </form>
    </div>
  );
}

export default Signup;
