<template>
	<view class="xm-empty">
		<image src="@/static/img/list-blank.png" class="xm-empty-img"></image>
		<view class="xm-empty-txt">暂无数据</view>
	</view>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
	.xm-empty {
		height: 100%;
		width: 100%;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		.xm-empty-img {
			height: 280rpx;
			width: 280rpx;
		}
		.xm-empty-txt {
			margin-top: 20rpx;
			color: #999;
		}
	}
</style>
