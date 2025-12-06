<template>
	<view class="mypaper" @touchstart="() => idleTracker.updateLastActive()" @click="idleTracker.updateLastActive()">
		<view class="mypaper__head">
			<!-- <view class="exer-info">
				<uni-icons customPrefix="iconfont" type="icon-tongzhi" color="#231815" size="34rpx"></uni-icons>
				<text class="exer-info__progress-rate">答题进度：23/55</text>
				<text class="exer-info__right-rate">正确率：45%</text>
			</view> -->
			<progress :percent="timePercent" activeColor="#10AEFF" class="progress" stroke-width="2" />
			<view class="exer-model">
				<view class="exer-model__inner">
					<button
						:class="['exer-model__option', { 'exer-model__option--answer': toolbars.answerShow }]"
						@click="
							toolbars.answerShow = true;
							commentEnable = false;
						"
					>
						答题
					</button>
					<button
						:class="['exer-model__option', { 'exer-model__option--remember': !toolbars.answerShow }]"
						@click="
							toolbars.answerShow = false;
							commentEnable = true;
							commentQuery();
						"
					>
						背题
					</button>
				</view>
			</view>
		</view>
		<view class="mypaper-main">
			<view class="toolbar">
				<button class="toolbar__type" type="primary">{{ dictStore.getValue('QUESTION_TYPE', curExamQuestion?.questionType) }}题</button>
				<view class="toolbar__btn-group">
					<!-- <view
						:class="['toolbar__btn', { 'toolbar__btn--selected': isMark(myExerQuestions[curQuestionIndex].questionId) }]"
						@click="mark(myExerQuestions[curQuestionIndex]?.questionId)"
					>
						<uni-icons
							customPrefix="iconfont"
							:type="isMark(myExerQuestions[curQuestionIndex].questionId) ? 'icon-biaoji2' : 'icon-igw-l-sign'"
							:color="isMark(myExerQuestions[curQuestionIndex].questionId) ? '#FF8C11' : '#231815'"
							size="30rpx"
						></uni-icons>
						<text>标记</text>
					</view> -->
					<view
						:class="['toolbar__btn', { 'toolbar__btn--selected': favQuestionIds.includes(_myExerQuestions[curQuestionIndex]?.questionId ?? -1) }]"
						@click="questionFav"
					>
						<uni-icons
							customPrefix="iconfont"
							:type="favQuestionIds.includes(_myExerQuestions[curQuestionIndex]?.questionId ?? -1) ? 'icon-lianxi-66' : 'icon-lianxi-64'"
							:color="favQuestionIds.includes(_myExerQuestions[curQuestionIndex]?.questionId ?? -1) ? '#FF8C11' : '#231815'"
							size="30rpx"
						></uni-icons>
						<text>收藏</text>
					</view>
					<view
						class="toolbar__btn toolbar__btn--grasp"
						v-if="wrongQuestions.some((wq) => wq.questionId === _myExerQuestions[curQuestionIndex]?.questionId)"
						@click="wrongQuestionReset"
					>
						<text class="toolbar__txt-err">
							答错{{ wrongQuestions.find((wq) => wq.questionId === _myExerQuestions[curQuestionIndex]?.questionId).wrongNum }}次&nbsp;
						</text>
						<uni-icons customPrefix="iconfont" type="icon-lianxi-61" color="#1EA1EE" size="30rpx"></uni-icons>
						<text>已掌握</text>
					</view>
				</view>
				<!--  -->
			</view>
			<view :style="{ height: questionHeight + 'px' }" class="mypaper-main__scroll">
				<scroll-view scroll-y="true" style="height: 100%">
					<xmky-question
						v-if="curExamQuestion"
						v-model="curExamQuestion.userAnswers"
						:type="curExamQuestion?.questionType"
						:title="curExamQuestion.title"
						:img-ids="curExamQuestion.imgFileIds"
						:video-id="curExamQuestion.videoFileId"
						:options="curExamQuestion.options"
						:answers="curExamQuestion.answers"
						:mark-type="curExamQuestion.markType"
						:score="curExamQuestion.score"
						:analysis="curExamQuestion.analysis"
						:user-score="curExamQuestion?.userScore"
						:answer-show="!toolbars.answerShow"
						:user-answer-show="toolbars.answerShow"
						:analysis-show="!toolbars.answerShow || curExamQuestion?.userScore != null"
						:editable="toolbars.answerShow && curExamQuestion?.userScore == null"
					>
						<template #title-pre>
							<text>{{ curQuestionIndex + 1 }}、</text>
						</template>
						<template #title-post>
							<text>（{{ curExamQuestion.score }}分）</text>
						</template>
					</xmky-question>
					<view v-if="commentEnable && !toolbars.answerShow">
						<view v-if="comments.length" class="comment">
							<!-- <text class="comment__txt">
								已分享思路：
								<text class="comment__value">{{ commentNum }}条</text>
							</text> -->
							<text class="comment__txt" style="color: #04c7f2" @click="popup.open()">点击这里分享思路</text>
							<view v-if="isHot" class="comment__txt" @click="isHot = false">
								<uni-icons customPrefix="iconfont" type="icon-liebiao" color="#8f939c" size="36rpx"></uni-icons>
								<text>按时间</text>
							</view>
							<view v-else class="comment__txt" @click="isHot = true">
								<uni-icons customPrefix="iconfont" type="icon-liebiao" color="#8f939c" size="36rpx"></uni-icons>
								<text>按热度</text>
							</view>
						</view>
						<text v-else class="comment">
							<text v-if="!toolbars.answerShow" class="comment__txt" @click="popup.open()">暂无思路，点击这里分享你的想法吧</text>
						</text>
						<xmky-comment
							v-for="(myComment, index) in _comments"
							:key="index"
							:name="myComment.userName || '匿名'"
							reply-name=""
							:content="myComment.content"
							:update-time="myComment.updateTime"
							:like-num="myComment.likeNum"
							:is-like="myComment.isLike"
							@like="commentLike(myComment)"
							@reply="(content: string) => commentReply(myComment, content)"
						>
							<xmky-comment
								v-for="(subMyComment, index) in myComment.children"
								:key="index"
								:name="subMyComment.userName || '匿名'"
								:reply-name="subMyComment.replyUserName"
								:content="subMyComment.content"
								:update-time="subMyComment.updateTime"
								:like-num="subMyComment.likeNum"
								:is-like="subMyComment.isLike"
								@like="commentLike(subMyComment)"
								@reply="(content: string) => commentReply(subMyComment, content)"
							></xmky-comment>
						</xmky-comment>
					</view>
				</scroll-view>
			</view>
		</view>
		<view class="mypaper-foot">
			<view class="answer-nav" @click="answerSheet.open()">
				<uni-icons customPrefix="iconfont" type="icon-datiqia" color="transparent" size="48rpx" class="answer-nav__icon"></uni-icons>
				<view class="answer-nav__outer">
					<view class="answer-nav__inner">
						<text v-if="_myExerQuestions && _myExerQuestions.length" class="answer-nav__cur">{{ curQuestionIndex + 1 || '-' }}</text>
						<text class="answer-nav__total">&nbsp;/&nbsp;{{ _myExerQuestions.length }}</text>
					</view>
					<text class="answer-nav__text">答题卡</text>
				</view>
			</view>
			<button
				class="question-pre"
				:disabled="curQuestionIndex <= 0"
				@click="
					curQuestionIndex--;
					questionView();
					if (commentEnable) commentQuery();
				"
			>
				上一题
			</button>
			<button
				class="question-next"
				type="primary"
				:disabled="curQuestionIndex >= _myExerQuestions.length - 1"
				@click="
					curQuestionIndex++;
					questionView();
					if (commentEnable) commentQuery();
				"
			>
				下一题
			</button>
			<button v-show="toolbars.answerShow && curExamQuestion?.userScore == null" class="question-confirm" type="primary" @click="answer">确认作答</button>

			<xmky-popup ref="answerSheet" name="答题卡" class="answer-sheet">
				<view class="answer-sheet__head">
					<!-- <view class="answer-sheet__state answer-sheet__state--mark"></view>
					<text class="answer-sheet__name">标记</text> -->
					<view class="answer-sheet__state answer-sheet__state--answered"></view>
					<text class="answer-sheet__name">已答</text>

					<view class="answer-sheet__state answer-sheet__state--right"></view>
					<text class="answer-sheet__name">答对</text>
					<view class="answer-sheet__state answer-sheet__state--wrong"></view>
					<text class="answer-sheet__name">答错</text>
					<view class="answer-sheet__state answer-sheet__state--half"></view>
					<text class="answer-sheet__name">半对</text>
				</view>
				<view class="answer-sheet__main">
					<template v-for="(myExerQuestion, index) in _myExerQuestions" :index="index" :key="index">
						<view
							@click="
								curQuestionIndex = index;
								questionView();
								answerSheet.close();
								if (commentEnable) commentQuery();
							"
							:class="[
								'answer-sheet__question-no',
								{ 'answer-sheet__question-no--answered': isAnswer(index) },
								{ 'answer-sheet__question-no--half': isHalf(index) },
								{ 'answer-sheet__question-no--right': isRight(index) },
								{ 'answer-sheet__question-no--wrong': isWrong(index) },
								{ 'answer-sheet__question-no--mark': isMark(myExerQuestion.questionId) } // 标记最大，放到最后覆盖
							]"
						>
							<text>{{ index + 1 }}</text>
						</view>
					</template>
				</view>
			</xmky-popup>
		</view>
	</view>
	<uni-popup ref="inputDialog" type="dialog">
		<uni-popup-dialog ref="inputClose" mode="input" title="自评分数" value="1.0" placeholder="主观题需要自评" @confirm="answerOfObjective"></uni-popup-dialog>
	</uni-popup>
	<xmky-popup ref="popup" name="分享思路" class="popup">
		<view class="popup__txt"></view>
		<uni-easyinput
			type="textarea"
			v-model="commentForm.content"
			:maxlength="128"
			autoHeight
			placeholder="我是这样解题的..."
			:styles="{ borderColor: '#04C7F2' }"
			primaryColor="#04C7F2"
			class="popup__input"
		></uni-easyinput>
		<button type="primary" class="popup__btn" @click="commentAdd">发送</button>
	</xmky-popup>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { Exer } from '@/ts/exer.d';
