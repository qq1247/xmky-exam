import http from "@/util/http"; // 导入http中创建的axios实例
import qs from "qs"; // 根据需求是否导入qs模块

async function post(url, params = {}) {
  return await http.post(
    url,
    qs.stringify(params, { arrayFormat: "repeat" })
  );
}

export default {
  // 数据字典
  dictListpage(params) {
    return post("dict/list", params);
  },
  dictList(params) {
    return post("dict/list", params);
  },
  dictAdd(params) {
    return post("dict/add", params);
  },
  dictEdit(params) {
    return post("dict/edit", params);
  },
  dictDel(params) {
    return post("dict/del", params);
  },
  dictGet(params) {
    return post("dict/get", params);
  },

    // 组织机构相关
    orgListpage(params) {
      return post("org/list", params);
    },
    orgTreeList(params) {
      return post("org/treeList", params);
    },
    orgAdd(params) {
      return post("org/add", params);
    },
    orgEdit(params) {
      return post("org/edit", params);
    },
    orgDel(params) {
      return post("org/del", params);
    },
    orgGet(params) {
      return post("org/get", params);
    },

  // 试题相关
  questionList(params) {
    return post("question/list", params);
  },

  questionAdd(params) {
    return post("question/add", params);
  },

  questionGet(params) {
    return post("question/get", params);
  },

  // 登陆相关
  login(params) {
    return post("login/in", params);
  },
};
