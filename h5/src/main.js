import Vue from 'vue'

import 'normalize.css/normalize.css'
import 'assets/style/element-variables.scss'
import ElementUI from 'element-ui'
Vue.use(ElementUI)

import 'assets/style/index.scss'

import * as tools from '@/utils/tools.js'
Vue.prototype.$tools = tools
Vue.config.productionTip = false

import App from './App.vue'
import router from './router'
import store from './store'

import './router/init'

new Vue({
  router,
  store,
  render: (h) => h(App)
}).$mount('#app')