import { MyCommon } from '@/ts/my-comment.d';
import {
	myExerQuestion,
	myExerAnswer,
	myExerQuestionFav,
	myExerWrongQuestionReset,
	myExerFavQuestionList,
	myExerWrongQuestionList,
	myExerTrack,
	myExerQuestionList,
	myExerComment
} from '@/api/my-exer';
import { MyExerQuestion } from '@/ts/my-exer-question.d';
import { ExamQuestion } from '@/ts/paper.d';
import { useIdleTrack } from '@/composables/xmky-idle-track';
import { myCommentAdd, myCommentLike, myCommentList, myCommentReply } from '@/api/my-comment';

/************************变量定义相关***********************/
defineProps({
	// [Vue warn]: Extraneous non-props attributes (exerId, id) were passed to component but could not be automatically inherited because component renders fragment or text root nodes.
	exerId: String, // 根据实际类型调整
	id: String
});
const dictStore = useDictStore();
const pageParm = reactive({
	exerId: 0,
	id: 0
}); // 页面参数
const toolbars = reactive({
	randShow: false,
	answerShow: true
}); // 工具栏
const myExerQuestions = ref<MyExerQuestion[]>([]); // 我的练习试题
const questionCache = ref<Map<number, ExamQuestion>>(new Map()); // 试题缓存
const windowSize = ref<number>(10); // 滑动窗口大小（一次性加载当前试题前后十道题，使练习时页面切换更加顺畅）
const curQuestionIndex = ref<number>(-1); // 当前试题索引
const curExamQuestion = ref<ExamQuestion>();

