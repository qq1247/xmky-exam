<template>
	<view class="rank">
		<view class="rank-top">
			<image class="rank-top-bg" src="@/static/img/home-bg.png"></image>
			<text class="rank-top-name">考试排行榜</text>
		</view>
		<view class="rank-conter">
			<view class="rank-conter-user">
				<view class="rank-conter-user1">
					<view class="rank-conter-user1-head">
						<text>小学教学资格认证</text>
					</view>
					<view class="rank-conter-user1-content">
						<view class="rank-conter-user1-content1">
							<image class="rank-conter-user1-content1-avatar" src="@/static/img/user-avatar.png"></image>
							<text class="rank-conter-user1-content1-name">达拉蹦蹦蹦蹦</text>
						</view>
						<view class="rank-conter-user1-content2">
							<view class="rank-conter-user1-content2-txt">
								<text>我的考试排名：</text>
							</view>
							<view>
								<text class="rank-conter-user1-content2-score">80</text>
								<text class="rank-conter-user1-content2-score1">分</text>
								<text class="rank-conter-user1-content2-rank">第</text>
								<text class="rank-conter-user1-content2-rank1">18</text>
								<text class="rank-conter-user1-content2-rank">名</text>
							</view>
						</view>
						<image class="rank-conter-user1-content3" src="@/static/img/user-rank.png"></image>
					</view>
				</view>
			</view>
		</view>
		<view class="rank-bottom">
			<view class="rank-bottom-list-head">
				<view class="rank-bottom-list-head1"><text>排名</text></view>
				<view class="rank-bottom-list-head2"><text>姓名</text></view>
				<view class="rank-bottom-list-head3"><text>分数</text></view>
			</view>
			<view class="rank-bottom-list-row">
				<view class="rank-bottom-list-row1"><image src="@/static/img/user-rank-1.png"></image></view>
				<view class="rank-bottom-list-row2"><text>蓝受香菇</text></view>
				<view class="rank-bottom-list-row3"><text>99</text></view>
			</view>
			<view class="rank-bottom-list-row">
				<view class="rank-bottom-list-row1"><image src="@/static/img/user-rank-2.png"></image></view>
				<view class="rank-bottom-list-row2"><text>朝花夕拾</text></view>
				<view class="rank-bottom-list-row3"><text>95</text></view>
			</view>
			<view class="rank-bottom-list-row">
				<view class="rank-bottom-list-row1"><image src="@/static/img/user-rank-3.png"></image></view>
				<view class="rank-bottom-list-row2"><text>一剪梅</text></view>
				<view class="rank-bottom-list-row3"><text>92</text></view>
			</view>
			<view class="rank-bottom-list-row">
				<view class="rank-bottom-list-row1"><text>04</text></view>
				<view class="rank-bottom-list-row2"><text>会发光的星星闪…</text></view>
				<view class="rank-bottom-list-row3"><text>89</text></view>
			</view>
			<view class="rank-bottom-list-row">
				<view class="rank-bottom-list-row1"><text>05</text></view>
				<view class="rank-bottom-list-row2"><text>RainSounds</text></view>
				<view class="rank-bottom-list-row3"><text>79</text></view>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { dictIndexList } from '@/api/dict';

/************************变量定义相关***********************/
// 变量定义

/************************组件生命周期相关*********************/
// 组件显示前
onLoad(async () => {});

