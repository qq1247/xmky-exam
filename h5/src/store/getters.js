/*
 * @Description: store getters
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-11 11:33:30
 * @LastEditors: Che
 * @LastEditTime: 2021-09-14 15:09:07
 */
const getters = {
  token: (state) => state.user.token,
  userId: (state) => state.user.userId,
  name: (state) => state.user.name,
  roles: (state) => state.user.roles,
  orgName: (state) => state.setting.orgName,
  permission_routes: (state) => state.permission.routes,
}
export default getters
