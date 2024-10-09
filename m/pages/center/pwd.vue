<template>
	<view class="pwd">
		<view class="pwd-head"></view>
		<view class="pwd-main">
			<uni-forms class="pwd-main__form" ref="formRef" :model="form" :rules="formRules">
				<uni-forms-item name="oldPwd">
					<uni-easyinput v-model="form.oldPwd" type="password" prefixIcon="locked" :styles="{ backgroundColor: '#F3F6F9' }" focus clearable placeholder="请输入旧密码" />
				</uni-forms-item>
				<uni-forms-item name="newPwd">
					<uni-easyinput v-model="form.newPwd" type="password" prefixIcon="locked" :styles="{ backgroundColor: '#F3F6F9' }" focus clearable placeholder="请输入新密码" />
				</uni-forms-item>
			</uni-forms>
			<button class="pwd-main__confirm" type="primary" @click="pwd">确定</button>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { loginPwd } from '@/api/login';

/************************变量定义相关***********************/
const form = reactive({
	oldPwd: '',
	newPwd: ''
});
const formRef = ref();
const formRules = reactive({
	oldPwd: {
		rules: [
			{
				required: true,
				errorMessage: '旧密码不能为空'
			},
			{
				minLength: 6,
				maxLength: 12,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	},
	newPwd: {
		rules: [
			{
				required: true,
				errorMessage: '新密码不能为空'
			},
			{
				minLength: 6,
				maxLength: 12,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	}
});

// 密码修改
async function pwd() {
	formRef.value.validate().then(async (res) => {
		// 数据校验
		let validate = await formRef.value
			.validate()
			.then(() => true)
			.catch(() => false);
		if (!validate) {
			return;
		}

		// 密码修改
		let { code, msg } = await loginPwd({ ...form });
		if (code !== 200) {
			return;
		}
		uni.navigateBack();
	});
}
</script>

<style lang="scss" scoped>
.pwd {
	height: inherit;
	.pwd-head {
		height: 100rpx;
	}
	.pwd-main {
		padding: 0rpx 60rpx;
		:deep(.is-input-border) {
			border-width: 0rpx;
			border-bottom-width: 1rpx;
			border-radius: 0rpx;
		}
		.pwd-main__confirm {
			margin-top: 60rpx;
			width: 628rpx;
			height: 100rpx;
			line-height: 100rpx;
			border-radius: 50px;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}
}
</style>
