/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-12 10:25:32
 * @LastEditors: Che
 * @LastEditTime: 2021-08-12 10:28:53
 */

const title = '在线考试'

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
