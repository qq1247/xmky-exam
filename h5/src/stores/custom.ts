import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

// 自定义缓存
export const useCustomStore = defineStore('custom', () => {
    const title = ref('') // 标题
    const content = ref('') // 内容
    return { title, content }
}, {
    persist: true
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useCustomStore, import.meta.hot))
}
