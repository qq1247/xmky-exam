<template>
	<view class="container">
		<u-navbar title="我的模拟" :placeholder="true" autoBack :border-bottom="false"></u-navbar>
		<block v-if="questionTypeOpenList.lenght">
			<view class="exam" v-for="(question,index) in questionTypeOpenList" :key="question.id"
				@click="goTest(question)">
				<view class="exam-title">
					<text>{{question.questionTypeName}}</text>
					<u-icon name="arrow-right" color="#0094e5" size="16"></u-icon>
				</view>
				<view class="exam-content">
					<view>开始时间：{{question.startTime}}</view>
					<view>结束时间：{{question.endTime}}</view>
				</view>
			</view>
		</block>
		<u-empty v-else mode="list" text="暂无模拟练习"></u-empty>
	</view>
</template>

<script>
	import {
		questionTypeOpenListPage
	} from '@/api/mine.js'
	import {
		setTitle
	} from '@/common/setTitle'
	export default {
		data() {
			return {
				questionTypeOpenList: []
			};
		},
		
		onShow() {
			this.getQuestionTypeOpenList()
		},
		
		mounted() {
			setTitle(getApp().globalData.entName)
		},
		
		methods: {
			async getQuestionTypeOpenList() {
				const res = await questionTypeOpenListPage({
					state: 1,
					pageSize: 100,
					curPage: 1,
				})

				if (res?.code !== 200) return
				this.questionTypeOpenList = res.data.list
			},
			goTest({
				questionTypeId,
				startTime,
				endTime,
				commentState
			}) {
				const $_startTime = new Date(startTime).getTime()
				const $_endTime = new Date(endTime).getTime()
				const now = new Date().getTime()
				if (now < $_startTime || now > $_endTime) {
					this.$u.toast('尚未开放模拟！')
					return
				}
				const params = {
					questionTypeId,
					commentState,
				}
				uni.navigateTo({
					url: `/pages/test/test?params=${JSON.stringify(params)}`
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		padding-top: 20rpx;
	}

	.exam {
		background: #fff;
		border-radius: 20rpx;
		margin-bottom: 20rpx;
		width: 710rpx;
		background: #fff;
		box-shadow: 0 0 16rpx -6rpx rgba(0, 0, 0, .15);

		.exam-title {
			padding: 20rpx;
			font-size: 28rpx;
			border-bottom: 2rpx solid #ececec;
			font-weight: 600;
			display: flex;
			justify-content: space-between;
			align-items: center;
		}

		.exam-content {
			line-height: 50rpx;
			font-size: 24rpx;
			color: #999;
			padding: 20rpx 30rpx;
		}
	}
</style>
