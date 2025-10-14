<template>
	<view class="mypaper">
		<view class="mypaper-head"></view>
		<view class="mypaper-main">
			<view class="mypaper-main__nav">
				<button class="mypaper-main__nav-type" type="primary">
					{{ examQuestions[curQuestionIndex].type === 1 ? '章节' : dictStore.getValue('QUESTION_TYPE', examQuestions[curQuestionIndex].questionType) }}
				</button>
			</view>
			<xmky-swiper ref="swiperRef" v-model="curQuestionIndex" :items="examQuestions" :style="{ height: questionHeight + 'px' }" class="mypaper-main__scroll">
				<template #default="{ item: examQuestion }">
					<scroll-view scroll-y="true" style="height: 100%">
						<xmky-question
							v-if="examQuestion.type === 2"
							v-model="examQuestion.userAnswers"
							:type="examQuestion.questionType"
							:mark-type="examQuestion.markType"
							:title="examQuestion.title"
							:img-ids="examQuestion.imgFileIds"
							:video-id="examQuestion.videoFileId"
							:score="examQuestion.score"
							:answers="examQuestion.answers"
							:user-score="examQuestion.userScore"
							:options="examQuestion.options"
							:analysis="examQuestion.analysis"
							:editable="false"
							:analysis-show="true"
							@change=""
						>
							<template #title-pre>
								<text class="mypaper-main__question-cur-no">{{ examQuestion.no }}、</text>
							</template>
							<template #title-post>
								<text>（{{ examQuestion.score }}分）</text>
							</template>
						</xmky-question>
						<view v-else>
							<view>{{ examQuestion.chapterName }}</view>
							<view>{{ examQuestion.chapterTxt }}</view>
						</view>
					</scroll-view>
				</template>
			</xmky-swiper>
		</view>
		<view class="mypaper-foot">
			<view class="answer-nav" @click="answerSheet.open()">
				<uni-icons customPrefix="iconfont" type="icon-datiqia" color="transparent" size="48rpx" class="answer-nav__icon"></uni-icons>
				<view class="answer-nav__outer">
					<view class="answer-nav__inner">
						<text class="answer-nav__cur">{{ examQuestions[curQuestionIndex].no || '-' }}</text>
						<text class="answer-nav__total">&nbsp;/&nbsp;{{ questionNum }}</text>
					</view>
					<text class="answer-nav__text">答题卡</text>
				</view>
			</view>
			<button class="mypaper-foot__pre-question" type="primary" @click="swiperRef.pre()">上一题</button>
			<button class="mypaper-foot__next-question" type="primary" @click="swiperRef.next()">下一题</button>
			<button class="mypaper-foot__finish" type="primary" @click="$router.go(-1)">返回</button>

			<xmky-popup ref="answerSheet" name="答题卡" class="answer-sheet">
				<view class="answer-sheet-head">
					<view class="answer-sheet-head__state answer-sheet-head__state--answer"></view>
					<text class="answer-sheet-head__name">已答</text>
				</view>
				<view class="answer-sheet-main">
					<template v-for="(examQuestion, index) in examQuestions" :index="index" :key="index">
						<text v-if="examQuestion.type === 1" @click="curQuestionIndex = index" class="answer-sheet_chapter-name">{{ examQuestion.chapterName }}</text>
						<view v-else @click="curQuestionIndex = index" :class="['answer-sheet__question-no', { 'answer-sheet__question-no--answer': isAnswer(examQuestion) }]">
							<text>{{ examQuestion.no }}</text>
						</view>
					</template>
				</view>
			</xmky-popup>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { ExamQuestion } from '@/ts/paper.d';
import { myMarkPaper } from '@/api/my-mark';
import { loginSysTime } from '@/api/login';
import { toChinaNum } from '@/util/numberUtil';
import { useUserStore } from '@/stores/user';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const userStore = useUserStore();
const swiperRef = ref(); // 滑块索引
const questionHeight = ref(0); // 试题滚动高度
const curQuestionIndex = ref(0); // 当前试题索引
const examQuestions = ref([{}] as ExamQuestion[]);

const answerSheet = ref(); // 答题卡

