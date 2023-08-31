import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'
/**
 * 字典存储
 */
export const useDictStore = defineStore('dict', () => {
	// 变量定义
    const dicts = ref([])
	
	// 获取值
	function getValue(dictIndex, dictKey) {// 用两个等号对比，兼容数字
		return dicts.value.find(dict => dict.dictIndex == dictIndex && dict.dictKey == dictKey)?.dictValue
	}
	
	// 获取列表
	function getList(dictIndex) {// 用两个等号对比，兼容数字
		return dicts.value.filter(dict => dict.dictIndex == dictIndex)
	}
	
	return { dicts, getValue, getList }
}, {
	unistorage: true
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useDictStore, import.meta.hot))
}