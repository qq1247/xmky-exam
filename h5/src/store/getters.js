const getters = {
  name: (state) => state.user.name,
  token: (state) => state.user.token,
  roles: (state) => state.user.roles,
  userId: (state) => state.user.userId,
  onlyRole: (state) => state.user.onlyRole,
  userAvatar: (state) => state.user.userAvatar,
  entName: (state) => state.setting.entName,
  permission_routes: (state) => state.permission.routes
}
export default getters
