import request from './request'

/**
 * @name: userListPage
 * @description: 用户列表信息
 * @param {*}
 * @return {*}
 */
export const userListPage = (params) => request('user/listpage', params)

/**
 * @name: userGet
 * @description: 获取用户信息
 * @param {*}
 * @return {*}
 */
export const userGet = (params) => request('user/get', params)

/**
 * @name: userAdd
 * @description: 添加用户信息
 * @param {*}
 * @return {*}
 */
export const userAdd = (params) => request('user/add', params)

/**
 * @name: userEdit
 * @description: 修改用户信息
 * @param {*}
 * @return {*}
 */
export const userEdit = (params) => request('user/edit', params)

/**
 * @name: userDel
 * @description: 删除用户信息
 * @param {*}
 * @return {*}
 */
export const userDel = (params) => request('user/del', params)

/**
 * @name: userPwdInit
 * @description: 初始用户密码
 * @param {*}
 * @return {*}
 */
export const userPwdInit = (params) => request('user/pwdInit', params)

/**
 * @name: userRole
 * @description: 权限变为子管理员
 * @param {*}
 * @return {*}
 */
export const userRole = (params) => request('user/role', params)

/**
 * @name: userOut
 * @description: 强制下线
 * @param {*}
 * @return {*}
 */
export const userOut = (params) => request('user/out', params)

/**
 * @name: userTemplate
 * @description: 用户模板
 * @param {*}
 * @return {*}
 */
export const userTemplate = (params, type) =>
  request('user/template', params, undefined, type)

/**
 * @name: userImport
 * @description: 用户导入
 * @param {*}
 * @return {*}
 */
export const userImport = (params) => request('user/import', params)

/**
 * @name: userFrozen
 * @description: 用户冻结
 * @param {*}
 * @return {*}
 */
export const userFrozen = (params) => request('user/frozen', params)
