import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Upload, X, FileText, Loader2, CheckCircle } from 'lucide-react';
import { getUserPolicies } from '../../../api/policyApi';
import { initiateClaim, uploadDocument } from '../../../api/claimsApi';
import { useAppSelector } from '../../../store';
import { useToast } from '../../../shared/components/Toast';

const schema = yup.object({
  policyId: yup.number().min(1, 'Please select a policy').required('Policy is required'),
  description: yup.string().min(20, 'Please provide at least 20 characters').required('Description is required'),
  claimedAmount: yup.number().min(1, 'Amount must be greater than 0').required('Claimed amount is required'),
});

export default function FileClaim() {
  const { userId } = useAppSelector((s) => s.auth);
  const [policies, setPolicies] = useState([]);
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState('form');
  const [claimId, setClaimId] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);
  const navigate = useNavigate();
  const { showToast } = useToast();

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
    defaultValues: { policyId: 0 },
  });

  useEffect(() => {
    if (!userId) return;
    getUserPolicies(userId)
      .then((r) => setPolicies(r.data.filter((p) => p.status === 'ACTIVE')))
      .catch(() => {});
  }, [userId]);

  const handleFileChange = (e) => {
    const selected = Array.from(e.target.files || []);
    const valid = selected.filter((f) => f.size <= 10 * 1024 * 1024);
    if (valid.length < selected.length) showToast({ type: 'error', title: 'File too large', message: 'Max file size is 10MB.' });
    setFiles((prev) => [...prev, ...valid].slice(0, 5));
    e.target.value = '';
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.currentTarget.style.borderColor = 'var(--border-medium)';
    e.currentTarget.style.background = 'var(--surface-2)';
    const dropped = Array.from(e.dataTransfer.files).filter((f) => f.size <= 10 * 1024 * 1024);
    setFiles((prev) => [...prev, ...dropped].slice(0, 5));
  };

  const onSubmit = async (data) => {
    if (!userId) return;
    setLoading(true);
    try {
      const claimRes = await initiateClaim({ userId, policyId: Number(data.policyId), description: data.description, claimedAmount: Number(data.claimedAmount) });
      const cId = claimRes.data.id;
      setClaimId(cId);
      if (files.length > 0) {
        setStep('uploading');
        for (let i = 0; i < files.length; i++) {
          await uploadDocument(cId, files[i]);
          setUploadProgress(Math.round(((i + 1) / files.length) * 100));
        }
      }
      setStep('done');
      showToast({ type: 'success', title: 'Claim Filed!', message: `Claim #${cId} submitted successfully.` });
    } catch (err) {
      showToast({ type: 'error', title: 'Submission Failed', message: err.response?.data?.message || 'Please try again.' });
      setStep('form');
    } finally { setLoading(false); }
  };

  if (step === 'done') return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh' }}>
      <div className="card" style={{ textAlign: 'center', maxWidth: '420px', padding: '48px 36px' }}>
        <div style={{ width: '64px', height: '64px', background: 'var(--green-bg)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 20px' }}>
          <CheckCircle size={32} style={{ color: 'var(--green)' }} />
        </div>
        <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '22px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>Claim Submitted!</h2>
        <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '0 0 24px' }}>Your claim #{claimId} has been filed. Our team will review it within 48 hours.</p>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => navigate(`/claims/${claimId}`)} className="btn-primary" style={{ flex: 1, justifyContent: 'center' }}>View Claim</button>
          <button onClick={() => navigate('/claims')} className="btn-ghost" style={{ flex: 1, justifyContent: 'center' }}>All Claims</button>
        </div>
      </div>
    </div>
  );

  return (
    <div>
      <div style={{ marginBottom: '28px' }}>
        <p className="page-role-label">CUSTOMER</p>
        <h1 className="page-title">File a Claim</h1>
        <p className="page-subtitle">Describe your incident and attach supporting documents</p>
      </div>

      <div style={{ maxWidth: '680px' }}>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="card" style={{ marginBottom: '20px' }}>
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 20px' }}>Claim Information</h2>

            <div style={{ marginBottom: '16px' }}>
              <label className="form-label" htmlFor="policyId">Select Policy</label>
              {policies.length === 0 ? (
                <div style={{ background: 'var(--amber-bg)', border: '1px solid var(--amber-border)', borderRadius: '10px', padding: '12px 14px', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--amber)' }}>
                  No active policies found. <a href="/policies" style={{ color: 'var(--amber)', fontWeight: 600 }}>Browse policies →</a>
                </div>
              ) : (
                <select id="policyId" className={`form-input${errors.policyId ? ' error' : ''}`} {...register('policyId')}>
                  <option value={0}>— Select a policy —</option>
                  {policies.map((p) => <option key={p.id} value={p.id}>{p.policyTypeName} (Policy #{p.id})</option>)}
                </select>
              )}
              {errors.policyId && <p className="form-error">{errors.policyId.message}</p>}
            </div>

            <div style={{ marginBottom: '16px' }}>
              <label className="form-label" htmlFor="description">Incident Description</label>
              <textarea id="description" className={`form-input${errors.description ? ' error' : ''}`} style={{ minHeight: '120px', resize: 'vertical', lineHeight: 1.6 }} placeholder="Describe what happened in detail. Include the date, nature of the incident, and any relevant context..." {...register('description')} />
              {errors.description && <p className="form-error">{errors.description.message}</p>}
            </div>

            <div>
              <label className="form-label" htmlFor="claimedAmount">Claimed Amount (₹)</label>
              <input id="claimedAmount" type="number" min={1} step={1} className={`form-input${errors.claimedAmount ? ' error' : ''}`} placeholder="e.g. 50000" {...register('claimedAmount')} />
              {errors.claimedAmount && <p className="form-error">{errors.claimedAmount.message}</p>}
            </div>
          </div>

          <div className="card" style={{ marginBottom: '24px' }}>
            <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 16px' }}>
              Supporting Documents <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', fontWeight: 400, color: 'var(--text-muted)' }}>(optional, max 5)</span>
            </h2>

            <label htmlFor="doc-upload"
              style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '8px', padding: '28px', border: '2px dashed var(--border-medium)', borderRadius: '12px', cursor: 'pointer', background: 'var(--surface-2)', transition: 'border-color 0.15s, background 0.15s' }}
              onDragOver={(e) => { e.preventDefault(); e.currentTarget.style.borderColor = 'var(--brand)'; e.currentTarget.style.background = 'var(--brand-subtle)'; }}
              onDragLeave={(e) => { e.currentTarget.style.borderColor = 'var(--border-medium)'; e.currentTarget.style.background = 'var(--surface-2)'; }}
              onDrop={handleDrop}
            >
              <Upload size={24} style={{ color: 'var(--text-faint)' }} />
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-secondary)', fontWeight: 600 }}>Click to browse or drag & drop</span>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-faint)' }}>PDF, JPG, PNG · Max 10MB each</span>
            </label>
            <input id="doc-upload" type="file" accept=".pdf,.jpg,.jpeg,.png" multiple onChange={handleFileChange} style={{ display: 'none' }} />

            {files.length > 0 && (
              <div style={{ marginTop: '12px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {files.map((f, i) => (
                  <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 12px', background: 'var(--surface-2)', borderRadius: '8px', border: '1px solid var(--border)' }}>
                    <FileText size={16} style={{ color: 'var(--brand)', flexShrink: 0 }} />
                    <span style={{ flex: 1, fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-primary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{f.name}</span>
                    <span style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: 'var(--text-faint)', flexShrink: 0 }}>{(f.size / 1024).toFixed(0)} KB</span>
                    <button type="button" onClick={() => setFiles((prev) => prev.filter((_, idx) => idx !== i))} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-faint)', padding: '2px', flexShrink: 0 }}>
                      <X size={14} />
                    </button>
                  </div>
                ))}
              </div>
            )}

            {step === 'uploading' && (
              <div style={{ marginTop: '12px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>Uploading documents...</span>
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--brand)', fontWeight: 600 }}>{uploadProgress}%</span>
                </div>
                <div style={{ height: '6px', background: 'var(--surface-3)', borderRadius: '99px', overflow: 'hidden' }}>
                  <div style={{ width: `${uploadProgress}%`, height: '100%', background: 'var(--brand)', borderRadius: '99px', transition: 'width 0.3s ease' }} />
                </div>
              </div>
            )}
          </div>

          <div style={{ display: 'flex', gap: '12px' }}>
            <button type="button" className="btn-ghost" onClick={() => navigate('/claims')} style={{ flexShrink: 0 }}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={loading || policies.length === 0} style={{ flex: 1, justifyContent: 'center', height: '48px' }}>
              {loading ? <><Loader2 size={16} className="spin" /> {step === 'uploading' ? 'Uploading...' : 'Submitting...'}</> : 'Submit Claim'}
            </button>
          </div>
        </form>
      </div>
      <style>{`.spin{animation:spin 1s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}`}</style>
    </div>
  );
}
