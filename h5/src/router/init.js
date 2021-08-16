/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-12 10:17:05
 * @LastEditors: Che
 * @LastEditTime: 2021-08-16 10:24:49
 */

import router, { constantRoutes } from './index'
import store from '@/store/index'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getInfo } from '@/utils/storage'
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  document.title = getPageTitle(to.meta.title)

  const hasToken = getInfo().accessToken

  if (hasToken) {
    if (store.state.permission.routes.length > constantRoutes.length) {
      next()
      NProgress.done()
    } else {
      try {
        const roles = store.state.user.roles
        const accessRoutes = await store.dispatch(
          'permission/generateRoutes',
          roles
        )
        router.addRoutes(accessRoutes)
        next({ ...to, replace: true })
      } catch (error) {
        await store.dispatch('user/resetToken')
        Message.error(error || 'Has Error')
        next(`/login?redirect=${to.path}`)
        NProgress.done()
      }
    }
  } else {
    store.dispatch('permission/generateRoutes', [])
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

router.onError((error) => {
  const pattern = /Loading chunk (\d)+ failed/g
  const isChunkLoadFailed = error.message.match(pattern)
  const targetPath = router.history.pending.fullPath
  if (isChunkLoadFailed) {
    router.replace(targetPath)
  }
})
