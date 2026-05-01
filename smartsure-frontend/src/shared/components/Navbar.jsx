import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShieldCheck, LogOut, Menu } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '../../store';
import { logout } from '../../store/slices/authSlice';
import LogoutDialog from './LogoutDialog';

export default function Navbar({ variant = 'public', onMenuClick }) {
  const [showLogout, setShowLogout] = useState(false);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { firstName, lastName } = useAppSelector((s) => s.auth);

  const handleConfirmLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  const initials = firstName && lastName ? `${firstName[0]}${lastName[0]}`.toUpperCase() : '??';

  return (
    <>
      <nav className="navbar">
        <Link to="/" className="navbar-brand">
          <div className="navbar-logo"><ShieldCheck size={18} /></div>
          <span className="navbar-wordmark">SmartSure</span>
        </Link>

        {variant === 'public' ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Link to="/login" className="btn-ghost" style={{ padding: '8px 18px', fontSize: '14px', textDecoration: 'none' }}>Sign in</Link>
            <Link to="/register" className="btn-primary" style={{ padding: '8px 18px', fontSize: '14px', textDecoration: 'none' }}>Get covered</Link>
          </div>
        ) : (
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            {onMenuClick && (
              <button onClick={onMenuClick} aria-label="Open menu" className="hamburger-btn" style={{ display: 'none', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)', padding: '6px', borderRadius: '8px' }}>
                <Menu size={20} />
              </button>
            )}
            <div style={{ width: '36px', height: '36px', borderRadius: '50%', background: 'var(--brand-subtle)', color: 'var(--brand)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--font-heading)', fontWeight: 700, fontSize: '13px', flexShrink: 0 }}>
              {initials}
            </div>
            <span className="user-name-display" style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-secondary)', fontWeight: 500 }}>{firstName}</span>
            <button
              onClick={() => setShowLogout(true)}
              aria-label="Sign out"
              style={{ width: '36px', height: '36px', borderRadius: '8px', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'background 0.15s' }}
              onMouseEnter={(e) => (e.currentTarget.style.background = 'var(--surface-2)')}
              onMouseLeave={(e) => (e.currentTarget.style.background = 'none')}
            >
              <LogOut size={20} />
            </button>
          </div>
        )}
      </nav>

      <LogoutDialog isOpen={showLogout} onConfirm={handleConfirmLogout} onCancel={() => setShowLogout(false)} />

      <style>{`
        @media (max-width: 768px) { .hamburger-btn { display: flex !important; } }
        @media (max-width: 640px) { .user-name-display { display: none !important; } }
      `}</style>
    </>
  );
}