const host = ref(uni.getStorageSync('BASE_URL'));
const pageParm = reactive({
	examId: null,
	userId: null
});

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	pageParm.examId = options.examId;
	pageParm.userId = options.userId;

	await paperQuery();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.mypaper-main__scroll')
		.boundingClientRect((data: any) => {
			questionHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************计算属性相关*************************/
const questionNum = computed(() => {
	return examQuestions.value.reduce((total, examQuestion) => {
		return examQuestion.type === 2 ? total + 1 : total;
	}, 0);
}); // 试题数量
const isAnswer = computed(() => (examQuestion: ExamQuestion) => examQuestion.userAnswers?.some((userAnswer) => userAnswer.length)); // 是否作答

/************************事件相关*****************************/
// 试卷查询
async function paperQuery() {
	let { data } = await myMarkPaper({ examId: pageParm.examId, userId: pageParm.userId });
	let no = 1;
	examQuestions.value = (data as any[]).map((examQuestion: ExamQuestion) => {
		if (examQuestion.type === 2) {
			examQuestion.no = no++; // 处理题号
		}
		return examQuestion;
	});
}

// 预览图片
function preview(imgFileIds: number[], index: number) {
	let urls = imgFileIds.map((imgFileId) => `${host.value}/file/download?id=${imgFileId}`);
	uni.previewImage({
		current: index,
		urls: urls
	});
}
</script>

<style lang="scss" scoped>
.mypaper {
	height: inherit;
	background-color: white;
	display: flex;
	flex-direction: column;
	.mypaper-head {
		.mypaper-head__time {
			padding: 40rpx 20rpx 15rpx 20rpx;
			.mypaper-head__time-count-down {
				font-size: 30rpx;
				color: #8f939c;
			}
		}
		:deep(.mypaper-head__time-progress) {
			.uni-progress-inner-bar {
				height: 4rpx;
				margin-top: -2rpx;
			}
		}
	}
	.mypaper-main {
		flex: 1;
		display: flex;
		flex-direction: column;
		.mypaper-main__nav {
			display: flex;
			justify-content: space-between;
			margin: 40rpx 30rpx 20rpx 30rpx;
			align-items: center;
			.mypaper-main__nav-type {
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
		.mypaper-main__scroll {
			padding: 0rpx 30rpx;
			.mypaper-main__question-cur-no {
				font-size: 34rpx;
				color: #0d9df6;
			}
			// #ifdef MP-WEIXIN
			:deep(swiper) {
				height: 100%;
				swiper-item {
					& > view {
						height: 100%;
					}
				}
			}
			// #endif
			.user-files {
				display: flex;
				justify-content: left;
				align-items: flex-end;
				margin-top: 10rpx;
				.img-group {
					display: flex;
					flex-direction: column;
					.img-group__inner {
						display: flex;
					}
					.img {
						display: flex;
						flex-direction: column;
						align-items: center;
						.img__inner {
							display: inline-block;
							height: 180rpx;
							width: 180rpx;
							background-color: #fff;
							border: 1px solid #dcdfe6;
							border-radius: 6rpx;
							margin-left: 10rpx;
						}
						.img__txt {
							font-size: 24rpx;
							color: #000000;
						}
						.img__icon {
							margin-left: 10rpx;
						}
					}
					.upload-btn {
						display: flex;
						flex-direction: column;
						margin-top: 10rpx;
						.upload-btn__icon {
							width: 48rpx;
							height: 48rpx;
							background-color: #fcc129;
							border-radius: 50%;
							align-content: center;
							// #ifdef MP-WEIXIN
							text-align: center;
							// #endif
						}
						.upload-btn__txt {
							font-size: 22rpx;
							color: #000000;
						}
					}
				}
				.video-group {
					display: flex;
					flex-direction: column;
					margin-left: 20rpx;
					.video-group__inner {
					}
					.video {
						display: flex;
						flex-direction: column;
						align-items: center;
						.video__inner {
							width: 240rpx;
							height: 180rpx;
						}
						.video__txt {
							font-size: 24rpx;
							color: #000000;
						}
						.video__icon {
							margin-left: 10rpx;
						}
					}
					.upload-btn {
						display: flex;
						flex-direction: column;
						margin-top: 10rpx;
						.upload-btn__icon {
							width: 48rpx;
							height: 48rpx;
							background-color: #287ce7;
							border-radius: 50%;
							align-content: center;
							// #ifdef MP-WEIXIN
							text-align: center;
							// #endif
						}
						.upload-btn__txt {
							font-size: 22rpx;
							color: #000000;
							align-content: center;
						}
					}
				}
			}
			.question-qa-answer {
				margin-top: 20rpx;
				padding: 20rpx;
				background: #f6f7fc;
				border-radius: 10rpx;
				.question-qa-answer__label {
					margin-top: 20rpx;
					font-weight: bold;
					font-size: 30rpx;
					color: #999999;
				}
				.question-qa-answer__content {
					display: inline-block;
					margin-top: 20rpx;
					line-height: 60rpx;
				}
			}
		}
	}
	.mypaper-foot {
		display: flex;
		margin: 20rpx 30rpx;
		.answer-nav {
			flex: 1;
			display: flex;
			align-items: center;
			.answer-nav__icon {
				background-clip: text;
				color: transparent;
				background-image: linear-gradient(0deg, #259ff8, #04c7f2);
			}
			.answer-nav__outer {
				display: flex;
				flex-direction: column;
				margin-left: 10rpx;
				.answer-nav__inner {
					display: flex;
					justify-content: center;
					line-height: 20rpx;
					.answer-nav__cur {
						font-size: 24rpx;
						color: #0d9df6;
					}
					.answer-nav__total {
						font-size: 24rpx;
						color: #303133;
					}
				}
				.answer-nav__text {
					font-size: 24rpx;
					color: #8f939c;
				}
			}
		}
		.mypaper-foot__pre-question {
			width: 160rpx;
			height: 66rpx;
			line-height: 66rpx;
			font-size: 30rpx;
			color: #0d9df6;
			border-radius: 33rpx;
			margin: 0rpx 0rpx 0rpx 20rpx;
			background-color: white;
			border: 1rpx solid #04c7f2;
			&::after {
				border: 0rpx;
			}
		}
		.mypaper-foot__next-question {
			width: 160rpx;
			height: 66rpx;
			line-height: 66rpx;
			font-size: 30rpx;
			color: #0d9df6;
			border-radius: 33rpx;
			margin: 0rpx 0rpx 0rpx 20rpx;
			background-color: white;
			border: 1rpx solid #04c7f2;
			&::after {
				border: 0rpx;
			}
		}
		.mypaper-foot__finish {
			width: 160rpx;
			height: 66rpx;
			line-height: 66rpx;
			font-size: 32rpx;
			color: #ffffff;
			border-radius: 33rpx;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
			margin: 0rpx 0rpx 0rpx 20rpx;
			&::after {
				border: 0rpx;
			}
		}
		.answer-sheet {
			.answer-sheet-head {
				display: flex;
				justify-content: end;
				align-items: center;
				margin-bottom: 30rpx;
				.answer-sheet-head__state {
					width: 22rpx;
					height: 22rpx;
					margin-left: 30rpx;
					border-radius: 50%;
					background: #000;
					border: 1rpx solid #000;
				}
				.answer-sheet-head__state--default {
					background: #e9e9eb;
					border: 1rpx solid #8f939c;
				}
				.answer-sheet-head__state--answer {
					background: #d4e4ff;
					border: 1rpx solid #2979ff;
				}
				.answer-sheet-head__state--mark {
					background: #fdedd9;
					border: 1rpx solid #f3a73f;
				}
				.answer-sheet-head__name {
					margin-left: 10rpx;
					font-size: 30rpx;
					color: #8f939c;
				}
			}
			.answer-sheet-main {
				flex: 1;
				display: grid;
				grid-template-columns: repeat(10, 1fr);
				gap: 5px 5px;
				.answer-sheet_chapter-name {
					grid-column: 1 / -1;
					margin-top: 10px;
					font-size: 36rpx;
					color: #303133;
				}
				.answer-sheet__question-no {
					display: flex;
					justify-content: center;
					align-items: center;
					width: 60rpx;
					height: 60rpx;
					border-radius: 50%;
					font-size: 32rpx;
					color: #8f939c;
					border: 1px solid #8f939c;
					background-color: #e9e9eb;
				}
				.answer-sheet__question-no--answer {
					color: #2979ff;
					border: 1px solid #2979ff;
					background-color: #d4e4ff;
				}
				.answer-sheet__question-no--mark {
					color: #f3a73f;
					border: 1px solid #f3a73f;
					background-color: #fdedd9;
				}
			}
		}
	}
}
</style>
