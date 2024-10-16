<template>
	<view class="mypaper">
		<view class="mypaper-head">
			<view class="mypaper-head__time">
				<uni-icons customPrefix="iconfont" type="icon-shijian1" color="#231815" size="34rpx"></uni-icons>
				<xm-count-down
					:expireTime="myExam.answerEndTime"
					@end="finishAuto"
					preTxt=" 剩余"
					color="#8F939C"
					@change="percent"
					class="mypaper-head__time-count-down"
				></xm-count-down>
			</view>
			<progress :percent="timePercent" activeColor="#10AEFF" class="mypaper-head__time-progress" stroke-width="2" />
		</view>
		<view class="mypaper-main">
			<view class="mypaper-main__nav">
				<button class="mypaper-main__nav-type" type="primary">
					{{ examQuestions[curQuestionIndex].type === 1 ? '章节' : dictStore.getValue('QUESTION_TYPE', examQuestions[curQuestionIndex].questionType) }}
				</button>
				<uni-icons
					v-if="examQuestions[curQuestionIndex].type === 2"
					customPrefix="iconfont"
					:type="isMark(examQuestions[curQuestionIndex]) ? 'icon-biaoji2' : 'icon-igw-l-sign'"
					:color="isMark(examQuestions[curQuestionIndex]) ? '#FF8C11' : '#231815'"
					size="36rpx"
					@click="mark(examQuestions[curQuestionIndex].no)"
				></uni-icons>
			</view>
			<xm-swiper ref="swiperRef" v-model="curQuestionIndex" :items="examQuestions" :style="{ height: questionHeight + 'px' }" class="mypaper-main__scroll">
				<template #default="{ item: examQuestion }">
					<scroll-view scroll-y="true" style="height: 100%">
						<xm-question
							v-if="examQuestion.type === 2"
							v-model="examQuestion.userAnswers"
							:type="examQuestion.questionType"
							:markType="examQuestion.markType"
							:title="examQuestion.title"
							:score="examQuestion.score"
							:answers="examQuestion.answers"
							:userScore="examQuestion.userScore"
							:options="examQuestion.options"
							:analysis="examQuestion.analysis"
							:editable="examing"
							:analysisShow="analysisShow"
							@change="(answers: string[]) => answer(examQuestion, answers)"
						>
							<template #title-pre>
								<text class="mypaper-main__question-cur-no">{{ examQuestion.no }}、</text>
							</template>
							<template #title-post>
								<text>（{{ examQuestion.score }}分）</text>
							</template>
						</xm-question>
						<view v-else>
							<view>{{ examQuestion.chapterName }}</view>
							<view>{{ examQuestion.chapterTxt }}</view>
						</view>
					</scroll-view>
				</template>
			</xm-swiper>
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
			<button v-if="examing" class="mypaper-foot__finish" type="primary" @click="finish">交卷</button>
			<button v-if="!examing" class="mypaper-foot__finish" type="primary" @click="toHome">返回</button>

			<xm-popup ref="answerSheet" name="答题卡" class="answer-sheet">
				<view class="answer-sheet-head">
					<view class="answer-sheet-head__state answer-sheet-head__state--mark"></view>
					<text class="answer-sheet-head__name">标记</text>
					<view class="answer-sheet-head__state answer-sheet-head__state--answer"></view>
					<text class="answer-sheet-head__name">已答</text>
				</view>
				<view class="answer-sheet-main">
					<template v-for="(examQuestion, index) in examQuestions" :index="index" :key="index">
						<text v-if="examQuestion.type === 1" @click="curQuestionIndex = index" class="answer-sheet_chapter-name">{{ examQuestion.chapterName }}</text>
						<view
							v-else
							@click="curQuestionIndex = index"
							:class="[
								'answer-sheet__question-no',
								{ 'answer-sheet__question-no--answer': isAnswer(examQuestion) },
								{ 'answer-sheet__question-no--mark': isMark(examQuestion) }
							]"
						>
							<text>{{ examQuestion.no }}</text>
						</view>
					</template>
				</view>
			</xm-popup>
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
import { myExamGet, myExamExamGet, myExamPaper, myExamAnswer, myExamFinish } from '@/api/myExam';
import { loginSysTime } from '@/api/login';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const swiperRef = ref(); // 滑块索引
const questionHeight = ref(0); // 试题滚动高度
const timePercent = ref(0); // 时间进度条
const curQuestionIndex = ref(0); // 当前试题索引
const exam = reactive<Exam>({
	id: null, // 考试信息
	name: '',
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
	limitMinute: null
});
const myExam = reactive<MyExam>({
	examId: null, // 我的考试信息
	userId: null,
	answerStartTime: '',
	answerEndTime: '',
	markStartTime: '',
	markEndTime: '',
	objectiveScore: null,
	totalScore: null,
	state: null,
	markState: null,
	answerState: null,
	no: null
});
const examQuestions = ref([{}] as ExamQuestion[]);
const timerId = ref(); // 延时器ID，用于防抖

