<template>
	<view class="exam-statis">
		<view class="exam-statis__head">
			<!-- <uni-search-bar
				v-model="queryForm.examName"
				bgColor="#fff"
				clearButton="auto"
				cancelButton="none"
				radius="16"
				placeholder="请输入用户名称"
				@confirm="
					myExamQuery(false);
					todoExamQuery(false);
				"
				@cancel="
					myExamQuery(false);
					todoExamQuery(false);
				"
			></uni-search-bar> -->
		</view>
		<view class="exam-statis__main">
			<uv-tabs
				:list="[{ name: '统计信息' }, { name: '考试排名' }]"
				:current="curTabIndex"
				:scrollable="false"
				lineHeight="4rpx"
				lineWidth="350rpx"
				lineColor="#0D9DF6"
				:activeStyle="{
					fontSize: '32rpx',
					color: '#333333'
				}"
				:inactiveStyle="{
					fontSize: '30rpx',
					color: '#8F939C'
				}"
				@change="(item) => (curTabIndex = item.index)"
			></uv-tabs>
			<scroll-view scroll-y="true" class="exam-statis__scroll" :style="{ height: taskListHeight + 'px' }">
				<template v-if="curTabIndex === 0">
					<view class="statis">
						<view class="statis__head">
							<view class="statis__icon">
								<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
							</view>
							<text class="statis__title">统计信息</text>
						</view>
						<view v-if="exam.markState !== 3" class="statis__main">考试未结束</view>
						<view v-else class="statis__main">
							<view class="statis__table">
								<view class="statis__row">
									<text class="statis__label">应考人数：</text>
									<text class="statis__value">{{ statis.userNum }}人</text>
								</view>
								<view class="statis__row">
									<text class="statis__label">未考人数：</text>
									<text class="statis__value">{{ statis.missUserNum }}人</text>
								</view>
								<view class="statis__row">
									<text class="statis__label">挂科人数：</text>
									<text class="statis__value">{{ statis.failUserNum }}人</text>
								</view>
							</view>
							<view class="statis__table">
								<view class="statis__row">
									<text class="statis__label">最高分数：</text>
									<text class="statis__value">{{ statis.maxScore }}分</text>
								</view>
								<view class="statis__row">
									<text class="statis__label">平均分数：</text>
									<text class="statis__value">{{ statis.avgScore }}分</text>
								</view>
								<view class="statis__row">
									<text class="statis__label">最低分数：</text>
									<text class="statis__value">{{ statis.minScore }}分</text>
								</view>
							</view>
						</view>
					</view>
					<view class="statis">
						<view class="statis__head">
							<view class="statis__icon">
								<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
							</view>
							<text class="statis__title">试题类型统计</text>
						</view>
						<view>
							<qiun-data-charts canvas-id="chart-2" type="pie" :opts="questionStatisOpts" :chartData="questionStatisData.typeStatis" :canvas2d="true" />
						</view>
					</view>
					<view class="statis">
						<view class="statis__head">
							<view class="statis__icon">
								<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
							</view>
							<text class="statis__title">分数段统计</text>
						</view>
						<view>
							<qiun-data-charts canvas-id="chart-3" type="column" :opts="scoreStatisOpts" :chartData="scoreStatisData" :canvas2d="true" />
						</view>
					</view>
				</template>
				<template v-if="curTabIndex === 1">
					<view v-for="(examRank, index) in examRankListpage.list" :key="index" class="list">
						<view class="list__wrap">
							<view class="list__title">
								{{ examRank.userName }} | {{ examRank.orgName }} | {{ examRank.myExamTotalScore != null ? examRank.myExamTotalScore : '-' }}分
							</view>
							<view class="list__outer">
								<view class="list__tag list__tag--type">第{{ examRank.myExamNo || '-' }}名</view>
								<view class="list__tag llist__tag--mark-type">
									{{ dictStore.getValue('EXAM_STATE', examRank.myExamState) }}
								</view>
								<view class="list__tag list__tag--score">
									{{ dictStore.getValue('MARK_STATE', examRank.myExamMarkState) }}
								</view>
								<view class="list__tag list__tag--user-name">答题用时{{ diff(examRank.myExamAnswerStartTime, examRank.myExamAnswerEndTime) }}分钟</view>
							</view>
						</view>
						<view class="list__nav" @click="toPaper(examRank.examId, examRank.userId)">
							<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
						</view>
					</view>
					<uni-load-more
						v-if="examRankListpage.list?.length"
						:status="examRankListpage.status"
						:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
						@clickLoadMore="examRankQuery(true)"
					></uni-load-more>
				</template>
			</scroll-view>
		</view>
		<view class="exam-statis-bottom"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { Exam } from '@/ts/exam.d';
