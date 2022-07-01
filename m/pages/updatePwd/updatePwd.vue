<template>
	<view class="container">
		<u-navbar title="修改密码" :placeholder="true" autoBack :border-bottom="false">
		</u-navbar>

		<view class="content">
			<u--input placeholder="请输入旧密码" prefixIcon="lock" prefixIconStyle="font-size: 22px" v-model="oldPwd">
			</u--input>
			<u--input placeholder="请输入新密码" prefixIcon="lock-open" prefixIconStyle="font-size: 22px" v-model="newPwd">
			</u--input>
			<view class="confirm" @click="updatePwd">修改</view>
		</view>
	</view>
</template>

<script>
	import { loginPwd } from '@/api/common.js'
	import {
		setTitle
	} from '@/common/setTitle'
	export default {
		data() {
			return {
				oldPwd: '',
				newPwd: ''
			};
		},
		
		mounted() {
			setTitle(getApp().globalData.entName)
		},
		
		methods: {
			async updatePwd() {
				if(this.oldPwd === '' || this.newPwd === '') {
					uni.$u.toast('请填写密码！')
					return
				}
				
				const res = await loginPwd({
					oldPwd: this.oldPwd,
					newPwd: this.newPwd
				})
				
				if (res.code !== 200) {
					uni.$u.toast(res.msg)
					return
				}
				
				uni.$u.toast('修改密码成功!')
				setTimeout(() => {
					uni.navigateBack({})
				}, 300);
			}
		}
	}
</script>

<style lang="scss" scoped>
.content {
	background: #fff;
	width: 100%;
	padding: 20rpx;
	.u-input {
		width: 630rpx;
		margin: 40rpx auto 0;
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
	margin: 80rpx auto 0;
}
</style>
