import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { format } from 'date-fns';
import { FileText, CheckCircle, Clock, XCircle, AlertCircle, ArrowLeft } from 'lucide-react';
import { getClaimStatus, getClaimDocuments } from '../../../api/claimsApi';
import StatusBadge from '../../../shared/components/StatusBadge';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const TIMELINE_STEPS = [
  { status: 'SUBMITTED',    label: 'Submitted',    icon: CheckCircle },
  { status: 'UNDER_REVIEW', label: 'Under Review', icon: Clock },
  { status: 'APPROVED',     label: 'Approved',     icon: CheckCircle },
];

const STATUS_ORDER = ['DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'CLOSED'];
const getStepIndex = (status) => STATUS_ORDER.indexOf(status);

export default function ClaimDetail() {
  const { id } = useParams();
  const [claim, setClaim] = useState(null);
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    (async () => {
      try {
        const [c, d] = await Promise.all([getClaimStatus(Number(id)), getClaimDocuments(Number(id))]);
        setClaim(c.data);
        setDocs(d.data);
      } catch { setError('Failed to load claim details.'); }
      finally { setLoading(false); }
    })();
  }, [id]);

  if (loading) return (
    <div>
      <p className="page-role-label">CUSTOMER</p>
      <h1 className="page-title">Claim Details</h1>
      <SkeletonRow count={5} height={72} />
    </div>
  );

  if (error || !claim) return (
    <div style={{ textAlign: 'center', padding: '60px 0' }}>
      <AlertCircle size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
      <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px', color: 'var(--text-muted)' }}>{error || 'Claim not found.'}</p>
      <Link to="/claims" style={{ color: 'var(--brand)', fontFamily: 'var(--font-body)', fontSize: '14px', fontWeight: 600, textDecoration: 'none' }}>← Back to Claims</Link>
    </div>
  );

  const isRejected = claim.status === 'REJECTED';
  const isResolved = ['APPROVED', 'REJECTED', 'CLOSED'].includes(claim.status);
  const stepIndex = getStepIndex(claim.status);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '28px', flexWrap: 'wrap' }}>
        <Link to="/claims" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', textDecoration: 'none', fontFamily: 'var(--font-body)', fontSize: '13px' }}>
          <ArrowLeft size={14} /> Claims
        </Link>
        <span style={{ color: 'var(--border-medium)' }}>/</span>
        <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)', fontWeight: 600 }}>Claim #{claim.id}</span>
        <StatusBadge status={claim.status} />
      </div>

      <div className="cd-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: '24px', alignItems: 'start' }}>
        <div>
          <div className="card" style={{ marginBottom: '20px' }}>
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 20px' }}>Claim Summary</h2>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '20px' }}>
              {[
                { label: 'Claimed Amount', value: `₹${claim.claimedAmount.toLocaleString('en-IN')}` },
                { label: 'Policy ID', value: `#${claim.policyId}` },
                { label: 'Submitted', value: claim.submittedAt ? format(new Date(claim.submittedAt), 'dd MMM yyyy') : '—' },
                { label: 'Resolved', value: claim.resolvedAt ? format(new Date(claim.resolvedAt), 'dd MMM yyyy') : '—' },
              ].map(item => (
                <div key={item.label} style={{ background: 'var(--surface-2)', borderRadius: '10px', padding: '14px' }}>
                  <div style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-muted)', marginBottom: '4px', textTransform: 'uppercase', letterSpacing: '0.06em' }}>{item.label}</div>
                  <div style={{ fontFamily: 'var(--font-heading)', fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)' }}>{item.value}</div>
                </div>
              ))}
            </div>
            <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '6px', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Description</div>
            <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-primary)', lineHeight: 1.7, margin: 0 }}>{claim.description}</p>

            {claim.remarks && (
              <div style={{ marginTop: '16px', background: isRejected ? 'var(--red-bg)' : 'var(--green-bg)', border: `1px solid ${isRejected ? 'var(--red-border)' : 'var(--green-border)'}`, borderRadius: '10px', padding: '12px 14px' }}>
                <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', fontWeight: 600, color: isRejected ? 'var(--red)' : 'var(--green)', marginBottom: '4px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  {isRejected ? <XCircle size={14} /> : <CheckCircle size={14} />} Admin Remarks
                </div>
                <p style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-secondary)', margin: 0, lineHeight: 1.6 }}>{claim.remarks}</p>
              </div>
            )}
          </div>

          <div className="card">
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 16px' }}>Documents</h2>
            {docs.length === 0 ? (
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)' }}>No documents uploaded.</p>
            ) : docs.map(doc => (
              <div key={doc.id} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 12px', background: 'var(--surface-2)', borderRadius: '8px', border: '1px solid var(--border)', marginBottom: '8px' }}>
                <FileText size={16} style={{ color: 'var(--brand)', flexShrink: 0 }} />
                <span style={{ flex: 1, fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)' }}>{doc.fileName}</span>
                <span style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-faint)' }}>{doc.uploadedAt ? format(new Date(doc.uploadedAt), 'dd MMM') : '—'}</span>
                {doc.downloadUrl && <a href={doc.downloadUrl} target="_blank" rel="noreferrer" style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--brand)', fontWeight: 600, textDecoration: 'none' }}>Download</a>}
              </div>
            ))}
          </div>
        </div>

        <div className="card" style={{ position: 'sticky', top: 'calc(var(--navbar-height) + 20px)' }}>
          <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 20px' }}>Status Timeline</h2>
          {TIMELINE_STEPS.map((step, i) => {
            const isDone = stepIndex >= getStepIndex(step.status);
            const isCurrent = claim.status === step.status;
            const isLast = i === TIMELINE_STEPS.length - 1;
            return (
              <div key={step.status} style={{ display: 'flex', gap: '12px' }}>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                  <div style={{ width: '28px', height: '28px', borderRadius: '50%', flexShrink: 0, background: isDone ? 'var(--brand)' : 'var(--surface-3)', border: `2px solid ${isDone ? 'var(--brand)' : 'var(--border-medium)'}`, display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'all 0.3s ease' }} className={isCurrent ? 'pulse-ring' : ''}>
                    <step.icon size={14} style={{ color: isDone ? '#fff' : 'var(--text-faint)' }} />
                  </div>
                  {!isLast && <div style={{ width: '2px', flex: 1, minHeight: '32px', background: isDone ? 'var(--brand)' : 'var(--border)', margin: '4px 0' }} />}
                </div>
                <div style={{ paddingBottom: isLast ? 0 : '20px' }}>
                  <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: isCurrent ? 700 : 500, color: isDone ? 'var(--text-primary)' : 'var(--text-faint)' }}>{step.label}</div>
                  {isCurrent && <div style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--brand)', marginTop: '2px' }}>Current status</div>}
                </div>
              </div>
            );
          })}

          {isResolved && (
            <div style={{ marginTop: '16px', padding: '12px', background: isRejected ? 'var(--red-bg)' : 'var(--green-bg)', borderRadius: '10px', border: `1px solid ${isRejected ? 'var(--red-border)' : 'var(--green-border)'}`, textAlign: 'center' }}>
              <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: isRejected ? 'var(--red)' : 'var(--green)' }}>
                {isRejected ? '✗ Claim Rejected' : '✓ Claim Approved'}
              </div>
            </div>
          )}
        </div>
      </div>
      <style>{`@media(max-width:900px){.cd-grid{grid-template-columns:1fr!important}}`}</style>
    </div>
  );
}
