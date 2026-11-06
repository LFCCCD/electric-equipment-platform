const TOKEN_KEY = 'equipment_admin_token'
const USER_KEY = 'equipment_admin_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function getAdminUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || '{}')
  } catch {
    return {}
  }
}

export function saveSession(data) {
  localStorage.setItem(TOKEN_KEY, data.token)
  localStorage.setItem(USER_KEY, JSON.stringify({
    id: data.id,
    name: data.name,
    userName: data.userName
  }))
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
