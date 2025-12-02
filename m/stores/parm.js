import { ref } from 'vue';
import { defineStore, acceptHMRUpdate } from 'pinia';

export const useParmStore = defineStore(
	'parm',
	() => {
		const sysName = ref(''); // 系统名称
		const customTitle = ref(''); // 服务支持标题
		const customContent = ref(''); // 服务支持内容
		const icp = ref(''); // 备案信息
		const userRegist = ref(2); // 用户注册
		return { sysName, customTitle, customContent, icp, userRegist };
	},
	{
		unistorage: true
	}
);

if (import.meta.hot) {
	import.meta.hot.accept(acceptHMRUpdate(useParmStore, import.meta.hot));
}
