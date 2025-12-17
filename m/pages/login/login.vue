<template>
	<view class="login">
		<view class="login-head">
			<image class="login-head__bg" src="@/static/img/login-bg.png"></image>
			<view class="login-head__wrap">
				<image class="login-head__logo" :src="`${baseUrl}/login/logo`"></image>
				<text class="login-head__sysname">{{ parmStore.sysName }}</text>
			</view>
		</view>
		<view class="login-main">
			<uni-forms v-if="loginType == 1" ref="formRef" :model="form" :rules="formRules">
				<uni-forms-item name="loginName">
					<uni-easyinput
						v-model="form.loginName"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9', color: '#333' }"
						placeholder="请输入账号"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="pwd">
					<uni-easyinput
						v-model="form.pwd"
						type="password"
						prefixIcon="locked"
						:styles="{ backgroundColor: '#F3F6F9', color: '#333' }"
						clearable
						placeholder="请输入密码"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item>
					<view class="login-main__btn-group">
						<view>
							<text v-if="parmStore.userRegist === 1" @click="loginType = 3" class="login-main__switc-btn">用户注册</text>
							<text @click="loginType = 2" class="login-main__switc-btn">临时登录</text>
						</view>
						<button class="login-main__login" type="primary" @click="login">登录</button>
					</view>
				</uni-forms-item>
			</uni-forms>
			<uni-forms v-if="loginType == 2" ref="tempFormRef" :model="tempForm" :rules="tempFormRules">
				<uni-forms-item name="userName">
					<uni-easyinput
						v-model="tempForm.userName"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入姓名和手机号"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="examName">
					<uni-easyinput
						v-model="tempForm.examName"
						prefixIcon="locked"
						:styles="{ backgroundColor: '#F3F6F9' }"
						clearable
						placeholder="请输入考试名称"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item>
					<view class="login-main__btn-group">
						<view>
							<text v-if="parmStore.userRegist === 1" @click="loginType = 3" class="login-main__switc-btn">用户注册</text>
							<text @click="loginType = 1" class="login-main__switc-btn">账号登录</text>
						</view>
						<button class="login-main__login" type="primary" @click="tempIn">登录</button>
					</view>
				</uni-forms-item>
			</uni-forms>
			<uni-forms v-if="loginType == 3" ref="userRegistFormRef" :model="userRegistForm" :rules="userRegistFormRules">
				<uni-forms-item name="loginName">
					<uni-easyinput
						v-model="userRegistForm.loginName"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入登录账号"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="pwd">
					<uni-easyinput
						v-model="userRegistForm.pwd"
						type="password"
						prefixIcon="locked"
						:styles="{ backgroundColor: '#F3F6F9', color: '#333' }"
						clearable
						placeholder="请输入登录密码"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="name">
					<uni-easyinput
						v-model="userRegistForm.name"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入姓名"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item name="orgCode">
					<uni-easyinput
						v-model="userRegistForm.orgCode"
						prefixIcon="person"
						:focus="true"
						:styles="{ backgroundColor: '#F3F6F9' }"
						placeholder="请输入机构代码"
						class="login-main__input"
					/>
				</uni-forms-item>
				<uni-forms-item>
					<view class="login-main__btn-group">
						<view>
							<text v-if="parmStore.userRegist === 1" @click="loginType = 3" class="login-main__switc-btn">用户注册</text>
							<text @click="loginType = 1" class="login-main__switc-btn">账号登录</text>
						</view>
						<button class="login-main__login" type="primary" @click="userRegist">注册</button>
					</view>
				</uni-forms-item>
			</uni-forms>
		</view>
		<view class="login-foot">
			<rich-text :nodes="`<div style='text-align: center;'>${parmStore.icp}</div>`" class="copyright" @tap="" />
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { loginIn, loginSysTime, loginTempIn, loginParm, loginEncrypt, loginUserRegist } from '@/api/login';
import { examExamGet } from '@/api/exam';
import { dictIndexList } from '@/api/dict';
import { useUserStore } from '@/stores/user';
import { useDictStore } from '@/stores/dict';
import { useParmStore } from '@/stores/parm';
import { myExamGeneratePaper } from '@/api/my-exam';
import { escape2Html } from '@/util/htmlUtil';
// #ifdef MP-WEIXIN
import WxmpRsa from 'wxmp-rsa';
// #endif
// #ifdef H5
import forge from 'node-forge';
// #endif

/************************变量定义相关***********************/
const userStore = useUserStore();
const parmStore = useParmStore();
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

