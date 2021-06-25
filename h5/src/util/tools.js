import Vue from 'vue'
import { Message } from 'element-ui'
const message = (msg, type = 'success', date = 1500) => {
  Message({
    message: msg,
    duration: date,
    type: type
  })
}

const resetData = (el, name) => {
  Object.assign(el.$data[name], el.$options.data()[name])
}

export { message, resetData }
