import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { formatDistanceToNow } from 'date-fns';
import { ClipboardCheck, Search } from 'lucide-react';
import { getAdminPendingClaims } from '../../../api/adminApi';
import StatusBadge from '../../../shared/components/StatusBadge';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const ClaimsManagement= () => {
  const [claims, setClaims] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getAdminPendingClaims()
      .then(r => setClaims(r.data))
      .catch(() => setError('Failed to load claims.'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = claims.filter(c =>
    String(c.id).includes(search) ||
    c.description.toLowerCase().includes(search.toLowerCase()) ||
    String(c.userId).includes(search)
  );

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">ADMIN</p>
        <h1 className="page-title">Claims Management</h1>
        <p className="page-subtitle">Review and process pending claim submissions</p>
      </div>

      {/* Search */}
      <div style={{ position: 'relative', marginBottom: '20px' }}>
        <Search size={18} style={{ position: 'absolute', left: '16px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-faint)', pointerEvents: 'none' }} />
        <input
          type="text"
          className="form-input"
          style={{ paddingLeft: '44px', height: '46px', borderRadius: '12px' }}
          placeholder="Search by claim ID, user ID, or description..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      {error && <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px', color: 'var(--red)', fontFamily: 'var(--font-body)', fontSize: '14px' }}>{error}</div>}

      {loading ? <SkeletonRow count={6} height={82} /> : filtered.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 24px' }}>
          <ClipboardCheck size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>
            {search ? 'No claims match your search' : 'No pending claims'}
          </h3>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: 0 }}>
            {search ? 'Try a different search term.' : 'All claims have been reviewed.'}
          </p>
        </div>
      ) : (
        <div style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '16px', overflow: 'hidden', boxShadow: 'var(--shadow-card)' }}>
          {/* Table header */}
          <div style={{ display: 'grid', gridTemplateColumns: '80px 1fr 140px 120px 130px 100px', gap: '0', padding: '12px 20px', background: 'var(--surface-2)', borderBottom: '1px solid var(--border)' }}>
            {['Claim #', 'Description', 'Amount', 'User ID', 'Submitted', 'Status'].map(h => (
              <div key={h} style={{ fontFamily: 'var(--font-body)', fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>{h}</div>
            ))}
          </div>
          {filtered.map((claim, i) => (
            <Link
              key={claim.id}
              to={`/admin/claims/${claim.id}`}
              style={{
                display: 'grid',
                gridTemplateColumns: '80px 1fr 140px 120px 130px 100px',
                gap: '0',
                padding: '14px 20px',
                borderBottom: i < filtered.length - 1 ? '1px solid var(--border)' : 'none',
                alignItems: 'center',
                textDecoration: 'none',
                background: 'var(--surface)',
                transition: 'background 0.12s ease',
              }}
              onMouseEnter={e => (e.currentTarget.style.background = 'var(--surface-2)')}
              onMouseLeave={e => (e.currentTarget.style.background = 'var(--surface)')}
            >
              <span style={{ fontFamily: 'monospace', fontSize: '13px', color: 'var(--brand)', fontWeight: 600 }}>#{claim.id}</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', paddingRight: '12px' }}>
                {claim.description}
              </span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
                ₹{claim.claimedAmount.toLocaleString('en-IN')}
              </span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>User #{claim.userId}</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-faint)' }}>
                {formatDistanceToNow(new Date(claim.submittedAt), { addSuffix: true })}
              </span>
              <StatusBadge status={claim.status} />
            </Link>
          ))}
        </div>
      )}

      <div style={{ marginTop: '12px', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>
        Showing {filtered.length} of {claims.length} claims
      </div>
    </div>
  );
};

export default ClaimsManagement;
