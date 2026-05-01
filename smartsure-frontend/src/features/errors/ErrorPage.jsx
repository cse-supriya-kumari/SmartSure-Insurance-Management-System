import React from 'react';
import { useRouteError, Link } from 'react-router-dom';
import { AlertTriangle, Home } from 'lucide-react';

const ErrorPage= () => {
  const error = useRouteError();

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px 16px' }}>
      <div style={{ textAlign: 'center', maxWidth: '480px' }}>
        <div style={{ width: '72px', height: '72px', background: 'var(--amber-bg)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }}>
          <AlertTriangle size={36} style={{ color: 'var(--amber)' }} />
        </div>
        <h1 style={{ fontFamily: 'var(--font-heading)', fontSize: '26px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 10px' }}>
          Something went wrong
        </h1>
        <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px', color: 'var(--text-muted)', margin: '0 0 12px', lineHeight: 1.6 }}>
          An unexpected error occurred. Please try again or return to the home page.
        </p>
        {error?.message && (
          <div style={{ background: 'var(--surface-2)', border: '1px solid var(--border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '24px', fontFamily: 'monospace', fontSize: '13px', color: 'var(--text-muted)', wordBreak: 'break-word', textAlign: 'left' }}>
            {error.message}
          </div>
        )}
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
          <button onClick={() => window.location.reload()} className="btn-ghost">
            Reload page
          </button>
          <Link to="/" className="btn-primary" style={{ textDecoration: 'none' }}>
            <Home size={16} /> Go home
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ErrorPage;
