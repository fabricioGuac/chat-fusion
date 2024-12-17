import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/auth': {
        target: 'http://localhost:8080',
        secure: false,
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080',
        secure: false,
        changeOrigin: true,
      },
    },
  },
})
