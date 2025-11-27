import { ref } from 'vue';
import { defineStore, acceptHMRUpdate } from 'pinia';

export const useUserStore = defineStore(
	'user',
	() => {
		const id = ref(0); // ID
		const name = ref(''); // 姓名
		const headFileId = ref(0);
		const role = ref(''); // 角色（ADMIN：管理员；EXAM_USER：考试用户；SUB_ADMIN：子管理员；MARK_USER：阅卷用户；TEMP_USER：临时用户）
		const accessToken = ref(''); // 访问令牌
		const refreshToken = ref(''); // 刷新令牌

		function isAdmin() {
			return role.value === 'ADMIN';
		}
		function isExamUser() {
			return role.value === 'EXAM_USER';
		}
		function isSubAdmin() {
			return role.value === 'SUB_ADMIN';
		}
		function isMarkUser() {
			return role.value === 'MARK_USER';
		}
		function isTempUser() {
			return role.value === 'TEMP_USER';
		}

		function reset() {
			id.value = null;
			name.value = '';
			headFileId.value = null;
			role.value = '';
			accessToken.value = '';
			refreshToken.value = '';
		}
		return { id, name, headFileId, role, accessToken, refreshToken, isAdmin, isExamUser, isSubAdmin, isMarkUser, isTempUser, reset };
	},
	{
		unistorage: true
	}
);

if (import.meta.hot) {
	import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot));
}
