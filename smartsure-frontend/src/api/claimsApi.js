import api from './axios';

export const initiateClaim = (data) => api.post('/api/claims/initiate', data);

export const uploadDocument = (claimId, file) => {
  const formData = new FormData();
  formData.append('claimId', String(claimId));
  formData.append('file', file);
  return api.post('/api/claims/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

export const getClaimStatus = (claimId) => api.get(`/api/claims/status/${claimId}`);
export const getUserClaims = (userId) => api.get(`/api/claims/user/${userId}`);
export const getPendingClaims = () => api.get('/api/claims/pending');
export const updateClaimStatus = (claimId, status) =>
  api.put(`/api/claims/${claimId}/status`, null, { params: { status } });
export const getClaimDocuments = (claimId) => api.get(`/api/claims/${claimId}/documents`);
export const getTotalClaims = () => api.get('/api/claims/count');
export const getPendingClaimsCount = () => api.get('/api/claims/count/pending');
