/*
 * @Description: store getters
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 11:33:30
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:54:11
 */
const getters = {
  name: (state) => state.user.name,
  token: (state) => state.user.token,
  roles: (state) => state.user.roles,
  userId: (state) => state.user.userId,
  onlyRole: (state) => state.user.onlyRole,
  orgName: (state) => state.setting.orgName,
  tabIndex: (state) => state.setting.tabIndex,
  permission_routes: (state) => state.permission.routes,
}
export default getters
