/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 13:23:33
 * @LastEditors: Che
 * @LastEditTime: 2021-08-24 11:36:10
 */
const InfoKey = 'Login-Info'
const DriverKey = 'Question-Driver'

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

//
export const getDriver = () => getStorage(DriverKey)
export const setDriver = (data) => setStorage(DriverKey, data)