const questionHeight = ref(0); // 试题滚动高度
const timePercent = ref(2); // 进度条
const exer = reactive<Exer>({
	id: null,
	name: '',
	questionBankId: undefined,
	userIds: [],
	orgIds: [],
	state: null,
	rmkState: null
}); // 练习

const answerSheet = ref(); // 答题卡
const marks = ref<number[]>([]); // 标记

const inputDialog = ref();
const idleTracker = useIdleTrack();
const favQuestionIds = ref<number[]>([]);
const wrongQuestions = ref<any[]>([]);

const commentEnable = ref(false);
const commentForm = reactive({
	content: ''
	//anon: false,
});

const comments = ref<MyCommon[]>([]); // 评论列表（带二级列表）
const commentNum = ref(0);
const isHot = ref(false);
const popup = ref();

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	idleTracker.startTracking({
		startupDelaySec: Math.floor(Math.random() * 30) + 1,
		trackingIntervalSec: 60,
		inactiveThresholdSec: 300,
		onStatusUpdate: async (isIdle) => {
			if (isIdle) return;

			await myExerTrack({
				exerId: options.exerId
			});
		}
	});

	// 导航栏显示练习名称
	pageParm.exerId = options.exerId;
	pageParm.id = options.id;

	await exerQuery();
	uni.setNavigationBarTitle({
		title: exer.name
	});

	// 加载试题列表
	await questionQuery();
	await favQuestionQuery();
	await wrongQuestionQuery();

	// 显示第一题
	curQuestionIndex.value = 0;
	questionView();
	commentStateQuery();
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
const _myExerQuestions = computed(() => {
	return [...myExerQuestions.value].sort((a, b) => {
		// if (toolbars.randShow) {
		// 	return (a.shuffleNo || 0) - (b.shuffleNo || 0);
		// } else {
		return (a.no || 0) - (b.no || 0);
		// }
	});
}); // 我的练习试题列表（随机或顺序）
const isAnswer = computed(() => (index: number) => {
	const questionId = _myExerQuestions.value[index].questionId as number;
	const examQuestion = questionCache.value.get(questionId);
	if (!examQuestion) {
		// 如果没有查看过这道题
		return false;
	}
	if (!examQuestion.userAnswers?.length) {
		// 如果没有作答
		return false;
	}
	if (examQuestion.questionType === 3 || examQuestion.questionType === 5) {
		// 如果是填空问答，一个字都没有
		if (examQuestion.userAnswers?.every((userAnswer) => userAnswer.length === 0)) {
			return false;
		}
	}
	return true;
}); // 是否已答
const isRight = computed(() => (index: number) => {
	const myExerQuestion = _myExerQuestions.value[index];
	if (myExerQuestion.userScore == null) {
		// 如果没有作答
		return false;
	}

	return myExerQuestion.userScore === myExerQuestion.score;
}); // 是否答对
const isHalf = computed(() => (index: number) => {
	const myExerQuestion = _myExerQuestions.value[index];
	if (myExerQuestion.userScore == null) {
		// 如果没有作答
		return false;
	}

	return myExerQuestion.userScore > 0 && myExerQuestion.userScore !== myExerQuestion.score;
}); // 是否半对
const isWrong = computed(() => (index: number) => {
	const myExerQuestion = _myExerQuestions.value[index];
	if (myExerQuestion.userScore == null) {
		// 如果没有作答
		return false;
	}

	return myExerQuestion.userScore === 0;
}); // 是否答错
const isMark = computed(() => (questionId: number) => {
	return marks.value.includes(questionId);
}); // 是否标记

