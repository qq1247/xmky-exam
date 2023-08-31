<template>
	<uni-card class="my-exam-page" :is-full="true" spacing="0">
		<uni-forms ref="formRef" :rules="formRules" :modelValue="form">
			<uni-forms-item label="旧密码" name="oldPwd">
				<uni-easyinput type="password" v-model="form.oldPwd" focus placeholder="请输入旧密码" />
			</uni-forms-item>
			<uni-forms-item label="新密码" name="newPwd">
				<uni-easyinput type="password" v-model="form.newPwd" placeholder="请输入新密码" />
			</uni-forms-item>
		</uni-forms>
		<button type="primary" @click="pwdUpdate">确定</button>
	</uni-card>

</template>

<script lang="ts" setup>
	import { ref, reactive, computed } from 'vue'
	import http from '@/utils/request.js'

	const form = reactive({
		oldPwd: '',
		newPwd: '',
	})
	const formRef = ref()
	const formRules = reactive({
		oldPwd: {
			rules: [{
				required: true,
				errorMessage: '旧密码不能为空'
			}, {
				minLength: 6,
				maxLength: 12,
				errorMessage: '长度介于{minLength}-{maxLength}',
			}]
		},
		newPwd: {
			rules: [{
				required: true,
				errorMessage: '新密码不能为空'
			}, {
				minLength: 6, 
				maxLength: 12,
				errorMessage: '长度介于{minLength}-{maxLength}',
			}]
		}
	})
	
	// 密码修改
	async function pwdUpdate() {
		formRef.value.validate().then(async res => {
			let { code, msg } = await http.post("login/pwd", { ...form })
			if (code !== 200) {
				return
			}
			uni.navigateBack();
		})
	}
</script>

<style lang="scss" scoped>
</style>