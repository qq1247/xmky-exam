import { ref } from 'vue'
import { defineStore, acceptHMRUpdate } from 'pinia'

export const useUserStore = defineStore('user', () => {
    const id = ref(0)
    const name = ref('')
    const headFileId = ref(0)
    const roles = ref([]) 
    const accessToken = ref('')
    return { id, name, headFileId, roles, accessToken }
}, {
	unistorage: true
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}