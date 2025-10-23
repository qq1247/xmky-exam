<template>
	<view class="my-paper">
		<view class="my-paper__head">
			<view class="my-paper__time">
				<uni-icons customPrefix="iconfont" type="icon-shijian1" color="#231815" size="34rpx"></uni-icons>
				<xmky-count-down v-if="marking" :expireTime="exam.markEndTime" preTxt=" 剩余" color="#8F939C" @change="percent" class="my-paper__time-count-down"></xmky-count-down>
			</view>
			<progress :percent="timePercent" activeColor="#10AEFF" class="my-paper__time-progress" stroke-width="2" />
		</view>
		<view class="my-paper__main">
			<view class="my-paper__nav">
				<button class="my-paper__nav-type" type="primary">
					{{ objectiveQestions[curQuestionIndex]?.type === 1 ? '章节' : dictStore.getValue('QUESTION_TYPE', objectiveQestions[curQuestionIndex]?.questionType) }}
				</button>
			</view>
			<view :style="{ height: scrollHeight + 'px' }" class="my-paper__scroll">
				<scroll-view scroll-y="true" style="height: 100%">
					<xmky-question
						v-if="hasQuestionShow"
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
							<text class="question__no">{{ examQuestion.no }}、</text>
						</template>
						<template #title-post>
							<text>（{{ examQuestion.score }}分）</text>
						</template>
						<template #user-answer-post>
							<view v-if="examQuestion.questionType === 5 && examQuestion.markType === 2" class="user-files">
								<view class="img-group">
									<view class="img-group__inner">
										<view v-for="(imgFileId, index) in examQuestion.answerImgFileIds" :key="index" class="img">
											<image
												:src="`${host}/file/download?id=${imgFileId}`"
												mode="aspectFit"
												view
												@click="preview(examQuestion.answerImgFileIds, index)"
												class="img__inner"
											></image>
											<view>
												<text class="img__txt">图{{ toChinaNum(index + 1) }}</text>
											</view>
										</view>
									</view>
								</view>
								<view class="video-group">
									<view class="video-group__inner">
										<view v-if="examQuestion.answerVideoFileIds?.length > 0" class="video">
											<video :src="`${host}/file/download?id=${examQuestion.answerVideoFileIds[0]}`" class="video__inner"></video>
											<view>
												<text class="video__txt">视频</text>
											</view>
										</view>
									</view>
								</view>
							</view>
						</template>
						<template #user-score-post>
							<view v-if="examQuestion.markType === 2 && (examQuestion.questionType === 3 || examQuestion.questionType === 5)" class="question-qa-answer">
								<text class="question-qa-answer__label">阅题</text>
								<uni-forms :label-width="70" class="question-qa-answer__content">
									<uni-forms-item label="本题：" name="num" required>
										<view style="display: flex;align-items: center;"><!-- 微信小程序的结构，会把text放到外面，特殊处理一下 -->
											<uni-number-box v-model="examQuestion.userScore" :min="0" :max="examQuestion.score" :step="0.5" @change="() => score(examQuestion)" />
											<text>分</text>
										</view>
									</uni-forms-item>
									<uni-forms-item label="批语" name="num" required>
										<uni-easyinput type="textarea" v-model="examQuestion.remark" placeholder="请输入批语" :maxlength="48" @blur="() => score(examQuestion)" />
									</uni-forms-item>
								</uni-forms>
								<!-- <view>
										<text class="question-qa-answer__content">{{ examQuestion.remark }}</text>
									</view> -->
							</view>
						</template>
					</xmky-question>
				</scroll-view>
			</view>
		</view>
		<view class="my-paper__foot">
			<view class="answer-nav" @click="answerSheet.open()">
				<uni-icons customPrefix="iconfont" type="icon-datiqia" color="transparent" size="48rpx" class="answer-nav__icon"></uni-icons>
				<view class="answer-nav__outer">
					<view class="answer-nav__inner">
						<text class="answer-nav__cur">{{ objectiveQestions[curQuestionIndex]?.no || '-' }}</text>
						<text class="answer-nav__total">&nbsp;/&nbsp;{{ questionNum }}</text>
					</view>
					<text class="answer-nav__text">答题卡</text>
				</view>
			</view>
			<button
				v-if="curQuestionIndex >= 0"
				:class="['my-paper__btn', { 'my-paper__btn--inactive': curQuestionIndex <= 0 }]"
				@click="
					() => {
						if (curQuestionIndex <= 0) return;
						curQuestionIndex--;
					}
				"
			>
				上一题
			</button>
			<button
				v-if="curQuestionIndex <= objectiveQestions.length - 1"
				:class="['my-paper__btn', { 'my-paper__btn--inactive': curQuestionIndex >= objectiveQestions.length - 1 }]"
				@click="
					() => {
						if (curQuestionIndex >= objectiveQestions.length - 1) return;
						curQuestionIndex++;
					}
				"
			>
				下一题
			</button>
			<button v-if="marking" class="my-paper__foot__finish" type="primary" @click="finish">完成</button>

			<xmky-popup ref="answerSheet" name="答题卡" class="answer-sheet">
				<view class="answer-sheet-head">
					<view class="answer-sheet-head__state answer-sheet-head__state--mark"></view>
					<text class="answer-sheet-head__name">标记</text>
					<view class="answer-sheet-head__state answer-sheet-head__state--answer"></view>
					<text class="answer-sheet-head__name">已答</text>
				</view>
				<view class="answer-sheet-main">
					<template v-for="(examQuestion, index) in objectiveQestions" :index="index" :key="index">
						<text v-if="examQuestion.type === 1" @click="curQuestionIndex = index" class="answer-sheet_chapter-name">{{ examQuestion.chapterName }}</text>
						<view
							v-else
							@click="curQuestionIndex = index"
							:class="[
								'answer-sheet__question',
								{ 'answer-sheet__question--right': isRight(examQuestion) },
								{ 'answer-sheet__question--wrong': isWrong(examQuestion) },
								{ 'answer-sheet__question--half': isHalf(examQuestion) }
							]"
						>
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
import { MyExam } from '@/ts/myExam.d';
import { Exam } from '@/ts/exam.d';
import { ExamQuestion } from '@/ts/paper.d';
import { myMarkMarkList, myMarkPaper, myMarkScore, myMarkFinish } from '@/api/my-mark';
import { examGet } from '@/api/exam';
import { loginSysTime } from '@/api/login';
import { toChinaNum } from '@/util/numberUtil';
import { useUserStore } from '@/stores/user';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const userStore = useUserStore();
const pageParm = reactive({
	examId: null,
	userId: null
});
const swiperRef = ref(); // 滑块索引
const scrollHeight = ref(0); // 试题滚动高度
const timePercent = ref(0); // 时间进度条
const examQuestions = ref<ExamQuestion[]>([]); // 试卷
const timerId = ref(); // 延时器ID，用于防抖

