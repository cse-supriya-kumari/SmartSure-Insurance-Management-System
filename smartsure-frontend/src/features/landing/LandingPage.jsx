import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, FilePlus2, Clock, Lock, BarChart3, Users, CheckCircle } from 'lucide-react';

const features = [
  { icon: ShieldCheck, title: 'Instant Policy Activation', desc: "Purchase any plan and get immediate coverage. Your policy activates the moment payment is confirmed." },
  { icon: FilePlus2, title: 'One-Click Claim Filing', desc: "Submit an insurance claim in under two minutes. Upload evidence, add a description, and you're done." },
  { icon: Clock, title: '48-Hour Claim Resolution', desc: 'Our streamlined review process means you hear back within 48 hours of submitting your claim.' },
  { icon: Lock, title: 'Bank-Grade Security', desc: 'All data is encrypted in transit and at rest. JWT authentication ensures only you can access your account.' },
  { icon: BarChart3, title: 'Live Claim Tracking', desc: 'Know exactly where your claim stands at every stage — from submission to final settlement.' },
  { icon: Users, title: 'Dedicated Admin Support', desc: 'A team of administrators reviews every claim manually to ensure fair and accurate decisions.' },
];

const steps = [
  { n: '01', title: 'Create your account', desc: 'Register in seconds with your name, email, and contact details.' },
  { n: '02', title: 'Browse and purchase a policy', desc: 'Choose from our range of insurance products and activate instantly.' },
  { n: '03', title: 'File a claim with documents', desc: 'Submit your claim online and upload supporting documents easily.' },
  { n: '04', title: 'Receive your settlement', desc: 'Get your claim reviewed and settlement processed within 48 hours.' },
];

