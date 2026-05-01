import React from 'react';

export default function SkeletonRow({ count = 4, height = 64 }) {
  return (
    <>
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="skeleton-row" style={{ height: `${height}px` }} />
      ))}
    </>
  );
}
