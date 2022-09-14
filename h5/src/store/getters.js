const getters = {
  name: (state) => state.user.name,
  token: (state) => state.user.token,
  roles: (state) => state.user.roles,
  userId: (state) => state.user.userId,
  onlyRole: (state) => state.user.onlyRole,
  userAvatar: (state) => state.user.userAvatar,
  entName: (state) => state.setting.entName,
  permission_routes: (state) => state.permission.routes,
  subjective: (state) => {
    state.paperQuestion.every(pq => {
      return pq.markType === 1;
    });
  },
}
export default getters
