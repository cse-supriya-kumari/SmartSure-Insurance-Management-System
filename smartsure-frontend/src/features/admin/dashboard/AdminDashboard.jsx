import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Users, FileText, ClipboardCheck, AlertCircle, TrendingUp, BarChart3 } from 'lucide-react';
import { getDashboard, getReports } from '../../../api/adminApi';
import MetricStrip from '../../../shared/components/MetricStrip';
import SkeletonRow from '../../../shared/components/SkeletonRow';
import { useAppSelector } from '../../../store';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';

export default function AdminDashboard() {
  const { name } = useAppSelector((s) => s.auth);
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
        <h1 className="page-title">Welcome back, {name?.split(' ')[0]}</h1>
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

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '24px', marginBottom: '24px' }} className="dash-middle-grid">
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
            <BarChart3 size={18} style={{ color: 'var(--brand)' }} />
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>Claim Distribution</h2>
          </div>
          <div style={{ height: '300px', width: '100%' }}>
            {report ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={[
                      { name: 'Approved', value: report.approvedClaims },
                      { name: 'Rejected', value: report.rejectedClaims },
                      { name: 'Pending',  value: dashboard?.pendingClaims || 0 },
                    ].filter(d => d.value > 0)}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    <Cell fill="var(--green)" />
                    <Cell fill="var(--red)" />
                    <Cell fill="var(--amber)" />
                  </Pie>
                  <Tooltip 
                    contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: 'var(--shadow-md)' }}
                    itemStyle={{ fontFamily: 'var(--font-body)', fontSize: '12px' }}
                  />
                  <Legend 
                    verticalAlign="bottom" 
                    height={36} 
                    formatter={(val) => <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>{val}</span>}
                  />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'var(--text-faint)' }}>Loading chart...</div>
            )}
          </div>
        </div>

        {report && (
          <div className="card">
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
              <TrendingUp size={18} style={{ color: 'var(--brand)' }} />
              <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>Financial Overview</h2>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
               <div style={{ background: 'var(--brand-subtle)', borderRadius: '12px', padding: '24px', textAlign: 'center' }}>
                  <div style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--brand)', marginBottom: '8px', fontWeight: 600 }}>Total Platform Revenue</div>
                  <div style={{ fontFamily: 'var(--font-heading)', fontSize: '36px', fontWeight: 800, color: 'var(--brand)' }}>₹{report.totalRevenue.toLocaleString('en-IN')}</div>
               </div>
               <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <div style={{ background: 'var(--surface-2)', borderRadius: '12px', padding: '16px' }}>
                    <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>Approved Claims</div>
                    <div style={{ fontFamily: 'var(--font-heading)', fontSize: '20px', fontWeight: 700, color: 'var(--green)' }}>{report.approvedClaims}</div>
                  </div>
                  <div style={{ background: 'var(--surface-2)', borderRadius: '12px', padding: '16px' }}>
                    <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>Rejected Claims</div>
                    <div style={{ fontFamily: 'var(--font-heading)', fontSize: '20px', fontWeight: 700, color: 'var(--red)' }}>{report.rejectedClaims}</div>
                  </div>
               </div>
            </div>
          </div>
        )}
      </div>

      {/* Hidden old report section */}
      {false && report && (
        <div className="card">
          {/* ... */}
        </div>
      )}

      <style>{`
        @media(max-width:1100px){.dash-middle-grid{grid-template-columns:1fr!important}}
        @media(max-width:900px){.ad-grid{grid-template-columns:1fr 1fr!important}}
        @media(max-width:640px){.ad-grid,.rpt-grid{grid-template-columns:1fr!important}}
        @media(max-width:900px){.rpt-grid{grid-template-columns:1fr 1fr!important}}
      `}</style>
    </div>
  );
}
