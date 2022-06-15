<template>
	<view class="container">
		<u-navbar title="考试结果" :left-icon-size="0" :placeholder="true" :border-bottom="false">
		</u-navbar>

		<view class="result-content">
			<view v-if="countDown" class="count-down">
				{{ countDown }}
			</view>
			<view v-else class="exam-result">
				本场考试得分：{{ scoreState ? score : '**' }}
				<view v-if="!scoreState" class="exam-tip">此次考试暂未公开成绩</view>
				<view class="btn" @click="goHome">
					返回首页
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		myExamListPage
	} from '@/api/mine.js'
	import {
		delay
	} from '@/common/utils.js'
	export default {
		data() {
			return {
				score: 0,
				countDown: 5,
				examId: 16,
				scoreState: true
			};
		},
		onLoad(options) {
			const {
				examId,
				scoreState
			} = JSON.parse(options.params)
			this.examId = examId
			this.scoreState = scoreState
			this.getResult()
		},
		methods: {
			// 获取答案
			async getResult() {
				const scoreData = await delay()
					.then(() => {
						this.countDown--
						return myExamListPage({
							examId: this.examId
						})
					})
					.catch((err) => {
						return err.data
					})

				if (!this.countDown) {
					this.score = scoreData.data?.list[0]?.totalScore || '请稍后查询'
					return
				} else {
					this.getResult()
				}
			},
			
			// 返回首頁
			goHome (){
				uni.switchTab({
					url: '/pages/index/index'
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.result-content {
		width: 100%;
		height: calc(100vh - 50px);
		background: #fff;
		display: flex;
		justify-content: center;
		padding-top: 300rpx;
		box-sizing: border-box;

		.count-down {
			background: #fff;
			width: 300rpx;
			height: 300rpx;
			line-height: 300rpx;
			border-radius: 50%;
			font-size: 120rpx;
			color: #0094e5;
			box-shadow: 0 0 66rpx 6rpx rgba(#0094e5, .4);
			text-align: center;
		}

		.exam-result {
			font-size: 18px;
			display: flex;
			flex-direction: column;
			align-items: center;
		}

		.exam-tip {
			font-size: 14px;
			margin-top: 10px;
			color: #ff8e19;
		}
		
		.btn {
			width: 630rpx;
			height: 80rpx;
			background: linear-gradient(45deg, rgba(#0094e5, .4) 30%, #0094e5 100%);
			border-radius: 40rpx;
			text-align: center;
			line-height: 80rpx;
			color: #FFFFFF;
			font-size: 32rpx;
			font-weight: 400;
			margin-top: 80rpx;
		}
		
	}
</style>
