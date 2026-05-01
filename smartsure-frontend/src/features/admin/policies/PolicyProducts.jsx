import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Plus, Edit2, Trash2, ShieldCheck, X, Loader2, Check } from 'lucide-react';
import { getAllPolicyTypes, createPolicyType, updatePolicyType, deletePolicyType } from '../../../api/policyApi';
import { useToast } from '../../../shared/components/Toast';
import SkeletonRow from '../../../shared/components/SkeletonRow';

const schema = yup.object({
  name: yup.string().required('Name is required'),
  description: yup.string().required('Description is required'),
  basePremium: yup.number().min(1, 'Must be > 0').required('Premium is required'),
  coverageAmount: yup.number().min(1, 'Must be > 0').required('Coverage is required'),
  durationMonths: yup.number().min(1).max(120).required('Duration is required'),
});


const PolicyProducts= () => {
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const { showToast } = useToast();

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    loadPolicies();
  }, []);

  const loadPolicies = () => {
    setLoading(true);
    getAllPolicyTypes()
      .then(r => setPolicies(r.data))
      .catch(() => showToast({ type: 'error', title: 'Failed to load policies' }))
      .finally(() => setLoading(false));
  };

  const openCreate = () => { setEditing(null); reset({}); setShowForm(true); };
  const openEdit = (p) => { setEditing(p); reset({ name: p.name, description: p.description, basePremium: p.basePremium, coverageAmount: p.coverageAmount, durationMonths: p.durationMonths }); setShowForm(true); };
  const closeForm = () => { setShowForm(false); setEditing(null); };

  const onSubmit = async (data) => {
    setSaving(true);
    try {
      if (editing?.id) {
        const updated = await updatePolicyType(editing.id, { ...data });
        setPolicies(prev => prev.map(p => p.id === editing.id ? updated.data : p));
        showToast({ type: 'success', title: 'Policy Updated', message: `"${data.name}" has been updated.` });
      } else {
        const created = await createPolicyType(data);
        setPolicies(prev => [...prev, created.data]);
        showToast({ type: 'success', title: 'Policy Created', message: `"${data.name}" is now live.` });
      }
      closeForm();
    } catch (err) {
      showToast({ type: 'error', title: 'Save Failed', message: err.response?.data?.message || 'Please try again.' });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (p) => {
    if (!p.id) return;
    setDeleting(p.id);
    setConfirmDelete(null);
    try {
      await deletePolicyType(p.id);
      setPolicies(prev => prev.filter(x => x.id !== p.id));
      showToast({ type: 'success', title: 'Policy Deleted', message: `"${p.name}" has been removed.` });
    } catch (err) {
      showToast({ type: 'error', title: 'Delete Failed', message: err.response?.data?.message || 'Please try again.' });
    } finally {
      setDeleting(null);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '16px', flexWrap: 'wrap', marginBottom: '28px' }}>
        <div>
          <p className="page-role-label">ADMIN</p>
          <h1 className="page-title">Policy Products</h1>
          <p className="page-subtitle">Manage your insurance product catalogue</p>
        </div>
        <button className="btn-primary" onClick={openCreate} style={{ flexShrink: 0 }}>
          <Plus size={16} /> Add Product
        </button>
      </div>

      {loading ? (
        <SkeletonRow count={4} height={100} />
      ) : policies.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 24px' }}>
          <ShieldCheck size={40} style={{ color: 'var(--text-faint)', marginBottom: '12px' }} />
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>No products yet</h3>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '0 0 20px' }}>Create your first insurance product.</p>
          <button className="btn-primary" onClick={openCreate}><Plus size={16} /> Add Product</button>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {policies.map(p => (
            <div key={p.id} className="card" style={{ display: 'flex', alignItems: 'flex-start', gap: '16px', flexWrap: 'wrap' }}>
              <div style={{ width: '44px', height: '44px', background: 'var(--brand-subtle)', borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                <ShieldCheck size={20} style={{ color: 'var(--brand)' }} />
              </div>
              <div style={{ flex: 1, minWidth: '200px' }}>
                <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 4px' }}>{p.name}</h3>
                <p style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)', margin: '0 0 10px', lineHeight: 1.5 }}>{p.description}</p>
                <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>₹{p.basePremium.toLocaleString('en-IN')}/mo</span>
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>Coverage: ₹{p.coverageAmount.toLocaleString('en-IN')}</span>
                  <span style={{ fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-muted)' }}>{p.durationMonths} months</span>
                </div>
              </div>
              <div style={{ display: 'flex', gap: '8px', flexShrink: 0 }}>
                {confirmDelete?.id === p.id ? (
                  <>
                    <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)', alignSelf: 'center' }}>Delete?</span>
                    <button onClick={() => handleDelete(p)} disabled={!!deleting} style={{ background: 'var(--red)', color: '#fff', border: 'none', borderRadius: '8px', padding: '8px 12px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px', fontSize: '13px', fontFamily: 'var(--font-body)' }}>
                      {deleting === p.id ? <Loader2 size={14} className="spin" /> : <Check size={14} />} Yes
                    </button>
                    <button onClick={() => setConfirmDelete(null)} style={{ background: 'var(--surface-2)', color: 'var(--text-secondary)', border: '1px solid var(--border-medium)', borderRadius: '8px', padding: '8px 12px', cursor: 'pointer', fontSize: '13px', fontFamily: 'var(--font-body)' }}>Cancel</button>
                  </>
                ) : (
                  <>
                    <button onClick={() => openEdit(p)} style={{ background: 'var(--surface-2)', border: '1px solid var(--border-medium)', borderRadius: '8px', padding: '8px 10px', cursor: 'pointer', color: 'var(--text-secondary)' }}>
                      <Edit2 size={14} />
                    </button>
                    <button onClick={() => setConfirmDelete(p)} style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '8px', padding: '8px 10px', cursor: 'pointer', color: 'var(--red)' }}>
                      <Trash2 size={14} />
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Form Drawer / Modal */}
      {showForm && (
        <div className="dialog-overlay" onClick={e => { if (e.target === e.currentTarget) closeForm(); }}>
          <div style={{ background: 'var(--surface)', borderRadius: '20px', boxShadow: 'var(--shadow-modal)', padding: '32px', width: '100%', maxWidth: '540px', maxHeight: '90vh', overflowY: 'auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '20px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>
                {editing ? 'Edit Policy Product' : 'New Policy Product'}
              </h2>
              <button onClick={closeForm} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', padding: '4px' }}><X size={18} /></button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <div style={{ marginBottom: '16px' }}>
                <label className="form-label">Product Name</label>
                <input className={`form-input${errors.name ? ' error' : ''}`} placeholder="e.g. Health Shield Pro" {...register('name')} />
                {errors.name && <p className="form-error">{errors.name.message}</p>}
              </div>
              <div style={{ marginBottom: '16px' }}>
                <label className="form-label">Description</label>
                <textarea className={`form-input${errors.description ? ' error' : ''}`} style={{ minHeight: '80px', resize: 'vertical' }} placeholder="Describe what this policy covers..." {...register('description')} />
                {errors.description && <p className="form-error">{errors.description.message}</p>}
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px', marginBottom: '24px' }}>
                <div>
                  <label className="form-label">Base Premium (₹/mo)</label>
                  <input type="number" min={1} className={`form-input${errors.basePremium ? ' error' : ''}`} placeholder="2499" {...register('basePremium')} />
                  {errors.basePremium && <p className="form-error">{errors.basePremium.message}</p>}
                </div>
                <div>
                  <label className="form-label">Coverage (₹)</label>
                  <input type="number" min={1} className={`form-input${errors.coverageAmount ? ' error' : ''}`} placeholder="500000" {...register('coverageAmount')} />
                  {errors.coverageAmount && <p className="form-error">{errors.coverageAmount.message}</p>}
                </div>
                <div>
                  <label className="form-label">Duration (months)</label>
                  <input type="number" min={1} max={120} className={`form-input${errors.durationMonths ? ' error' : ''}`} placeholder="12" {...register('durationMonths')} />
                  {errors.durationMonths && <p className="form-error">{errors.durationMonths.message}</p>}
                </div>
              </div>
              <div style={{ display: 'flex', gap: '10px' }}>
                <button type="button" className="btn-ghost" onClick={closeForm} style={{ flex: '0 0 auto' }}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={saving} style={{ flex: 1, justifyContent: 'center', height: '46px' }}>
                  {saving ? <><Loader2 size={16} className="spin" /> Saving...</> : editing ? 'Save Changes' : 'Create Product'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <style>{`.spin { animation: spin 1s linear infinite; } @keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};

export default PolicyProducts;