import { MyExam } from '@/ts/myExam.d';
import { Page } from '@/ts/page.d';
import { examGet } from '@/api/exam';
import { reportExamRankListpage, reportExamStatis } from '@/api/report';
import { loginSysTime } from '@/api/login';
import dayjs from 'dayjs';
import { diff } from '@/util/timeUtil';
import { PaperStatis } from '@/ts/paper.d';

/************************变量定义相关***********************/
const curTabIndex = ref(0); // 当前选择标签页
const dictStore = useDictStore();
const pageParm = reactive({
	examId: null
});
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
	passScore: null,
	totalScore: null,
	markType: null,
	genType: null,
	loginType: null,
	sxes: [],
	state: null,
	userNum: null,
	limitMinute: null,
	retakeNum: null,
	anonState: null
});
const examRankListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 考试排名列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

const statis = reactive({
	// 统计信息
	userNum: 0, // 应考人数
	missUserNum: 0, // 未考人数
	failUserNum: 0, // 挂科人数
	questionNum: 0, // 试题数量
	objectiveQuestionNum: 0, // 客观题数
	avgScore: 0, // 最高分数
	minScore: 0, // 最低分数
	maxScore: 0, // 平均分数
	sdScore: 0 // 标准差值
});

const questionStatisOpts = reactive({
	color: ['#4692D8', '#06BCE3', '#978CEC', '#DD8CEC', '#85e3a4'],
	enableScroll: false,
	legend: {
		position: 'left',
		lineHeight: 26
	},
	extra: {
		pie: {
			activeOpacity: 0.5,
			activeRadius: 10,
			offsetAngle: 0,
			labelWidth: 15,
			border: true,
			borderWidth: 3,
			borderColor: '#FFFFFF'
		}
	}
});

let questionStatisData = reactive<PaperStatis>({
	typeStatis: [],
	markTypeStatis: {
		subjective: 0,
		objective: 0
	}
});

let scoreStatisOpts = reactive({
	color: ['#04C7F2', '#259FF8'],
	padding: [15, 15, 0, 5],
	enableScroll: false,
	legend: {},
	xAxis: {
		disableGrid: true
	},
	yAxis: {
		data: [
			{
				min: 0
			}
		]
	},
	extra: {
		column: {
			type: 'group',
			width: 30,
			activeBgColor: '#000000',
			activeBgOpacity: 0.08,
			linearType: 'custom',
			seriesGap: 5,
			linearOpacity: 0.5,
			barBorderCircle: true,
			customColor: ['#04C7F2', '#259FF8']
		}
	}
});
let scoreStatisData = reactive({
	categories: [],
	series: []
});

/************************组件生命周期相关*********************/
onLoad(async (option) => {
	pageParm.examId = option.examId;
	await examQuery(); // 这里保持同步，下面用到
	await examRankQuery(false); // 考试未结束也允许查看
	if (exam.markState === 3) {
		await examStatisQuery();
	}
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.exam-statis__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 20;
		})
		.exec();
});

/************************事件相关*****************************/
// 考试查询
async function examQuery() {
	const { data } = await examGet({ id: pageParm.examId });
	exam.id = data.id;
	exam.name = data.name;
	exam.genType = data.genType;
	exam.markType = data.markType;
	exam.loginType = data.loginType;
	exam.startTime = data.startTime;
	exam.endTime = data.endTime;
	exam.limitMinute = data.limitMinute;
	exam.markStartTime = data.markStartTime;
	exam.markEndTime = data.markEndTime;
	exam.markState = data.markState;
	exam.scoreState = data.scoreState;
	exam.rankState = data.rankState;
	exam.passScore = data.passScore;
	exam.totalScore = data.totalScore;
	exam.sxes = data.sxes;
	exam.userNum = data.userNum;
	exam.state = data.state;
	exam.retakeNum = data.retakeNum;
}

