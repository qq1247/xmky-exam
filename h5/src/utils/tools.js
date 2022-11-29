import router from '@/router/index'
import { getOneDict } from '@/utils/getDict'

/**
 * @name: delay
 * @description: 异步倒计时
 * @param { msec } 秒数
 * @return {*}
 */
const delay = (msec = 1) => {
  return new Promise((resolve) => {
    setTimeout(() => resolve(), msec * 1000)
  })
}

/**
 * @name: getQueryParam
 * @description: 获取url指定参数
 * @param { url, name } url地址，参数名称
 * @return {*}
 */
const getQueryParam = (url, name) => {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
  var r = url.slice(url.indexOf('?') + 1).match(reg)
  if (r != null) return decodeURI(r[2])
  return null
}

/**
 * @name: formateTime
 * @description: 格式化时间
 * @param { time } 毫秒数
 * @return { hours, minutes, seconds }  时分秒
 */
const formateTime = (time) => {
  const SECOND = 1000
  const MINUTE = 60 * SECOND
  const HOUR = 60 * MINUTE
  const hours = Math.floor(time / HOUR)
  const minutes = Math.floor((time % HOUR) / MINUTE)
  const seconds = Math.floor((time % MINUTE) / SECOND)

  return {
    hours,
    minutes,
    seconds
  }
}

/**
 * @name: intToChinese
 * @description: 数字转换成汉字
 * @param { value } 数字
 * @return {*}
 */
const intToChinese = (value) => {
  const str = String(value)
  const len = str.length - 1
  const digits = [
    '',
    '十',
    '百',
    '千',
    '万',
    '十',
    '百',
    '千',
    '亿',
    '十',
    '百',
    '千',
    '万',
    '十',
    '百',
    '千',
    '亿'
  ]
  const num = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九']
  return str.replace(/([1-9]|0+)/g, ($, $1, idx, full) => {
    let pos = 0
    if ($1[0] !== '0') {
      pos = len - idx
      if (idx === 0 && $1[0] === 1 && digits[len - idx] === '十') {
        return digits[len - idx]
      }
      return num[$1[0]] + digits[len - idx]
    } else {
      const left = len - idx
      const right = len - idx + $1.length
      if (Math.floor(right / 4) - Math.floor(left / 4) > 0) {
        pos = left - (left % 4)
      }
      if (pos) {
        return digits[pos] + num[$1[0]]
      } else if (idx + $1.length >= len) {
        return ''
      } else {
        return num[$1[0]]
      }
    }
  })
}

/**
 * @name: switchTab
 * @description: 切换tab
 * @param { name, params }
 * @return {*}
 */
const switchTab = (name, params) => {
  router.push({ name, params })
}

/**
 * @name: $tools.computeMinute
 * @description: 计算分钟数
 * @param { startTime, endTime }
 * @return {*}
 */
const computeMinute = (startTime, endTime) => {
  if (!startTime || !endTime) {
    return '--'
  }
  const diffTime = new Date(endTime).getTime() - new Date(startTime).getTime()
  const minutes = Math.ceil(diffTime / (60 * 1000))
  return `${minutes}分钟`
}

/**
 * 获取字典数组
 * 
 * @param {*} index 索引
 * @param {*} key 键
 * @returns 值数组
 */
const getDicts = (index) => {
  return getOneDict(index)
}

/**
 * 获取字典值
 * 
 * @param {*} index 索引
 * @param {*} key 键
 * @returns 值
 */
const getDictValue = (index, key) => {
  return getOneDict(index).find(
    (item) => item.dictKey == key
  )?.dictValue
}

let serverTime = ''
const getServerTime = () => {
  return serverTime
}
const setServerTime = (date) => {
  serverTime = date;
}


export {
  delay,
  switchTab,
  formateTime,
  intToChinese,
  getQueryParam,
  computeMinute,
  getDicts,
  getDictValue,
  setServerTime,
  getServerTime,
}
