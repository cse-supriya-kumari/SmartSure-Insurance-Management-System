import api from './axios';

export const register = (data) => api.post('/api/auth/register', data);
export const login = (data) => api.post('/api/auth/login', data);
export const validateToken = () => api.get('/api/auth/validate');
export const getAllUsers = () => api.get('/api/auth/users');
export const getUserById = (id) => api.get(`/api/auth/users/${id}`);
export const updateUserStatus = (id, data) => api.put(`/api/auth/users/${id}/status`, data);
export const getTotalUsers = () => api.get('/api/auth/users/count');
