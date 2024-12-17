import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000, 
    open: true,
    proxy: {
      '/spring': {
        target: 'http://localhost:8080', // Spring Boot backend port
        secure: false,
        changeOrigin: true,
      },
    },
  },
})
