/*
 * @Description: post封装
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-30 13:36:21
 * @LastEditors: Che
 * @LastEditTime: 2021-08-11 22:51:40
 */
import http from '@/utils/http'
import qs from 'qs'

const request = (
  url,
  params = {},
  responseType = 'json',
  headers = 'application/x-www-form-urlencoded'
) =>
  http({
    url: url,
    method: 'post',
    data:
      headers == 'application/x-www-form-urlencoded'
        ? qs.stringify(params, { arrayFormat: 'repeat' })
        : params,
    responseType: responseType,
    headers: {
      'Content-Type': headers,
    },
  })

export default request
