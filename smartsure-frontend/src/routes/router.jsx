import React, { Suspense } from 'react';
import { createBrowserRouter, Navigate, useLocation } from 'react-router-dom';
import { PageLoader } from '../shared/components/PageLoader';
import PublicLayout from '../layouts/PublicLayout';
import MainLayout from '../layouts/MainLayout';
import { useAppSelector } from '../store';

const LandingPage       = React.lazy(() => import('../features/landing/LandingPage'));
const LoginPage         = React.lazy(() => import('../features/auth/LoginPage'));
const RegisterPage      = React.lazy(() => import('../features/auth/RegisterPage'));
const CustomerDashboard = React.lazy(() => import('../features/customer/dashboard/CustomerDashboard'));
const BrowsePolicies    = React.lazy(() => import('../features/customer/policies/BrowsePolicies'));
const PolicyDetail      = React.lazy(() => import('../features/customer/policies/PolicyDetail'));
const MyPolicies        = React.lazy(() => import('../features/customer/policies/MyPolicies'));
const MyClaims          = React.lazy(() => import('../features/customer/claims/MyClaims'));
const FileClaim         = React.lazy(() => import('../features/customer/claims/FileClaim'));
const ClaimDetail       = React.lazy(() => import('../features/customer/claims/ClaimDetail'));
const AdminDashboard    = React.lazy(() => import('../features/admin/dashboard/AdminDashboard'));
const ClaimsManagement  = React.lazy(() => import('../features/admin/claims/ClaimsManagement'));
const ClaimReview       = React.lazy(() => import('../features/admin/claims/ClaimReview'));
const PolicyProducts    = React.lazy(() => import('../features/admin/policies/PolicyProducts'));
const UserManagement    = React.lazy(() => import('../features/admin/users/UserManagement'));
const NotFound          = React.lazy(() => import('../features/errors/NotFound'));
const Unauthorized      = React.lazy(() => import('../features/errors/Unauthorized'));
const ErrorPage         = React.lazy(() => import('../features/errors/ErrorPage'));

const wrap = (el) => <Suspense fallback={<PageLoader />}>{el}</Suspense>;

function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAppSelector((s) => s.auth);
  const location = useLocation();
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;
  return children;
}

function AdminRoute({ children }) {
  const { isAuthenticated, role } = useAppSelector((s) => s.auth);
  const location = useLocation();
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;
  if (role !== 'ROLE_ADMIN') return <Navigate to="/unauthorized" replace />;
  return children;
}

function GuestRoute({ children }) {
  const { isAuthenticated } = useAppSelector((s) => s.auth);
  if (isAuthenticated) return <Navigate to="/dashboard" replace />;
  return children;
}

export const router = createBrowserRouter([
  {
    path: '/',
    element: <PublicLayout />,
    errorElement: wrap(<ErrorPage />),
    children: [
      { index: true, element: wrap(<LandingPage />) },
      { path: 'login',    element: wrap(<GuestRoute>{wrap(<LoginPage />)}</GuestRoute>) },
      { path: 'register', element: wrap(<GuestRoute>{wrap(<RegisterPage />)}</GuestRoute>) },
    ],
  },
  {
    path: '/',
    element: <ProtectedRoute><MainLayout /></ProtectedRoute>,
    errorElement: wrap(<ErrorPage />),
    children: [
      { path: 'dashboard',    element: wrap(<CustomerDashboard />) },
      { path: 'policies',     element: wrap(<BrowsePolicies />) },
      { path: 'policies/:id', element: wrap(<PolicyDetail />) },
      { path: 'my-policies',  element: wrap(<MyPolicies />) },
      { path: 'claims',       element: wrap(<MyClaims />) },
      { path: 'claims/file',  element: wrap(<FileClaim />) },
      { path: 'claims/:id',   element: wrap(<ClaimDetail />) },
    ],
  },
  {
    path: '/admin',
    element: <AdminRoute><MainLayout /></AdminRoute>,
    errorElement: wrap(<ErrorPage />),
    children: [
      { path: 'dashboard', element: wrap(<AdminDashboard />) },
      { path: 'claims',    element: wrap(<ClaimsManagement />) },
      { path: 'claims/:id',element: wrap(<ClaimReview />) },
      { path: 'policies',  element: wrap(<PolicyProducts />) },
      { path: 'users',     element: wrap(<UserManagement />) },
    ],
  },
  { path: '/unauthorized', element: wrap(<Unauthorized />) },
  { path: '*',             element: wrap(<NotFound />) },
]);
