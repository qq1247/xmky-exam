import Vue from 'vue'
import { Message } from 'element-ui'
const message = (msg, type = 'success', date = 1500) => {
  Message({
    message: msg,
    duration: date,
    type: type,
  })
}

const resetData = (el, name) => {
  const $data = el.$data[name]
  const data = el.$options.data()[name]
  for (const attrName in $data) {
    if (attrName !== 'rules') {
      $data[attrName] = data[attrName]
    }
  }
}

export { message, resetData }
