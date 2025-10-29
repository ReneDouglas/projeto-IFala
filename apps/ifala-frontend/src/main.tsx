import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { App } from './App.tsx';
declare global {
  interface Window {
    grecaptcha?: {
      ready?: (cb: () => void) => void;
      execute: (
        siteKey: string,
        options?: { action?: string },
      ) => Promise<string>;
      reset?: () => void;
    };
  }
}
export {};

const siteKey = import.meta.env.VITE_RECAPTCHA_SITE_KEY;
if (siteKey) {
  const script = document.createElement('script');
  script.src = `https://www.google.com/recaptcha/api.js?render=${siteKey}`;
  script.async = true;
  document.head.appendChild(script);
} else {
  console.error('VITE_RECAPTCHA_SITE_KEY não está definida no ambiente.');
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
