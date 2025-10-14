<template>
	<view class="xmky-empty">
		<image src="@/static/img/list-blank.png" class="xmky-empty-img"></image>
		<view class="xmky-empty-txt">暂无数据</view>
	</view>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
	.xmky-empty {
		height: 100%;
		width: 100%;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		.xmky-empty-img {
			height: 280rpx;
			width: 280rpx;
		}
		.xmky-empty-txt {
			margin-top: 20rpx;
			color: #999;
		}
	}
</style>
