import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

// 参数缓存
export const useParmStore = defineStore('parm', () => {
    const sysName = ref('') // 系统名称
    const customTitle = ref('') // 服务支持标题
    const customContent = ref('') // 服务支持内容
    const icp = ref('') // 备案信息
    return { sysName, customTitle, customContent, icp }
}, {
    persist: true
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useParmStore, import.meta.hot))
}