const _comments = computed(() => {
	if (!isHot.value) {
		return comments.value;
	}

	return [...comments.value].sort((a, b) => {
		return (b.likeNum as number) - (a.likeNum as number);
	});
});
/************************事件相关*****************************/
// 练习查询
async function exerQuery() {
	// const { data } = await myExerExerGet({ exerId: pageParm.exerId });
	// exer.id = data.id;
	// exer.name = data.name;
	// exer.questionBankId = data.questionBankId;
	// exer.state = data.state;
	// exer.rmkState = data.rmkState;
}

// 试题列表查询
async function questionQuery() {
	const { data } = await myExerQuestionList({ id: pageParm.id });
	myExerQuestions.value.push(...data);
}
// 收藏试题列表查询
async function favQuestionQuery() {
	const { data } = await myExerFavQuestionList({});
	favQuestionIds.value.push(...data);
}
// 错题列表查询
async function wrongQuestionQuery() {
	const { data } = await myExerWrongQuestionList({});
	wrongQuestions.value.push(...data);
}

/**
 * 试题预览
 *
 * 利用滑动窗口策略，提前加载部分数据，使练习时页面切换更加顺畅
 */
async function questionView() {
	const curQuestionId = _myExerQuestions.value[curQuestionIndex.value]?.questionId as number;
	if (questionCache.value.has(curQuestionId)) {
		curExamQuestion.value = questionCache.value.get(curQuestionId);
		return;
	}

	const startIndex = Math.max(0, curQuestionIndex.value - windowSize.value);
	const endIndex = Math.min(_myExerQuestions.value.length - 1, curQuestionIndex.value + windowSize.value);
	const uncacheQuestionId = _myExerQuestions.value
		.slice(startIndex, endIndex + 1)
		.map((q) => q.questionId)
		.filter((questionId) => !questionCache.value.has(questionId as number));

	Promise.all(
		uncacheQuestionId.map((questionId) =>
			myExerQuestion({
				id: pageParm.id,
				questionId
			}).then((res: any) => {
				questionCache.value.set(questionId as number, res.data);
			})
		)
	).then(() => {
		curExamQuestion.value = questionCache.value.get(curQuestionId);
	});
}

