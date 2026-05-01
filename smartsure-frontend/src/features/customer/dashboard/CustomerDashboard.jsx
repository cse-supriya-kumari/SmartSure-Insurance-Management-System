import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ClipboardList, FileText } from 'lucide-react';
import { formatDistanceToNow } from 'date-fns';
import { getUserPolicies } from '../../../api/policyApi';
import { getUserClaims } from '../../../api/claimsApi';
import { useAppSelector } from '../../../store';
import StatusBadge from '../../../shared/components/StatusBadge';
import MetricStrip from '../../../shared/components/MetricStrip';
import SkeletonRow from '../../../shared/components/SkeletonRow';

function getGreeting() {
  const h = new Date().getHours();
  if (h < 12) return 'morning';
  if (h < 17) return 'afternoon';
  return 'evening';
}

const CustomerDashboard= () => {
  const { userId, firstName } = useAppSelector(s => s.auth);
  const [policies, setPolicies] = useState([]);
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!userId) return;
    const load = async () => {
      try {
        const [p, c] = await Promise.all([getUserPolicies(userId), getUserClaims(userId)]);
        setPolicies(p.data);
        setClaims(c.data);
      } catch {
        setError('Failed to load dashboard data.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  const activePolicies = policies.filter(p => p.status === 'ACTIVE').length;
  const openClaims = claims.filter(c => c.status === 'SUBMITTED' || c.status === 'UNDER_REVIEW').length;
  const pendingClaims = claims.filter(c => c.status === 'SUBMITTED').length;
  const totalCoverage = policies.reduce((acc, p) => acc + (p.premiumAmount || 0), 0);

  const metrics = [
    { label: 'Active Policies', value: activePolicies },
    { label: 'Total Claims', value: claims.length },
    { label: 'Pending Claims', value: pendingClaims, color: pendingClaims > 0 ? 'var(--amber)' : undefined },
    { label: 'Total Coverage ₹', value: `₹${totalCoverage.toLocaleString('en-IN')}` },
  ];

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontFamily: 'var(--font-heading)', fontSize: '26px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 4px' }}>
          Good {getGreeting()}, {firstName} 👋
        </h1>
        <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: 0 }}>
          You have {activePolicies} active {activePolicies === 1 ? 'policy' : 'policies'} and {openClaims} open {openClaims === 1 ? 'claim' : 'claims'}.
        </p>
      </div>

      {error && <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px', color: 'var(--red)', fontFamily: 'var(--font-body)', fontSize: '14px' }}>{error}</div>}

      <MetricStrip items={metrics} />

      <div className="dash-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
        {/* Recent Claims */}
        <div className="card">
          <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 16px' }}>Recent Claims</h2>
          {loading ? <SkeletonRow count={3} height={52} /> : claims.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-faint)' }}>
              <ClipboardList size={32} style={{ marginBottom: '8px', color: 'var(--text-faint)' }} />
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', margin: '0 0 10px' }}>No claims filed yet</p>
              <Link to="/claims/file" style={{ color: 'var(--brand)', fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600 }}>File your first claim →</Link>
            </div>
          ) : claims.slice(0, 5).map(c => (
            <Link key={c.id} to={`/claims/${c.id}`} style={{ display: 'flex', alignItems: 'center', gap: '12px', padding: '10px 0', borderBottom: '1px solid var(--border)', textDecoration: 'none' }}>
              <span style={{ fontFamily: 'monospace', fontSize: '12px', color: 'var(--text-muted)', flexShrink: 0 }}>#{c.id}</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)', flex: 1, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{c.description}</span>
              <StatusBadge status={c.status} />
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-faint)', flexShrink: 0 }}>{formatDistanceToNow(new Date(c.submittedAt), { addSuffix: true })}</span>
            </Link>
          ))}
        </div>

        {/* My Policies */}
        <div className="card">
          <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 16px' }}>My Policies</h2>
          {loading ? <SkeletonRow count={3} height={52} /> : policies.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-faint)' }}>
              <FileText size={32} style={{ marginBottom: '8px', color: 'var(--text-faint)' }} />
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', margin: '0 0 10px' }}>No policies found</p>
              <Link to="/policies" style={{ color: 'var(--brand)', fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600 }}>Browse policies →</Link>
            </div>
          ) : policies.map(p => (
            <div key={p.id} style={{ display: 'flex', alignItems: 'center', gap: '12px', padding: '10px 0', borderBottom: '1px solid var(--border)' }}>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>{p.policyTypeName}</div>
                <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>₹{p.premiumAmount.toLocaleString('en-IN')}/month</div>
              </div>
              <StatusBadge status={p.status} />
            </div>
          ))}
        </div>
      </div>

      <style>{`@media (max-width: 768px) { .dash-grid { grid-template-columns: 1fr !important; } }`}</style>
    </div>
  );
};

export default CustomerDashboard;
