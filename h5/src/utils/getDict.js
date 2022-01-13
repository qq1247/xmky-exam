/*
 * @Description: 获取数据字典
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-11-15 11:29:43
 * @LastEditors: Che
 * @LastEditTime: 2021-12-27 10:26:33
 */
import { dictListPage } from 'api/base'
import { getDict, setDict } from './storage'

/**
 * @name: getDictList
 * @description: 获取所有类型的参数
 * @param {*}
 * @return {*}
 */
const getDictList = async () => {
  const {
    data: { list: dictList },
  } = await dictListPage({
    curPage: 1,
    pageSize: 100,
  })
  setDict(dictList)
}

/**
 * @name: getOneDict
 * @description: 获取单一类型的参数
 * @param { name } 类型名称
 * @return {*}
 */
const getOneDict = (name) => getDict().filter((item) => item.dictIndex === name)

export { getDictList, getOneDict }
