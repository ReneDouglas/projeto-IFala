import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
<<<<<<< HEAD
  /*
 
  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
=======
  server: {
    host: true,
    proxy: {
      '/api': {
        target: 'http://ifala-backend:8080',
>>>>>>> 77fd83da062b91bb1fbe91741f69f3468c93fcb8
        changeOrigin: true,
      },
    },
  },
<<<<<<< HEAD
  */
=======
>>>>>>> 77fd83da062b91bb1fbe91741f69f3468c93fcb8
});
