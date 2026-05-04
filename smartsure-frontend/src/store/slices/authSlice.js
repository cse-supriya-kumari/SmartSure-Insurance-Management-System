import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  token: localStorage.getItem('token'),
  role: localStorage.getItem('role'),
  userId: localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null,
  isAuthenticated: !!localStorage.getItem('token'),
  name: localStorage.getItem('name'),
  email: localStorage.getItem('email'),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginSuccess: (state, action) => {
      const { token, role, userId, email, name } = action.payload;
      state.token = token;
      state.role = role;
      state.userId = userId;
      state.isAuthenticated = true;
      state.name = name;
      state.email = email;

      localStorage.setItem('token', token);
      localStorage.setItem('role', role);
      localStorage.setItem('userId', String(userId));
      localStorage.setItem('name', name);
      localStorage.setItem('email', email);
    },
    logout: (state) => {
      state.token = null;
      state.role = null;
      state.userId = null;
      state.isAuthenticated = false;
      state.name = null;
      state.email = null;

      ['token', 'role', 'userId', 'name', 'email'].forEach((k) =>
        localStorage.removeItem(k)
      );
    },
  },
});

export const { loginSuccess, logout } = authSlice.actions;
export default authSlice.reducer;
