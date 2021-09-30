/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-27 17:31:01
 * @LastEditors: Che
 * @LastEditTime: 2021-09-29 14:34:46
 */
const resetData = (el, name) => {
  const $data = el.$data[name]
  const data = el.$options.data()[name]
  for (const attrName in $data) {
    if (attrName !== 'rules') {
      $data[attrName] = data[attrName]
    }
  }
}

const delay = (msec = 1) => {
  return new Promise((resolve) => {
    setTimeout(() => resolve(), msec * 1000)
  })
}

const getQueryParam = (url, name) => {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
  var r = url.slice(url.indexOf('?') + 1).match(reg)
  if (r != null) return decodeURI(r[2])
  return null
}

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
    seconds,
  }
}

export { resetData, delay, getQueryParam, formateTime }
