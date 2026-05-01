import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ClipboardList, Plus } from 'lucide-react';
import { formatDistanceToNow, format } from 'date-fns';
import { getUserClaims } from '../../../api/claimsApi';
import { useAppSelector } from '../../../store';
import StatusBadge from '../../../shared/components/StatusBadge';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const STATUS_FILTERS = ['ALL', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'CLOSED'];

const MyClaims= () => {
  const { userId } = useAppSelector(s => s.auth);
  const [claims, setClaims] = useState([]);
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!userId) return;
    getUserClaims(userId)
      .then(r => setClaims(r.data))
      .catch(() => setError('Failed to load your claims.'))
      .finally(() => setLoading(false));
  }, [userId]);

  const filtered = activeFilter === 'ALL' ? claims : claims.filter(c => c.status === activeFilter);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '16px', flexWrap: 'wrap', marginBottom: '28px' }}>
        <div>
          <p className="page-role-label">CUSTOMER</p>
          <h1 className="page-title">My Claims</h1>
          <p className="page-subtitle">Track all your insurance claims</p>
        </div>
        <Link to="/claims/file" className="btn-primary" style={{ textDecoration: 'none', flexShrink: 0 }}>
          <Plus size={16} /> File New Claim
        </Link>
      </div>

      {/* Filter chips */}
      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '20px' }}>
        {STATUS_FILTERS.map(filter => (
          <button
            key={filter}
            onClick={() => setActiveFilter(filter)}
            style={{
              background: activeFilter === filter ? 'var(--brand)' : 'var(--surface)',
              color: activeFilter === filter ? '#fff' : 'var(--text-secondary)',
              border: `1.5px solid ${activeFilter === filter ? 'var(--brand)' : 'var(--border-medium)'}`,
              borderRadius: '20px',
              padding: '6px 14px',
              fontFamily: 'var(--font-body)',
              fontSize: '12px',
              fontWeight: 600,
              cursor: 'pointer',
              transition: 'all 0.15s ease',
            }}
          >
            {filter.replace('_', ' ')}
            {filter !== 'ALL' && ` (${claims.filter(c => c.status === filter).length})`}
            {filter === 'ALL' && ` (${claims.length})`}
          </button>
        ))}
      </div>

      {error && <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px', color: 'var(--red)', fontFamily: 'var(--font-body)', fontSize: '14px' }}>{error}</div>}

      {loading ? (
        <SkeletonRow count={4} height={100} />
      ) : filtered.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 24px' }}>
          <ClipboardList size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>
            {activeFilter === 'ALL' ? 'No claims yet' : `No ${activeFilter.replace('_', ' ').toLowerCase()} claims`}
          </h3>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '0 0 20px' }}>
            {activeFilter === 'ALL' ? 'File your first claim when you need to.' : 'Try a different filter.'}
          </p>
          {activeFilter === 'ALL' && <Link to="/claims/file" className="btn-primary" style={{ textDecoration: 'none' }}>File a Claim</Link>}
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {filtered.map(claim => (
            <Link
              key={claim.id}
              to={`/claims/${claim.id}`}
              style={{ textDecoration: 'none' }}
            >
              <div className="card" style={{ transition: 'border-color 0.2s ease, box-shadow 0.2s ease' }}
                onMouseEnter={e => { (e.currentTarget).style.borderColor = 'var(--brand-border)'; (e.currentTarget).style.boxShadow = 'var(--shadow-hover)'; }}
                onMouseLeave={e => { (e.currentTarget).style.borderColor = 'var(--border)'; (e.currentTarget).style.boxShadow = 'var(--shadow-card)'; }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap' }}>
                  <div style={{ flex: 1, minWidth: '200px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px', flexWrap: 'wrap' }}>
                      <span style={{ fontFamily: 'monospace', fontSize: '13px', color: 'var(--brand)', fontWeight: 600 }}>Claim #{claim.id}</span>
                      <StatusBadge status={claim.status} />
                    </div>
                    <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-primary)', margin: '0 0 4px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: '500px' }}>
                      {claim.description}
                    </p>
                    <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-faint)' }}>
                      Submitted {formatDistanceToNow(new Date(claim.submittedAt), { addSuffix: true })} · {format(new Date(claim.submittedAt), 'dd MMM yyyy')}
                    </span>
                  </div>
                  <div style={{ textAlign: 'right', flexShrink: 0 }}>
                    <div style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)' }}>
                      ₹{claim.claimedAmount.toLocaleString('en-IN')}
                    </div>
                    <div style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-muted)' }}>Claimed amount</div>
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyClaims;