const answerSheet = ref(); // 答题卡
const markList = ref<any[]>([]); // 阅卷列表
const curQuestionIndex = ref(0); // 当前试题索引

const host = ref(uni.getStorageSync('BASE_URL'));
const exam = reactive<Exam>({
	id: null,
	name: '',
	paperName: '',
	startTime: '',
	endTime: '',
	markStartTime: '',
	markEndTime: '',
	markState: null,
	scoreState: null,
	rankState: null,
	anonState: null,
	passScore: null,
	totalScore: null,
	markType: null,
	genType: null,
	loginType: null,
	sxes: [],
	state: null,
	userNum: null,
	limitMinute: null,
	retakeNum: null
});

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	pageParm.examId = options.examId;
	pageParm.userId = options.userId;

	examQuery();
	await markListQuery();
	await paperQuery();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.my-paper__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************计算属性相关*************************/
const marking = computed(() => exam.markState === 2); // 是否阅卷中
const objectiveQestions = computed(() => examQuestions.value.filter((examQuestion) => examQuestion.type === 2 && examQuestion.markType === 2)); // 主观题
const questionNum = computed(() => examQuestions.value.filter((examQuestion) => examQuestion.type === 2).length); // 试题数量
const hasQuestionShow = computed(() => objectiveQestions.value.length !== 0); // 是否可以显示
const isHalf = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore > 0 && question.userScore !== question.score); // 是否半对
const isRight = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === question.score); // 是否答对
const isWrong = computed(() => (question: ExamQuestion) => question.userScore != null && question.userScore === 0); // 是否答错
const unMarkQuestions = computed(() => objectiveQestions.value.filter((examQuestion) => examQuestion.markType === 2 && examQuestion.userScore == null)); // 未批阅试题
const examQuestion = computed(() => {
	return objectiveQestions.value[curQuestionIndex.value];
});

/************************事件相关*****************************/
// 考试查询
async function examQuery() {
	const { data } = await examGet({ id: pageParm.examId });
	exam.id = data.id;
	exam.name = data.name;
	exam.paperName = data.paperName;
	exam.startTime = data.startTime;
	exam.endTime = data.endTime;
	exam.markStartTime = data.markStartTime;
	exam.markEndTime = data.markEndTime;
	exam.markState = data.markState;
	exam.scoreState = data.scoreState;
	exam.rankState = data.rankState;
	exam.passScore = data.passScore;
	exam.totalScore = data.totalScore;
	exam.markType = data.markType;
	exam.genType = data.genType;
	exam.sxes = data.sxes;
	exam.state = data.state;
	exam.userNum = data.userNum;
	exam.limitMinute = data.limitMinute;
	exam.retakeNum = data.retakeNum;
}

