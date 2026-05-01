import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShieldCheck, Clock, DollarSign, Loader2, CheckCircle } from 'lucide-react';
import { getPolicyTypeById, purchasePolicy } from '../../../api/policyApi';
import { useAppSelector } from '../../../store';
import { useToast } from '../../../shared/components/Toast';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const BENEFITS = [
  'Instant activation upon purchase',
  'Cashless claim settlement',
  '24/7 customer support',
  'No hidden charges',
  'Digital policy document',
  'Auto-renewal option',
];

export default function PolicyDetail() {
  const { id } = useParams();
  const { userId } = useAppSelector((s) => s.auth);
  const [policy, setPolicy] = useState(null);
  const [loading, setLoading] = useState(true);
  const [purchasing, setPurchasing] = useState(false);
  const [purchased, setPurchased] = useState(false);
  const [error, setError] = useState('');
  const { showToast } = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    if (!id) return;
    getPolicyTypeById(Number(id))
      .then((r) => setPolicy(r.data))
      .catch(() => setError('Policy not found.'))
      .finally(() => setLoading(false));
  }, [id]);

  const handlePurchase = async () => {
    if (!userId || !policy?.id) return;
    setPurchasing(true);
    try {
      await purchasePolicy({ userId, policyTypeId: policy.id });
      setPurchased(true);
      showToast({ type: 'success', title: 'Policy Purchased!', message: `${policy.name} is now active.` });
      setTimeout(() => navigate('/my-policies'), 1500);
    } catch (err) {
      showToast({ type: 'error', title: 'Purchase Failed', message: err.response?.data?.message || 'Please try again.' });
    } finally { setPurchasing(false); }
  };

  if (loading) return (
    <div>
      <p className="page-role-label">CUSTOMER · POLICY DETAIL</p>
      <h1 className="page-title">Policy Details</h1>
      <SkeletonRow count={5} height={72} />
    </div>
  );

  if (error || !policy) return (
    <div style={{ textAlign: 'center', padding: '60px 0' }}>
      <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px', color: 'var(--text-muted)' }}>{error || 'Policy not found.'}</p>
    </div>
  );

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">CUSTOMER · POLICY DETAIL</p>
        <h1 className="page-title">{policy.name}</h1>
        <p className="page-subtitle">{policy.description}</p>
      </div>

      <div className="pd-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: '24px', alignItems: 'start' }}>
        <div>
          <div className="card" style={{ marginBottom: '20px' }}>
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 20px' }}>Policy Details</h2>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              {[
                { label: 'Coverage Amount', value: `₹${policy.coverageAmount.toLocaleString('en-IN')}`, icon: ShieldCheck, color: 'var(--brand)' },
                { label: 'Monthly Premium', value: `₹${policy.basePremium.toLocaleString('en-IN')}`, icon: DollarSign, color: 'var(--green)' },
                { label: 'Policy Duration', value: `${policy.durationMonths} months`, icon: Clock, color: 'var(--amber)' },
                { label: 'Total Cost', value: `₹${(policy.basePremium * policy.durationMonths).toLocaleString('en-IN')}`, icon: DollarSign, color: 'var(--purple)' },
              ].map(item => (
                <div key={item.label} style={{ background: 'var(--surface-2)', borderRadius: '12px', padding: '16px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                    <item.icon size={16} style={{ color: item.color }} />
                    <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>{item.label}</span>
                  </div>
                  <div style={{ fontFamily: 'var(--font-heading)', fontSize: '20px', fontWeight: 700, color: 'var(--text-primary)' }}>{item.value}</div>
                </div>
              ))}
            </div>
          </div>

          <div className="card">
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 16px' }}>What's Included</h2>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              {BENEFITS.map((b) => (
                <div key={b} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <CheckCircle size={14} style={{ color: 'var(--green)', flexShrink: 0 }} />
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-secondary)' }}>{b}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="card" style={{ position: 'sticky', top: 'calc(var(--navbar-height) + 20px)' }}>
          <div style={{ textAlign: 'center', marginBottom: '20px' }}>
            <div style={{ width: '56px', height: '56px', background: 'var(--brand-subtle)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px' }}>
              <ShieldCheck size={28} style={{ color: 'var(--brand)' }} />
            </div>
            <div style={{ fontFamily: 'var(--font-heading)', fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)' }}>
              ₹{policy.basePremium.toLocaleString('en-IN')}
            </div>
            <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>per month × {policy.durationMonths} months</div>
          </div>

          <div style={{ background: 'var(--surface-2)', borderRadius: '10px', padding: '12px 16px', marginBottom: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>Coverage</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>₹{policy.coverageAmount.toLocaleString('en-IN')}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>Duration</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>{policy.durationMonths} months</span>
            </div>
          </div>

          {purchased ? (
            <div style={{ background: 'var(--green-bg)', border: '1px solid var(--green-border)', borderRadius: '10px', padding: '14px', textAlign: 'center' }}>
              <CheckCircle size={20} style={{ color: 'var(--green)', marginBottom: '4px' }} />
              <div style={{ fontFamily: 'var(--font-body)', fontSize: '14px', fontWeight: 600, color: 'var(--green)' }}>Purchase Successful!</div>
              <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--green)', marginTop: '2px' }}>Redirecting to your policies...</div>
            </div>
          ) : (
            <button className="btn-primary" onClick={handlePurchase} disabled={purchasing} style={{ width: '100%', justifyContent: 'center', height: '48px', fontSize: '15px' }}>
              {purchasing ? <><Loader2 size={16} className="spin" /> Processing...</> : 'Purchase Policy'}
            </button>
          )}

          <p style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-faint)', textAlign: 'center', margin: '12px 0 0', lineHeight: 1.5 }}>
            By purchasing, you agree to our terms & conditions.
          </p>
        </div>
      </div>
      <style>{`.spin{animation:spin 1s linear infinite}@keyframes spin{to{transform:rotate(360deg)}} @media(max-width:900px){.pd-grid{grid-template-columns:1fr!important}}`}</style>
    </div>
  );
}
