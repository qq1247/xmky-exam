<template>
	<view class="xmky-card__list">
		<view class="xmky-card__title">
			<text v-if="preTxt" class="xmky-card__pre-txt">{{ preTxt }}</text>
			<text class="xmky-card__name">{{ name }}</text>
			<view v-if="tagName" class="xmky-card__tag">
				<text>{{ tagName }}</text>
			</view>
		</view>
		<view class="xmky-card__content">
			<slot name="content"></slot>
		</view>
		<view class="xmky-card__opt">
			<slot name="opt"></slot>
		</view>
	</view>
</template>

<script lang="ts" setup>
/************************变量定义相关***********************/
const props = withDefaults(
	defineProps<{
		preTxt?: string; // 前缀文本
		name: string; // 名称
		tagName: string; //标签名称
	}>(),
	{}
);
</script>

<style lang="scss" scoped>
.xmky-card__list {
	background: #ffffff;
	border-radius: 16rpx;
	margin: 20rpx 0rpx 30rpx 0rpx;
	padding: 30rpx 30rpx 20rpx 30rpx;
	.xmky-card__title {
		display: flex;
		align-items: flex-end;
		margin: 20rpx 0rpx;
		.xmky-card__pre-txt {
			display: inline-block;
			width: 55rpx;
			height: 42rpx;
			line-height: 42rpx;
			border-radius: 10rpx;
			text-align: center;
			font-size: 32rpx;
			color: #ffffff;
			margin-right: 10rpx;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
		.xmky-card__name {
			display: inline-block;
			height: 34rpx;
			line-height: 34rpx;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			font-size: 32rpx;
			color: #333333;
		}
		.xmky-card__tag {
			display: inline-block;
			margin-left: 10rpx;
			min-width: 76rpx;
			padding: 0rpx 8rpx;
			font-size: 26rpx;
			border-radius: 8rpx 8rpx 8rpx 8rpx;
			border: 1px solid transparent;
			border-radius: 8rpx;
			background-clip: padding-box, border-box;
			background-origin: padding-box, border-box;
			height: 34rpx;
			line-height: 34rpx;
			background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #259ff8, #04c7f2);
			color: #04c7f2;
		}
	}
	.xmky-card__content {
		font-size: 26rpx;
		color: #8f939c;
		margin: 30rpx 0rpx 10rpx 0rpx;
		line-height: 62rpx;
	}
	.xmky-card__opt {
		display: flex;
		align-items: center;
		border-top: 1rpx solid #e5e5e5;
		padding-top: 15rpx;
		font-size: 26rpx;
		color: #8f939c;
	}
}
</style>
