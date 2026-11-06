import axios from 'axios'
import { showFailToast } from 'vant'
import { clearSession, getToken } from '../utils/session'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.authentication = token
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body?.code === 1) return body.data
    showFailToast(body?.msg || '操作失败')
    return Promise.reject(new Error(body?.msg || '操作失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      clearSession()
      if (!location.pathname.includes('/login')) location.href = '/login'
      showFailToast('登录已过期')
    } else {
      showFailToast(error.response?.data?.msg || '网络请求失败')
    }
    return Promise.reject(error)
  }
)

export default http
