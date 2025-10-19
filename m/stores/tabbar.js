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
		const examUser = ref([
			{ pagePath: '/pages/exam-user/home/home', text: '首页', icon: 'icon-icon-home' },
			{ pagePath: '/pages/exam-user/my-exer/my-exer', text: '练习', icon: 'icon-icon-pencil' },
			{ pagePath: '/pages/exam-user/my-exam/my-exam', text: '考试', icon: 'icon-icon-pen' },
			{ pagePath: '/pages/center/center', text: '个人中心', icon: 'icon-icon-people' }
		]);
		const markUser = ref([
			{ pagePath: '/pages/mark-user/my-mark/my-mark', text: '阅卷', icon: 'icon-icon-pencil' },
			{ pagePath: '/pages/center/center', text: '个人中心', icon: 'icon-icon-people' }
		]);

		return { admin, examUser, markUser };
	},
	{
		unistorage: true
	}
);

if (import.meta.hot) {
	import.meta.hot.accept(acceptHMRUpdate(useTabbarStore, import.meta.hot));
}
