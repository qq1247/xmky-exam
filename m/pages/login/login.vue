<template>
	<view class="container">

		<u-navbar :left-icon-size="0" :placeholder="true" :border-bottom="false"></u-navbar>

		<image :src="logoImg" class="logo"></image>

		<view class="box">
			<view class="input_box">
				<view class="left">账号</view>
				<u-input v-model="account" type="text" placeholder="请输入账号的" height="80" />
			</view>
			<view class="input_box">
				<view class="left">密码</view>
				<u-input v-model="password" type="text" placeholder="请输入密码" height="80" />
			</view>
			<view class="confirm" @click="login">登录</view>
		</view>
	</view>
</template>

<script>
	import {
		login,
		getSysName
	} from '@/api/common.js'
	import {
		setTitle
	} from '@/common/setTitle'
	export default {
		data() {
			return {
				account: '', // test
				password: '', // 111111
				agreementStatus: false,
				sendCodeState: true,
				logoImg: '/api/login/entLogo'
			};
		},
		mounted() {
			this.getSysName()
		},
		methods: {
			//登录
			login() {
				login({
					loginName: this.account,
					pwd: this.password
				}).then((res) => {
					uni.setStorageSync("accessToken", res.data.accessToken);
					uni.setStorageSync("userInfo", res.data);
					setTimeout(() => {
						uni.switchTab({
							url:'/pages/mine/mine'
						})
					}, 300);
				})
			},
			getSysName() {
				getSysName().then(res => {
					getApp().globalData.entName = res.data
					setTitle(res.data)
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.logo {
		display: block;
		width: 200rpx;
		height: 200rpx;
		margin: 200rpx auto;
		border-radius: 50%;
	}

	.box {
		width: 100%;
		padding: 0rpx 60rpx;
		box-sizing: border-box;

		.input_box {
			width: 630rpx;
			background: #FFFFFF;
			border-radius: 40rpx;
			display: flex;
			align-items: center;
			box-sizing: border-box;
			border: 2rpx solid #0094e5;
			padding: 0rpx 20rpx;
			margin-bottom: 40rpx;

			.left {
				color: #000000;
				font-size: 28rpx;
				font-weight: 400;
				margin-left: 40rpx;
				margin-right: 52rpx;
			}
		}

		.confirm {
			width: 630rpx;
			height: 80rpx;
			background: linear-gradient(45deg, rgba(#0094e5, .4) 30%, #0094e5 100%);
			border-radius: 40rpx;
			text-align: center;
			line-height: 80rpx;
			color: #FFFFFF;
			font-size: 32rpx;
			font-weight: 400;
		}
	}

	.u-border {
		border-width: 0 !important;
	}
</style>
