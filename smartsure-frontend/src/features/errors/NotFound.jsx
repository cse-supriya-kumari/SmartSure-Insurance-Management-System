import React from 'react';
import { Link } from 'react-router-dom';
import { Home, SearchX } from 'lucide-react';

const NotFound= () => {
  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px 16px' }}>
      <div style={{ textAlign: 'center', maxWidth: '480px' }}>
        <div style={{ fontFamily: 'var(--font-heading)', fontSize: '120px', fontWeight: 800, color: 'var(--brand-subtle)', lineHeight: 1, marginBottom: '16px', userSelect: 'none' }}>
          404
        </div>
        <div style={{ width: '64px', height: '64px', background: 'var(--surface-2)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }}>
          <SearchX size={32} style={{ color: 'var(--text-faint)' }} />
        </div>
        <h1 style={{ fontFamily: 'var(--font-heading)', fontSize: '26px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 10px' }}>
          Page not found
        </h1>
        <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px', color: 'var(--text-muted)', margin: '0 0 28px', lineHeight: 1.6 }}>
          The page you're looking for doesn't exist or has been moved.
        </p>
        <Link to="/" className="btn-primary" style={{ textDecoration: 'none' }}>
          <Home size={16} /> Back to home
        </Link>
      </div>
    </div>
  );
};

export default NotFound;
