<template>
	<view class="my">
		<uni-list>
			<uni-list-item :title="userStore.name" note="暂无描述" thumb="/static/user.png" thumb-size="lg" />
			<uni-list-item title="修改密码" link showArrow to="/pages/my/pwd" />
			<uni-list-item title="公告列表" link showArrow to="/pages/bulletin/bulletin" />
			<uni-list-item title="退出登录" link showArrow clickable @click="out" />
		</uni-list>
	</view>
</template>

<script lang="ts" setup>
	import http from '@/utils/request.js'
	import { useUserStore } from "@/stores/user"
	
	// 定义变量
	const userStore = useUserStore()
	
	// 退出登录
	async function out() {
		let { code } = await http.post('login/out', {})
		if (code !== 200) {
			return
		}
		uni.redirectTo({
			url: '/pages/login/login'
		})
	}
</script>

<style lang="scss" scoped>
	.my {
		.my-head {
			height: 200rpx;
			background-color: $uni-primary;
		}
	}
</style>