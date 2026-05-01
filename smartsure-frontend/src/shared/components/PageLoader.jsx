// PageLoader.jsx
import React from 'react';
import { ShieldCheck } from 'lucide-react';

export function PageLoader() {
  return (
    <div style={{ position: 'fixed', inset: 0, background: 'var(--bg)', zIndex: 9998, display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', gap: '16px' }}>
      <div className="brand-pulse" style={{ width: '44px', height: '44px', background: 'var(--brand)', borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <ShieldCheck size={22} color="#fff" />
      </div>
      <span style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)' }}>Loading SmartSure...</span>
    </div>
  );
}
