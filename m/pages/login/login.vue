<template>
	<view class="login">
		<view class="login-head">
			<image class="login-head__bg" src="@/static/img/login-bg.png"></image>
			<view class="login-head__wrap">
				<image class="login-head__logo" :src="`${baseURL}/login/logo`"></image>
				<text class="login-head__sysname">{{ userStore.user.sysName }}</text>
			</view>
		</view>
		<view class="login-main">
			<uni-forms ref="formRef" :model="form" :rules="formRules">
				<uni-forms-item name="loginName">
					<uni-easyinput
						v-model="form.loginName"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入账号"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="pwd">
					<uni-easyinput
						v-model="form.pwd"
						type="password"
						prefixIcon="locked"
						:styles="{ backgroundColor: '#F3F6F9' }"
						clearable
						placeholder="请输入密码"
						class="login-main__input"
					/>
				</uni-forms-item>
			</uni-forms>
			<button class="login-main__login" type="primary" @click="login">登录</button>
		</view>
		<view class="login-foot"></view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { loginIn, loginEnt } from '@/api/login';
import { dictIndexList } from '@/api/dict';
import { useUserStore } from '@/stores/user';
import { useDictStore } from '@/stores/dict';
import { baseURL } from '@/static/config';

/************************变量定义相关***********************/
const userStore = useUserStore();
const dictStore = useDictStore();
const form = reactive({
	loginName: 'test1',
	pwd: '111111'
});
const formRef = ref();
const formRules = {
	loginName: {
		rules: [{ required: true, errorMessage: '请输入账号' }]
	},
	pwd: {
		rules: [{ required: true, errorMessage: '请输入密码' }]
	}
};

/************************组件生命周期相关*********************/
onLoad(async () => {
	let {
		data: { name }
	} = await loginEnt();

	userStore.user.sysName = name;
});

/************************事件相关*****************************/
// 登录
async function login() {
	// 数据校验
	let validate = await formRef.value
		.validate()
		.then(() => true)
		.catch(() => false);
	if (!validate) {
		return;
	}

	// 用户登录
	let { code, data } = await loginIn({ ...form });
	if (code !== 200) {
		return;
	}

	// 用户信息保存
	userStore.user.id = data.userId;
	userStore.user.name = data.userName;
	userStore.user.type = data.type;
	userStore.user.accessToken = data.accessToken;

	// 数据字典保存
	let { data: dicts } = await dictIndexList();
	dictStore.dicts = dicts;

	// 进入相关页面
	let redirectPath = uni.getStorageSync('redirectPath');
	if (redirectPath) {
		uni.removeStorageSync('redirectPath');
		uni.reLaunch({
			url: redirectPath
		});
	} else {
		uni.switchTab({ url: '/pages/home/home' });
	}
}
</script>
<style lang="scss" scoped>
.login {
	height: inherit;
	display: flex;
	flex-direction: column;
	.login-head {
		display: flex;
		height: 480rpx;

		.login-head__bg {
			position: absolute;
			width: 750rpx;
			height: 480rpx;
		}

		.login-head__wrap {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			z-index: 1;
			.login-head__logo {
				margin: 40rpx;
				padding: 18rpx;
				width: 155rpx;
				height: 155rpx;
				border: 10rpx solid #22bdf7;
				border-radius: 50%;
				box-shadow: 0rpx 0rpx 0rpx 10rpx #02a0f6;
			}
			.login-head__sysname {
				font-size: 36rpx;
				color: #fff;
			}
		}
	}
	.login-main {
		padding: 0rpx 60rpx;
		:deep(.is-input-border) {
			border-width: 0rpx;
			border-bottom-width: 1rpx;
			border-radius: 0rpx;
		}
		.login-main__login {
			margin-top: 60rpx;
			width: 628rpx;
			height: 100rpx;
			line-height: 100rpx;
			border-radius: 50px;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}

	.login-foot {
	}
}
</style>
