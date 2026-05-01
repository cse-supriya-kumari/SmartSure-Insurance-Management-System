import api from './axios';

export const getAllPolicyTypes = () => api.get('/api/policies/types');
export const getPolicyTypeById = (id) => api.get(`/api/policies/types/${id}`);
export const purchasePolicy = (data) =>
  api.post('/api/policies/purchase', data, {
    headers: { 'Idempotency-Key': crypto.randomUUID() },
  });
export const getUserPolicies = (userId) => api.get(`/api/policies/user/${userId}`);
export const getPolicyById = (id) => api.get(`/api/policies/${id}`);
export const getTotalPolicies = () => api.get('/api/policies/count');
export const createPolicyType = (data) => api.post('/api/policies/types', data);
export const updatePolicyType = (id, data) => api.put(`/api/policies/types/${id}`, data);
export const deletePolicyType = (id) => api.delete(`/api/policies/types/${id}`);
