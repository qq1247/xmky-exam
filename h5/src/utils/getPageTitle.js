import store from '@/store/index'
const title = store.state.setting.orgName || '在线考试'

/**
 * @name: getPageTitle
 * @description: 获取页面title
 * @param {*} pageTitle
 * @return {*}
 */
export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
