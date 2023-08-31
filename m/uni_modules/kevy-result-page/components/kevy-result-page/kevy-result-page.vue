<template>
	<view class="t-wrap">
		<view class="t-krp-header">
			<image class="t-icon" :src="icon"></image>
			<view class="t-title">{{title}}</view>
			<view class="t-description">{{description}}</view>
			<view class="t-detail-con" v-if="detailData && detailData.length>0">
				<view class="t-detail" v-for="(item,index) in detailData" :key="index">
					<text :style="{'font-weight':item['bold']?'600':'unset'}">{{item.label}}</text>
					<text :style="{'font-weight':item['bold']?'600':'unset'}">{{item.value}}</text>
				</view>
				<view class="t-collapse" @click="changeExpand" v-if="details.length>2">
					<view :style="{'transform': isExpand?'rotate(-45deg)':'rotate(135deg)'}"></view>
				</view>
			</view>
			<view class="t-bg-wrap">
				<view class="t-bg" :style="{'background-color':primarColor}"></view>
			</view>
		</view>
		<view class="t-krp-footer" v-if="primaryBtnText || secondaryBtnText"
			:style="{'justify-content': primaryBtnText && secondaryBtnText?'space-between':'center'}">
			<view v-if="secondaryBtnText" hover-class="t-hover" class="t-btn t-secondary" @click="secondaryBtnClick">
				{{secondaryBtnText}}</view>
			<view v-if="primaryBtnText" hover-class="t-hover" class="t-btn t-primary" @click="primaryBtnClick"
				:style="{'background-color':primarColor}">{{primaryBtnText}}</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "kevyResultPage",
		props: {
			/**
			 * 类型
			 */
			type: {
				type: String,
				default: 'success'
			},
			/**
			 * 标题
			 */
			title: {
				type: String,
				default: ''
			},
			/**
			 * 描述
			 */
			description: {
				type: String,
				default: ''
			},
			/**
			 * 详细数量列表，超过三条自动折叠
			 */
			details: {
				type: Array,
				default: []
			},
			/**
			 * 主背景色
			 */
			primarColor: {
				type: String,
				default: '#4fc08d'
			},
			/**
			 * 主要按钮文字，不填不显示主要按钮
			 */
			primaryBtnText: {
				type: String,
				default: ''
			},
			/**
			 * 次要按钮文字，不填不显示次要按钮
			 */
			secondaryBtnText: {
				type: String,
				default: ''
			},
		},
		data() {
			return {
				//是否展开
				isExpand: false,
			};
		},
		created() {

		},
		computed: {
			//状态图标
			icon: function() {
				return '/uni_modules/kevy-result-page/static/icon/icon_' + this.type + '.png';
			},
			//详情数据
			detailData: function() {
				if (this.isExpand) {
					return this.details;
				} else {
					return this.details.length > 2 ? this.details.slice(0, 2) : this.details;
				}
			}

		},
		methods: {
			//切换展开状态
			changeExpand() {
				this.isExpand = !this.isExpand;
			},
			//主按钮点击
			primaryBtnClick() {
				this.$emit('primaryBtnClick');
			},
			//辅助按钮点击
			secondaryBtnClick() {
				this.$emit('secondaryBtnClick');
			}
		}
	}
</script>

<style lang="scss" scoped>
	.t-wrap {
		width: 100%;
		box-sizing: border-box;
		position: relative;
		background-color: rgb(245, 245, 245);
		height: 100vh;
	}

	.t-krp-header {
		display: flex;
		align-items: center;
		flex-direction: column;
		background-color: transparent;
		position: relative;
		padding: 40rpx 40rpx 200rpx;
		z-index: 1;
		overflow: hidden;

		.t-icon {
			width: 64rpx;
			height: 64rpx;
		}

		.t-title {
			font-size: 36rpx;
			color: #ffffff;
			text-align: center;
			line-height: 1.4;
			margin-top: 20rpx;
		}

		.t-description {
			margin-top: 16rpx;
			margin-bottom: 48rpx;
			font-size: 28rpx;
			color: hsla(0, 0%, 100%, .6);
			line-height: 1.4;
			text-align: center;
		}

		.t-detail-con {
			width: 100%;
			box-sizing: border-box;

			.t-detail {
				width: 100%;
				display: flex;
				flex-direction: row;
				justify-content: space-between;
				margin-bottom: 10rpx;
				color: #ffffff;
				font-size: 28rpx;
			}

			.t-collapse {
				box-sizing: border-box;
				padding: 17rpx;
				width: 100%;
				display: flex;
				view {
					opacity: 0.6;
					width: 20rpx;
					height: 20rpx;
					margin: auto auto 20rpx;
					border-top: 4rpx solid #ffffff;
					border-right: 4rpx solid #ffffff;
					transform: rotate(135deg);
				}
			}
		}

		.t-bg-wrap {
			position: relative;
			align-self: flex-start;
			top: 54rpx;

			.t-bg {
				--width: 440vw;
				position: absolute;
				height: var(--width);
				width: var(--width);
				left: calc(var(--width)*-1/2 + 50vw - 40rpx);
				top: calc(var(--width)*-1 + 1vw);
				border-radius: 50%;
				z-index: -1;
			}
		}
	}

	.t-krp-footer {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		box-sizing: border-box;
		position: fixed;
		bottom: 0;
		width: 100%;
		padding: 24rpx 24rpx 48rpx;
		background-color: #f5f5f5;
		z-index: 3;

		.t-btn {
			padding: 22rpx 24rpx;
			font-size: 36rpx;
			display: flex;
			flex-direction: row;
			justify-content: center;
			align-items: center;
			box-sizing: border-box;
			border-radius: 8rpx;
			flex: 1 1;
			max-width: calc(50vw - 36rpx);
		}

		.t-primary {
			color: #ffffff;
		}

		.t-secondary {
			background: #ffffff;
			color: rgb(51, 51, 51);
		}

		.t-hover {
			opacity: 0.7;
		}
	}
</style>