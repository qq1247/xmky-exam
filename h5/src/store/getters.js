const getters = {
  token: (state) => state.user.token,
  userId: (state) => state.user.userId,
  name: (state) => state.user.name,
  roles: (state) => state.user.roles,
  permission_routes: (state) => state.permission.routes,
}
export default getters
