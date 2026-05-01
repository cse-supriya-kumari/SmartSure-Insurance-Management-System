import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Search, FileText, ClipboardList, FilePlus2, ClipboardCheck, ShieldCheck, Users } from 'lucide-react';
import { useAppSelector } from '../../store';

const userNavGroups = [
  { section: 'OVERVIEW', items: [{ icon: LayoutDashboard, label: 'Dashboard', to: '/dashboard' }] },
  { section: 'INSURANCE', items: [
    { icon: Search, label: 'Browse Policies', to: '/policies' },
    { icon: FileText, label: 'My Policies', to: '/my-policies' },
  ]},
  { section: 'CLAIMS', items: [
    { icon: ClipboardList, label: 'My Claims', to: '/claims' },
    { icon: FilePlus2, label: 'File a Claim', to: '/claims/file' },
  ]},
];

const adminNavGroups = [
  { section: 'OVERVIEW', items: [{ icon: LayoutDashboard, label: 'Dashboard', to: '/admin/dashboard' }] },
  { section: 'OPERATIONS', items: [
    { icon: ClipboardCheck, label: 'Claims Management', to: '/admin/claims' },
    { icon: ShieldCheck, label: 'Policy Products', to: '/admin/policies' },
  ]},
  { section: 'ADMINISTRATION', items: [
    { icon: Users, label: 'User Management', to: '/admin/users' },
  ]},
];

export default function Sidebar({ isOpen, onClose }) {
  const { role, firstName, lastName } = useAppSelector((s) => s.auth);
  const isAdmin = role === 'ROLE_ADMIN';
  const navGroups = isAdmin ? adminNavGroups : userNavGroups;
  const initials = firstName && lastName ? `${firstName[0]}${lastName[0]}`.toUpperCase() : '??';

  return (
    <>
      <div className={`sidebar-backdrop${isOpen ? ' visible' : ''}`} onClick={onClose} />
      <aside className={`sidebar${isOpen ? ' mobile-open' : ''}`}>
        {navGroups.map((group) => (
          <div key={group.section}>
            <div className="sidebar-section-label">{group.section}</div>
            {group.items.map((item) => (
              <NavLink key={item.to} to={item.to} className="nav-item" onClick={onClose}>
                <item.icon size={16} />
                <span>{item.label}</span>
              </NavLink>
            ))}
          </div>
        ))}

        <div className="sidebar-user-card">
          <div style={{ width: '32px', height: '32px', borderRadius: '50%', background: 'var(--brand-subtle)', color: 'var(--brand)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'var(--font-heading)', fontWeight: 700, fontSize: '12px', flexShrink: 0 }}>
            {initials}
          </div>
          <div style={{ minWidth: 0 }}>
            <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
              {firstName} {lastName}
            </div>
            <span style={{ fontSize: '10px', fontFamily: 'var(--font-body)', background: 'var(--brand-subtle)', color: 'var(--brand)', borderRadius: '999px', padding: '2px 8px', fontWeight: 600 }}>
              {isAdmin ? 'Admin' : 'Customer'}
            </span>
          </div>
        </div>
      </aside>
    </>
  );
}