const tempForm = reactive({
	userName: '',
	examName: ''
});
const tempFormRef = ref();
const tempFormRules = {
	userName: {
		rules: [
			{ required: true, errorMessage: '请输入姓名和手机号' },
			{
				minLength: 2,
				maxLength: 18,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	},
	examName: {
		rules: [{ required: true, errorMessage: '请输入考试名称' }]
	}
};

const userRegistForm = reactive({
	// 表单
	loginName: '',
	pwd: '',
	name: '',
	orgCode: ''
});
const userRegistFormRef = ref(); // 表单引用
const userRegistFormRules = {
	loginName: {
		rules: [
			{ required: true, errorMessage: '请输入登录账号' },
			{
				minLength: 2,
				maxLength: 18,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	},
	pwd: {
		rules: [
			{ required: true, errorMessage: '请输入登录密码' },
			{
				minLength: 4,
				maxLength: 16,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	},
	name: {
		rules: [
			{ required: true, errorMessage: '请输入姓名' },
			{
				minLength: 2,
				maxLength: 8,
				errorMessage: '长度介于{minLength}-{maxLength}'
			}
		]
	},
	orgCode: {
		rules: [
			{ required: true, errorMessage: '请输入机构代码' },
			{
				minLength: 8,
				maxLength: 8,
				errorMessage: '长度等于8'
			}
		]
	}
};

const loginType = ref(1); // 登录类型（1：账号登录；2：匿名登录）

/************************组件生命周期相关*********************/
onLoad(async (option) => {
	redirectPath.value = option.redirectPath;

	let { data } = await loginParm({});
	parmStore.sysName = data.sysName;
	parmStore.customTitle = data.customTitle;
	parmStore.customContent = data.customContent.replaceAll('\n', '<br/>');
	parmStore.icp = escape2Html(data.icp || '');
	parmStore.userRegist = data.userRegist;
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

	const { code: _code, data: _encrypt } = await loginEncrypt({ loginName: form.loginName });
	if (_code !== 200) {
		return;
	}

	let encryptedPwd = null;
	try {
		// #ifdef H5
		const base64Key = _encrypt.publicKey;
		const lines = base64Key.match(/.{1,64}/g) || [];
		const pemKey = `-----BEGIN PUBLIC KEY-----\n${lines.join('\n')}\n-----END PUBLIC KEY-----`;
		const publicKey = forge.pki.publicKeyFromPem(pemKey);

		const encryptedBytes = publicKey.encrypt(forge.util.encodeUtf8(`${_encrypt.nonce}:${form.pwd}`), 'RSAES-PKCS1-V1_5');
		encryptedPwd = forge.util.encode64(encryptedBytes);
		// #endif

		// #ifdef MP-WEIXIN
		const rsa = new WxmpRsa();
		rsa.setPublicKey(_encrypt.publicKey);
		encryptedPwd = rsa.encryptLong(`${_encrypt.nonce}:${form.pwd}`);
		// #endif
	} catch (error) {
		uni.showToast({ title: '生成秘钥失败', icon: 'error' });
		return;
	}

	// 用户登录
	let { code, data } = await loginIn({
		loginName: form.loginName,
		pwd: encryptedPwd
	});
	if (code !== 200) {
		return;
	}

	// 用户信息保存
	userStore.id = data.userId;
	userStore.name = data.userName;
	userStore.role = data.role;
	userStore.accessToken = data.accessToken;
	userStore.refreshToken = data.refreshToken;
	userStore.avatarFileId = data.avatarFileId;

	// 数据字典保存
	let { data: dicts } = await dictIndexList({});
	dictStore.dicts = dicts;

	// 进入相关页面
	if (userStore.isAdmin() || userStore.isSubAdmin()) {
		uni.redirectTo({ url: '/pages/admin/question-bank/question-bank' });
	} else if (userStore.role === 'EXAM_USER') {
		uni.redirectTo({ url: '/pages/exam-user/home/home' });
	} else if (userStore.role === 'MARK_USER') {
		uni.redirectTo({ url: '/pages/mark-user/my-mark/my-mark' });
	}
}

// 临时登录
async function tempIn() {
	// 数据校验
	let validate = await tempFormRef.value
		.validate()
		.then(() => true)
		.catch(() => false);
	if (!validate) {
		return;
	}

	let { code: examCode, data: examData } = await examExamGet({
		name: tempForm.examName
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
	let { code, data } = await loginTempIn({ name: tempForm.userName });
	if (code !== 200) {
		return;
	}

	// 用户信息缓存
	userStore.id = data.userId;
	userStore.name = data.userName;
	userStore.role = data.role;
	userStore.accessToken = data.accessToken;
	userStore.refreshToken = data.refreshToken;

	// 数据字典缓存
	let { data: dicts } = await dictIndexList({});
	dictStore.dicts = dicts;

	// 生成试卷
	const { code: myExamCode } = await myExamGeneratePaper({ examId: examData.id });
	if (myExamCode !== 200) {
		return;
	}

	// 进入试卷页面
	uni.reLaunch({ url: `/pages/exam-user/my-exam/my-read?examId=${examData.id}` });
}

// 登录
async function userRegist() {
	// 数据校验
	let validate = await userRegistFormRef.value
		.validate()
		.then(() => true)
		.catch(() => false);
	if (!validate) {
		return;
	}

	const { code } = await loginUserRegist({ ...userRegistForm });
	if (code !== 200) {
		return;
	}

	uni.showModal({ content: '注册成功，请等待管理员审核', icon: 'success' });

	userRegistForm.loginName = ''; // 成功后清空，防止多次误点
	userRegistForm.name = '';
	userRegistForm.orgCode = '';
	userRegistForm.pwd = '';
}
</script>
<style lang="scss" scoped>
.login {
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh);
	// #endif

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
		flex: 1;
		display: flex;
		justify-content: center;
		align-items: flex-end;
		padding: 20rpx 0rpx;
		font-size: 22rpx;
		color: #888;

		:deep(a) {
			color: #888;
			text-decoration: none;

			&:hover,
			&:active {
				text-decoration: underline;
			}
		}
	}
}
</style>
