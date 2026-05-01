import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FileText, Calendar } from 'lucide-react';
import { format } from 'date-fns';
import { getUserPolicies } from '../../../api/policyApi';
import { useAppSelector } from '../../../store';
import StatusBadge from '../../../shared/components/StatusBadge';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const MyPolicies= () => {
  const { userId } = useAppSelector(s => s.auth);
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!userId) return;
    getUserPolicies(userId)
      .then(r => setPolicies(r.data))
      .catch(() => setError('Failed to load your policies.'))
      .finally(() => setLoading(false));
  }, [userId]);

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">CUSTOMER</p>
        <h1 className="page-title">My Policies</h1>
        <p className="page-subtitle">All your purchased insurance policies</p>
      </div>

      {error && <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px', color: 'var(--red)', fontFamily: 'var(--font-body)', fontSize: '14px' }}>{error}</div>}

      {loading ? (
        <SkeletonRow count={4} height={110} />
      ) : policies.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 24px' }}>
          <FileText size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>No policies yet</h3>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '0 0 20px' }}>Purchase your first policy to get covered.</p>
          <Link to="/policies" className="btn-primary" style={{ textDecoration: 'none' }}>Browse Policies</Link>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {policies.map(policy => {
            const unpaid = policy.premiums?.filter(p => p.status === 'UNPAID' || p.status === 'OVERDUE').length || 0;
            const paid = policy.premiums?.filter(p => p.status === 'PAID').length || 0;
            const total = policy.premiums?.length || 0;
            const progress = total > 0 ? Math.round((paid / total) * 100) : 0;

            return (
              <div key={policy.id} className="card">
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px', flexWrap: 'wrap' }}>
                  <div style={{ width: '48px', height: '48px', background: 'var(--brand-subtle)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                    <FileText size={22} style={{ color: 'var(--brand)' }} />
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap', marginBottom: '4px' }}>
                      <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>{policy.policyTypeName}</h3>
                      <StatusBadge status={policy.status} />
                    </div>

                    <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', marginBottom: '12px' }}>
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px', fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>
                        <Calendar size={12} /> Start: {policy.startDate ? format(new Date(policy.startDate), 'dd MMM yyyy') : '—'}
                      </span>
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px', fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>
                        <Calendar size={12} /> End: {policy.endDate ? format(new Date(policy.endDate), 'dd MMM yyyy') : '—'}
                      </span>
                      <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>
                        ₹{policy.premiumAmount.toLocaleString('en-IN')}/month
                      </span>
                    </div>

                    {total > 0 && (
                      <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                          <span style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-muted)' }}>Premium progress: {paid}/{total} paid</span>
                          {unpaid > 0 && <span style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--amber)', fontWeight: 600 }}>{unpaid} overdue</span>}
                        </div>
                        <div style={{ height: '6px', background: 'var(--surface-3)', borderRadius: '99px', overflow: 'hidden' }}>
                          <div style={{ width: `${progress}%`, height: '100%', background: unpaid > 0 ? 'var(--amber)' : 'var(--green)', borderRadius: '99px', transition: 'width 0.5s ease' }} />
                        </div>
                      </div>
                    )}
                  </div>

                  <div style={{ textAlign: 'right', flexShrink: 0 }}>
                    <span style={{ fontFamily: 'monospace', fontSize: '12px', color: 'var(--text-faint)' }}>Policy #{policy.id}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default MyPolicies;