const answerSheet = ref(); // 答题卡
const marks = ref<number[]>([]); // 标记

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	exam.id = options.examId;

	await paperQuery(); //先打开试卷，生成考试时间，改为考试中，在查询我的考试信息
	await myExamQuery(); // 放在paperQuery后面执行

	await examQuery();
	uni.setNavigationBarTitle({
		title: exam.name
	});
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
const examing = computed(() => (myExam.state === 1 && myExam.markState != 3) || myExam.state === 2); // 考试中
const analysisShow = computed(() => {
	return (
		(exam.scoreState == 1 && exam.markState == 3) || // 如果是考试结束后显示成绩，需要等到考试结束
		(exam.scoreState == 3 && myExam.markState == 3) // 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
	);
}); // 结果显示
const questionNum = computed(() => {
	return examQuestions.value.reduce((total, examQuestion) => {
		return examQuestion.type === 2 ? total + 1 : total;
	}, 0);
}); // 试题数量
const userAnswerNum = computed(() => {
	return examQuestions.value.reduce((total, examQuestion) => {
		return examQuestion.type === 2 && examQuestion.userAnswers.some((userAnswer) => userAnswer.length > 0) ? total + 1 : total;
	}, 0);
}); // 用户答题数量
const isAnswer = computed(() => (examQuestion: ExamQuestion) => examQuestion.userAnswers?.some((userAnswer) => userAnswer.length)); // 是否作答
const isMark = computed(() => (examQuestion: ExamQuestion) => marks.value.includes(examQuestion.no)); // 是否标记

/************************事件相关*****************************/
// 考试查询
async function examQuery() {
	let { data } = await myExamExamGet({ examId: exam.id });
	exam.name = data.name;
	exam.startTime = data.startTime;
	exam.endTime = data.endTime;
	exam.markStartTime = data.markStartTime;
	exam.markEndTime = data.markEndTime;
	exam.markState = data.markState;
	exam.scoreState = data.scoreState;
	exam.rankState = data.rankState;
	exam.anonState = data.anonState;
	exam.passScore = data.passScore;
	exam.totalScore = data.totalScore;
	exam.markType = data.markType;
	exam.genType = data.genType;
	exam.sxes = data.sxes;
	exam.state = data.state;
	exam.userNum = data.userNum;
	exam.limitMinute = data.limitMinute;
}
// 我的考试查询
async function myExamQuery() {
	let { data } = await myExamGet({ examId: exam.id });
	myExam.examId = data.examId;
	myExam.userId = data.userId;
	myExam.answerStartTime = data.answerStartTime;
	myExam.answerEndTime = data.answerEndTime;
	myExam.markStartTime = data.markStartTime;
	myExam.markEndTime = data.markEndTime;
	myExam.objectiveScore = data.objectiveScore;
	myExam.totalScore = data.totalScore;
	myExam.state = data.state;
	myExam.markState = data.markState;
	myExam.answerState = data.answerState;
	myExam.no = data.no;
}

// 试卷查询
async function paperQuery() {
	let { data } = await myExamPaper({ examId: exam.id });
	let no = 1;
	examQuestions.value = (data as any[]).map((examQuestion: ExamQuestion) => {
		if (examQuestion.type === 2) {
			examQuestion.no = no++; // 处理题号
		}
		return examQuestion;
	});
}

// 作答
function answer(examQuestion: ExamQuestion, answers: string[]) {
	clearTimeout(timerId.value);
	timerId.value = setTimeout(async () => {
		await myExamAnswer({
			examId: exam.id,
			questionId: examQuestion.questionId,
			answers: answers
		});
	}, 500);
}

