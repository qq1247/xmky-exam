<template>
	<view class="container">
		<u-navbar title="在线考试" :left-icon-size="0" :placeholder="true" :border-bottom="false">
		</u-navbar>

		<view class="bulletin" v-if="bulletinList.length">
			<u-notice-bar :text="bulletinList" direction="column" mode="link" @click="getBulletinDetail"></u-notice-bar>
		</view>


		<view class="exam-intro">
			待考列表
		</view>
		<block v-if="examList.some(
                  (exam) =>
                    (exam.state === 1 && exam.markState !== 3) ||
                    exam.state == 2
                )">
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
		<u-empty v-else mode="data" text="暂无待考"></u-empty>


		<view class="exam-intro">
			模拟列表
		</view>
		<block v-if="questionTypeOpenList.length">
			<view class="exam" v-for="question in questionTypeOpenList" :key="question.id" @click="goTest(question)">
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
		<u-empty v-else mode="data" text="暂无模拟"></u-empty>
	</view>
</template>

<script>
	import {
		bulletinListPage
	} from '@/api/common.js'
	import {
		myExamListPage,
		questionTypeOpenListPage
	} from '@/api/mine.js'
	import {
		setTitle
	} from '@/common/setTitle'
	import dayjs from 'dayjs'
	export default {
		data() {
			return {
				examList: [],
				bulletinList: [],
				questionTypeOpenList: []
			}
		},
		onShow() {
			this.getExamList()
			this.getBulletinList()
			this.getQuestionTypeOpenList()
		},
		mounted() {
			setTitle(getApp().globalData.entName)
		},
		methods: {
			// 获取公告列表
			async getBulletinList() {
				const {
					data: {
						list
					}
				} = await bulletinListPage({
					curPage: 1,
					pageSize: 100
				})
				this.bulletinList = list.reduce((acc, cur) => {
					if (cur.showType === 2 || cur.showType === 1) {
						acc.push(cur.title)
					}
					return acc
				}, [])
			},

			// 获取考试列表
			async getExamList(time = undefined) {
				const days = dayjs(time).daysInMonth()
				const startDate = time || dayjs().date(1).format('YYYY/MM/DD')
				const endDate = dayjs(time).date(days).format('YYYY/MM/DD')
				let examList = await myExamListPage({
					curPage: 1,
					pageSize: 10,
					startTime: `${startDate} 00:00:00`,
					endTime: `${endDate} 23:59:59`
				})

				if (examList?.code !== 200) return
				this.examList = examList.data.list.filter((item) => {
					return item.markState !== 3
				})
			},

			// 获取开放题库
			async getQuestionTypeOpenList() {
				const days = dayjs().daysInMonth()
				const startDate = dayjs().date(1).format('YYYY/MM/DD')
				const endDate = dayjs().date(days).format('YYYY/MM/DD')
				const res = await questionTypeOpenListPage({
					state: 1,
					pageSize: 10,
					curPage: 1,
					startTime: `${startDate} 00:00:00`,
					endTime: `${endDate} 23:59:59`,
				})

				if (res?.code !== 200) return
				this.questionTypeOpenList = res.data.list.filter((item) => {
					const $_endTime = new Date(item.endTime).getTime()
					const now = new Date().getTime()
					return now < $_endTime
				})
			},

			// 查看公告详情
			getBulletinDetail(index) {
				uni.navigateTo({
					url: `/pages/bulletinDetail/bulletinDetail?index=${index}`
				})
			},

			// 去模拟练习
			goTest({
				questionTypeId,
				commentState
			}) {
				const params = {
					questionTypeId,
					commentState,
				}
				uni.navigateTo({
					url: `/pages/test/test?params=${JSON.stringify(params)}`
				})
			},

			// 去考试
			goExam({
				examId,
				paperId,
				examStartTime,
				examEndTime,
				state,
				markState
			}) {
				const _examStartTime = new Date(examStartTime).getTime()
				const _examEndTime = new Date(examEndTime).getTime()
				const now = new Date().getTime()
				if (now < _examStartTime) {
					uni.$u.toast('考试未开始，请等待...')
					return
				}

				const params = {
					examId,
					paperId,
					examEndTime,
					preview: _examStartTime < now && now > _examEndTime,
					state,
					markState
				}

				uni.navigateTo({
					url: `/pages/exam/exam?params=${JSON.stringify(params)}`
				})

			},
		}
	}
</script>

<style lang="scss" scoped>
	.banner {
		width: 100%;
		margin-bottom: 16rpx;
		background: #fff;
	}

	.bulletin {
		width: 100%;
		margin-bottom: 32rpx;
	}


	.exam-intro {
		width: 100%;
		font-size: 28rpx;
		color: #999;
		font-weight: 600;
		text-align: left;
		padding: 20rpx 0 20rpx 32rpx;
		margin-bottom: 20rpx;
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

	.u-empty {
		margin-bottom: 40rpx;
	}
</style>
