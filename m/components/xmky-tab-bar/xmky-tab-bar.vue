<template>
	<view class="xmky-tabbar">
		<view v-for="(tab, index) in props.tabs" :key="index" class="tab" @click="toPage(tab.pagePath)">
			<uni-icons
				customPrefix="iconfont"
				:type="tab.icon"
				:color="props.current === index ? 'transparent' : '#7A7E83'"
				size="46rpx"
				:class="['tab__icon', { 'tab__icon--active': props.current === index }]"
			/>
			<text :class="['tab__txt', { 'tab__txt--active': props.current === index }]">
				{{ tab.text }}
			</text>
		</view>
	</view>
</template>

<script setup lang="ts">
const props = defineProps<{
	current: number;
	tabs: {
		pagePath: string;
		text: string;
		icon: string;
	}[];
}>();

function toPage(pagePath: string) {
	uni.redirectTo({ url: pagePath });
}
</script>

<style lang="scss" scoped>
.xmky-tabbar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	display: flex;
	justify-content: space-around;
	align-items: center;
	height: calc(50px + var(--window-bottom, 0px));/* 高度 = 基础高度 50px + 安全区域底部 */
	background-color: #fff;
	border-top: 1px solid rgba(0, 0, 0, 0.33);
	z-index: 999;

	.tab {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 50px;
		flex: 1;

		.tab__icon {
			width: 48rpx;
			height: 48rpx;

			&.tab__icon--active {
				background: linear-gradient(to top, #04c7f2 0%, #259ff8 100%);
				-webkit-background-clip: text;
				-webkit-text-fill-color: transparent;
				background-clip: text;
				display: inline-block;
			}
		}

		.tab__txt {
			font-size: 20rpx;
			margin-top: 4rpx;
			color: #7a7e83;

			&.tab__txt--active {
				background: linear-gradient(to top, #04c7f2 0%, #259ff8 100%);
				background-clip: text;
				color: transparent;
				-webkit-background-clip: text;
				-webkit-text-fill-color: transparent;
			}
		}
	}
}
</style>
