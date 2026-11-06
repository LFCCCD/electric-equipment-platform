import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
    proxy: {
      '/admin': 'http://localhost:8080',
      '/user': 'http://localhost:8080',
      '/media': 'http://localhost:8080'
    }
  }
})
