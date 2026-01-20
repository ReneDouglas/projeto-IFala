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
  script.defer = true;
  script.onerror = () => {
    console.error('❌ Erro ao carregar o script do reCAPTCHA');
  };
  document.head.appendChild(script);
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
