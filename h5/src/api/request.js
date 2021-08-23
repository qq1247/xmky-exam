/*
 * @Description: post封装
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-30 13:36:21
 * @LastEditors: Che
 * @LastEditTime: 2021-08-20 16:38:04
 */
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
