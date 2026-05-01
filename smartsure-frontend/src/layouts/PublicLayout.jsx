import React from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from '../shared/components/Navbar';

export default function PublicLayout() {
  return (
    <div>
      <Navbar variant="public" />
      <div style={{ paddingTop: 'var(--navbar-height)' }}>
        <Outlet />
      </div>
    </div>
  );
}
