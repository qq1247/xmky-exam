/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-27 17:31:01
 * @LastEditors: Che
 * @LastEditTime: 2021-09-14 17:47:31
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

export { resetData, delay, getQueryParam }
