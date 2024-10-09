import { ref } from 'vue';
import { defineStore, acceptHMRUpdate } from 'pinia';

export const useUserStore = defineStore(
	'user',
	() => {
		const user = ref({
			id: 0,
			name: '',
			type: 0,
			accessToken: '',
			sysName: ''
		});
		return { user };
	},
	{
		unistorage: true
	}
);

if (import.meta.hot) {
	import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
