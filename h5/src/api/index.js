import http from "@/util/http" // 导入http中创建的axios实例
import qs from "qs" // 根据需求是否导入qs模块

async function post(url, params = {}) {
  return await http.post(url, qs.stringify(params, { arrayFormat: "repeat" }))
}

export default {
  // 数据字典
  dictListpage(params) {
    return post("dict/list", params)
  },
  dictList(params) {
    return post("dict/list", params)
  },
  dictGet(params) {
    return post("dict/get", params)
  },
  dictAdd(params) {
    return post("dict/add", params)
  },
  dictEdit(params) {
    return post("dict/edit", params)
  },
  dictDel(params) {
    return post("dict/del", params)
  },

  // 组织机构相关
  orgListpage(params) {
    return post("org/list", params)
  },
  orgTreeList(params) {
    return post("org/treeList", params)
  },
  orgGet(params) {
    return post("org/get", params)
  },
  orgAdd(params) {
    return post("org/add", params)
  },
  orgEdit(params) {
    return post("org/edit", params)
  },
  orgDel(params) {
    return post("org/del", params)
  },

  // 试题相关
  questionList(params) {
    return post("question/list", params)
  },
  questionGet(params) {
    return post("question/get", params)
  },
  questionAdd(params) {
    return post("question/add", params)
  },
  questionEdit(params) {
    return post("question/edit", params)
  },
  questionDel(params) {
    return post("question/del", params)
  },

  // 登陆相关
  login(params) {
    return post("login/in", params)
  }
}
