/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 11:33:30
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:54:30
 */
import { login, loginOrgName } from 'api/common'
import {
  getInfo,
  setInfo,
  removeInfo,
  setSetting,
  getDict,
  removeDict,
} from '@/utils/storage'
import { getDictList } from '@/utils/getDict'
import router, { resetRouter } from '@/router/index'

const state = {
  token: getInfo().accessToken,
  name: getInfo().userName,
  userId: getInfo().userId,
  roles: getInfo().roles,
  onlyRole: getInfo().onlyRole,
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_USER_ID: (state, userId) => {
    state.userId = userId
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  },
  SET_ONLY_ROLE: (state, onlyRole) => {
    state.onlyRole = onlyRole
  },
}

const actions = {
  /**
   * @name: login
   * @description: 登录
   * @param {*} commit
   * @param {*} userInfo
   * @return {*}
   */
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ loginName: username.trim(), pwd: password })
        .then(async (res) => {
          const { data } = res
          const role = data.roles.includes('subAdmin')
            ? ['subAdmin']
            : data.roles
          commit('SET_TOKEN', data.accessToken)
          commit('SET_ROLES', data.roles || ['user'])
          commit('SET_NAME', data.userName)
          commit('SET_USER_ID', data.userId)
          commit('SET_ONLY_ROLE', role)
          const { data: orgName } = await loginOrgName()
          commit(
            'setting/CHANGE_SETTING',
            {
              key: 'orgName',
              value: orgName,
            },
            { root: true }
          )
          setInfo({ onlyRole: role, ...data })
          setSetting({ orgName })
          if (!getDict()) {
            getDictList()
          }
          resolve()
        })
        .catch((error) => {
          reject(error)
        })
    })
  },

  /**
   * @name: resetToken
   * @description: 重置缓存信息
   * @param {*} commit
   * @return {*}
   */
  resetToken({ commit }) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', '')
      commit('SET_ROLES', [])
      commit('SET_ONLY_ROLE', [])
      commit('SET_ONLY_ROLE', [])
      commit('permission/SET_ROUTES', [], { root: true })
      commit(
        'setting/CHANGE_SETTING',
        {
          key: 'tabIndex',
          value: '1',
        },
        { root: true }
      )
      removeInfo()
      removeDict()
      resetRouter()
      resolve()
    })
  },

  /**
   * @name: changeRoles
   * @description: 更新roles
   * @param {*} commit
   * @param {*} dispatch
   * @param {*} role
   * @return {*}
   */
  async changeRoles({ commit, dispatch }, role) {
    const token = role + '-token'

    commit('SET_TOKEN', token)
    setToken(token)

    const { roles } = await dispatch('getInfo')

    resetRouter()

    // generate accessible routes map based on roles
    const accessRoutes = await dispatch('permission/generateRoutes', roles, {
      root: true,
    })
    // dynamically add accessible routes
    router.addRoutes(accessRoutes)
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
}