// 获取阅卷列表
async function markListQuery() {
	const { data } = await myMarkMarkList({ examId: pageParm.examId });
	markList.value = data;
}

// 试卷查询
async function paperQuery() {
	const { data } = await myMarkPaper({
		examId: pageParm.examId,
		userId: pageParm.userId
	});
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

// 进度
function percent(curTime: Date) {
	let answerStartTime = new Date(exam.markStartTime.replaceAll('-', '/'));
	let answerEndTime = new Date(exam.markEndTime.replaceAll('-', '/'));
	let total = answerEndTime.getTime() - answerStartTime.getTime();
	let remind = answerEndTime.getTime() - curTime.getTime();
	timePercent.value = Math.floor(((total - remind) / total) * 100);
}

// 打分
async function score(examQuestion: ExamQuestion) {
	clearTimeout(timerId.value);
	timerId.value = setTimeout(async () => {
		if (!examQuestion.userScore) {
			examQuestion.userScore = 0;
		}

		const { code } = await myMarkScore({
			examId: pageParm.examId,
			userId: pageParm.userId,
			questionId: examQuestion.questionId,
			userScore: examQuestion.userScore,
			remark: examQuestion.remark || ''
		});

		if (code !== 200) {
			examQuestion.userScore = null; // 如果打分失败，把页面的分数清空掉，重新尝试打分
			return;
		}
	}, 500);
}

// 完成阅卷
async function finish() {
	if (unMarkQuestions.value.length) {
		uni.showToast({ title: `${unMarkQuestions.value.length}题未阅`, icon: 'error' });
		return;
	}

	uni.showLoading({ title: `正在上传成绩...`, mask: true });
	const { code } = await myMarkFinish({
		examId: pageParm.examId,
		userId: pageParm.userId
	});

	uni.hideLoading();
	if (code === 200) {
		uni.showToast({ title: `批阅成功`, icon: 'error' });
	}
}
</script>

<style lang="scss" scoped>
.my-paper {
	height: inherit;
	background-color: white;
	display: flex;
	flex-direction: column;
	.my-paper__head {
		.my-paper__time {
			padding: 40rpx 20rpx 15rpx 20rpx;
			.my-paper__time-count-down {
				font-size: 30rpx;
				color: #8f939c;
			}
		}
		:deep(.my-paper__time-progress) {
			.uni-progress-inner-bar {
				height: 4rpx;
				margin-top: -2rpx;
			}
		}
	}
	.my-paper__main {
		flex: 1;
		display: flex;
		flex-direction: column;
		.my-paper__nav {
			display: flex;
			justify-content: space-between;
			margin: 40rpx 30rpx 20rpx 30rpx;
			align-items: center;
			.my-paper__nav-type {
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
		.my-paper__scroll {
			padding: 0rpx 30rpx;
			.question__no {
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
					margin-top: 10rpx;
					:deep(.uni-forms-item__content) {
						display: flex;
						align-items: center;
					}
				}
			}
		}
	}
	.my-paper__foot {
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
		.my-paper__btn {
			width: 160rpx;
			height: 66rpx;
			line-height: 66rpx;
			font-size: 30rpx;
			color: #0d9df6;
			border-radius: 33rpx;
			margin: 0rpx 0rpx 0rpx 20rpx;
			background-color: white;
			border: 1rpx solid #04c7f2;
			&.my-paper__btn--inactive {
				color: #8f939c;
				border: 1rpx solid #8f939c;
			}
			&::after {
				border: 0rpx;
			}
		}
		.my-paper__foot__finish {
			width: 160rpx;
			height: 66rpx;
			line-height: 66rpx;
			font-size: 24rpx;
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
				.answer-sheet__question {
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
				.answer-sheet__question--right {
					color: #d1f2d7;
					border: 1px solid #d1f2d7;
					background-color: #18bc37;
				}
				.answer-sheet__question--wrong {
					color: #fad8d6;
					border: 1px solid #fad8d6;
					background-color: #e43d33;
				}
				.answer-sheet__question--half {
					color: #f3a73f;
					background: linear-gradient(to bottom, #fad8d6 50%, #d1f2d7 50%);
					border: 1rpx solid #18bc37;
				}
			}
		}
	}
}
</style>
