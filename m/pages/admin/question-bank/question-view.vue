<template>
	<view class="question-view">
		<view class="question-view__head">
			<view class="view-model">
				<view class="view-model__inner">
					<button :class="['view-model__option', { 'view-model__option--answer': !toolbars.answerShow }]" @click="toolbars.answerShow = !toolbars.answerShow">
						无答案
					</button>
					<button :class="['view-model__option', { 'view-model__option--remember': toolbars.answerShow }]" @click="toolbars.answerShow = !toolbars.answerShow">
						带答案
					</button>
				</view>
			</view>
		</view>
		<view class="question-view__main">
			<view class="toolbar">
				<button class="toolbar__type" type="primary">{{ dictStore.getValue('QUESTION_TYPE', question?.type) }}题</button>
			</view>
			<xmky-question
				v-if="question"
				v-model="question.userAnswers"
				:type="question?.type"
				:title="question.title"
				:img-ids="question.imgFileIds"
				:video-id="question.videoFileId"
				:options="question.options"
				:answers="question.answers"
				:mark-type="question.markType"
				:score="question.score"
				:analysis="question.analysis"
				:user-score="question?.userScore"
				:answer-show="toolbars.answerShow"
				:user-answer-show="false"
				:analysis-show="toolbars.answerShow"
				:editable="false"
			>
				<template #title-pre>
					<text>{{ 1 }}、</text>
				</template>
				<template #title-post>
					<text>（{{ question.score }}分）</text>
				</template>
			</xmky-question>
		</view>
	</view>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { Question } from '@/ts/question.d';
import { questionGet } from '@/api/question';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const question = ref<Question>();
const toolbars = reactive({
	answerShow: false
});

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	let { data } = await questionGet({ id: options.id });
	question.value = data;
});
</script>
<style lang="scss" scoped>
.question-view {
	padding: 20rpx;
	// #ifdef H5
	min-height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	min-height: calc(100vh - 0px);
	// #endif
	background-color: #fff;
	.question-view__head {
		.view-model {
			display: flex;
			justify-content: center;
			margin-top: 20rpx;
			.view-model__inner {
				display: flex;
				.view-model__option {
					width: 160rpx;
					height: 64rpx;
					line-height: 64rpx;
					font-size: 28rpx;
					background: initial;
					border: 2rpx solid #1ea1ee;
					color: #1ea1ee;
					&::after {
						border-radius: initial;
						border: initial;
					}
					&:nth-child(1) {
						border-radius: 33rpx 0rpx 0rpx 33rpx;
					}
					&:nth-child(2) {
						border-radius: 0rpx 33rpx 33rpx 0rpx;
					}
				}
				.view-model__option--answer {
					background: #1ea1ee;
					color: white;
				}
				.view-model__option--remember {
					background: #1ea1ee;
					color: white;
				}
			}
		}
	}
	.question-view__main {
		display: flex;
		flex-direction: column;
		.toolbar {
			display: flex;
			justify-content: space-between;
			margin: 40rpx 30rpx 20rpx 30rpx;
			align-items: center;
			.toolbar__type {
				width: 154rpx;
				height: 54rpx;
				line-height: 54rpx;
				font-size: 32rpx;
				color: #1ea1ee;
				border-radius: 50px;
				background: linear-gradient(to right, rgba(4, 183, 242, 0.15) 0%, rgba(0, 125, 252, 0.15) 100%);
				margin: initial;
				&::after {
					border-radius: initial;
					border: initial;
				}
			}
		}
	}
}
</style>
