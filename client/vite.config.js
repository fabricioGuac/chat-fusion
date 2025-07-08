import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
// Uses process.env because the vite config runs at node level
const backendUrl = process.env.VITE_API_URL || 'http://localhost:8080'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/auth': {
        target: backendUrl,
        secure: false,
        changeOrigin: true,
      },
      '/api': {
        target: backendUrl,
        secure: false,
        changeOrigin: true,
      },
      '/actuator': {
        target: backendUrl,
        secure: false,
        changeOrigin: true,
      },
    },
  },
})
