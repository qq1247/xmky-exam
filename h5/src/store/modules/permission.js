/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 11:33:30
 * @LastEditors: Che
 * @LastEditTime: 2021-11-19 14:10:08
 */
import { manageRoutes, businessRoutes, constantRoutes } from '@/router/index'

/**
 * @name: hasPermission
 * @description: 是否具有权限
 * @param {*} roles
 * @param {*} route
 * @return {*}
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some((role) => route.meta.roles.includes(role))
  } else {
    return true
  }
}

/**
 * @name: filterAsyncRoutes
 * @description: 过滤权限
 * @param {*} routes
 * @param {*} roles
 * @return {*}
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []

  routes.forEach((route) => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [],
  addRoutes: [],
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  },
}

const actions = {
  generateRoutes({ commit }, roles) {
    return new Promise((resolve) => {
      let accessedRoutes
      if (roles.includes('admin')) {
        accessedRoutes = manageRoutes || []
      } else {
        accessedRoutes =
          roles.length === 0 ? [] : filterAsyncRoutes(businessRoutes, roles)
      }
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
}
