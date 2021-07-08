const state = {
  userInfo: localStorage.getItem('userInfo')
    ? localStorage.getItem('userInfo')
    : ''
}
export default state
