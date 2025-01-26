<template>
	<view class="myread">
		<view class="myread-head">
			<image class="myread-head__bg" src="@/static/img/home-bg.png"></image>
			<view class="myread-user">
				<view class="myread-user-head">
					<view class="myread-user-head__icon">
						<uni-icons customPrefix="iconfont" type="icon-icon-people" color="white" size="26rpx"></uni-icons>
					</view>
					<text class="myread-user-head__title">用户信息</text>
				</view>
				<view class="myread-user-content">
					<image class="myread-user-content__avatar" src="@/static/img/user-avatar.png"></image>
					<view class="myread-user-content__wrapper">
						<view class="myread-user-content__outer">
							<view class="myread-user-content__inner">
								<text class="myread-exam__label">账号：</text>
								<text class="myread-exam__value">{{ user.loginName || '-' }}</text>
							</view>
						</view>
						<view class="myread-user-content__outer">
							<view class="myread-user-content__inner">
								<text class="myread-exam__label">姓名：</text>
								<text class="myread-exam__value">{{ user.name || '-' }}</text>
							</view>
							<view class="myread-user-content__inner">
								<text class="myread-exam__label">机构：</text>
								<text class="myread-exam__value">{{ user.orgName || '-' }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="myread-main">
			<scroll-view scroll-y="true" class="myread-main__scroll" :style="{ height: myreadMainHeight + 'px' }">
				<view class="myread-warn">
					<view class="myread-warn-head">
						<view class="myread-warn-head__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="myread-warn-head__title">注意事项</text>
					</view>
					<view class="myread-warn-content" style="font-size: 26rpx">
						<view>考试结束时间到，程序会自动交卷。</view>
						<view>交卷后不可再次考试，请慎重!</view>
					</view>
				</view>
				<view class="myread-exam">
					<view class="myread-exam-head">
						<view class="myread-exam-head__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="myread-exam-head__title">考试信息</text>
					</view>
					<view class="myread-exam-content">
						<view class="myread-exam__row">
							<text class="myread-exam__label">考试名称：</text>
							<text class="myread-exam__value">{{ exam.name }}</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">考试时间：</text>
							<text class="myread-exam__value">{{ dateTimeFormat(exam.startTime) }} - {{ dateTimeFormat(exam.endTime) }}</text>
						</view>
						<view v-if="mark" class="myread-exam__row">
							<text class="myread-exam__label">阅卷时间：</text>
							<text class="myread-exam__value">{{ dateTimeFormat(exam.markStartTime) }} - {{ dateTimeFormat(exam.markEndTime) }}</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">限时考试：</text>
							<text class="myread-exam__value">{{ exam.limitMinute ? `${exam.limitMinute}分钟` : '无' }}</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">成绩查询：</text>
							<text class="myread-exam__value">{{ dictStore.getValue('SCORE_STATE', exam.scoreState) }}</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">排名状态：</text>
							<text class="myread-exam__value">{{ dictStore.getValue('STATE_ON', exam.rankState) }}</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">考试分数：</text>
							<text class="myread-exam__value">{{ exam.passScore }} / {{ exam.totalScore }}（及格 / 总分）</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">试卷类型：</text>
							<text class="myread-exam__value">
								{{
									exam.markType === 1
										? dictStore.getValue('PAPER_MARK_TYPE', exam.markType)
										: dictStore.getValue('PAPER_MARK_TYPE', 1) + ' | ' + dictStore.getValue('PAPER_MARK_TYPE', 2)
								}}
							</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">组卷方式：</text>
							<text class="myread-exam__value">{{ dictStore.getValue('PAPER_GEN_TYPE', exam.genType) }}</text>
						</view>
						<view class="myread-exam__row">
							<text class="myread-exam__label">防止作弊：</text>
							<text class="myread-exam__value">
								<template v-if="!exam.sxes.length">无</template>
								<template else v-for="(sxe, index) in exam.sxes" :key="index">{{ index > 0 ? '、' : '' }}{{ ['', '试题乱序', '选项乱序'][sxe] }}</template>
							</text>
						</view>
					</view>
				</view>
				<view class="myread-myexam">
					<view class="myread-myexam-head">
						<view class="myread-myexam-head__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="myread-myexam-head__title">我的考试</text>
					</view>
					<view class="myread-myexam-content">
						<view class="myread-myexam__row">
							<text class="myread-myexam__label">答题时间：</text>
							<text class="myread-myexam__value">{{ dateTimeFormat(myExam.answerStartTime) }} - {{ dateTimeFormat(myExam.answerEndTime) }}</text>
						</view>
						<view v-if="scoreShow" class="myread-myexam__row">
							<text class="myread-myexam__label">考试分数：</text>
							<text class="myread-myexam__value">
								{{ myExam.objectiveScore == null ? '-' : myExam.objectiveScore }} / {{ myExam.totalScore == null ? '-' : myExam.totalScore }}（客观分 / 合计）
							</text>
							<view
								class="myread-myexam__score"
								:class="{ 'myread-myexam__score--succ': myExam.answerState === 1, 'myread-myexam__score--fail': myExam.answerState === 2 }"
							>
								<text>{{ dictStore.getValue('ANSWER_STATE', myExam.answerState) }}</text>
							</view>
						</view>
						<view v-if="rankshow" class="myread-myexam__row">
							<text class="myread-myexam__label">考试排名：</text>
							<text class="myread-myexam__value">{{ myExam.no == null ? '-' : myExam.no }} / {{ exam.userNum == null ? '-' : exam.userNum }}</text>
						</view>
						<view class="myread-myexam__time-wrap">
							<view class="myread-myexam__time">
								<uni-steps
									:options="
										dictStore.getList('EXAM_STATE').map((dict) => {
											dict.title = dict.dictValue;
											return dict;
										})
									"
									active-color="#0D9DF6"
									:active="myExam.state - 1"
									direction="column"
								/>
							</view>
							<view class="myread-myexam__time">
								<uni-steps
									:options="
										dictStore.getList('MARK_STATE').map((dict) => {
											dict.title = dict.dictValue;
											return dict;
										})
									"
									active-color="#0D9DF6"
									:active="myExam.markState - 1"
									direction="column"
								/>
							</view>
						</view>
					</view>
				</view>
				<view class="myread-paper">
					<view class="myread-paper-head">
						<view class="myread-paper-head__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-exam3" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="myread-paper-head__title">试卷信息</text>
					</view>
					<view class="myread-paper-content">
						<text class="myread-paper_statis-txt">
							客观题{{ questionStatisData.markTypeStatis.objective }}道 主观题{{ questionStatisData.markTypeStatis.subjective }}道
						</text>
						<view>
							<qiun-data-charts type="pie" :opts="questionStatisOpts" :chartData="questionStatisData.typeStatis" />
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
		<view class="myread-foot">
			<button
				class="myread-foot__exam-in"
				:class="{ 'myread-foot__exam-in--active': examActive, 'myread-foot__exam-in--disable': !examActive }"
				type="primary"
				@click="examIn"
			>
				<xm-count-down
					v-if="!examActive"
					:expireTime="exam.startTime"
					preTxt="等待 "
					color="#fff"
					class="myread-foot__time-count-down"
					@end="examActive = !examActive"
				></xm-count-down>
				<text>进入考试</text>
			</button>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { User } from '@/ts/user.d';
import { Exam } from '@/ts/exam.d';
import { MyExam } from '@/ts/myExam.d';
import { PaperStatis } from '@/ts/paper.d';
import { userGet } from '@/api/user';
import { myExamExamGet, myExamGet, myExamQuestionStatis, myExamGeneratePaper } from '@/api/myExam';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
// 变量定义
const dictStore = useDictStore();
const examId = ref(0);
const myreadMainHeight = ref(0);
const examActive = ref(false);
const user = reactive<User>({
	id: null,
	name: '',
	loginName: '',
	orgName: ''
});
const exam = reactive<Exam>({
	id: null,
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
	examId: null,
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

let questionStatisData = reactive<PaperStatis>({
	typeStatis: [],
	markTypeStatis: {
		subjective: 0,
		objective: 0
	}
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

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	examId.value = options.examId;
	if (options.loginType === '2') {
		let { code, msg } = await myExamGeneratePaper({ examId: examId.value });
		if (code !== 200) {
			return;
		}
	}

	examQuery();
	userQuery();
	myExamQuery();
	questionStatisQueryWithInit();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.myread-main__scroll')
		.boundingClientRect((data: any) => {
			myreadMainHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************计算属性相关*************************/
// 格式时间（yy-MM-dd HH:mm）
const dateTimeFormat = computed(() => (datetime: string) => {
	if (!datetime) {
		return '';
	}
	return datetime.substring(2, 16);
});
// 成绩展示
const scoreShow = computed(
	() => exam.scoreState === 1 || exam.scoreState === 3 // 只要是允许公布成绩，都显示出来占个位置
	// return (
	// 	(exam.scoreState == 1 && exam.markState == 3) || // 如果是考试结束后显示成绩，需要等到考试结束
	// 	(exam.scoreState == 3 && myExam.markState == 3) // 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
	// );
);
// 排名展示
const rankshow = computed(() => exam.rankState === 1);
// 是否阅卷
const mark = computed(() => exam.markType === 2);

/************************事件相关*****************************/
function examIn() {
	if (!examActive.value) {
		uni.showToast({ title: '考试未开始，请等待', icon: 'error' });
		return;
	}

	uni.navigateTo({
		url: '/pages/myExam/myPaper?examId=' + examId.value
	});
}

// 用户查询
async function userQuery() {
	let { data } = await userGet();
	user.id = data.id;
	user.name = data.name;
	user.loginName = data.loginName;
	user.orgName = data.orgName;
}

// 考试查询
async function examQuery() {
	let { data } = await myExamExamGet({ examId: examId.value });
	exam.id = examId.value;
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
	let { data } = await myExamGet({ examId: examId.value });
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

// 试题统计查询
async function questionStatisQueryWithInit() {
	let {
		data: { typeStatis, markTypeStatis }
	} = await myExamQuestionStatis({ examId: examId.value });
	let datas = (typeStatis as any[]).map((d) => {
		return {
			name: dictStore.getValue('QUESTION_TYPE', d.type),
			value: d.count
		};
	});
	questionStatisData.typeStatis = { series: [{ data: datas }] };
	questionStatisData.markTypeStatis = markTypeStatis;
}
</script>

<style lang="scss" scoped>
.myread {
	height: inherit;
	display: flex;
	flex-direction: column;
	.myread-head {
		display: flex;
		flex-direction: column;
		align-items: center;
		height: 230rpx;
		position: relative;
		margin-bottom: 40rpx;
		.myread-head__bg {
			position: absolute;
			width: 750rpx;
			height: 230rpx;
		}
		.myread-user {
			display: flex;
			flex-direction: column;
			position: absolute;
			width: 710rpx;
			height: 230rpx;
			bottom: -20rpx;
			z-index: 0;
			box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
			border-radius: 30rpx 30rpx 30rpx 30rpx;
			background: linear-gradient(to bottom, #bff3ff 0%, #e1f2ff 100%);
			overflow: hidden;
			.myread-user-head {
				display: flex;
				align-items: center;
				padding: 0rpx 30rpx;
				height: 80rpx;
				background: linear-gradient(to bottom, #bff3ff 0%, #b3eeff 100%);
				//border: 1rpx solid red;
				.myread-user-head__icon {
					display: flex;
					justify-content: center;
					align-items: center;
					height: 42rpx;
					width: 42rpx;
					background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
					border-radius: 8rpx 8rpx 8rpx 8rpx;
				}
				.myread-user-head__title {
					margin-left: 20rpx;
					font-weight: bold;
					font-size: 32rpx;
					color: #333333;
				}
			}
			.myread-user-content {
				flex: 1;
				display: flex;
				align-items: center;
				margin: 0rpx 50rpx;
				.myread-user-content__avatar {
					width: 100rpx;
					height: 100rpx;
					margin-right: 50rpx;
				}
				.myread-user-content__wrapper {
					flex: 1;
					display: flex;
					flex-direction: column;
					.myread-user-content__outer {
						display: flex;
						justify-content: space-between;
						.myread-user-content__inner {
							display: flex;
							.myread-exam__label {
								font-size: 26rpx;
								color: #8f939c;
								line-height: 48rpx;
							}
							.myread-exam__value {
								font-size: 26rpx;
								color: #333333;
								line-height: 48rpx;
							}
						}
					}
				}
			}
		}
	}
	.myread-main {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;

		margin: 0rpx 20rpx 20rpx 20rpx;
		overflow: hidden;
		box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
		border-radius: 30rpx 30rpx 30rpx 30rpx;
		.myread-main__scroll {
			.myread-warn {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.myread-warn-head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					.myread-warn-head__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.myread-warn-head__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.myread-warn-content {
					padding: 0rpx 30rpx;
					font-size: 22rpx;
					line-height: 42rpx;
					color: #e43d33;
				}
			}
			.myread-exam {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.myread-exam-head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.myread-exam-head__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.myread-exam-head__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.myread-exam-content {
					padding: 0rpx 30rpx;
					.myread-exam__row {
						height: 60rpx;
						.myread-exam__label {
							font-size: 26rpx;
							color: #8f939c;
							line-height: 60rpx;
						}
						.myread-exam__value {
							font-size: 26rpx;
							color: #333333;
							line-height: 60rpx;
						}
					}
				}
			}

			.myread-myexam {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.myread-myexam-head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.myread-myexam-head__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.myread-myexam-head__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.myread-myexam-content {
					padding: 0rpx 30rpx;
					.myread-myexam__row {
						height: 60rpx;
						.myread-myexam__label {
							font-size: 26rpx;
							color: #8f939c;
							line-height: 60rpx;
						}
						.myread-myexam__value {
							font-size: 26rpx;
							color: #333333;
							line-height: 60rpx;
						}
						.myread-myexam__score {
							display: inline-block;
							padding: 0rpx 8rpx;
							font-size: 26rpx;
							border-radius: 8rpx 8rpx 8rpx 8rpx;
							border: 1px solid transparent;
							border-radius: 8rpx;
							background-clip: padding-box, border-box;
							background-origin: padding-box, border-box;
						}
						.myread-myexam__score--succ {
							height: 32rpx;
							line-height: 32rpx;
							background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #009554, #14ec9d);
							color: #08b771;
						}
						.myread-myexam__score--fail {
							background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #f445a0, #f99c9c);
							color: #f66a9e;
						}
					}
					.myread-myexam__time-wrap {
						display: flex;
						background: #f6f7fc;
						border-radius: 20rpx 20rpx 20rpx 20rpx;
						overflow: hidden;
						.myread-myexam__time {
							flex: 1;
						}
					}
				}
			}

			.myread-paper {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.myread-paper-head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.myread-paper-head__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.myread-paper-head__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.myread-paper-content {
					position: relative;
					padding-left: 0rpx;
					.myread-paper_statis-txt {
						position: absolute;
						left: 30rpx;
						top: 20rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
				}
			}
		}
	}
	.myread-foot {
		margin: 20rpx 0rpx 50rpx 0rpx;
		.myread-foot__exam-in {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			width: 628rpx;
			height: 100rpx;
			border-radius: 50px;
			line-height: 40rpx;
			.myread-foot__time-count-down {
				line-height: 30rpx;
				font-size: 24rpx;
			}
		}
		.myread-foot__exam-in--active {
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
		.myread-foot__exam-in--disable {
			background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
		}
	}
}
</style>