/************************事件相关*****************************/
function toExam() {}
</script>
<style lang="scss" scoped>
.rank {
	height: inherit;
	.rank-top {
		display: flex;
		flex-direction: column;
		align-items: center;
		height: 320rpx;
		.rank-top-bg {
			position: absolute;
			width: 750rpx;
			height: 320rpx;
		}
		.rank-top-name {
			z-index: 0;
			font-weight: bold;
			font-size: 36rpx;
			color: #ffffff;
		}
	}
	.rank-conter {
		.rank-conter-user {
			height: 100rpx;
			position: relative;
			.rank-conter-user1 {
				display: flex;
				flex-direction: column;
				position: absolute;
				height: 290rpx;
				width: 710rpx;
				left: 50%;
				margin-left: -355rpx;
				top: -210rpx;
				background: linear-gradient(to bottom, #bff3ff 0%, #e1f2ff 100%);
				box-shadow: 0rpx 10rpx 20rpx 0rpx #c9ccce;
				border-radius: 32rpx;
				overflow: hidden;
				.rank-conter-user1-head {
					display: flex;
					align-items: center;
					height: 90rpx;
					background: linear-gradient(to top, #a3e4ff 0%, #b3eeff 100%);
					padding-left: 50rpx;
					font-weight: bold;
					font-size: 36rpx;
					color: #0d9df6;
				}
				.rank-conter-user1-content {
					display: flex;
					align-items: center;
					margin: 30rpx 0rpx 0rpx 0rpx;
					.rank-conter-user1-content1 {
						display: flex;
						flex-direction: column;
						width: 220rpx;
						align-items: center;
						justify-content: center;
						border-right: 1rpx solid #b0d7e6;
						.rank-conter-user1-content1-avatar {
							width: 100rpx;
							height: 100rpx;
						}
						.rank-conter-user1-content1-name {
							font-size: 26rpx;
							color: #333333;
							line-height: 38rpx;
							width: 130rpx;
							white-space: nowrap;  
							  overflow: hidden;  
							  text-overflow: ellipsis;  
							  text-align: center;
						}
					}
					.rank-conter-user1-content2 {
						flex: 1;
						display: flex;
						flex-direction: column;
						height: 100%;
						padding-left: 35rpx;
						.rank-conter-user1-content2-txt {
							display: flex;
							font-size: 26rpx;
							color: #8f939c;
							margin: 20rpx 0rpx 30rpx 0rpx;
						}
						.rank-conter-user1-content2-score {
							font-weight: bold;
							font-size: 60rpx;
							color: #ff8c11;
						}
						.rank-conter-user1-content2-score1 {
							font-weight: bold;
							font-size: 26rpx;
							color: #ff8c11;
							margin-right: 35rpx;
						}
						.rank-conter-user1-content2-rank {
							font-weight: bold;
							font-size: 26rpx;
							color: #ff8c11;
						}
						.rank-conter-user1-content2-rank1 {
							font-weight: bold;
							font-size: 48rpx;
							color: #ff8c11;
						}
					}
					.rank-conter-user1-content3 {
						position: absolute;
						right: 0;
						bottom: 0;
						width: 248rpx;
						height: 171rpx;
					}
				}
			}
		}
	}
	.rank-bottom {
		margin: 0rpx 20rpx;
		border-radius: 16rpx 16rpx 0rpx 0rpx;
		overflow: hidden;
		.rank-bottom-list-head {
			display: flex;
			font-weight: bold;
			font-size: 30rpx;
			color: #333333;
			background: #DFF2FF;
			height: 98rpx;
			.rank-bottom-list-head1 {
				display: flex;
				justify-content: center;
				align-items: center;
				width: 200rpx;
			}
			.rank-bottom-list-head2 {
				display: flex;
				justify-content: center;
				align-items: center;
				flex: 1;
			}
			.rank-bottom-list-head3 {
				display: flex;
				justify-content: center;
				align-items: center;
				width: 200rpx;
			}
		}
		.rank-bottom-list-row {
			display: flex;
			font-size: 30rpx;
			background: #fff;
			height: 98rpx;
			border-bottom: 1rpx solid #e5e5e5;
			.rank-bottom-list-row1 {
				display: flex;
				justify-content: center;
				align-items: center;
				width: 200rpx;
				
				uni-image, image {// 小程序为image
					width: 64rpx;
					height: 40rpx;
				}
			}
			.rank-bottom-list-row2 {
				display: flex;
				justify-content: center;
				align-items: center;
				flex: 1;
			}
			.rank-bottom-list-row3 {
				display: flex;
				justify-content: center;
				align-items: center;
				width: 200rpx;
				font-weight: bold;
			}
		}
	}
}
</style>