// 答题
async function answer() {
	if (curExamQuestion.value.markType === 2) {
		inputDialog.value.open();
		return;
	}

	const { code, data } = await myExerAnswer({
		id: pageParm.id,
		questionId: curExamQuestion.value?.questionId,
		userAnswers: curExamQuestion.value?.userAnswers,
		userScore: ''
	});
	if (code !== 200) {
		return;
	}

	curExamQuestion.value!.userScore = data;
	_myExerQuestions.value[curQuestionIndex.value].userScore = data;

	if (_myExerQuestions.value[curQuestionIndex.value].userScore !== _myExerQuestions.value[curQuestionIndex.value].score) {
		const found = wrongQuestions.value.find((wq) => wq.questionId === _myExerQuestions.value[curQuestionIndex.value].questionId);
		if (found) {
			found.wrongNum += 1;
		} else {
			wrongQuestions.value.push({
				questionId: _myExerQuestions.value[curQuestionIndex.value].questionId,
				wrongNum: 1
			});
		}
	}
}

// 答题（主观题）
async function answerOfObjective(val: string) {
	const parsedValue = Number(val);
	let userScore: number = isNaN(parsedValue) ? 0 : Math.round(parsedValue * 100) / 100;
	if (userScore < 0) {
		userScore = 0;
	}
	if (userScore > curExamQuestion.value.score) {
		userScore = curExamQuestion.value.score;
	}

	const { code, data } = await myExerAnswer({
		id: pageParm.id,
		questionId: curExamQuestion.value?.questionId,
		userAnswers: curExamQuestion.value?.userAnswers,
		userScore: userScore
	});
	if (code !== 200) {
		return;
	}

	curExamQuestion.value!.userScore = data;
	_myExerQuestions.value[curQuestionIndex.value].userScore = data;

	if (_myExerQuestions.value[curQuestionIndex.value].userScore !== _myExerQuestions.value[curQuestionIndex.value].score) {
		_myExerQuestions.value[curQuestionIndex.value].wrongAnswerNum = (_myExerQuestions.value[curQuestionIndex.value].wrongAnswerNum as number) + 1;
	}
}

// 收藏试题
async function questionFav() {
	const { code } = await myExerQuestionFav({
		id: pageParm.id,
		questionId: curExamQuestion.value?.questionId
	});
	if (code !== 200) {
		return;
	}

	const index = favQuestionIds.value.indexOf(curExamQuestion.value?.questionId as number);
	if (index > -1) {
		favQuestionIds.value.splice(index, 1);
	} else {
		favQuestionIds.value.push(curExamQuestion.value?.questionId as number);
	}
}
// 错题重置
async function wrongQuestionReset() {
	const { code } = await myExerWrongQuestionReset({
		id: pageParm.id,
		questionId: curExamQuestion.value?.questionId
	});
	if (code !== 200) {
		return;
	}

	const currentId = _myExerQuestions.value[curQuestionIndex.value]?.questionId;
	if (currentId == null) return;

	wrongQuestions.value = wrongQuestions.value.filter((wq) => wq.questionId !== currentId);
}

// 评论状态查询
async function commentStateQuery() {
	const { code, data } = await myExerComment({
		exerId: pageParm.exerId
	});

	if (code !== 200) {
		return;
	}

	commentEnable.value = data === 1;
}

// 评论查询
async function commentQuery() {
	const curQuestionId = _myExerQuestions.value[curQuestionIndex.value]?.questionId as number;
	const { code, data } = await myCommentList({
		questionId: curQuestionId
	});

	if (code !== 200) {
		return;
	}

	const list = data;
	const treeList: object[] = []; // 列表转树形列表
	const treeMap: any = {};
	const idFiled = 'id';
	const parentField = 'rootId';
	for (let i = 0; i < list.length; i++) {
		list[i]['id'] = list[i][idFiled];
		treeMap[list[i]['id']] = list[i];
	}

	for (let i = 0; i < list.length; i++) {
		if (treeMap[list[i][parentField]] && list[i]['id'] !== list[i][parentField]) {
			if (!treeMap[list[i][parentField]]['children']) {
				treeMap[list[i][parentField]]['children'] = [];
			}
			treeMap[list[i][parentField]]['children'].push(list[i]);
		} else {
			treeList.push(list[i]);
		}
	}

	comments.value = treeList as any[];
	commentNum.value = data.length;
}

// 评论添加
async function commentAdd() {
	const curQuestionId = _myExerQuestions.value[curQuestionIndex.value]?.questionId as number;

	const { code } = await myCommentAdd({
		questionId: curQuestionId,
		content: commentForm.content
	});
	if (code !== 200) {
		return;
	}

	commentForm.content = '';
	popup.value.close();
	commentQuery();
}

