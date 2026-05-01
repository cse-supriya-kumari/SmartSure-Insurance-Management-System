import React, { useEffect, useRef } from 'react';
import { LogOut } from 'lucide-react';

export default function LogoutDialog({ isOpen, onConfirm, onCancel }) {
  const cancelBtnRef = useRef(null);

  useEffect(() => {
    if (!isOpen) return;
    cancelBtnRef.current?.focus();
    const handler = (e) => { if (e.key === 'Escape') onCancel(); };
    document.addEventListener('keydown', handler);
    return () => document.removeEventListener('keydown', handler);
  }, [isOpen, onCancel]);

  if (!isOpen) return null;

  return (
    <div
      className="dialog-overlay"
      onClick={(e) => { if (e.target === e.currentTarget) onCancel(); }}
      role="dialog" aria-modal="true" aria-labelledby="logout-title"
    >
      <div className="dialog-box">
        <div style={{ width: '56px', height: '56px', borderRadius: '50%', background: 'var(--red-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto' }}>
          <LogOut size={32} style={{ color: 'var(--red)' }} />
        </div>
        <h2 id="logout-title" style={{ fontFamily: 'var(--font-heading)', fontSize: '22px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '16px', marginBottom: 0 }}>
          Sign out of SmartSure?
        </h2>
        <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', marginTop: '8px', lineHeight: 1.6 }}>
          Are you sure you want to sign out? You will need to log in again to access your account.
        </p>
        <div style={{ display: 'flex', gap: '12px', marginTop: '28px' }}>
          <button ref={cancelBtnRef} onClick={onCancel} style={{ flex: 1, background: 'var(--surface-2)', color: 'var(--text-primary)', border: '1.5px solid var(--border-medium)', borderRadius: '12px', padding: '12px 0', fontWeight: 600, fontSize: '14px', fontFamily: 'var(--font-body)', cursor: 'pointer' }}>
            No, stay
          </button>
          <button onClick={onConfirm} style={{ flex: 1, background: 'var(--red)', color: '#fff', border: 'none', borderRadius: '12px', padding: '12px 0', fontWeight: 600, fontSize: '14px', fontFamily: 'var(--font-body)', cursor: 'pointer' }}>
            Yes, sign out
          </button>
        </div>
      </div>
    </div>
  );
}
