import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Users, FileText, ClipboardCheck, AlertCircle, TrendingUp, BarChart3 } from 'lucide-react';
import { getDashboard, getReports } from '../../../api/adminApi';
import MetricStrip from '../../../shared/components/MetricStrip';
import SkeletonRow from '../../../shared/components/SkeletonRow';
import { useAppSelector } from '../../../store';

export default function AdminDashboard() {
  const { firstName } = useAppSelector((s) => s.auth);
  const [dashboard, setDashboard] = useState(null);
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const [d, r] = await Promise.all([getDashboard(), getReports()]);
        setDashboard(d.data);
        setReport(r.data);
      } catch { setError('Failed to load dashboard data.'); }
      finally { setLoading(false); }
    })();
  }, []);

  const metrics = dashboard ? [
    { label: 'Total Users',    value: dashboard.totalUsers },
    { label: 'Total Policies', value: dashboard.totalPolicies },
    { label: 'Total Claims',   value: dashboard.totalClaims },
    { label: 'Pending Claims', value: dashboard.pendingClaims, color: dashboard.pendingClaims > 0 ? 'var(--amber)' : undefined },
  ] : [];

  const quickLinks = [
    { icon: ClipboardCheck, title: 'Claims Management',  desc: 'Review and process pending claims',      to: '/admin/claims',   badge: dashboard?.pendingClaims },
    { icon: FileText,        title: 'Policy Products',   desc: 'Manage insurance product catalogue',     to: '/admin/policies', badge: null },
    { icon: Users,           title: 'User Management',   desc: 'View and manage customer accounts',      to: '/admin/users',    badge: null },
  ];

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">ADMIN PORTAL</p>
        <h1 className="page-title">Welcome back, {firstName}</h1>
        <p className="page-subtitle">Here's an overview of the SmartSure platform today</p>
      </div>

      {error && <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px', color: 'var(--red)', fontFamily: 'var(--font-body)', fontSize: '14px' }}>{error}</div>}

      {loading ? <SkeletonRow count={1} height={88} /> : <MetricStrip items={metrics} />}

      <div className="ad-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginBottom: '24px' }}>
        {quickLinks.map((link) => (
          <Link key={link.to} to={link.to} style={{ textDecoration: 'none' }}>
            <div className="card" style={{ transition: 'border-color 0.2s, box-shadow 0.2s' }}
              onMouseEnter={(e) => { e.currentTarget.style.borderColor = 'var(--brand-border)'; e.currentTarget.style.boxShadow = 'var(--shadow-hover)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.boxShadow = 'var(--shadow-card)'; }}
            >
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: '12px' }}>
                <div style={{ width: '40px', height: '40px', background: 'var(--brand-subtle)', borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <link.icon size={20} style={{ color: 'var(--brand)' }} />
                </div>
                {link.badge != null && link.badge > 0 && (
                  <span style={{ background: 'var(--amber)', color: '#fff', borderRadius: '20px', padding: '2px 8px', fontFamily: 'var(--font-body)', fontSize: '11px', fontWeight: 700 }}>
                    {link.badge}
                  </span>
                )}
              </div>
              <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 4px' }}>{link.title}</h3>
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)', margin: 0, lineHeight: 1.5 }}>{link.desc}</p>
            </div>
          </Link>
        ))}
      </div>

      {report && (
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
            <BarChart3 size={18} style={{ color: 'var(--brand)' }} />
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>Platform Report</h2>
          </div>
          <div className="rpt-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px' }}>
            {[
              { label: 'Total Revenue',    value: `₹${report.totalRevenue.toLocaleString('en-IN')}`, icon: TrendingUp,    color: 'var(--brand)' },
              { label: 'Approved Claims',  value: report.approvedClaims,                              icon: ClipboardCheck, color: 'var(--green)' },
              { label: 'Rejected Claims',  value: report.rejectedClaims,                              icon: AlertCircle,   color: 'var(--red)' },
            ].map((item) => (
              <div key={item.label} style={{ background: 'var(--surface-2)', borderRadius: '12px', padding: '18px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
                  <item.icon size={16} style={{ color: item.color }} />
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>{item.label}</span>
                </div>
                <div style={{ fontFamily: 'var(--font-heading)', fontSize: '24px', fontWeight: 700, color: 'var(--text-primary)' }}>{item.value}</div>
              </div>
            ))}
          </div>
        </div>
      )}

      <style>{`
        @media(max-width:900px){.ad-grid{grid-template-columns:1fr 1fr!important}}
        @media(max-width:640px){.ad-grid,.rpt-grid{grid-template-columns:1fr!important}}
        @media(max-width:900px){.rpt-grid{grid-template-columns:1fr 1fr!important}}
      `}</style>
    </div>
  );
}
