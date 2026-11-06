import http from './http'

export const login = (data) => http.post('/admin/operator/login', data)

export const getDashboard = () => Promise.all([
  http.get('/admin/workspace/businessData'),
  http.get('/admin/workspace/overviewOrders'),
  http.get('/admin/workspace/overviewEquipments'),
  http.get('/admin/workspace/overviewEquipmentPackages')
])

export const getCategories = (type) => http.get('/admin/category/list', { params: { type } })

export const getEquipmentPage = (params) => http.get('/admin/equipment/page', { params })
export const getEquipmentDetail = (id) => http.get(`/admin/equipment/${id}`)
export const saveEquipment = (data) => http.post('/admin/equipment', data)
export const updateEquipment = (data) => http.put('/admin/equipment', data)
export const deleteEquipment = (ids) => http.delete('/admin/equipment', { params: { ids: ids.join(',') } })
export const setEquipmentStatus = (id, status) => http.post(`/admin/equipment/status/${status}`, null, { params: { id } })

export const getPackagePage = (params) => http.get('/admin/package/page', { params })
export const getPackageDetail = (id) => http.get(`/admin/package/${id}`)
export const savePackage = (data) => http.post('/admin/package', data)
export const updatePackage = (data) => http.put('/admin/package', data)
export const deletePackage = (ids) => http.delete('/admin/package', { params: { ids: ids.join(',') } })
export const setPackageStatus = (id, status) => http.post(`/admin/package/status/${status}`, null, { params: { id } })

export const getOrders = (params) => http.get('/admin/order/conditionSearch', { params })
export const getOrderDetail = (id) => http.get(`/admin/order/details/${id}`)
export const confirmOrder = (id) => http.put('/admin/order/confirm', { id })
export const deliverOrder = (id) => http.put(`/admin/order/delivery/${id}`)
export const completeOrder = (id) => http.put(`/admin/order/complete/${id}`)
export const cancelOrder = (id, cancelReason) => http.put('/admin/order/cancel', { id, cancelReason })
