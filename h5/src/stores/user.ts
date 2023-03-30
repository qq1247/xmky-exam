import { ref, computed } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

/**
 * 用户储存
 */
export const useUserStore = defineStore('user', () => {
    const id = ref(0)
    const name = ref('')
    const headFileId = ref(0)
    const roles = ref([] as string[])
    const accessToken = ref('')

    return { id, name, headFileId, roles, accessToken }
}, {
    persist: {
        enabled: true
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}