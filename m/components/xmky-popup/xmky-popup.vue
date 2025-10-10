<template>
	<uni-popup ref="popup" type="share" safeArea background-color="#fff" border-radius="10px 10px 10px 10px" class="xm-popup">
		<view class="xm-popup-head">
			<text class="xm-popup-head__name">{{ name }}</text>
			<uni-icons customPrefix="iconfont" type="icon-guanbi" @click="popup.close()" color="#231815" size="34rpx" class="xm-popup-head__close-btn"></uni-icons>
		</view>
		<view class="xm-popup-main">
			<scroll-view scroll-y="true" class="xm-popup-main__scroll">
				<slot></slot>
			</scroll-view>
		</view>
	</uni-popup>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

/************************变量定义相关***********************/
defineExpose({ open, close });
const props = withDefaults(
	defineProps<{
		name: string;
	}>(),
	{
		name: ''
	}
);

const popup = ref(); // 答题卡

/************************事件相关*****************************/
// 打开
function open() {
	popup.value.open();
}
// 关闭
function close() {
	popup.value.close();
}
</script>

<style lang="scss" scoped>
.xm-popup {
	.xm-popup-head {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 0rpx 30rpx;

		height: 120rpx;
		.xm-popup-head__name {
			font-weight: bold;
			font-size: 42rpx;
			color: #303133;
		}
		.xm-popup-head__close-btn {
			position: absolute;
			right: 40rpx;
			top: 40rpx;
		}
	}
	.xm-popup-main {
		padding: 0rpx 30rpx;
		.xm-popup-main__scroll {
			height: 40vh;
		}
	}
}
</style>
