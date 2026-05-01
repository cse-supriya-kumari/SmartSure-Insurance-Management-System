import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  token: localStorage.getItem('token'),
  role: localStorage.getItem('role'),
  userId: localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null,
  isAuthenticated: !!localStorage.getItem('token'),
  firstName: localStorage.getItem('firstName'),
  lastName: localStorage.getItem('lastName'),
  email: localStorage.getItem('email'),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginSuccess: (state, action) => {
      const { token, role, userId, email, firstName, lastName } = action.payload;
      state.token = token;
      state.role = role;
      state.userId = userId;
      state.isAuthenticated = true;
      state.firstName = firstName;
      state.lastName = lastName;
      state.email = email;

      localStorage.setItem('token', token);
      localStorage.setItem('role', role);
      localStorage.setItem('userId', String(userId));
      localStorage.setItem('firstName', firstName);
      localStorage.setItem('lastName', lastName);
      localStorage.setItem('email', email);
    },
    logout: (state) => {
      state.token = null;
      state.role = null;
      state.userId = null;
      state.isAuthenticated = false;
      state.firstName = null;
      state.lastName = null;
      state.email = null;

      ['token', 'role', 'userId', 'firstName', 'lastName', 'email'].forEach((k) =>
        localStorage.removeItem(k)
      );
    },
  },
});

export const { loginSuccess, logout } = authSlice.actions;
export default authSlice.reducer;
