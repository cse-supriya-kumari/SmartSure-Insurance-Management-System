import React, { useEffect, useState } from 'react';
import { Search, Users, CheckCircle, XCircle, Loader2 } from 'lucide-react';
import { getAllAdminUsers, updateAdminUserStatus } from '../../../api/adminApi';
import { useToast } from '../../../shared/components/Toast';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const ROLE_FILTERS = ['ALL', 'ROLE_USER', 'ROLE_ADMIN'];

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [toggling, setToggling] = useState(null);
  const [filterRole, setFilterRole] = useState('ALL');
  const { showToast } = useToast();

  useEffect(() => {
    getAllAdminUsers()
      .then((r) => setUsers(r.data))
      .catch(() => showToast({ type: 'error', title: 'Failed to load users' }))
      .finally(() => setLoading(false));
  }, []);

  const handleToggle = async (user) => {
    setToggling(user.id);
    try {
      const updated = await updateAdminUserStatus(user.id, { active: !user.active });
      setUsers((prev) => prev.map((u) => (u.id === user.id ? updated.data : u)));
      showToast({ type: 'success', title: user.active ? 'User Deactivated' : 'User Activated', message: `${user.firstName} ${user.lastName}` });
    } catch {
      showToast({ type: 'error', title: 'Action Failed', message: 'Could not update user status.' });
    } finally { setToggling(null); }
  };

  const filtered = users.filter((u) => {
    const matchRole = filterRole === 'ALL' || u.role === filterRole;
    const matchSearch = search === '' ||
      `${u.firstName} ${u.lastName}`.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase()) ||
      String(u.id).includes(search);
    return matchRole && matchSearch;
  });

  const activeCount = users.filter((u) => u.active).length;

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">ADMIN</p>
        <h1 className="page-title">User Management</h1>
        <p className="page-subtitle">{users.length} registered users · {activeCount} active</p>
      </div>

      <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginBottom: '16px' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: '200px' }}>
          <Search size={18} style={{ position: 'absolute', left: '14px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-faint)', pointerEvents: 'none' }} />
          <input type="text" className="form-input" style={{ paddingLeft: '42px', height: '44px', borderRadius: '10px' }} placeholder="Search by name, email or ID..." value={search} onChange={(e) => setSearch(e.target.value)} />
        </div>
        <div style={{ display: 'flex', gap: '6px', flexShrink: 0 }}>
          {ROLE_FILTERS.map((r) => (
            <button key={r} onClick={() => setFilterRole(r)} style={{ background: filterRole === r ? 'var(--brand)' : 'var(--surface)', color: filterRole === r ? '#fff' : 'var(--text-secondary)', border: `1.5px solid ${filterRole === r ? 'var(--brand)' : 'var(--border-medium)'}`, borderRadius: '10px', padding: '0 16px', height: '44px', fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, cursor: 'pointer', transition: 'all 0.15s' }}>
              {r === 'ALL' ? 'All' : r === 'ROLE_USER' ? 'Customers' : 'Admins'}
            </button>
          ))}
        </div>
      </div>

      {loading ? <SkeletonRow count={6} height={68} /> : filtered.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 24px' }}>
          <Users size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>No users found</h3>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: 0 }}>Try adjusting your search or filters.</p>
        </div>
      ) : (
        <div style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '16px', overflow: 'hidden', boxShadow: 'var(--shadow-card)' }}>
          <div className="um-header" style={{ display: 'grid', gridTemplateColumns: '60px 1fr 200px 120px 80px 110px', padding: '12px 20px', background: 'var(--surface-2)', borderBottom: '1px solid var(--border)' }}>
            {['ID', 'Name', 'Email', 'Phone', 'Role', 'Status'].map((h) => (
              <div key={h} style={{ fontFamily: 'var(--font-body)', fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em' }}>{h}</div>
            ))}
          </div>
          {filtered.map((user, i) => (
            <div key={user.id} className="um-row" style={{ display: 'grid', gridTemplateColumns: '60px 1fr 200px 120px 80px 110px', padding: '14px 20px', borderBottom: i < filtered.length - 1 ? '1px solid var(--border)' : 'none', alignItems: 'center', background: 'var(--surface)' }}>
              <span style={{ fontFamily: 'monospace', fontSize: '12px', color: 'var(--text-faint)' }}>#{user.id}</span>

              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', minWidth: 0 }}>
                <div style={{ width: '32px', height: '32px', borderRadius: '50%', flexShrink: 0, background: 'var(--brand-subtle)', color: 'var(--brand)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--font-heading)', fontWeight: 700, fontSize: '12px' }}>
                  {user.firstName[0]}{user.lastName[0]}
                </div>
                <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 500, color: 'var(--text-primary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {user.firstName} {user.lastName}
                </span>
              </div>

              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', paddingRight: '8px' }}>{user.email}</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>{user.phone || '—'}</span>

              <span style={{ background: user.role === 'ROLE_ADMIN' ? 'var(--purple-bg)' : 'var(--brand-subtle)', color: user.role === 'ROLE_ADMIN' ? 'var(--purple)' : 'var(--brand)', border: `1px solid ${user.role === 'ROLE_ADMIN' ? 'var(--purple-border)' : 'var(--brand-border)'}`, borderRadius: '20px', padding: '3px 8px', fontSize: '11px', fontFamily: 'var(--font-body)', fontWeight: 600, whiteSpace: 'nowrap', display: 'inline-block' }}>
                {user.role === 'ROLE_ADMIN' ? 'Admin' : 'Customer'}
              </span>

              <button onClick={() => handleToggle(user)} disabled={toggling === user.id} style={{ display: 'flex', alignItems: 'center', gap: '5px', background: user.active ? 'var(--green-bg)' : 'var(--red-bg)', color: user.active ? 'var(--green)' : 'var(--red)', border: `1px solid ${user.active ? 'var(--green-border)' : 'var(--red-border)'}`, borderRadius: '8px', padding: '5px 10px', fontFamily: 'var(--font-body)', fontSize: '12px', fontWeight: 600, cursor: 'pointer', transition: 'all 0.15s', whiteSpace: 'nowrap' }}>
                {toggling === user.id ? <Loader2 size={12} className="spin" /> : user.active ? <CheckCircle size={12} /> : <XCircle size={12} />}
                {user.active ? 'Active' : 'Inactive'}
              </button>
            </div>
          ))}
        </div>
      )}

      <div style={{ marginTop: '12px', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>
        Showing {filtered.length} of {users.length} users
      </div>

      <style>{`.spin{animation:spin 1s linear infinite}@keyframes spin{to{transform:rotate(360deg)}} @media(max-width:768px){.um-header{display:none!important}.um-row{grid-template-columns:1fr 1fr!important;gap:8px!important}}`}</style>
    </div>
  );
}
