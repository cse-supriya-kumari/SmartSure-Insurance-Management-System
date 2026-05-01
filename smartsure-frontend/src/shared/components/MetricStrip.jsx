import React from 'react';

export default function MetricStrip({ items }) {
  return (
    <div className="metric-strip">
      {items.map((item, i) => (
        <div key={i} className="metric-item">
          <div className="metric-number" style={item.color ? { color: item.color } : {}}>
            {item.value}
          </div>
          <div className="metric-label">{item.label}</div>
        </div>
      ))}
    </div>
  );
}
