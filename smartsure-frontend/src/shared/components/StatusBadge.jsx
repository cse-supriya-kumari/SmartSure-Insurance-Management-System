import React from 'react';

const BADGE_CONFIG = {
  CREATED:      { bg: 'var(--blue-bg)',   text: 'var(--blue)',   border: 'var(--blue-border)',   label: 'Created' },
  ACTIVE:       { bg: 'var(--green-bg)',  text: 'var(--green)',  border: 'var(--green-border)',  label: 'Active' },
  EXPIRED:      { bg: 'var(--gray-bg)',   text: 'var(--gray)',   border: 'var(--gray-border)',   label: 'Expired' },
  CANCELLED:    { bg: 'var(--red-bg)',    text: 'var(--red)',    border: 'var(--red-border)',    label: 'Cancelled' },
  DRAFT:        { bg: 'var(--gray-bg)',   text: 'var(--gray)',   border: 'var(--gray-border)',   label: 'Draft' },
  SUBMITTED:    { bg: 'var(--blue-bg)',   text: 'var(--blue)',   border: 'var(--blue-border)',   label: 'Submitted' },
  UNDER_REVIEW: { bg: 'var(--amber-bg)',  text: 'var(--amber)',  border: 'var(--amber-border)',  label: 'Under Review' },
  APPROVED:     { bg: 'var(--green-bg)',  text: 'var(--green)',  border: 'var(--green-border)',  label: 'Approved' },
  REJECTED:     { bg: 'var(--red-bg)',    text: 'var(--red)',    border: 'var(--red-border)',    label: 'Rejected' },
  CLOSED:       { bg: 'var(--gray-bg)',   text: 'var(--gray)',   border: 'var(--gray-border)',   label: 'Closed' },
  PAID:         { bg: 'var(--green-bg)',  text: 'var(--green)',  border: 'var(--green-border)',  label: 'Paid' },
  UNPAID:       { bg: 'var(--amber-bg)',  text: 'var(--amber)',  border: 'var(--amber-border)',  label: 'Unpaid' },
  OVERDUE:      { bg: 'var(--red-bg)',    text: 'var(--red)',    border: 'var(--red-border)',    label: 'Overdue' },
  INACTIVE:     { bg: 'var(--red-bg)',    text: 'var(--red)',    border: 'var(--red-border)',    label: 'Inactive' },
  HEALTHY:      { bg: 'var(--green-bg)',  text: 'var(--green)',  border: 'var(--green-border)',  label: 'Healthy' },
};

export default function StatusBadge({ status }) {
  const cfg = BADGE_CONFIG[status] || {
    bg: 'var(--gray-bg)', text: 'var(--gray)', border: 'var(--gray-border)', label: status,
  };
  return (
    <span style={{
      background: cfg.bg, color: cfg.text, border: `1px solid ${cfg.border}`,
      borderRadius: '20px', padding: '3px 10px', fontSize: '11px', fontWeight: 600,
      fontFamily: 'var(--font-body)', whiteSpace: 'nowrap', display: 'inline-block',
    }}>
      {cfg.label}
    </span>
  );
}
