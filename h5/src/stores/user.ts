import { ref, computed } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

/**
 * 用户储存
 */
export const useUserStore = defineStore('user', () => {
    const id = ref(0) // ID
    const name = ref('') // 姓名
    const headFileId = ref(0)
    const type = ref(0)  // 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
    const accessToken = ref('') // 访问令牌

    return { id, name, headFileId, type, accessToken }
}, {
    persist: {
        enabled: true
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}