// 评论点赞
async function commentLike(myComment: MyCommon) {
	const { code } = await myCommentLike({
		id: myComment.id
	});
	if (code !== 200) {
		return;
	}

	commentQuery();
}

// 评论回复
async function commentReply(parentMyComment: MyCommon, content: string) {
	const curQuestionId = _myExerQuestions.value[curQuestionIndex.value]?.questionId as number;

	const { code } = await myCommentReply({
		questionId: curQuestionId,
		content: content,
		parentId: parentMyComment.id
	});
	if (code !== 200) {
		return;
	}

	commentQuery();
}
</script>

<style lang="scss" scoped>
.mypaper {
	height: inherit;
	background-color: white;
	display: flex;
	flex-direction: column;
	.mypaper__head {
		.exer-info {
			padding: 40rpx 20rpx 15rpx 20rpx;
			.exer-info__progress-rate {
				margin-left: 20px;
			}
			.exer-info__right-rate {
				margin-left: 20px;
			}
		}
		:deep(.progress) {
			.uni-progress-inner-bar {
				height: 4rpx;
				margin-top: -2rpx;
			}
		}
		.exer-model {
			display: flex;
			justify-content: center;
			margin-top: 20rpx;
			.exer-model__inner {
				display: flex;
				.exer-model__option {
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
				.exer-model__option--answer {
					background: #1ea1ee;
					color: white;
				}
				.exer-model__option--remember {
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
			.toolbar__btn-group {
				display: flex;
				font-size: 26rpx;
				.toolbar__btn {
					margin-left: 30rpx;
					color: #231815;
					.toolbar__txt-err {
						color: #ff6960;
					}
					&.toolbar__btn--selected {
						color: #ff8c11;
					}
					&.toolbar__btn--grasp {
						color: #1ea1ee;
					}
				}
			}
		}
		.mypaper-main__scroll {
			padding: 0rpx 30rpx;
			.question__no {
				font-size: 34rpx;
				color: #0d9df6;
			}
			.comment {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-top: 30rpx;
				margin-bottom: 30rpx;
				padding-top: 30rpx;
				border-top: 1px solid #e5e5e5;
				.comment__txt {
					font-size: 34rpx;
					color: #8f939c;
					.comment__value {
						color: #303133;
					}
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
		.question-pre {
			question-nextht: 66rpx;
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
		.question-next {
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
		.question-confirm {
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
			.answer-sheet__head {
				display: flex;
				justify-content: end;
				align-items: center;
				margin-bottom: 30rpx;
				.answer-sheet__state {
					width: 22rpx;
					height: 22rpx;
					margin-left: 30rpx;
					border-radius: 50%;
					background: #000;
					border: 1rpx solid #000;
					background: #e9e9eb;
					border: 1rpx solid #8f939c;
				}
				.answer-sheet__state--answered {
					background: #d4e4ff;
					border: 1rpx solid #2979ff;
				}
				.answer-sheet__state--mark {
					background: #fdedd9;
					border: 1rpx solid #f3a73f;
				}
				.answer-sheet__state--right {
					background: #d1f2d7;
					border: 1rpx solid #18bc37;
				}
				.answer-sheet__state--wrong {
					background: #fad8d6;
					border: 1rpx solid #e43d33;
				}
				.answer-sheet__state--half {
					background: linear-gradient(to bottom, #fad8d6 50%, #d1f2d7 50%);
					border: 1rpx solid #18bc37;
				}
				.answer-sheet__name {
					margin-left: 10rpx;
					font-size: 30rpx;
					color: #8f939c;
				}
			}
			.answer-sheet__main {
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
				.answer-sheet__question-no--answered {
					color: #2979ff;
					border: 1px solid #2979ff;
					background-color: #d4e4ff;
				}
				.answer-sheet__question-no--right {
					color: #18bc37;
					border: 1px solid #18bc37;
					background-color: #d1f2d7;
				}
				.answer-sheet__question-no--wrong {
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
.popup {
	display: flex;
	.popup__txt {
		margin-bottom: 20rpx;
		font-size: 30rpx;
		color: #303133;
	}
	.popup__name {
		color: #8f939c;
	}
	.popup__input {
	}
	.popup__btn {
		margin-top: 20rpx;
		// width: 628rpx;
		height: 100rpx;
		line-height: 100rpx;
		border-radius: 50px;
		background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
	}
}
</style>
