/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 13:23:33
 * @LastEditors: Che
 * @LastEditTime: 2021-08-13 13:22:15
 */
const InfoKey = 'Login-Info'
const asyncKey = 'Async-Routes'
const finallyKey = 'Finally-Routes'

const getStorage = (key) => {
  const value =
    (localStorage.getItem(key) && JSON.parse(localStorage.getItem(key))) || ''
  return value
}

const setStorage = (key, data) => {
  return localStorage.setItem(key, JSON.stringify(data))
}

const removeStorage = (key) => {
  return localStorage.removeItem(key)
}

export const getInfo = () => getStorage(InfoKey)
export const setInfo = (data) => setStorage(InfoKey, data)
export const removeInfo = () => removeStorage(InfoKey)

export const getAsyncRoutes = () => getStorage(asyncKey)
export const setAsyncRoutes = (data) => setStorage(asyncKey, data)
export const removeAsyncRoutes = () => removeStorage(asyncKey)

export const getFinallyRoutes = () => getStorage(finallyKey)
export const setFinallyRoutes = (data) => setStorage(finallyKey, data)
export const removeFinallyRoutes = () => removeStorage(finallyKey)
