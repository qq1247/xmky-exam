import http from "@/util/http" // 导入http中创建的axios实例
import qs from "qs" // 根据需求是否导入qs模块

async function post(url, params = {}) {
  const res = await http.post(
    url,
    qs.stringify(params, { arrayFormat: "repeat" })
  )
  return res.data
}

export default {
  // 数据字典
  dictList(params) {
    return post("dict/list", params)
  },

  // 试题相关
  questionList(params) {
    return post("question/list", params)
  },

  questionAdd(params) {
    return post("question/add", params)
  },

  questionGet(params) {
    return post("question/get", params)
  },

  // 登陆相关
  login(params) {
    return post("login/in", params)
  }
}