export default function LandingPage() {
  return (
    <div style={{ background: 'var(--bg)', overflow: 'hidden' }}>
      {/* Hero */}
      <section style={{ minHeight: 'calc(100vh - 60px)', background: 'radial-gradient(ellipse 60% 50% at 80% 20%, rgba(27,108,242,0.07), transparent)', display: 'flex', alignItems: 'center', padding: '80px 5% 60px', gap: '60px' }}>
        <div style={{ flex: '0 0 60%', maxWidth: '60%' }} className="hero-text-col">
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '11px', fontWeight: 700, letterSpacing: '0.12em', color: 'var(--brand)', textTransform: 'uppercase', marginBottom: '16px' }}>Trusted Insurance Platform</p>
          <h1 className="hero-headline" style={{ fontFamily: 'var(--font-heading)', fontWeight: 800, lineHeight: 1.1, color: 'var(--text-primary)', margin: '0 0 20px' }}>
            Insurance that works when you need it most.
          </h1>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '18px', color: 'var(--text-muted)', lineHeight: 1.6, maxWidth: '500px', marginBottom: '32px' }}>
            Purchase policies, file claims, and track every step of your insurance journey — all from one secure platform.
          </p>
          <div className="hero-ctas" style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginBottom: '28px' }}>
            <Link to="/register" className="btn-primary" style={{ textDecoration: 'none', padding: '14px 28px', fontSize: '15px' }}>Get started free</Link>
            <Link to="/login" className="btn-ghost" style={{ textDecoration: 'none', padding: '14px 28px', fontSize: '15px' }}>Sign in to your account</Link>
          </div>
          <div className="trust-strip" style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
            {['No credit card required', 'Claims settled in 48 hours', 'Bank-grade security'].map((t) => (
              <span key={t} style={{ display: 'flex', alignItems: 'center', gap: '6px', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-faint)' }}>
                <CheckCircle size={14} style={{ color: 'var(--green)', flexShrink: 0 }} /> {t}
              </span>
            ))}
          </div>
        </div>

        <div className="hero-card-col" style={{ flex: '0 0 35%' }}>
          <div style={{ background: 'var(--surface)', borderRadius: '16px', boxShadow: 'var(--shadow-hover)', padding: '28px', border: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
              <span style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>Health Shield Pro</span>
              <span style={{ background: 'var(--green-bg)', color: 'var(--green)', border: '1px solid var(--green-border)', borderRadius: '20px', padding: '3px 10px', fontSize: '11px', fontWeight: 600 }}>ACTIVE</span>
            </div>
            <div style={{ fontFamily: 'var(--font-heading)', fontSize: '36px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '8px' }}>₹5,00,000</div>
            <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)', marginBottom: '16px' }}>Coverage Amount</div>
            <div style={{ height: '6px', background: 'var(--surface-2)', borderRadius: '99px', overflow: 'hidden', marginBottom: '8px' }}>
              <div style={{ width: '65%', height: '100%', background: 'var(--brand)', borderRadius: '99px' }} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'var(--font-body)', fontSize: '12px', color: 'var(--text-faint)', marginBottom: '20px' }}>
              <span>Premium paid: 8/12</span><span>₹2,499/mo</span>
            </div>
            <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--brand)', fontWeight: 600 }}>View details →</div>
          </div>
        </div>
      </section>

      {/* Stats */}
      <section style={{ background: 'var(--surface)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)', padding: '48px 5%' }}>
        <div className="stats-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', maxWidth: '900px', margin: '0 auto' }}>
          {[{ num: '10,000+', label: 'Active policyholders' }, { num: '₹48 Cr', label: 'Total claims settled' }, { num: '< 48 h', label: 'Average claim resolution' }].map((s, i) => (
            <div key={i} style={{ textAlign: 'center', padding: '16px', borderRight: i < 2 ? '1px solid var(--border)' : 'none' }}>
              <div style={{ fontFamily: 'var(--font-heading)', fontSize: '40px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>{s.num}</div>
              <div style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)' }}>{s.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Features */}
      <section style={{ padding: '80px 5%' }}>
        <div style={{ textAlign: 'center', marginBottom: '48px' }}>
          <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '36px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 12px' }}>Everything you need</h2>
          <p style={{ fontFamily: 'var(--font-body)', fontSize: '16px', color: 'var(--text-muted)' }}>Built for speed, security and simplicity.</p>
        </div>
        <div className="features-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px', maxWidth: '1100px', margin: '0 auto' }}>
          {features.map((f) => (
            <div key={f.title} style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '16px', padding: '28px 24px', transition: 'box-shadow 0.2s, border-color 0.2s' }}
              onMouseEnter={(e) => { e.currentTarget.style.boxShadow = 'var(--shadow-hover)'; e.currentTarget.style.borderColor = 'var(--brand-border)'; }}
              onMouseLeave={(e) => { e.currentTarget.style.boxShadow = ''; e.currentTarget.style.borderColor = 'var(--border)'; }}>
              <div style={{ width: '48px', height: '48px', background: 'var(--brand-subtle)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '16px' }}>
                <f.icon size={24} style={{ color: 'var(--brand)' }} />
              </div>
              <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>{f.title}</h3>
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: 0, lineHeight: 1.6 }}>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* How it works */}
      <section style={{ background: 'var(--surface)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)', padding: '80px 5%' }}>
        <div style={{ textAlign: 'center', marginBottom: '48px' }}>
          <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '36px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 12px' }}>How it works</h2>
        </div>
        <div className="steps-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '24px', maxWidth: '1100px', margin: '0 auto' }}>
          {steps.map((step, i) => (
            <div key={i} style={{ textAlign: 'center' }}>
              <div style={{ fontFamily: 'var(--font-heading)', fontSize: '48px', fontWeight: 800, color: 'var(--brand-subtle)', marginBottom: '12px', lineHeight: 1 }}>{step.n}</div>
              <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', margin: '0 0 8px' }}>{step.title}</h3>
              <p style={{ fontFamily: 'var(--font-body)', fontSize: '14px', color: 'var(--text-muted)', margin: 0, lineHeight: 1.6 }}>{step.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section style={{ background: 'var(--brand)', padding: '80px 5%', textAlign: 'center' }}>
        <h2 style={{ fontFamily: 'var(--font-heading)', fontSize: '36px', fontWeight: 700, color: '#fff', margin: '0 0 24px' }}>Ready to protect what matters?</h2>
        <Link to="/register" style={{ display: 'inline-block', color: '#fff', border: '2px solid rgba(255,255,255,0.6)', borderRadius: '10px', padding: '14px 32px', fontFamily: 'var(--font-body)', fontSize: '15px', fontWeight: 600, textDecoration: 'none', transition: 'background 0.2s' }}
          onMouseEnter={(e) => (e.currentTarget.style.background = 'rgba(255,255,255,0.12)')}
          onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}>
          Create your account
        </Link>
      </section>

      {/* Footer */}
      <footer style={{ background: 'var(--surface)', borderTop: '1px solid var(--border)', padding: '32px 5%' }}>
        <div className="footer-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '24px', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '28px', height: '28px', background: 'var(--brand)', borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><ShieldCheck size={14} color="#fff" /></div>
            <span style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, color: 'var(--text-primary)', fontSize: '15px' }}>SmartSure</span>
          </div>
          <div style={{ textAlign: 'center', fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-faint)' }}>© {new Date().getFullYear()} SmartSure. All rights reserved.</div>
          <div style={{ display: 'flex', gap: '20px', justifyContent: 'flex-end' }}>
            {['Privacy', 'Terms', 'Contact'].map((l) => (
              <a key={l} href="#!" style={{ fontFamily: 'var(--font-body)', fontSize: '13px', color: 'var(--text-muted)', textDecoration: 'none' }}>{l}</a>
            ))}
          </div>
        </div>
      </footer>

      <style>{`
        .hero-headline { font-size: 64px; }
        @media (max-width: 1024px) { .hero-headline { font-size: 48px; } }
        @media (max-width: 640px)  { .hero-headline { font-size: 34px; } }
        @media (max-width: 768px)  { .hero-card-col { display: none; } .hero-text-col { flex: 0 0 100%; max-width: 100%; } }
        @media (max-width: 480px)  { .hero-ctas { flex-direction: column; } .trust-strip { flex-direction: column; gap: 10px; } }
        @media (max-width: 640px)  { .features-grid,.steps-grid,.stats-grid,.footer-grid { grid-template-columns: 1fr !important; } }
        @media (min-width: 641px) and (max-width: 1023px) { .features-grid { grid-template-columns: repeat(2, 1fr) !important; } }
      `}</style>
    </div>
  );
}
