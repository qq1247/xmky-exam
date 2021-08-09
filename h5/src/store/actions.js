import * as types from './mutation-types'
const actions = {
  setUserInfo({ commit }, payload) {
    commit(types.SET_USER_INFO, payload)
  },
  delUserInfo({ commit }) {
    commit(types.DEL_USER_INFO)
  },
}

export default actions
