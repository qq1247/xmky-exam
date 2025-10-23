<template>
	<view class="my-read">
		<view class="my-read__head">
			<image class="my-read__bg" src="@/static/img/home-bg.png"></image>
			<view class="user">
				<view class="user__head">
					<view class="user__icon">
						<uni-icons customPrefix="iconfont" type="icon-icon-people" color="white" size="26rpx"></uni-icons>
					</view>
					<text class="user__title">用户信息</text>
				</view>
				<view class="user__content">
					<image class="user-avatar" src="@/static/img/user-avatar.png"></image>
					<view class="user-avatar__wrapper">
						<view class="user-avatar__outer">
							<view class="user-avatar__inner">
								<text class="user-avatar__label">账号：</text>
								<text class="user-avatar__value">{{ user.loginName || '-' }}</text>
							</view>
						</view>
						<view class="user-avatar__outer">
							<view class="user-avatar__inner">
								<text class="user-avatar__label">姓名：</text>
								<text class="user-avatar__value">{{ user.name || '-' }}</text>
							</view>
							<view class="user-avatar__inner">
								<text class="user-avatar__label">角色：</text>
								<text class="user-avatar__value">阅卷用户</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="my-read__main">
			<scroll-view scroll-y="true" class="my-read__scroll" :style="{ height: scrollHeight + 'px' }">
				<view class="warn">
					<view class="warn__head">
						<view class="warn__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="warn__title">注意事项</text>
					</view>
					<view class="warn__content" style="font-size: 26rpx">
						<view>1、阅卷结束时间到，系统会自动结束阅卷，请注意把握时间。</view>
					</view>
				</view>
				<view class="exam">
					<view class="exam__head">
						<view class="exam__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="exam__title">考试信息</text>
					</view>
					<view class="exam__content">
						<view class="exam__row">
							<text class="exam__label">考试名称：</text>
							<text class="exam__value">{{ exam.name }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">开始时间：</text>
							<text class="exam__value">{{ exam.startTime }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">结束时间：</text>
							<text class="exam__value">{{ exam.endTime }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">限时答题：</text>
							<text class="exam__value">{{ exam.limitMinute ? `${exam.limitMinute}分钟` : '无' }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">成绩查询：</text>
							<text class="exam__value">{{ dictStore.getValue('SCORE_STATE', exam.scoreState) }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">公布排名：</text>
							<text class="exam__value">{{ dictStore.getValue('STATE_ON', exam.rankState) }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">考试分数：</text>
							<text class="exam__value">{{ exam.passScore }}/{{ exam.totalScore }}（及格/总分）</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">试卷类型：</text>
							<text class="exam__value">{{ dictStore.getValue('PAPER_MARK_TYPE', exam.markType) }}试卷</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">组卷方式：</text>
							<text class="exam__value">{{ dictStore.getValue('PAPER_GEN_TYPE', exam.genType) }}</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">防 作 弊：</text>
							<!-- 在一行显示 -->
							<text class="exam__value" style="font-size: 22rpx">
								<template v-if="!exam.sxes.length">无</template>
								<template v-else v-for="(sxe, index) in exam.sxes" :key="index">{{ index > 0 ? '、' : '' }}{{ dictStore.getValue('EXAM_SXES', sxe) }}</template>
							</text>
						</view>
						<view class="exam__row">
							<text class="exam__label">考试人数：</text>
							<text class="exam__value">{{ exam.userNum || '-' }}人</text>
						</view>
					</view>
				</view>
				<view class="paper">
					<view class="paper__head">
						<view class="paper__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-exam3" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="paper__title">试卷信息</text>
					</view>
					<view class="paper__content">
						<text class="paper__txt">客观题{{ questionStatisData.markTypeStatis.objective }}道 主观题{{ questionStatisData.markTypeStatis.subjective }}道</text>
						<view>
							<qiun-data-charts canvas-id="mark-user-pie" type="pie" :opts="questionStatisOpts" :chartData="questionStatisData.typeStatis" :canvas2d="true" />
						</view>
					</view>
				</view>
				<view class="my-mark">
					<view class="my-mark__head">
						<view class="my-mark__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="my-mark__title">我的阅卷</text>
					</view>
					<view class="my-mark__main">
						<view class="form">
							<view class="my-mark__row">
								<text class="my-mark__label">
									一共
									<text class="my-mark__value">{{ claimInfo.paperNum }}</text>
									份试卷，已被领取
									<text class="my-mark__value">{{ claimInfo.markNum }}</text>
									份，剩余
									<text class="my-mark__value">{{ claimInfo.paperNum - claimInfo.markNum }}</text>
									份。
								</text>
							</view>
							<view class="my-mark__row">
								<text class="my-mark__label">
									你已领取
									<text class="my-mark__value">{{ claimInfo.myClaimNum }}</text>
									份试卷，未批阅
									<text class="my-mark__value">{{ claimInfo.myClaimNum - claimInfo.myMarkNum }}</text>
									份。
								</text>
							</view>
							<uni-forms-item label="现新领" name="num" required>
								<xmky-number v-model="form.num" :max="claimInfo.paperNum - claimInfo.markNum" />
								份
								<button :disabled="exam.markState !== 2" class="form__btn" :class="{ 'form__btn--disable': exam.markState !== 2 }" type="primary" @click="claim">
									<text>点击领取</text>
								</button>
							</uni-forms-item>
							<view class="my-mark__row">
								<text class="my-mark__label">阅卷考试：{{ exam.markStartTime }}</text>
							</view>
							<view class="my-mark__row">
								<text class="my-mark__label">阅卷结束：{{ exam.markEndTime }}</text>
							</view>
							<button class="my-mark__btn" :class="{ 'my-mark__btn--active': hasStartMark, 'my-mark__btn--disable': !hasStartMark }" type="primary" @click="toMark">
								<xmky-count-down
									v-if="!hasStartMark"
									:expireTime="exam.markStartTime"
									preTxt="等待 "
									color="#fff"
									class="time-count-down"
									@end="hasStartMark = true"
								></xmky-count-down>
								<text>进入阅卷</text>
							</button>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
		<view class="my-read__foot"></view>
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
import { examGet } from '@/api/exam';
import { myMarkQuestionStatis, myMarkClaimInfo, myMarkClaim } from '@/api/my-mark';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
// 变量定义
const dictStore = useDictStore();
const pageParm = reactive({
	examId: null
});
const form = reactive<any>({
	num: 0 // 新批阅份数
});
const scrollHeight = ref(0);
const user = reactive<User>({
	id: null,
	name: '',
	loginName: '',
	orgName: ''
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
	no: null,
	ver: null
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
const claimInfo = reactive({
	// 领取信息
	paperNum: 0,
	markNum: 0,
	myClaimNum: 0,
	myMarkNum: 0
});
const hasStartMark = ref(false);

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	pageParm.examId = options.examId;
	userQuery();
	examQuery();
	questionStatisQueryWithInit();
	claimInfoQuery();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.my-read__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top;
		})
		.exec();
});

/************************计算属性相关*************************/
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

/************************事件相关*****************************/
// 去阅卷
function toMark() {
	if (!hasStartMark.value) {
		uni.showToast({ title: '阅卷未开始，请等待', icon: 'error' });
		return;
	}

	if (!claimInfo.myClaimNum) {
		uni.showToast({ title: '未领取试卷', icon: 'error' });
		return;
	}

	uni.navigateTo({
		url: `/pages/mark-user/my-mark/my-user?examId=${pageParm.examId}`
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
	let { data } = await examGet({ id: pageParm.examId });
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
	exam.anonState = data.anonState;
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

// 试题统计查询
async function questionStatisQueryWithInit() {
	let {
		data: { typeStatis, markTypeStatis }
	} = await myMarkQuestionStatis({ examId: pageParm.examId });
	let datas = (typeStatis as any[]).map((d) => {
		return {
			name: dictStore.getValue('QUESTION_TYPE', d.type),
			value: d.count
		};
	});
	questionStatisData.typeStatis = { series: [{ data: datas }] };
	questionStatisData.markTypeStatis = markTypeStatis;
}

// 获取领取信息
async function claimInfoQuery() {
	const { data } = await myMarkClaimInfo({ examId: pageParm.examId });
	claimInfo.paperNum = data.paperNum;
	claimInfo.markNum = data.markNum;
	claimInfo.myClaimNum = data.myClaimNum;
	claimInfo.myMarkNum = data.myMarkNum;
}

// 分配试卷
async function claim() {
	await myMarkClaim({
		examId: pageParm.examId,
		num: form.num
	});

	// 刷新领取信息
	claimInfoQuery();
}
</script>

<style lang="scss" scoped>
.my-read {
	height: inherit;
	display: flex;
	flex-direction: column;
	.my-read__head {
		display: flex;
		flex-direction: column;
		align-items: center;
		height: 230rpx;
		position: relative;
		margin-bottom: 40rpx;
		.my-read__bg {
			position: absolute;
			width: 750rpx;
			height: 230rpx;
		}
		.user {
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
			.user__head {
				display: flex;
				align-items: center;
				padding: 0rpx 30rpx;
				height: 80rpx;
				background: linear-gradient(to bottom, #bff3ff 0%, #b3eeff 100%);
				//border: 1rpx solid red;
				.user__icon {
					display: flex;
					justify-content: center;
					align-items: center;
					height: 42rpx;
					width: 42rpx;
					background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
					border-radius: 8rpx 8rpx 8rpx 8rpx;
				}
				.user__title {
					margin-left: 20rpx;
					font-weight: bold;
					font-size: 32rpx;
					color: #333333;
				}
			}
			.user__content {
				flex: 1;
				display: flex;
				align-items: center;
				margin: 0rpx 50rpx;
				.user-avatar {
					width: 100rpx;
					height: 100rpx;
					margin-right: 50rpx;
				}
				.user-avatar__wrapper {
					flex: 1;
					display: flex;
					flex-direction: column;
					.user-avatar__outer {
						display: flex;
						justify-content: space-between;
						.user-avatar__inner {
							display: flex;
							.user-avatar__label {
								font-size: 26rpx;
								color: #8f939c;
								line-height: 48rpx;
							}
							.user-avatar__value {
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
	.my-read__main {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;

		margin: 0rpx 20rpx 0rpx 20rpx;
		box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
		.my-read__scroll {
			.warn {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.warn__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					.warn__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.warn__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.warn__content {
					padding: 0rpx 30rpx;
					font-size: 22rpx;
					line-height: 42rpx;
					color: #e43d33;
				}
			}
			.exam {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.exam__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.exam__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.exam__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.exam__content {
					padding: 0rpx 30rpx;
					.exam__row {
						height: 60rpx;
						.exam__label {
							font-size: 26rpx;
							color: #8f939c;
							line-height: 60rpx;
						}
						.exam__value {
							font-size: 26rpx;
							color: #333333;
							line-height: 60rpx;
						}
					}
				}
			}

			.my-mark {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.my-mark__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.my-mark__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.my-mark__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.my-mark__main {
					padding: 0rpx 30rpx;
					:deep(.form) {
						.my-mark__row {
							height: 60rpx;
							.my-mark__label {
								font-size: 26rpx;
								color: #8f939c;
								line-height: 60rpx;
							}
							.my-mark__value {
								font-size: 26rpx;
								color: #333333;
								line-height: 60rpx;
							}
						}
						.uni-forms-item__content {
							display: flex;
							align-items: center;
						}
						.form__btn {
							width: 160rpx;
							height: 56rpx;
							line-height: 56rpx;
							font-size: 24rpx;
							color: #0d9df6;
							margin: 0rpx 0rpx 0rpx 20rpx;
							background-color: white;
							border: 1rpx solid #04c7f2;
							&::after {
								border: 0rpx;
							}
						}
						.form__btn--disable {
							border: initial;
							background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
						}
						.my-mark__btn {
							height: 100rpx;
							margin: initial;
							border-radius: 33rpx 33rpx 33rpx 33rpx;
							line-height: 100rpx;
							font-size: 30rpx;
							color: #fefeff;
							background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
							&.my-mark__btn--disable {
								background: linear-gradient(to right, #dfdfdf 0%, #cacaca 100%);
							}
						}
					}
				}
			}

			.paper {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx 0rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.paper__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.paper__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.paper__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.paper__content {
					position: relative;
					padding-left: 0rpx;
					.paper__txt {
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
	.my-read__foot {
	}
}
</style>
