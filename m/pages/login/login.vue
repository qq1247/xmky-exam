<template>
	<view class="login">
		<view class="login-logo">
			<image :src="`/login/logo`"></image>
			<text>{{ entName }}</text>
		</view>
		<uni-forms ref="formRef" :model="form" :rules="formRules">
			<uni-forms-item name="loginName">
				<uni-easyinput v-model="form.loginName" prefixIcon="person" :focus="true" placeholder="请输入账号" />
			</uni-forms-item>
			<uni-forms-item name="pwd">
				<uni-easyinput v-model="form.pwd" type="password" prefixIcon="locked" clearable placeholder="请输入密码" />
			</uni-forms-item>
		</uni-forms>
		<button class="uni-btn" type="primary" @click="login">登录</button>
	</view>
</template>

<script lang="ts" setup>
	import { ref, reactive } from "vue"
	import { onLoad } from "@dcloudio/uni-app"
	import http from "@/utils/request"
	import { useUserStore } from "@/stores/user"
	import { useDictStore } from "@/stores/dict"

	// 定义变量
	const userStore = useUserStore()
	const entName = ref('')// 系统名称
	const form = reactive({// 表单
		loginName: '',// 账号
		pwd: '',// 密码
	})
	const formRef = ref()// 表单引用
	const formRules = {// 表单校验规则
		loginName: {
			rules: [{ required: true, errorMessage: '请输入账号', }]
		},
		pwd: {
			rules: [{ required: true, errorMessage: '请输入密码', }]
		},
	}

	// 页面加载完毕，执行如下方法
	onLoad(async () => {
		let { data: { name } } = await http.post('login/ent')
		entName.value = name
	})

	// 登录
	async function login() {
		// 数据校验
		let validate = await formRef.value.validate().then(() => true).catch(() => false)
		if (!validate) {
			return
		}
		
		// 用户登录
		let { code, data } = await http.post('login/in', { ...form }, {})
		if (code != 200) {
			return
		}
		
		// 令牌缓存
		userStore.id = data.userId
		userStore.name = data.userName
		userStore.roles = data.roles
		userStore.accessToken = data.accessToken
		
		// 数据字典缓存
		let { data: { list } } = await http.post('dict/listpage', { pageSize: 100 })
		const dictStore = useDictStore()
		dictStore.dicts = list
	
		// 跳转到首页
		uni.switchTab({ url:'/pages/index/index' })
	}
</script>

<style lang="scss" scoped>
	.login {
		display: flex;
		flex-direction: column;
		height: 100%;
		background: white;
		padding: 60rpx;

		:deep(.is-input-border) {
			border: initial;
			border-bottom: 1rpx solid #F0F0F0;
			border-radius: 0rpx;
		}

		.login-logo {
			display: flex;
			justify-content: center;
			align-items: center;
			margin-bottom: 60rpx;

			image {
				width: 65rpx;
				height: 65rpx;
			}

			uni-text {
				padding-left: 20rpx;
				font-size: 20px;
				// font-weight: bold;
			}
		}
	}
</style>