<template>
	<view class="no-login">
		<view class="no-login-head">
			<image class="no-login-head__bg" src="@/static/img/login-bg.png"></image>
			<view class="no-login-head__wrap">
				<image class="no-login-head__logo" :src="`${baseUrl}/login/logo`"></image>
				<text class="no-login-head__sysname">{{ userStore.user.sysName }}</text>
			</view>
		</view>
		<view class="no-login-main">
			<uni-forms ref="formRef" :model="form" :rules="formRules">
				<uni-forms-item name="name">
					<uni-easyinput
						v-model="form.name"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入姓名和手机号"
						class="no-login-main__input"
					/>
				</uni-forms-item>
			</uni-forms>
			<button class="no-login-main__login" type="primary" @click="login">确认</button>
		</view>
		<view class="no-login-foot"></view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { loginNoLogin, loginEnt } from '@/api/login';
import { dictIndexList } from '@/api/dict';
import { useUserStore } from '@/stores/user';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
const userStore = useUserStore();
const dictStore = useDictStore();
const baseUrl = uni.getStorageSync('BASE_URL');
const redirectPath = ref('');
const form = reactive({
	name: ''
});
const formRef = ref();
const formRules = {
	name: {
		rules: [
			{ required: true, errorMessage: '请输入姓名和手机号' },
			{
				minLength: 2,
				maxLength: 16,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	}
};

/************************组件生命周期相关*********************/
onLoad(async (option) => {
	redirectPath.value = option.redirectPath;

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
	let { code, data } = await loginNoLogin({ ...form });
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
	uni.reLaunch({
		url: redirectPath.value
	});
}
</script>
<style lang="scss" scoped>
.no-login {
	height: inherit;
	display: flex;
	flex-direction: column;
	.no-login-head {
		display: flex;
		height: 480rpx;

		.no-login-head__bg {
			position: absolute;
			width: 750rpx;
			height: 480rpx;
		}

		.no-login-head__wrap {
			flex: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			z-index: 1;
			.no-login-head__logo {
				margin: 40rpx;
				padding: 18rpx;
				width: 155rpx;
				height: 155rpx;
				border: 10rpx solid #22bdf7;
				border-radius: 50%;
				box-shadow: 0rpx 0rpx 0rpx 10rpx #02a0f6;
			}
			.no-login-head__sysname {
				font-size: 36rpx;
				color: #fff;
			}
		}
	}
	.no-login-main {
		padding: 0rpx 60rpx;
		:deep(.is-input-border) {
			border-width: 0rpx;
			border-bottom-width: 1rpx;
			border-radius: 0rpx;
		}
		.no-login-main__login {
			margin-top: 60rpx;
			width: 628rpx;
			height: 100rpx;
			line-height: 100rpx;
			border-radius: 50px;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}

	.no-login-foot {
	}
}
</style>
