<template>
	<view class="custom">
		<view class="custom__main-title">
			<text :decode="true">{{ parmStore.customTitle }}</text>
		</view>
		<view class="custom__sub-title">
			<text :decode="true"></text>
		</view>
		<view class="custom__content">
			<text :decode="true">{{ parmStore.customContent }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app';
import { useParmStore } from '@/stores/parm';

/************************变量定义相关***********************/
const parmStore = useParmStore();

/************************组件生命周期相关*********************/
onLoad(async () => {});

/************************事件相关*****************************/
</script>

<style lang="scss" scoped>
.custom {
	height: inherit;
	background-color: #fff;
	padding: 30rpx;
	.custom__main-title {
		font-size: 32rpx;
		color: #333333;
		line-height: 46rpx;
		text-align: center;
	}
	.custom__sub-title {
		font-size: 26rpx;
		color: #8f939c;
		text-align: center;
		line-height: 46rpx;
	}
	.custom__content {
		margin-top: 20rpx;
		font-size: 26rpx;
		color: #8f939c;
		line-height: 40rpx;
		line-height: 46rpx;
	}
}
</style>