// 考试统计查询
async function examStatisQuery() {
	const { data } = await reportExamStatis({
		examId: pageParm.examId
	});
	statis.userNum = data.userNum;
	statis.missUserNum = data.missUserNum;
	statis.failUserNum = data.failUserNum;
	statis.questionNum = data.questionNum;
	statis.objectiveQuestionNum = data.objectiveQuestionNum;
	statis.avgScore = data.avgScore;
	statis.minScore = data.minScore;
	statis.maxScore = data.maxScore;
	statis.sdScore = data.sdScore;

	questionStatisData.typeStatis = { series: [{ data: data.questionTypeList }] };
	scoreStatisData.series = [{ name: '人 / 分数段', data: data.scoreGradeList.map((scoreGrade: any) => scoreGrade.value) }];
	scoreStatisData.categories = data.scoreGradeList.map((scoreGrade: any) => scoreGrade.name);
}

// 练习跟踪列表
async function examRankQuery(append: boolean) {
	examRankListpage.status = 'loading';
	examRankListpage.curPage = append ? examRankListpage.curPage + 1 : 1;

	const { data: sysTime } = await loginSysTime({});
	const startTime = dayjs(sysTime).subtract(1, 'year').startOf('month').valueOf(); // 去年同月的第一天（去年的今天会丢数据）
	const endTime = dayjs(sysTime).startOf('day').valueOf(); // 今天

	let { data } = await reportExamRankListpage({
		examId: pageParm.examId,
		curPage: examRankListpage.curPage,
		pageSize: examRankListpage.pageSize
	});

	if (append) {
		data.list.length && examRankListpage.list.push(...data.list);
	} else {
		examRankListpage.list = data.list;
	}
	examRankListpage.total = data.total;
	examRankListpage.status = examRankListpage.list.length < examRankListpage.total ? 'more' : 'no-more';
}

// 打开用户试卷
async function toPaper(examId: number, userId: number) {
	uni.navigateTo({ url: `/pages/admin/exam/exam-paper?examId=${examId}&userId=${userId}` });
}
</script>
<style lang="scss" scoped>
.exam-statis {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	.exam-statis__head {
	}
	.exam-statis__main {
		// 小程序没有uni-scroll-view-content
		// #ifdef H5
		:deep(.uni-scroll-view-content) {
			.uv-tabs__wrapper__nav__line {
				bottom: -1rpx;
			}
			.uv-badge {
				display: flex;
				justify-content: center;
				align-items: center;
				min-width: 40rpx;
				min-height: 30rpx;
				// background: linear-gradient(to right, #22d29a 0%, #05b77f 100%);
				background: linear-gradient(to right, #facc22 0%, #f87b00 100%);
				border-radius: 14rpx 14rpx 14rpx 4rpx;
				margin-top: -30rpx;
			}
		}
		// #endif
		// #ifdef MP-WEIXIN
		:deep(.uv-tabs__wrapper__scroll-view) {
			border-bottom: 2rpx solid #dfdfdf;
			.uv-tabs__wrapper__nav__line {
				bottom: -1rpx;
			}
			.uv-badge {
				display: flex;
				justify-content: center;
				align-items: center;
				min-width: 40rpx;
				min-height: 30rpx;
				// background: linear-gradient(to right, #22d29a 0%, #05b77f 100%);
				background: linear-gradient(to right, #facc22 0%, #f87b00 100%);
				border-radius: 14rpx 14rpx 14rpx 4rpx;
				margin-top: -30rpx;
			}
		}
		//#endif
		.exam-statis__scroll {
			overflow: hidden;
			border-radius: 30rpx 30rpx 30rpx 30rpx;
			margin-top: 20rpx;
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
			.statis {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.statis__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.statis__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.statis__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.statis__main {
					display: flex;
					padding: 0rpx 30rpx;
					.statis__table {
						flex: 1;
						.statis__row {
							height: 60rpx;
							.statis__label {
								font-size: 26rpx;
								color: #8f939c;
								line-height: 60rpx;
							}
							.statis__value {
								font-size: 26rpx;
								color: #333333;
								line-height: 60rpx;
							}
						}
					}
				}
			}
		}
	}
}
</style>
