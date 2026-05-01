import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Search } from 'lucide-react';
import { getAllPolicyTypes } from '../../../api/policyApi';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const BrowsePolicies= () => {
  const [policies, setPolicies] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getAllPolicyTypes()
      .then(r => setPolicies(r.data))
      .catch(() => setError('Failed to load policies.'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = policies.filter(p =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.description.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">CUSTOMER</p>
        <h1 className="page-title">Browse Policies</h1>
        <p className="page-subtitle">Explore our range of insurance products</p>
      </div>

      {/* Search bar */}
      <div style={{ position: 'relative', marginBottom: '24px' }}>
        <Search size={18} style={{ position: 'absolute', left: '16px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-faint)', pointerEvents: 'none' }} />
        <input
          type="text"
          className="form-input"
          style={{ paddingLeft: '44px', height: '46px', borderRadius: '12px' }}
          placeholder="Search policies by name or description..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      {error && <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px', color: 'var(--red)', fontFamily: 'var(--font-body)', fontSize: '14px' }}>{error}</div>}

      {loading ? (
        <SkeletonRow count={4} height={100} />
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-muted)' }}>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px' }}>No policies found for "{search}"</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {filtered.map(policy => (
            <div
              key={policy.id}
              className="card"
              style={{ display: 'flex', alignItems: 'flex-start', gap: '20px', transition: 'border-color 0.2s ease, box-shadow 0.2s ease', cursor: 'default' }}
              onMouseEnter={e => { (e.currentTarget).style.borderColor = 'var(--brand-border)'; (e.currentTarget).style.boxShadow = 'var(--shadow-hover)'; }}
              onMouseLeave={e => { (e.currentTarget).style.borderColor = 'var(--border)'; (e.currentTarget).style.boxShadow = 'var(--shadow-card)'; }}
            >
              <div style={{ flex: 1, minWidth: 0 }}>
                <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 6px' }}>{policy.name}</h3>
                <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '0 0 12px', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                  {policy.description}
                </p>
                <Link to={`/policies/${policy.id}`} style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--brand)', fontWeight: 600, textDecoration: 'none' }}>
                  View details and purchase →
                </Link>
              </div>

              <div style={{ textAlign: 'right', flexShrink: 0 }}>
                <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end', marginBottom: '12px' }}>
                  <span style={{ background: 'var(--brand-subtle)', color: 'var(--brand)', border: '1px solid var(--brand-border)', borderRadius: '20px', padding: '4px 10px', fontSize: '12px', fontFamily: 'var(--font-body)', fontWeight: 600 }}>
                    ₹{policy.coverageAmount.toLocaleString('en-IN')} coverage
                  </span>
                  <span style={{ background: 'var(--surface-2)', color: 'var(--text-muted)', border: '1px solid var(--border)', borderRadius: '20px', padding: '4px 10px', fontSize: '12px', fontFamily: 'var(--font-body)' }}>
                    {policy.durationMonths} mo
                  </span>
                </div>
                <div style={{ fontFamily: 'var(--font-heading)', fontSize: '20px', fontWeight: 700, color: 'var(--brand)' }}>
                  ₹{policy.basePremium.toLocaleString('en-IN')}<span style={{ fontSize: '13px', fontWeight: 500, color: 'var(--text-muted)' }}>/month</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default BrowsePolicies;
