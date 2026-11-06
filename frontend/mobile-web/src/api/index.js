import http from './http'

export const login = (data) => http.post('/user/user/demo-login', data)
export const getCategories = (type) => http.get('/user/category/list', { params: { type } })
export const getEquipmentList = (categoryId) => http.get('/user/equipment/list', { params: { categoryId } })
export const getEquipmentDetail = (id) => http.get(`/user/equipment/${id}`)
export const getPackageList = (categoryId) => http.get('/user/package/list', { params: { categoryId } })
export const getPackageDetail = (id) => http.get(`/user/package/${id}`)
export const getPackageEquipment = (id) => http.get(`/user/package/equipment/${id}`)

export const getCart = () => http.get('/user/shoppingCart/list')
export const addCart = (data) => http.post('/user/shoppingCart/add', data)
export const subtractCart = (data) => http.post('/user/shoppingCart/sub', data)
export const cleanCart = () => http.delete('/user/shoppingCart/clean')

export const getAddresses = () => http.get('/user/deliveryAddress/list')
export const getDefaultAddress = () => http.get('/user/deliveryAddress/default')
export const saveAddress = (data) => http.post('/user/deliveryAddress', data)
export const updateAddress = (data) => http.put('/user/deliveryAddress', data)
export const setDefaultAddress = (id) => http.put('/user/deliveryAddress/default', { id })
export const deleteAddress = (id) => http.delete('/user/deliveryAddress', { params: { id } })

export const submitOrder = (data) => http.post('/user/order/submit', data)
export const payOrder = (orderNumber) => http.put('/user/order/payment', { orderNumber })
export const getOrders = (params) => http.get('/user/order/historyOrders', { params })
export const getOrderDetail = (id) => http.get(`/user/order/orderDetail/${id}`)
export const completeOrder = (id) => http.put(`/user/order/complete/${id}`)
