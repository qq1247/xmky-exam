<template>
	<view class="container">
		<u-navbar title="我的设置" :placeholder="true" autoBack :border-bottom="false">
		</u-navbar>
		
		<div class="cell">
			<u-cell icon="setting" title="修改密码" :isLink="true" arrow-direction="right" :border="false" url="/pages/updatePwd/updatePwd"></u-cell>
		</div>
		
		<view class="confirm" @click="loginOut">退出</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			};
		},
		methods: {
			loginOut() {
				uni.clearStorageSync()
				uni.navigateTo({
					url: '/pages/login/login'
				})
			}
		}
	}
</script>

<style lang="scss"scoped>
.cell {
	width: 100%;
	background: #fff;
	margin-top: 16rpx;
}

.confirm {
	position: fixed;
	bottom: 40rpx;
	left: 60rpx;
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
</style>
