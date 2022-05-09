import http from '@/utils/http'
import qs from 'qs'

/**
 * @name: request
 * @description: post请求封装
 * @param {*}
 * @return {*}
 */
const request = (
  url,
  params = {},
  timeout = 6000,
  responseType = 'json',
  headers = 'application/x-www-form-urlencoded'
) =>
  http({
    url: url,
    method: 'post',
    data:
      headers === 'application/x-www-form-urlencoded'
        ? qs.stringify(params, { arrayFormat: 'repeat' })
        : params,
    responseType: responseType,
    headers: {
      'Content-Type': headers
    },
    timeout: timeout
  })

export default request
