import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { ShieldCheck, Eye, EyeOff, Loader2 } from 'lucide-react';
import { register as registerApi } from '../../api/authApi';
import { useAppDispatch } from '../../store';
import { loginSuccess } from '../../store/slices/authSlice';
import { useToast } from '../../shared/components/Toast';

const schema = yup.object({
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  email: yup.string().email('Enter a valid email').required('Email is required'),
  phone: yup.string().matches(/^\d{10}$/, 'Phone must be 10 digits').required('Phone is required'),
  address: yup.string().required('Address is required'),
  password: yup.string().min(8, 'At least 8 characters').matches(/[a-zA-Z]/, 'Must contain a letter').matches(/[0-9]/, 'Must contain a number').required('Password is required'),
  confirmPassword: yup.string().oneOf([yup.ref('password')], 'Passwords do not match').required('Please confirm your password'),
  role: yup.string().oneOf(['ROLE_USER', 'ROLE_ADMIN']).required(),
});

function getStrength(pwd) {
  let s = 0;
  if (pwd.length >= 8) s++;
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) s++;
  if (/[0-9]/.test(pwd)) s++;
  if (/[^a-zA-Z0-9]/.test(pwd)) s++;
  return s;
}
const strengthLabels = ['', 'Weak', 'Fair', 'Good', 'Strong'];
const strengthColors = ['', 'var(--red)', 'var(--amber)', 'var(--blue)', 'var(--green)'];

export default function RegisterPage() {
  const [showPwd, setShowPwd] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  const [pwdValue, setPwdValue] = useState('');
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
    defaultValues: { role: 'ROLE_USER' },
  });

  const strength = getStrength(pwdValue);

  const onSubmit = async (data) => {
    setLoading(true);
    setApiError('');
    try {
      const { confirmPassword, ...payload } = data;
      const res = await registerApi(payload);
      dispatch(loginSuccess(res.data));
      showToast({ type: 'success', title: 'Account created!', message: `Welcome, ${res.data.firstName}!` });
      navigate(res.data.role === 'ROLE_ADMIN' ? '/admin/dashboard' : '/dashboard');
    } catch (err) {
      setApiError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const f = (id, label, type = 'text', placeholder = '') => (
    <div>
      <label className="form-label" htmlFor={id}>{label}</label>
      <input id={id} type={type} placeholder={placeholder} className={`form-input${errors[id] ? ' error' : ''}`} {...register(id)} />
      {errors[id] && <p className="form-error">{errors[id].message}</p>}
    </div>
  );

  return (
    <div style={{ minHeight: 'calc(100vh - 60px)', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px 16px' }}>
      <div style={{ width: '100%', maxWidth: '640px' }}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '28px' }}>
          <div style={{ width: '48px', height: '48px', background: 'var(--brand)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px' }}>
            <ShieldCheck size={24} color="#fff" />
          </div>
          <h1 style={{ fontFamily: 'var(--font-heading)', fontSize: '26px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>Create your account</h1>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '6px 0 0' }}>Get covered in minutes</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="reg-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '16px' }}>
              {f('firstName', 'First Name', 'text', 'John')}
              {f('lastName', 'Last Name', 'text', 'Doe')}
            </div>
            <div style={{ marginBottom: '16px' }}>{f('email', 'Email Address', 'email', 'you@example.com')}</div>
            <div className="reg-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '16px' }}>
              {f('phone', 'Phone Number', 'tel', '9876543210')}
              {f('address', 'Address', 'text', 'Mumbai, Maharashtra')}
            </div>

            <div className="reg-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '8px' }}>
              <div>
                <label className="form-label" htmlFor="password">Password</label>
                <div style={{ position: 'relative' }}>
                  <input id="password" type={showPwd ? 'text' : 'password'} className={`form-input${errors.password ? ' error' : ''}`} style={{ paddingRight: '40px' }} placeholder="Min 8 chars" {...register('password', { onChange: (e) => setPwdValue(e.target.value) })} />
                  <button type="button" onClick={() => setShowPwd((p) => !p)} style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-faint)' }}>
                    {showPwd ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.password && <p className="form-error">{errors.password.message}</p>}
              </div>
              <div>
                <label className="form-label" htmlFor="confirmPassword">Confirm Password</label>
                <div style={{ position: 'relative' }}>
                  <input id="confirmPassword" type={showConfirm ? 'text' : 'password'} className={`form-input${errors.confirmPassword ? ' error' : ''}`} style={{ paddingRight: '40px' }} placeholder="Repeat password" {...register('confirmPassword')} />
                  <button type="button" onClick={() => setShowConfirm((p) => !p)} style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-faint)' }}>
                    {showConfirm ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.confirmPassword && <p className="form-error">{errors.confirmPassword.message}</p>}
              </div>
            </div>

            {pwdValue && (
              <div style={{ marginBottom: '16px' }}>
                <div style={{ display: 'flex', gap: '4px', marginBottom: '4px' }}>
                  {[1, 2, 3, 4].map((i) => (
                    <div key={i} style={{ flex: 1, height: '4px', borderRadius: '99px', background: i <= strength ? strengthColors[strength] : 'var(--surface-3)', transition: 'background 0.3s' }} />
                  ))}
                </div>
                <span style={{ fontFamily: 'var(--font-body)', fontSize: '11px', color: strengthColors[strength] }}>{strengthLabels[strength]}</span>
              </div>
            )}

            <div style={{ marginBottom: '24px' }}>
              <label className="form-label" htmlFor="role">Account Type</label>
              <select id="role" className="form-input" {...register('role')}>
                <option value="ROLE_USER">Customer</option>
                <option value="ROLE_ADMIN">Admin</option>
              </select>
            </div>

            {apiError && (
              <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 14px', marginBottom: '16px', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--red)' }}>{apiError}</div>
            )}

            <button type="submit" className="btn-primary" disabled={loading} style={{ width: '100%', justifyContent: 'center', height: '46px', fontSize: '15px' }}>
              {loading ? <><Loader2 size={16} className="spin" /> Creating account...</> : 'Create account'}
            </button>
          </form>

          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', textAlign: 'center', marginTop: '20px', marginBottom: 0 }}>
            Already have an account? <Link to="/login" style={{ color: 'var(--brand)', fontWeight: 600, textDecoration: 'none' }}>Sign in</Link>
          </p>
        </div>
      </div>
      <style>{`.spin{animation:spin 1s linear infinite}@keyframes spin{to{transform:rotate(360deg)}} @media(max-width:640px){.reg-grid{grid-template-columns:1fr!important}}`}</style>
    </div>
  );
}
