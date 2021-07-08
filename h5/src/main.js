import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementUI from 'element-ui'
import CKEditor from '@ckeditor/ckeditor5-vue2'
import api from '@/api'
import * as tools from '@/util/tools.js'
Vue.use(ElementUI)
Vue.use(CKEditor)

import '@/assets/style/normalize.css'
import '@/assets/style/theme/index.css'
import '@/assets/style/element-ui-ex.scss'

Vue.prototype.$https = api
Vue.prototype.$tools = tools
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
