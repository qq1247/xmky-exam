import { ref } from 'vue';
import { defineStore, acceptHMRUpdate } from 'pinia';

export const useTabbarStore = defineStore(
	'tabbar',
	() => {
		const admin = ref([
			{ pagePath: '/pages/admin/question-bank/question-bank', text: '题库', icon: 'icon-icon-top_01' },
			{ pagePath: '/pages/admin/exer/exer', text: '练习', icon: 'icon-icon-pencil' },
			{ pagePath: '/pages/admin/exam/exam', text: '考试', icon: 'icon-icon-pen' },
			{ pagePath: '/pages/admin/user/user', text: '机构用户', icon: 'icon-liebiao-06' },
			{ pagePath: '/pages/center/center', text: '个人中心', icon: 'icon-icon-people' }
		]);

		return { admin };
	},
	{
		unistorage: true
	}
);

if (import.meta.hot) {
	import.meta.hot.accept(acceptHMRUpdate(useTabbarStore, import.meta.hot));
}
