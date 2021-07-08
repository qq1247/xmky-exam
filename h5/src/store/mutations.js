import * as types from './mutation-types'

const mutations = {
  [types.SET_USER_INFO](state, userInfo) {
    state.userInfo = userInfo
    localStorage.setItem('userInfo', userInfo)
  },
  [types.DEL_USER_INFO](state) {
    state.userInfo = ''
    localStorage.removeItem('userInfo')
  }
}

export default mutations
