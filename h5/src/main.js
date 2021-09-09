/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-27 17:31:01
 * @LastEditors: Che
 * @LastEditTime: 2021-08-12 18:08:53
 */
import Vue from 'vue'

import 'normalize.css/normalize.css'
import 'assets/style/element-variables.scss'
import ElementUI from 'element-ui'
import CKEditor from '@ckeditor/ckeditor5-vue2'
Vue.use(ElementUI)
Vue.use(CKEditor)

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
  render: (h) => h(App),
}).$mount('#app')
