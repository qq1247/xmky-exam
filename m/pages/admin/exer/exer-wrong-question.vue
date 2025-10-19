<template>
	<view class="exer-wrong-question">
		<view v-for="(question, index) in questions" :key="index" class="list">
			<view class="list__wrap">
				<view class="list__title">{{ index + 1 }}、{{ question.title }}</view>
				<view class="list__outer">
					<view class="list__tag list__tag--type">答错{{ question.wrongAnswerNum }}次</view>
				</view>
			</view>
		</view>
		<xmky-empty v-if="!questions.length"></xmky-empty>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { MyExam } from '@/ts/myExam.d';
import { Page } from '@/ts/page.d';
import { reportExerTrackListpage as _exerTrackListpage, reportExerWrongQuestionListpage as _exerWrongQuestionListpage } from '@/api/report';
import { loginSysTime } from '@/api/login';
import dayjs from 'dayjs';
import _ from 'lodash';

/************************变量定义相关***********************/
const questions = ref([]); // 当前选择标签页

/************************组件生命周期相关*********************/
onLoad(async (option) => {
	const data = uni.getStorageSync('tracks');
	uni.removeStorageSync('tracks');
	questions.value = data;
});
</script>
<style lang="scss" scoped>
.exer-wrong-question {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	.list {
		display: flex;
		border-bottom: 1px solid #dfdfdf;
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 10rpx;
		border-radius: 16rpx;
		.list__wrap {
			flex: 1;
			min-width: 0rpx;
			.list__title {
				font-size: 32rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap; // 单行省略
			}
			.list__outer {
				display: flex;
				margin-top: 10rpx;
				.list__tag {
					min-width: 80rpx;
					padding: 6rpx 10rpx;
					border-radius: 4rpx;
					font-size: 22rpx;
					margin-right: 20rpx;
					text-align: center;
					&.list__tag--type {
						color: #1ea1ee;
						border: 1px solid #c0eaff;
					}
					&.llist__tag--mark-type {
						color: #fc8113;
						border: 1px solid #fed9b3;
					}
					&.list__tag--score {
						color: #fe7068;
						border: 1px solid #ffcac7;
					}
					&.list__tag--user-name {
						color: #1ac693;
						border: 1px solid #afe7d6;
					}
				}
			}
		}
		.list__nav {
			width: 90rpx;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}
}
</style>
