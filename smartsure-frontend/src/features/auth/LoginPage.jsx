import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { ShieldCheck, Mail, Eye, EyeOff, Loader2 } from 'lucide-react';
import { login } from '../../api/authApi';
import { useAppDispatch } from '../../store';
import { loginSuccess } from '../../store/slices/authSlice';
import { useToast } from '../../shared/components/Toast';

const schema = yup.object({
  email: yup.string().email('Enter a valid email').required('Email is required'),
  password: yup.string().min(6, 'Minimum 6 characters').required('Password is required'),
});

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { showToast } = useToast();

  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: yupResolver(schema) });

  const onSubmit = async (data) => {
    setLoading(true);
    setApiError('');
    try {
      const res = await login(data);
      dispatch(loginSuccess(res.data));
      showToast({ type: 'success', title: 'Welcome back!', message: `Signed in as ${res.data.name}` });
      const from = location.state?.from?.pathname;
      navigate(from || (res.data.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'));
    } catch (err) {
      setApiError(err.response?.data?.message || 'Invalid email or password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: 'calc(100vh - 60px)', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px 16px' }}>
      <div style={{ width: '100%', maxWidth: '440px' }}>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '28px' }}>
          <div style={{ width: '48px', height: '48px', background: 'var(--brand)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px' }}>
            <ShieldCheck size={24} color="#fff" />
          </div>
          <h1 style={{ fontFamily: 'var(--font-heading)', fontSize: '26px', fontWeight: 700, color: 'var(--text-primary)', margin: 0 }}>Welcome back</h1>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: '6px 0 0' }}>Sign in to your SmartSure account</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <div style={{ marginBottom: '16px' }}>
              <label className="form-label" htmlFor="email">Email address</label>
              <div style={{ position: 'relative' }}>
                <Mail size={16} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-faint)', pointerEvents: 'none' }} />
                <input id="email" type="email" autoComplete="email" className={`form-input${errors.email ? ' error' : ''}`} style={{ paddingLeft: '38px' }} placeholder="you@example.com" {...register('email')} />
              </div>
              {errors.email && <p className="form-error">{errors.email.message}</p>}
            </div>

            <div style={{ marginBottom: '24px' }}>
              <label className="form-label" htmlFor="password">Password</label>
              <div style={{ position: 'relative' }}>
                <input id="password" type={showPassword ? 'text' : 'password'} autoComplete="current-password" className={`form-input${errors.password ? ' error' : ''}`} style={{ paddingRight: '40px' }} placeholder="••••••••" {...register('password')} />
                <button type="button" onClick={() => setShowPassword((p) => !p)} aria-label="Toggle password" style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-faint)', padding: '4px' }}>
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
              {errors.password && <p className="form-error">{errors.password.message}</p>}
            </div>

            {apiError && (
              <div style={{ background: 'var(--red-bg)', border: '1px solid var(--red-border)', borderRadius: '10px', padding: '12px 14px', marginBottom: '16px', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--red)' }}>
                {apiError}
              </div>
            )}

            <button type="submit" className="btn-primary" disabled={loading} style={{ width: '100%', justifyContent: 'center', height: '46px', fontSize: '15px' }}>
              {loading ? <><Loader2 size={16} className="spin" /> Signing in...</> : 'Sign in'}
            </button>
          </form>

          <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', textAlign: 'center', marginTop: '20px', marginBottom: 0 }}>
            Don't have an account? <Link to="/register" style={{ color: 'var(--brand)', fontWeight: 600, textDecoration: 'none' }}>Register now</Link>
          </p>
        </div>
      </div>
      <style>{`.spin{animation:spin 1s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}`}</style>
    </div>
  );
}
