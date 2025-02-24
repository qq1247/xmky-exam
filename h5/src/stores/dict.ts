import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'
import { type Dict } from '@/ts/sys/dict'

// 字典缓存
export const useDictStore = defineStore('dict', () => {
    const dicts = ref([] as Dict[])
    function getValue(dictIndex: string, dictKey: string | number): string | undefined {// 用两个等号对比，兼容数字
        return dicts.value.find((dict) =>
            dict.dictIndex == dictIndex && dict.dictKey == dictKey)?.dictValue
    }
    function getList(dictIndex: string): Dict[] {
        return dicts.value.filter((dict) => dict.dictIndex == dictIndex)
    }
    return { dicts, getValue, getList }
},
    {
        persist: true,
    },
)



if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useDictStore, import.meta.hot))
}

