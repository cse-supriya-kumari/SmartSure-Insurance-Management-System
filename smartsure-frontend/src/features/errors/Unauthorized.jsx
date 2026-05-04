import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShieldOff, ArrowLeft } from 'lucide-react';
import { useAppSelector } from '../../store';

const Unauthorized= () => {
  const navigate = useNavigate();
  const { isAuthenticated, role } = useAppSelector(s => s.auth);

  const dashboardLink = role === 'ADMIN' ? '/admin/dashboard' : '/dashboard';

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px 16px' }}>
      <div style={{ textAlign: 'center', maxWidth: '480px' }}>
        <div style={{ width: '72px', height: '72px', background: 'var(--red-bg)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }}>
          <ShieldOff size={36} style={{ color: 'var(--red)' }} />
        </div>
        <div style={{ fontFamily: 'var(--font-heading)', fontSize: '72px', fontWeight: 800, color: 'var(--red-bg)', lineHeight: 1, marginBottom: '8px', userSelect: 'none' }}>
          403
        </div>
        <h1 style={{ fontFamily: 'var(--font-heading)', fontSize: '26px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 10px' }}>
          Access Denied
        </h1>
        <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px', color: 'var(--text-muted)', margin: '0 0 28px', lineHeight: 1.6 }}>
          You don't have permission to access this page. This area is restricted to administrators.
        </p>
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center', flexWrap: 'wrap' }}>
          <button onClick={() => navigate(-1)} className="btn-ghost" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <ArrowLeft size={16} /> Go back
          </button>
          {isAuthenticated && (
            <Link to={dashboardLink} className="btn-primary" style={{ textDecoration: 'none' }}>
              My Dashboard
            </Link>
          )}
          {!isAuthenticated && (
            <Link to="/login" className="btn-primary" style={{ textDecoration: 'none' }}>
              Sign in
            </Link>
          )}
        </div>
      </div>
    </div>
  );
};

export default Unauthorized;
