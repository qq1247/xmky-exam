const getters = {
  name: (state) => state.user.name,
  token: (state) => state.user.token,
  roles: (state) => state.user.roles,
  userId: (state) => state.user.userId,
  onlyRole: (state) => state.user.onlyRole,
  orgName: (state) => state.user.orgName,
  userAvatar: (state) => state.user.userAvatar,
  permission_routes: (state) => state.permission.routes
}
export default getters
