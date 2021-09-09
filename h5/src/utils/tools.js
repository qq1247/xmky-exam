/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-27 17:31:01
 * @LastEditors: Che
 * @LastEditTime: 2021-08-19 16:55:02
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

export { resetData, delay }
