import api from './axios';

export const getDashboard = () => api.get('/api/admin/reports/dashboard');
export const getReports = () => api.get('/api/admin/reports');
export const getAdminPendingClaims = () => api.get('/api/admin/claims/pending');
export const getAdminClaimById = (claimId) => api.get(`/api/admin/claims/${claimId}`);
export const getAdminClaimDocuments = (claimId) => api.get(`/api/admin/claims/${claimId}/documents`);
export const reviewClaim = (claimId, data) => api.put(`/api/admin/claims/${claimId}/review`, data);
export const getAllAdminUsers = () => api.get('/api/admin/users');
export const getAdminUserById = (id) => api.get(`/api/admin/users/${id}`);
export const updateAdminUserStatus = (id, data) => api.put(`/api/admin/users/${id}/status`, data);
export const createPolicyProduct = (data) => api.post('/api/admin/policies', data);
export const updatePolicyProduct = (id, data) => api.put(`/api/admin/policies/${id}`, data);
export const deletePolicyProduct = (id) => api.delete(`/api/admin/policies/${id}`);
