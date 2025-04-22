<template>
	<view class="login">
		<view class="login-head">
			<image class="login-head__bg" src="@/static/img/login-bg.png"></image>
			<view class="login-head__wrap">
				<image class="login-head__logo" :src="`${baseUrl}/login/logo`"></image>
				<text class="login-head__sysname">{{ userStore.user.sysName }}</text>
			</view>
		</view>
		<view class="login-main">
			<uni-forms v-if="loginType == 1" ref="formRef" :model="form" :rules="formRules">
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
				<uni-forms-item>
					<view class="login-main__btn-group">
						<text @click="loginType = 2" class="login-main__switc-btn">临时考试</text>
						<button class="login-main__login" type="primary" @click="login">登录</button>
					</view>
				</uni-forms-item>
			</uni-forms>
			<uni-forms v-if="loginType == 2" ref="anonFormRef" :model="anonForm" :rules="anonFormRules">
				<uni-forms-item name="userName">
					<uni-easyinput
						v-model="anonForm.userName"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入姓名和手机号"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="examName">
					<uni-easyinput
						v-model="anonForm.examName"
						prefixIcon="locked"
						:styles="{ backgroundColor: '#F3F6F9' }"
						clearable
						placeholder="请输入考试名称"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item>
					<view class="login-main__btn-group">
						<text @click="loginType = 1" class="login-main__switc-btn">账号登录</text>
						<button class="login-main__login" type="primary" @click="anonLogin">登录</button>
					</view>
				</uni-forms-item>
			</uni-forms>
		</view>
		<view class="login-foot"></view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { loginIn, loginEnt, loginSysTime, loginNoLogin } from '@/api/login';
import { examExamGet } from '@/api/exam.js';
import { dictIndexList } from '@/api/dict';
import { useUserStore } from '@/stores/user';
import { useDictStore } from '@/stores/dict';
import { myExamGeneratePaper } from '../../api/myExam';

/************************变量定义相关***********************/
const userStore = useUserStore();
const dictStore = useDictStore();
const baseUrl = ref(uni.getStorageSync('BASE_URL'));
const redirectPath = ref('');
const form = reactive({
	loginName: '',
	pwd: ''
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

const anonForm = reactive({
	userName: '',
	examName: ''
});
const anonFormRef = ref();
const anonFormRules = {
	userName: {
		rules: [
			{ required: true, errorMessage: '请输入姓名和手机号' },
			{
				minLength: 2,
				maxLength: 16,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	},
	examName: {
		rules: [{ required: true, errorMessage: '请输入考试名称' }]
	}
};

const loginType = ref(1); // 登录类型（1：账号登录；2：匿名登录）

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
	let { code, data } = await loginIn({ ...form });
	if (code !== 200) {
		return;
	}
	if (data.type !== 1 || data.type !== 4) {
		uni.showToast({ title: '暂不支持管理员登录', icon: 'error' });
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
	if (uni.getStorageSync('redirectPath')) {
		let _redirectPath = uni.getStorageSync('redirectPath'); // 过期登录返回之前页面
		uni.removeStorageSync('redirectPath');
		uni.reLaunch({ url: _redirectPath });
	} else {
		uni.switchTab({ url: '/pages/home/home' });
	}
}

// 登录
async function anonLogin() {
	// 数据校验
	let validate = await anonFormRef.value
		.validate()
		.then(() => true)
		.catch(() => false);
	if (!validate) {
		return;
	}

	let { code: examCode, data: examData } = await examExamGet({
		name: anonForm.examName
	});
	if (examCode !== 200) {
		return;
	}
	if (!examData.id) {
		uni.showModal({ content: '未找到该考试，请重新输入', icon: 'error' });
		return;
	}
	if (examData.loginType === 1) {
		uni.showModal({ content: '企业内部考试，请使用账号密码登录', icon: 'error' });
		return;
	}

	const { data: curTime } = await loginSysTime({});
	if (examData.startTime > curTime) {
		uni.showModal({ content: '考试未开始', icon: 'error' });
		return;
	}
	if (examData.endTime < curTime) {
		uni.showModal({ content: '考试已结束', icon: 'error' });
		return;
	}

	// 用户登录
	let { code, data } = await loginNoLogin({ name: anonForm.userName });
	if (code !== 200) {
		return;
	}
	if (data.type !== 1 && data.type !== 4) {
		uni.showToast({ title: '暂不支持管理员登录', icon: 'error' });
		return;
	}

	// 用户信息缓存
	userStore.user.id = data.userId;
	userStore.user.name = data.userName;
	userStore.user.type = data.type;
	userStore.user.accessToken = data.accessToken;

	// 数据字典缓存
	let { data: dicts } = await dictIndexList();
	dictStore.dicts = dicts;

	// 生成试卷
	const { code: myExamCode } = await myExamGeneratePaper({ examId: examData.id });
	if (myExamCode !== 200) {
		return;
	}

	// 进入试卷页面
	uni.reLaunch({ url: `/pages/myExam/myRead?examId=${examData.id}` });
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
		.login-main__btn-group {
			display: flex;
			flex-direction: column;
			align-items: flex-end;
			.login-main__switc-btn {
				// 包含switch，微信小程序编译不通过
				margin: 0px 10px 0px 0px;
				color: #0d9df6;
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
	}

	.login-foot {
	}
}
</style>
