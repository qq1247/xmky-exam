<template>
	<view class="container">
		<u-navbar title="我的考试" :placeholder="true" autoBack :border-bottom="false"></u-navbar>
		<block v-if="examList.length">
			<view class="exam" v-for="exam in examList" :key="exam.id" @click="goExam(exam)">
				<view class="exam-title">
					<text>{{exam.examName}}</text>
					<u-icon name="arrow-right" color="#0094e5" size="16"></u-icon>
				</view>
				<view class="exam-content">
					<view>开始时间：{{exam.examStartTime}}</view>
					<view>总分：{{exam.paperTotalScore}}</view>
					<view>及格：{{((exam.paperPassScore / 100) * exam.paperTotalScore).toFixed(2)}}</view>
				</view>
			</view>
		</block>
		<u-empty v-else mode="list" text="暂无考试"></u-empty>
	</view>
</template>

<script>
	import {
		myExamListPage
	} from '@/api/mine.js'
	import {
		setTitle
	} from '@/common/setTitle'
	export default {
		data() {
			return {
				examList: []
			};
		},
		onShow() {
			this.getExamList()
		},
		mounted() {
			setTitle(getApp().globalData.entName)
		},
		methods: {
			// 获取考试列表
			async getExamList(time = undefined) {
				let examList = await myExamListPage({
					curPage: 1,
					pageSize: 100,
				})

				if (examList?.code !== 200) return
				this.examList = examList.data.list
			},
			// 去考试
			goExam({
				examId,
				paperId,
				paperShowType,
				examStartTime,
				examEndTime,
				examMarkState,
				examMarkEndTime
			}) {
				const _examStartTime = new Date(examStartTime).getTime()
				const _examEndTime = new Date(examEndTime).getTime()
				const _examMarkEndTime = new Date(examMarkEndTime).getTime()
				const now = new Date().getTime()
				if (now < _examStartTime) {
					this.$u.toast('考试暂未开始！')
					return
				}

				if (now > _examEndTime && now < _examMarkEndTime) {
					this.$u.toast('阅卷暂未结束！')
					return
				}

				// 考试时间内和阅卷结束后的逻辑
				if (
					(_examStartTime < now && now < _examEndTime) ||
					(now > _examMarkEndTime && examMarkState === 3)
				) {

					const params = {
						examId,
						paperId,
						examEndTime,
						preview: !(_examStartTime < now && now < _examEndTime)
					}

					uni.navigateTo({
						url: `/pages/exam/exam?params=${JSON.stringify(params)}`
					})
				}
			},
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

		&:first-child {
			margin-top: 20rpx;
			background: #999;
		}

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
