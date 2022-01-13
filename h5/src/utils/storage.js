/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 13:23:33
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:55:40
 */
const InfoKey = 'Login-Info'
const SettingKey = 'Setting-Info'
const DictKey = 'Dict-Info'

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

export const getSetting = () => getStorage(SettingKey)
export const setSetting = (data) => setStorage(SettingKey, data)

export const getDict = () => getStorage(DictKey)
export const setDict = (data) => setStorage(DictKey, data)
export const removeDict = () => removeStorage(DictKey)
