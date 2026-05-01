import React, { useEffect } from 'react';
import { RouterProvider } from 'react-router-dom';
import { useAppSelector } from './store';
import { router } from './routes/router';
import ThemeToggleFAB from './shared/components/ThemeToggleFAB';
import { ToastProvider } from './shared/components/Toast';

export default function App() {
  const theme = useAppSelector((s) => s.theme.theme);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }, [theme]);

  return (
    <ToastProvider>
      <RouterProvider router={router} />
      <ThemeToggleFAB />
    </ToastProvider>
  );
}
