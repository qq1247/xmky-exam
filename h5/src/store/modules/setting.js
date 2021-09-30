/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-17 17:50:31
 * @LastEditors: Che
 * @LastEditTime: 2021-09-28 17:27:04
 */
import { getOrg } from '@/utils/storage'

const state = {
  hideHeader: false,
  hideFooter: false,
  lineTime: 0,
  orgLogo: getOrg().orgLogo,
  orgName: getOrg().orgName,
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
