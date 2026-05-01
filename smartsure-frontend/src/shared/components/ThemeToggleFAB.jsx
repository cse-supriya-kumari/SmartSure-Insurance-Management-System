import React from 'react';
import { Sun, Moon } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '../../store';
import { toggleTheme } from '../../store/slices/themeSlice';

export default function ThemeToggleFAB() {
  const dispatch = useAppDispatch();
  const theme = useAppSelector((s) => s.theme.theme);
  return (
    <button
      className="theme-fab"
      onClick={() => dispatch(toggleTheme())}
      aria-label={theme === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}
    >
      {theme === 'light'
        ? <Moon size={20} style={{ color: 'var(--text-secondary)' }} />
        : <Sun size={20} style={{ color: 'var(--amber)' }} />}
    </button>
  );
}
