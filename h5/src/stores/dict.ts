import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

/**
 * 字典储存
 * 
 */
export const useDictStore = defineStore('dict', {
    state: () => {
        return {
            dicts: ref([] as Dict[])
        }
    },
    getters: {
        getValue: (state) => {// 用两个等号对比，兼容数字
            return (dictIndex: String, dictKey: String | Number) => state.dicts.find((dict) =>
                dict.dictIndex == dictIndex && dict.dictKey == dictKey)?.dictValue
        },
        getList: (state) => {
            return (dictIndex: String) => state.dicts.filter((dict) => dict.dictIndex == dictIndex)
        },
    },
    persist: {
        enabled: true
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useDictStore, import.meta.hot))
}

/**
 * 字典接口
 */
interface Dict {
    dictIndex: string// 字典索引
    dictKey: string// 字典键
    dictValue: string// 字典值
    no: number// 排序
}