// 标记
function mark(no: number) {
	if (marks.value.includes(no)) {
		marks.value = marks.value.filter((_no) => _no != no);
	} else {
		marks.value.push(no);
	}
}

// 进度
function percent(curTime: Date) {
	let answerStartTime = new Date(myExam.answerStartTime.replaceAll('-', '/'));
	let answerEndTime = new Date(myExam.answerEndTime.replaceAll('-', '/'));
	let total = answerEndTime.getTime() - answerStartTime.getTime();
	let remind = answerEndTime.getTime() - curTime.getTime();
	timePercent.value = Math.floor(((total - remind) / total) * 100);
}

// 交卷
async function finish() {
	let unAnswerNum = questionNum.value - userAnswerNum.value;
	uni.showModal({
		title: '提示消息',
		content: unAnswerNum ? `剩余${unAnswerNum}道题未答，是否交卷？` : `共${questionNum.value}道题全部已答，是否交卷？`,
		showCancel: true,
		success: async (res) => {
			if (res.cancel) {
				return;
			}

			await finishAuto();
		}
	});
}

// 自动交卷
async function finishAuto() {
	if (!examing.value) {
		return;
	}

	// 交卷
	uni.showLoading({ title: '交卷中', mask: true });
	await myExamFinish({ examId: exam.id });

	// 如果考试成绩是不公布，返回首页
	if (exam.scoreState === 2) {
		uni.hideLoading();
		prompt('考试已结束');
		return;
	}

	// 如果考试成绩是交卷后公布，并且是主观题试卷，提示后返回首页（需人工阅卷）
	// if (exam.scoreState === 3 && exam.markType === 2) {
	// 	uni.hideLoading();// 情况不存在，业务上做了主观题试卷不允许交卷后公布，允许也没有意义
	// 	prompt(`主观题未阅，请于${exam.markEndTime}后查询`);
	// 	return;
	// }

	// 如果考试成绩是交卷后公布，并且是客观题试卷，查询成绩
	if (exam.scoreState === 3 && exam.markType === 1) {
		uni.hideLoading();
		await scoresView();
		return;
	}

	// 如果考试成绩是考试结束后公布，但是提前交卷，提示后返回首页
	let { data } = await loginSysTime();
	let curTime = new Date(data.replaceAll('-', '/'));
	let examEndTime = new Date(exam.endTime.replaceAll('-', '/'));
	let remainSecond = (examEndTime.getTime() - curTime.getTime()) / 1000;
	if (exam.scoreState === 1 && remainSecond > 3) {
		uni.hideLoading();
		prompt(`整场考试未结束，请于${exam.endTime}后查询`);
		return;
	}

	// 如果考试成绩是考试结束后公布，并且在接近整场考试结束时交卷，查询成绩
	if (exam.scoreState === 1 && remainSecond <= 3) {
		uni.hideLoading();
		await scoresView(); // 主观题试卷可以先看客观题得分
		return;
	}
}

// 成绩预览
async function scoresView(queryCount: number = 6) {
	uni.showLoading({ title: '成绩查询中', mask: true });
	for (let i = 0; i < queryCount; i++) {
		if (i >= 5) {
			prompt(`查询失败，请稍后再次查询`);
			return;
		}

		await new Promise((res) => setTimeout(res, 1000));
		await myExamQuery();

		if (myExam.markState == 2) {
			uni.hideLoading(); // 主观题需人工阅卷，先展示客观题得分
			prompt(`客观题${myExam.objectiveScore}分，总分请于${exam.markEndTime}后查询`);
			return;
		}
		if (myExam.markState == 3) {
			uni.hideLoading();
			prompt(`客观题${myExam.objectiveScore}分，总${myExam.totalScore}分, 成绩${dictStore.getValue('ANSWER_STATE', myExam.answerState)}`);
			return;
		}
	}
}

// 提示
function prompt(msg: string) {
	uni.showModal({
		title: '提示消息',
		content: msg,
		confirmText: '查看详情',
		cancelText: '返回首页',
		success: function (res) {
			if (res.confirm) {
				uni.navigateTo({ url: `/pages/myExam/myRead?examId=${exam.id}` });
			} else if (res.cancel) {
				uni.switchTab({ url: '/pages/home/home' });
			}
		}
	});
}

// 去首页
function toHome() {
	uni.switchTab({ url: '/pages/home/home' });
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
