<template>
	<view class="container">
		<u-navbar title="我的" :left-icon-size="0" :placeholder="true" :border-bottom="false">
		</u-navbar>

		<navigator class="user" url="/pages/setting/setting">
			<u-avatar v-if="userInfo.user" size="48" :src="`${baseURL}/${userInfo.user.headFileId}`"></u-avatar>
			<view class="user-info">
				<text class="user-name">
					{{ this.userName }}
				</text>
				<text class="user-level">
					普通用户
				</text>
			</view>
			<u-icon name="arrow-right"></u-icon>
		</navigator>

		<view class="report" v-if="userInfo.exam">
			<view class="report-item">
				<image src="@/static/my/index-paper.png"></image>
				<text class="item-intro">参加：{{ userInfo.exam.num }}</text>
			</view>
			<view class="report-item">
				<image src="@/static/my/index-exam.png"></image>
				<text class="item-intro">缺考：{{ userInfo.exam.missNum }}</text>
			</view>
			<view class="report-item">
				<image src="@/static/my/index-question.png"></image>
				<text class="item-intro">及格：{{ userInfo.exam.succNum }}</text>
			</view>
			<view class="report-item">
				<image src="@/static/my/index-mark.png"></image>
				<text class="item-intro">参加：{{ userInfo.exam.top }}</text>
			</view>
		</view>

		<div class="cell">
			<u-cell icon="setting-fill" title="我的考试" :isLink="true" url="/pages/examList/examList"
				arrow-direction="right">
				<u-image slot="icon" src="@/static/my/index-exam.png" width="16" height="16"></u-image>
			</u-cell>
			<u-cell icon="setting-fill" title="我的模拟" :isLink="true" url="/pages/testList/testList"
				arrow-direction="right" :border="false">
				<u-image slot="icon" src="@/static/my/index-question.png" width="16" height="16"></u-image>
			</u-cell>
		</div>
	</view>
</template>

<script>
import {
	getUserInfo
} from '@/api/mine.js'
import {
	setTitle
} from '@/common/setTitle'
export default {
	data() {
		return {
			baseURL: getApp().globalData.baseUrl,
			userInfo: {},
			loginInfo: uni.getStorageSync('userInfo'),
			userName: ""
		};
	},
	onShow() {
		this.getUserInfo()
		this.userName = uni.getStorageSync('userInfo').userName
	},
	mounted() {
		setTitle(getApp().globalData.entName)
	},
	methods: {
		async getUserInfo() {
			const userInfo = await getUserInfo()
			this.userInfo = Object.keys(userInfo.data).length ?
				userInfo.data :
				{
					user: {
						id: null,
						name: '',
						type: null,
						headFileId: null
					},
					org: {
						id: null,
						name: ''
					},
					exam: {
						num: 0,
						missNum: 0,
						succNum: 0,
						top: 0
					},
					score: {
						avg: null,
						min: null,
						max: null,
						sd: null
					}
				}
		}
	}
}
</script>

<style lang="scss" scoped>
.user {
	width: 100%;
	display: flex;
	padding: 10px 20px;
	background: #fff;
	box-sizing: border-box;

	.user-info {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		margin-left: 20px;
		flex: 1;
	}

	.user-name {
		font-size: 32rpx;
		color: #000;
	}

	.user-level {
		font-size: 24rpx;
		background: rgba(#0094e5, .6);
		color: #fff;
		width: 100rpx;
		padding: 2rpx 16rpx;
		text-align: center;
		border-radius: 40rpx;
		margin-left: -10rpx;
	}
}

.report {
	width: 710rpx;
	background: #fff;
	border-radius: 10rpx;
	margin: 16rpx auto;
	display: flex;

	.report-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 40rpx 0;

		uni-image {
			width: 48rpx;
			height: 48rpx;
		}

		.item-intro {
			margin-top: 16rpx;
			font-size: 26rpx;
			color: #666;
		}
	}
}

.cell {
	background: #fff;
	border-radius: 10rpx;
	width: 720rpx;

	/deep/ .u-cell__body {
		padding: 30rpx;
	}
}
</style>
