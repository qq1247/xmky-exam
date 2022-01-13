/*
 * @Description: 路由配置
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-11-22 09:46:17
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:53:54
 */
import Vue from 'vue'
import VueRouter from 'vue-router'
import constantRoutes from './constant'

// vue-router内部错误,没有进行catch处理
const originalPush = VueRouter.prototype.push
const originalReplace = VueRouter.prototype.replace
// push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject)
  }
  return originalPush.call(this, location).catch((err) => err)
}
// replace
VueRouter.prototype.replace = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalReplace.call(this, location, onResolve, onReject)
  }
  return originalReplace.call(this, location).catch((err) => err)
}

Vue.use(VueRouter)

const createRouter = () =>
  new VueRouter({
    mode: 'history',
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  })

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
