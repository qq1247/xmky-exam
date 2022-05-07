const InfoKey = 'Login-Info'
const SettingKey = 'Setting-Info'
const DictKey = 'Dict-Info'
const QuickKey = 'Quick-Info'

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

// 用户信息
export const getInfo = () => getStorage(InfoKey)
export const setInfo = (data) => setStorage(InfoKey, data)
export const removeInfo = () => removeStorage(InfoKey)

// 系统字段
export const getSetting = () => getStorage(SettingKey)
export const setSetting = (data) => setStorage(SettingKey, data)

// 数据字典
export const getDict = () => getStorage(DictKey)
export const setDict = (data) => setStorage(DictKey, data)
export const removeDict = () => removeStorage(DictKey)

// 快速考试
export const getQuick = () => getStorage(QuickKey)
export const setQuick = (data) => setStorage(QuickKey, data)
export const removeQuick = () => removeStorage(QuickKey)
