import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { format } from 'date-fns';
import { ArrowLeft, FileText, CheckCircle, XCircle, Loader2, AlertCircle } from 'lucide-react';
import { getAdminClaimById, getAdminClaimDocuments, reviewClaim } from '../../../api/adminApi';
import StatusBadge from '../../../shared/components/StatusBadge';
import SkeletonRow from '../../../shared/components/SkeletonRow';
import { useToast } from '../../../shared/components/Toast';

const schema = yup.object({
  decision: yup.mixed().oneOf(['APPROVED', 'REJECTED']).required('Please select a decision'),
  remarks: yup.string().min(10, 'At least 10 characters').required('Remarks are required'),
});

export default function ClaimReview() {
  const { id } = useParams();
  const [claim, setClaim] = useState(null);
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [reviewed, setReviewed] = useState(false);
  const [error, setError] = useState('');
  const { showToast } = useToast();
  const navigate = useNavigate();

  const { register, handleSubmit, watch, formState: { errors } } = useForm({ resolver: yupResolver(schema) });
  const decision = watch('decision');

  useEffect(() => {
    if (!id) return;
    (async () => {
      try {
        const [c, d] = await Promise.all([getAdminClaimById(Number(id)), getAdminClaimDocuments(Number(id))]);
        setClaim(c.data);
        setDocs(d.data);
      } catch { setError('Failed to load claim details.'); }
      finally { setLoading(false); }
    })();
  }, [id]);

  const onSubmit = async (data) => {
    setSubmitting(true);
    try {
      await reviewClaim(Number(id), { status: data.decision, remarks: data.remarks });
      setReviewed(true);
      showToast({ type: 'success', title: `Claim ${data.decision}`, message: `Claim #${id} ${data.decision.toLowerCase()}.` });
      setTimeout(() => navigate('/admin/claims'), 1500);
    } catch (err) {
      showToast({ type: 'error', title: 'Review Failed', message: err.response?.data?.message || 'Please try again.' });
    } finally { setSubmitting(false); }
  };

  const isAlreadyReviewed = claim && ['APPROVED', 'REJECTED', 'CLOSED'].includes(claim.status);

  if (loading) return <div><p className="page-role-label">ADMIN</p><h1 className="page-title">Review Claim</h1><SkeletonRow count={5} height={72} /></div>;

  if (error || !claim) return (
    <div style={{ textAlign: 'center', padding: '60px 0' }}>
      <AlertCircle size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
      <p style={{ fontFamily: 'var(--font-body)', fontSize: '15px', color: 'var(--text-muted)' }}>{error || 'Claim not found.'}</p>
      <Link to="/admin/claims" style={{ color: 'var(--brand)', fontFamily: 'var(--font-body)', fontSize: '14px', fontWeight: 600, textDecoration: 'none' }}>Back to Claims</Link>
    </div>
  );

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '28px', flexWrap: 'wrap' }}>
        <Link to="/admin/claims" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', textDecoration: 'none', fontFamily: 'var(--font-body)', fontSize: '13px' }}>
          <ArrowLeft size={14} /> Claims
        </Link>
        <span style={{ color: 'var(--border-medium)' }}>/</span>
        <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)', fontWeight: 600 }}>Claim #{claim.id}</span>
        <StatusBadge status={claim.status} />
      </div>

      <div className="cr-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 380px', gap: '24px', alignItems: 'start' }}>
        <div>
          <div className="card" style={{ marginBottom: '20px' }}>
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 20px' }}>Claim Information</h2>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px', marginBottom: '20px' }}>
              {[
                { label: 'User ID', value: `#${claim.userId}` },
                { label: 'Policy ID', value: `#${claim.policyId}` },
                { label: 'Claimed Amount', value: `₹${claim.claimedAmount.toLocaleString('en-IN')}` },
                { label: 'Submitted', value: format(new Date(claim.submittedAt), 'dd MMM yyyy, hh:mm a') },
              ].map(item => (
                <div key={item.label} style={{ background: 'var(--surface-2)', borderRadius: '10px', padding: '14px' }}>
                  <div style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '4px' }}>{item.label}</div>
                  <div style={{ fontFamily: 'var(--font-heading)', fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)' }}>{item.value}</div>
                </div>
              ))}
            </div>
            <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '8px' }}>Description</div>
            <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-primary)', lineHeight: 1.7, margin: 0 }}>{claim.description}</p>
            {claim.remarks && (
              <div style={{ marginTop: '16px', background: 'var(--surface-2)', borderRadius: '10px', padding: '12px 14px' }}>
                <div style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>Previous Remarks</div>
                <p style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)', margin: 0 }}>{claim.remarks}</p>
              </div>
            )}
          </div>

          <div className="card">
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 16px' }}>Documents ({docs.length})</h2>
            {docs.length === 0 ? (
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)' }}>No documents submitted.</p>
            ) : docs.map(doc => (
              <div key={doc.id} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 12px', background: 'var(--surface-2)', borderRadius: '8px', border: '1px solid var(--border)', marginBottom: '8px' }}>
                <FileText size={16} style={{ color: 'var(--brand)', flexShrink: 0 }} />
                <span style={{ flex: 1, fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)' }}>{doc.fileName}</span>
                <span style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-faint)' }}>{doc.fileType}</span>
                {doc.downloadUrl && <a href={doc.downloadUrl} target="_blank" rel="noreferrer" style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--brand)', fontWeight: 600, textDecoration: 'none' }}>Download</a>}
              </div>
            ))}
          </div>
        </div>

        <div className="card" style={{ position: 'sticky', top: 'calc(var(--navbar-height) + 20px)' }}>
          <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 20px' }}>
            {isAlreadyReviewed ? 'Review Decision' : 'Review This Claim'}
          </h2>

          {isAlreadyReviewed ? (
            <div style={{ textAlign: 'center', padding: '24px 0' }}>
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', marginBottom: '8px' }}>This claim has already been reviewed.</p>
              <StatusBadge status={claim.status} />
            </div>
          ) : reviewed ? (
            <div style={{ textAlign: 'center', padding: '24px 0' }}>
              <CheckCircle size={32} style={{ color: 'var(--green)', marginBottom: '8px' }} />
              <div style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--green)', fontWeight: 600 }}>Review submitted! Redirecting...</div>
            </div>
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <div style={{ marginBottom: '16px' }}>
                <label className="form-label">Decision</label>
                <div style={{ display: 'flex', gap: '10px' }}>
                  {['APPROVED', 'REJECTED'].map(opt => (
                    <label key={opt} style={{ flex: 1, display: 'flex', alignItems: 'center', gap: '8px', padding: '12px', borderRadius: '10px', cursor: 'pointer', border: `2px solid ${decision === opt ? (opt === 'APPROVED' ? 'var(--green)' : 'var(--red)') : 'var(--border-medium)'}`, background: decision === opt ? (opt === 'APPROVED' ? 'var(--green-bg)' : 'var(--red-bg)') : 'var(--surface)', transition: 'all 0.15s ease' }}>
                      <input type="radio" value={opt} {...register('decision')} style={{ display: 'none' }} />
                      {opt === 'APPROVED'
                        ? <CheckCircle size={16} style={{ color: decision === 'APPROVED' ? 'var(--green)' : 'var(--text-faint)' }} />
                        : <XCircle size={16} style={{ color: decision === 'REJECTED' ? 'var(--red)' : 'var(--text-faint)' }} />}
                      <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 600, color: decision === opt ? (opt === 'APPROVED' ? 'var(--green)' : 'var(--red)') : 'var(--text-secondary)' }}>{opt}</span>
                    </label>
                  ))}
                </div>
                {errors.decision && <p className="form-error">{errors.decision.message}</p>}
              </div>
              <div style={{ marginBottom: '20px' }}>
                <label className="form-label" htmlFor="remarks">Remarks</label>
                <textarea id="remarks" className={`form-input${errors.remarks ? ' error' : ''}`} style={{ minHeight: '120px', resize: 'vertical', lineHeight: 1.6 }} placeholder="Provide a clear explanation..." {...register('remarks')} />
                {errors.remarks && <p className="form-error">{errors.remarks.message}</p>}
              </div>
              <button type="submit" disabled={submitting} className={decision === 'REJECTED' ? 'btn-danger' : 'btn-primary'} style={{ width: '100%', justifyContent: 'center', height: '48px' }}>
                {submitting ? <><Loader2 size={16} className="spin" /> Submitting...</> : decision === 'REJECTED' ? 'Reject Claim' : 'Approve Claim'}
              </button>
            </form>
          )}
        </div>
      </div>
      <style>{`.spin{animation:spin 1s linear infinite}@keyframes spin{to{transform:rotate(360deg)}} @media(max-width:900px){.cr-grid{grid-template-columns:1fr!important}}`}</style>
    </div>
  );
}
