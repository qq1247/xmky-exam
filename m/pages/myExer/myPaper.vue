<template>
	<view class="mypaper">
		<view class="mypaper-head">
			<view class="mypaper-head__time">
				<uni-icons customPrefix="iconfont" type="icon-shijian1" color="#231815" size="34rpx"></uni-icons>
				<xm-count-down :expireTime="myExer.endTime" @end="finish" preTxt=" 剩余" color="#8F939C" @change="percent" class="mypaper-head__time-count-down"></xm-count-down>
			</view>
			<progress :percent="timePercent" activeColor="#10AEFF" class="mypaper-head__time-progress" stroke-width="2" />
			<view class="mypaper-head__model-wrap">
				<view class="mypaper-head__model">
					<button :class="['mypaper-head__model-opt', { 'mypaper-head__model-opt--answer': answerModel === 1 }]" @click="answerModel = 1">答题</button>
					<button :class="['mypaper-head__model-opt', { 'mypaper-head__model-opt--remember': answerModel === 2 }]" @click="answerModel = 2">背题</button>
				</view>
			</view>
		</view>
		<view class="mypaper-main">
			<view class="mypaper-main__nav">
				<button v-if="questions && questions.length" class="mypaper-main__nav-type" type="primary">
					{{ dictStore.getValue('QUESTION_TYPE', questions[curQuestionIndex].type) }}
				</button>
				<uni-icons
					customPrefix="iconfont"
					:type="isMark(questions[curQuestionIndex]) ? 'icon-biaoji2' : 'icon-igw-l-sign'"
					:color="isMark(questions[curQuestionIndex]) ? '#FF8C11' : '#231815'"
					size="36rpx"
					@click="mark(questions[curQuestionIndex].no)"
				></uni-icons>
			</view>
			<xm-swiper ref="swiperRef" v-model="curQuestionIndex" :items="questions" :style="{ height: questionHeight + 'px' }" class="mypaper-main__scroll">
				<template #default="{ item: question }">
					<scroll-view scroll-y="true" style="height: 100%">
						<xm-question
							v-model="question.userAnswers"
							:type="question.type"
							:markType="question.markType"
							:title="question.title"
							:score="question.score"
							:answers="question.answers"
							:userScore="question.userScore"
							:options="question.options"
							:analysis="question.analysis"
							:editable="exering(question)"
							:analysisShow="analysisShow(question)"
							:answerShow="answerShow(question)"
							@change="(userAnswers: string[]) =>autoNext(question, userAnswers)"
						>
							<template #title-pre>
								<text class="mypaper-main__question-cur-no">{{ question.no }}、</text>
							</template>
							<template #title-post>
								<text>（{{ question.score }}分）</text>
							</template>
						</xm-question>
					</scroll-view>
				</template>
			</xm-swiper>
		</view>
		<view class="mypaper-foot">
			<view class="answer-nav" @click="answerSheet.open()">
				<uni-icons customPrefix="iconfont" type="icon-datiqia" color="transparent" size="48rpx" class="answer-nav__icon"></uni-icons>
				<view class="answer-nav__outer">
					<view class="answer-nav__inner">
						<text v-if="questions && questions.length" class="answer-nav__cur">{{ questions[curQuestionIndex].no || '-' }}</text>
						<text class="answer-nav__total">&nbsp;/&nbsp;{{ questionNum }}</text>
					</view>
					<text class="answer-nav__text">答题卡</text>
				</view>
			</view>
			<button class="mypaper-foot__pre-question" type="primary" @click="pre">上一题</button>
			<button class="mypaper-foot__next-question" type="primary" @click="next">下一题</button>
			<button class="mypaper-foot__finish" type="primary" @click="finish">完成</button>

			<xm-popup ref="answerSheet" name="答题卡" class="answer-sheet">
				<view class="answer-sheet-head">
					<view class="answer-sheet-head__state answer-sheet-head__state--mark"></view>
					<text class="answer-sheet-head__name">标记</text>
					<view class="answer-sheet-head__state answer-sheet-head__state--answer"></view>
					<text class="answer-sheet-head__name">已答</text>

					<view class="answer-sheet-head__state answer-sheet-head__state--true"></view>
					<text class="answer-sheet-head__name">答对</text>
					<view class="answer-sheet-head__state answer-sheet-head__state--false"></view>
					<text class="answer-sheet-head__name">答错</text>
					<view class="answer-sheet-head__state answer-sheet-head__state--half"></view>
					<text class="answer-sheet-head__name">半对</text>
				</view>
				<view class="answer-sheet-main">
					<template v-for="(question, index) in questions" :index="index" :key="index">
						<view
							@click="curQuestionIndex = index"
							:class="[
								'answer-sheet__question-no',
								{ 'answer-sheet__question-no--answer': isAnswer(question) },
								{ 'answer-sheet__question-no--half': isHalf(question) },
								{ 'answer-sheet__question-no--true': isTrue(question) },
								{ 'answer-sheet__question-no--false': isFalse(question) },
								{ 'answer-sheet__question-no--mark': isMark(question) } // 标记最大，放到最后覆盖
							]"
						>
							<text>{{ question.no }}</text>
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
import { MyExer } from '@/ts/myExer.d';
import { Question } from '@/ts/question.d';
import { myExerGet, myExerQuestionList2 } from '@/api/myExer';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const swiperRef = ref(); // 滑块索引
const questionHeight = ref(0); // 试题滚动高度
const timePercent = ref(0); // 时间进度条
const curQuestionIndex = ref(0); // 当前试题索引
const myExer = reactive<MyExer>({
	id: null,
	name: '',
	startTime: '',
	endTime: ''
}); // 我的练习
const questions = ref<Question[]>([{} as Question]); // 试题列表
const answerModel = ref(1); // 答题模式（1：答题；2：背题）
const answerSheet = ref(); // 答题卡
const marks = ref<number[]>([]); // 标记

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	myExer.id = options.exerId;
	await questionQuery();
	await myExerQuery();

	uni.setNavigationBarTitle({
		title: myExer.name
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
const exering = computed(() => (question: Question) => answerModel.value === 1 && question.userScore == null); // 练习中
const analysisShow = computed(() => (question: Question) => answerModel.value === 2 || question.userScore != null); // 解析显示
const answerShow = computed(() => (question: Question) => answerModel.value === 2); // 标准答案显示
const questionNum = computed(() => questions.value.length); // 试题数量
const userAnswerNum = computed(() => questions.value.reduce((total, question) => (question.userAnswers.some((userAnswer) => userAnswer.length) ? total + 1 : total), 0)); // 用户答题数量

const isAnswer = computed(() => (question: Question) => question.userAnswers?.some((userAnswer) => userAnswer.length)); // 是否答题
const isMark = computed(() => (question: Question) => marks.value.includes(question.no)); // 是否标记
const isHalf = computed(() => (question: Question) => question.userScore != null && question.userScore > 0 && question.userScore !== question.score); // 是否半对
const isTrue = computed(() => (question: Question) => question.userScore != null && question.userScore === question.score); // 是否答对
const isFalse = computed(() => (question: Question) => question.userScore != null && question.userScore === 0); // 是否答错

/************************事件相关*****************************/
// 我的练习查询
async function myExerQuery() {
	let { data } = await myExerGet({ exerId: myExer.id });
	myExer.name = data.name;
	myExer.startTime = data.startTime;
	myExer.endTime = data.endTime;
}

// 试题查询
async function questionQuery() {
	let { data } = await myExerQuestionList2({ exerId: myExer.id });
	questions.value = (data as any[]).map((question: Question, index) => {
		question.no = index + 1;
		question.userAnswers = []; // 接口没有，填充
		question.userScore = null; // 接口没有，填充
		return question;
	});
}

// 打分
function scoring(question: Question) {
	if (answerModel.value === 2) {
		return;
	}
	// 如果是单选或判断，并且没打分，并且有答案
	if ((question.type === 1 || question.type === 4) && !scored(question) && answered(question)) {
		// 智能打分
		question.userScore = 0;
		if (question.userAnswers[0] === question.answers[0]) {
			question.userScore = question.score;
		}
		return;
	}

	// 如果是多选，并且没打分，并且有答案
	if (question.type === 2 && !scored(question) && answered(question)) {
		// 智能打分
		question.userScore = 0;
		let include = question.userAnswers.every((userAnswer: string) => question.answers.includes(userAnswer));
		if (include && question.answers.length === question.userAnswers.length) {
			question.userScore = question.score; // 全对满分
		} else if (include) {
			question.userScore = question.scores[0]; // 半对指定分
		}
		return;
	}
}

// 已打分
function scored(question: Question) {
	return question.userScore != null;
}

// 有答案
function answered(question: Question) {
	return (question.userAnswers || []).some((userAnswer) => userAnswer.length);
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
	let startTime = new Date(myExer.startTime.replaceAll('-', '/'));
	let endTime = new Date(myExer.endTime.replaceAll('-', '/'));
	let total = endTime.getTime() - startTime.getTime();
	let remind = endTime.getTime() - curTime.getTime();
	timePercent.value = Math.floor(((total - remind) / total) * 100);
}

// 上一题
async function pre() {
	// 如果是答题模式，并且是单选多选判断，并且已答题，并且没打分
	let curQuestion = questions.value[curQuestionIndex.value];
	if (answerModel.value === 1 && (curQuestion.type === 1 || curQuestion.type === 2 || curQuestion.type === 4) && answered(curQuestion) && !scored(curQuestion)) {
		// 智能打分
		scoring(curQuestion);
		// 如果不是满分，不进入下一题（第二次就打分了，不在进入）
		if (curQuestion.score != curQuestion.userScore) {
			return;
		}
	}

	curQuestionIndex.value <= 0 ? (curQuestionIndex.value = questionNum.value - 1) : curQuestionIndex.value--; // 划到上一题
}

// 下一题
async function next() {
	let curQuestion = questions.value[curQuestionIndex.value];
	// 如果是单选多选判断，并且已答题，并且没打分
	if ((curQuestion.type === 1 || curQuestion.type === 2 || curQuestion.type === 4) && answered(curQuestion) && !scored(curQuestion)) {
		// 智能打分
		scoring(curQuestion);
		// 如果不是满分，不进入下一题（第二次就打分了，不在进入）
		if (curQuestion.score != curQuestion.userScore) {
			return;
		}
	}

	curQuestionIndex.value >= questionNum.value - 1 ? (curQuestionIndex.value = 0) : curQuestionIndex.value++;
}

// 自动下一题
function autoNext(question: Question, userAnswers: string[]) {
	// 如果是单选或判断，并且是答题模式
	if ((question.type === 1 || question.type === 4) && answerModel.value === 1) {
		// 智能打分
		scoring(question);
		// 并且答对，自动下一题
		if (question.score === question.userScore) {
			setTimeout(() => {
				next();
			}, 500);
		}
	}
}

// 完成
async function finish() {
	let unAnswerNum = questionNum.value - userAnswerNum.value;
	uni.showModal({
		title: '提示消息',
		content: unAnswerNum ? `剩余${unAnswerNum}道题未答，是否退出？` : `共${questionNum.value}道题全部已答，是否退出？`,
		showCancel: true,
		success: async (res) => {
			if (res.cancel) {
				return;
			}

			uni.switchTab({ url: '/pages/home/home' });
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
		.mypaper-head__model-wrap {
			display: flex;
			justify-content: center;
			margin-top: 20rpx;
			.mypaper-head__model {
				display: flex;
				.mypaper-head__model-opt {
					width: 120rpx;
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
				.mypaper-head__model-opt--answer {
					background: #1ea1ee;
					color: white;
				}
				.mypaper-head__model-opt--remember {
					background: #1ea1ee;
					color: white;
				}
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
				.answer-sheet-head__state--true {
					background: #d1f2d7;
					border: 1rpx solid #18bc37;
				}
				.answer-sheet-head__state--false {
					background: #fad8d6;
					border: 1rpx solid #e43d33;
				}
				.answer-sheet-head__state--half {
					background: linear-gradient(to bottom, #fad8d6 50%, #d1f2d7 50%);
					border: 1rpx solid #18bc37;
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
				.answer-sheet__question-no--true {
					color: #18bc37;
					border: 1px solid #18bc37;
					background-color: #d1f2d7;
				}
				.answer-sheet__question-no--false {
					color: #e43d33;
					border: 1px solid #e43d33;
					background-color: #fad8d6;
				}
				.answer-sheet__question-no--half {
					color: #18bc37;
					border: 1px solid #18bc37;
					background: linear-gradient(to bottom, #fad8d6 50%, #d1f2d7 50%);
				}
				.answer-sheet__question-no--mark {
					color: #f3a73f;
					background: initial;
					border: 1px solid #f3a73f;
					background-color: #fdedd9;
				}
			}
		}
	}
}
</style>
