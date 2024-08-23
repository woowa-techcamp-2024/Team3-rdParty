import React from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink, useNavigate } from 'react-router-dom';
import './App.css';
import useAuthStore from './store';

import Signup from './components/Signup.js';
import Login from './components/Login.js';
import PerformanceList from './components/PerformanceList.js';
import PerformanceWaiting from './components/PerformanceWaiting.js';
import PerformanceSelect from './components/PerformanceSelect';
import PerformancePayment from './components/PerformancePayment.js';
import MyTickets from './components/MyTickets.js';

function App() {
  return (
    <Router>
      <div className="app">
        <Header />
        <main>
          <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
            <Route path="/performances" element={<PerformanceList />} />
            <Route path="/performances/:performanceId/waiting" element={<PerformanceWaiting />} />
            <Route path="/performances/:performanceId/select" element={<PerformanceSelect />} />
            <Route path="/performances/:performanceId/payment" element={<PerformancePayment />} />
            <Route path="/my-tickets" element={<MyTickets />} />
            <Route path="/" element={<PerformanceList />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

function Header() {
  const navigate = useNavigate();
  const { isLoggedIn, logout } = useAuthStore();

  const handleAuthAction = () => {
    if (isLoggedIn) {
      logout();
      navigate('/');
    } else {
      navigate('/login');
    }
  };

  return (
    <header>
      <nav>
        <div>
          <NavLink to="/" end>홈</NavLink>
          <NavLink to="/performances">공연 목록</NavLink>
          {isLoggedIn && <NavLink to="/my-tickets">내 티켓</NavLink>}
        </div>
        <div>
          {!isLoggedIn && (
            <NavLink to="/signup" className="signup-button">회원가입</NavLink>
          )}
          <button onClick={handleAuthAction}>
            {isLoggedIn ? '로그아웃' : '로그인'}
          </button>
        </div>
      </nav>
    </header>
  );
}

export default App;
