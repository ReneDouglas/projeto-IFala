import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    proxy: {
      '/api': {
        target: 'http://ifala-backend:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    // Increase warning limit to avoid noisy warnings for slightly large bundles.
    // Adjust as needed if you want stricter limits.
    chunkSizeWarningLimit: 700, // in kB
    rollupOptions: {
      output: {
        // Manual chunking to split large vendor libraries into separate files.
        // This helps with caching and reduces single large bundles.
        manualChunks(id: string) {
          if (id.includes('node_modules')) {
            if (id.includes('react') || id.includes('react-dom')) return 'vendor_react';
            if (id.includes('@mui') || id.includes('@material-ui')) return 'vendor_mui';
            if (id.includes('@emotion')) return 'vendor_emotion';
            if (id.includes('react-router-dom')) return 'vendor_router';
            if (id.includes('lodash')) return 'vendor_lodash';
            // default vendor chunk for other node_modules
            return 'vendor_misc';
          }
        },
      },
    },
  },
});
