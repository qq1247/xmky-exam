<template>
	<view class="notice">

		<image class="left_icon" mode="aspectFit" src="./notice_icon.png">

		</image>
		<view class="right_notice">
			<swiper class="notice_swiper" vertical easing-function="easeInOutCubic" autoplay interval="6000">
				<swiper-item v-for="(item,index) in noticeList" :key="index" class="sw_item" @click="itemClick(item)">
					<text class="sw_text" :style="{color:colors}">{{item.title}}</text>
					<image class="sw_image" src="./right.png"></image>
				</swiper-item>
			</swiper>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {};
		},

		components: {},
		props: {
			colors: {
				type: String,
				default: '#333'
			},
			noticeList: {
				type: Array
			}
		},
		methods: {
			itemClick(item) {

				this.$emit('click', item);
			}
		}
	};
</script>
<style lang="scss" scoped>
	.notice {
		height: 80upx;
		line-height: 80upx;
		margin: 0 3%;
		margin-top: 15upx;
		padding: 0 10upx;
		box-shadow: 0upx 0upx 10upx #eee;
		border-radius: 8upx;
	}

	.left_icon {

		width: 10%;
		height: 24px;
		float: left;
		padding-top: 18upx;
	}

	.left_icon .iconfont {
		display: inline-block;
		font-size: 44upx;
	}

	.right_notice {
		float: left;
		width: 90%;

	}

	.right_notice .notice_swiper {
		height: 80upx;
	}

	.notice_swiper .sw_item {
		height: 80upx;
	}

	.notice_swiper .sw_item .sw_text {
		font-size: 24upx;
		color: #333;
		display: inline-block;
	}

	.notice_swiper .sw_image {
		width: 40upx;
		height: 40upx;
		float: right;
		margin-top: 20upx;
	}
</style>