/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-17 17:50:31
 * @LastEditors: Che
 * @LastEditTime: 2021-12-20 10:50:33
 */
import { getSetting } from '@/utils/storage'

const state = {
  lineTime: 0,
  orgName: getSetting().orgName,
  tabIndex: getSetting().tabIndex,
}

const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
    }
  },
}

const actions = {
  changeSetting({ commit }, data) {
    return new Promise((resolve) => {
      commit('CHANGE_SETTING', data)
      resolve()
    })
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
}
