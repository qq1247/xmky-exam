<template>
	<view class="myread">
		<view class="myread-head">
			<image class="myread-head__bg" src="@/static/img/home-bg.png"></image>
			<view class="user">
				<view class="user__head">
					<view class="user__icon">
						<uni-icons customPrefix="iconfont" type="icon-icon-people" color="white" size="26rpx"></uni-icons>
					</view>
					<text class="user__title">用户信息</text>
				</view>
				<view class="user__main">
					<image class="user__avatar" src="@/static/img/user-avatar.png"></image>
					<view class="user__wrapper">
						<view class="user__outer">
							<view class="user__inner">
								<text class="user__label">账号：</text>
								<text class="user__value">{{ user.loginName || '-' }}</text>
							</view>
						</view>
						<view class="user__outer">
							<view class="user__inner">
								<text class="user__label">姓名：</text>
								<text class="user__value">{{ user.name || '-' }}</text>
							</view>
							<view class="user__inner">
								<text class="user__label">机构：</text>
								<text class="user__value">{{ user.orgName || '-' }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="myread-main">
			<scroll-view scroll-y="true" class="myread-main__scroll" :style="{ height: myreadMainHeight + 'px' }">
				<view class="warn">
					<view class="warn__head">
						<view class="warn__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="warn__title">注意事项</text>
					</view>
					<view class="warn__main" style="font-size: 26rpx">
						<view>1、进入此页面，会自动增量更新最新试题到自己的练习</view>
						<view>2、答错试题会收集到历史错题</view>
						<view>3、想重新练题，请点“重新练习”按钮</view>
					</view>
				</view>
				<view class="exer">
					<view class="exer__head">
						<view class="exer__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-pen" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="exer__title">练习信息</text>
					</view>
					<view class="exer__main">
						<view class="exer__row">
							<text class="exer__label">练习名称：</text>
							<text class="exer__value">{{ exer.name }}</text>
						</view>
						<view class="exer__row">
							<text class="exer__label">发布状态：</text>
							<text class="exer__value">{{ dictStore.getValue('STATE_PS', exer.state) }}</text>
						</view>
					</view>
				</view>
				<view class="question-bank">
					<view class="question-bank__head">
						<view class="question-bank__icon">
							<uni-icons customPrefix="iconfont" type="icon-icon-exam3" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="question-bank__title">题库信息</text>
					</view>
					<view class="question-bank__main">
						<text class="question-bank__statis">
							客观题{{ questionStatisData.markTypeStatis.objective }}道 主观题{{ questionStatisData.markTypeStatis.subjective }}道
						</text>
						<view>
							<qiun-data-charts type="pie" :opts="questionStatisOpts" :chartData="questionStatisData.typeStatis" />
						</view>
					</view>
				</view>
				<view class="my-exer">
					<view class="my-exer__head">
						<view class="my-exer__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="my-exer__title">我的练习</text>
					</view>
					<view class="my-exer__main">
						<uni-data-checkbox
							mode="list"
							v-model="exerType"
							selectedColor="#04c7f2"
							selectedTextColor="#04c7f2"
							:localdata="[
								{
									text: `单选题（进度：${answerNum(1)}/${questionNum(1)}；正确率：${correctAnswerRate(1)}%）`,
									value: 1,
									disable: questionNum(1) === 0
								},
								{
									text: `多选题（进度：${answerNum(2)}/${questionNum(2)}；正确率：${correctAnswerRate(2)}%）`,
									value: 2,
									disable: questionNum(2) === 0
								},
								{
									text: `填空题（进度：${answerNum(3)}/${questionNum(3)}；正确率：${correctAnswerRate(3)}%）`,
									value: 3,
									disable: questionNum(3) === 0
								},
								{
									text: `判断题（进度：${answerNum(4)}/${questionNum(4)}；正确率：${correctAnswerRate(4)}%）`,
									value: 4,
									disable: questionNum(4) === 0
								},
								{
									text: `问答题（进度：${answerNum(5)}/${questionNum(5)}；正确率：${correctAnswerRate(5)}%）`,
									value: 5,
									disable: questionNum(5) === 0
								},
								{
									text: `历史错题（${pullInfo.wrongAnswerNum}道）`,
									value: 11,
									disable: pullInfo.wrongAnswerNum === 0
								},
								{
									text: `我的收藏（${pullInfo.favNum}道）`,
									value: 12,
									disable: pullInfo.favNum === 0
								}
							]"
						></uni-data-checkbox>
					</view>
				</view>
			</scroll-view>
		</view>
		<view class="myread-foot">
			<button v-if="exerType >= 1 && exerType <= 5" class="myread-foot__exam-in myread-foot__exam-in--disable" type="primary" @click="reset">
				<text>重新练习</text>
			</button>
			<button class="myread-foot__exam-in myread-foot__exam-in--active" type="primary" @click="toExer">
				<text>进入练习</text>
			</button>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady, onShow } from '@dcloudio/uni-app';
import { User } from '@/ts/user.d';
import { Exer } from '@/ts/exer.d';
import { PaperStatis } from '@/ts/paper.d';
import { userGet } from '@/api/user';
import { myExerQuestionStatis, myExerPull, myExerListpage, myExerExerReset } from '@/api/my-exer';
import { useDictStore } from '@/stores/dict';
import { myExerExerGet } from '@/api/my-exer';
import { Page } from '@/ts/page.d';

/************************变量定义相关***********************/
// 变量定义
const dictStore = useDictStore();
const exerId = ref(0);
const myreadMainHeight = ref(0);
const user = reactive<User>({
	id: null,
	name: '',
	loginName: '',
	orgName: ''
}); // 用户
const exer = reactive<Exer>({
	id: null,
	name: '',
	questionBankId: undefined,
	userIds: [],
	orgIds: [],
	state: null,
	rmkState: null
}); // 练习
const listpage = reactive<Page<any>>({
	// 我的练习分页列表
	curPage: 1,
	pageSize: 20,
	total: 0,
	list: []
});
const pullInfo = reactive({
	questionTypeStatis: {} as any,
	questionBankUpdateNum: 0,
	wrongAnswerNum: 0,
	favNum: 0
}); // 拉取信息
const exerType = ref(1); // 当前选中练习类型

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
	exerId.value = options.exerId;
	userQuery();
	exerQuery();
	questionStatisQueryWithInit();

	await pull();
	myExerQuery();
});
onReady(() => {
	uni.createSelectorQuery()
		.select('.myread-main__scroll')
		.boundingClientRect((data: any) => {
			myreadMainHeight.value = uni.getWindowInfo().windowHeight - data.top - 100;
		})
		.exec();
});

/************************计算属性相关*************************/
const answerNum = computed(() => (type: number) => {
	return listpage.list.filter((myExer) => myExer.type === type)[0]?.answerNum || 0;
}); // 答题数量

const questionNum = computed(() => (type: number) => {
	return pullInfo.questionTypeStatis[type] || 0;
}); // 试题数量
const correctAnswerRate = computed(() => (type: number) => {
	let answerNum = listpage.list.filter((myExer) => myExer.type === type)[0]?.answerNum || 0;
	if (answerNum === 0) {
		// 未答题默认正确率100%
		return 100;
	}

	let correctAnswerNum = listpage.list.filter((myExer) => myExer.type === type)[0]?.correctAnswerNum || 0;

	return Math.round((correctAnswerNum / answerNum) * 100);
}); // 正确率

/************************事件相关*****************************/
// 用户查询
async function userQuery() {
	let { data } = await userGet();
	user.id = data.id;
	user.name = data.name;
	user.loginName = data.loginName;
	user.orgName = data.orgName;
}

// 练习查询
async function exerQuery() {
	let { data } = await myExerExerGet({ exerId: exerId.value });
	exer.id = data.id;
	exer.name = data.name;
	exer.questionBankId = data.questionBankId;
	exer.state = data.state;
	exer.rmkState = data.rmkState;
}

// 我的练习拉取
async function pull() {
	uni.showLoading({ title: '正在更新最新试题\n请等待。。。', mask: true });

	const { code, msg, data } = await myExerPull({
		exerId: exerId.value
	});

	if (code !== 200) {
		uni.showLoading({ title: msg, mask: true });
		return;
	}

	pullInfo.questionTypeStatis = data.questionTypeStatis;
	pullInfo.questionBankUpdateNum = data.questionBankUpdateNum;
	pullInfo.wrongAnswerNum = data.wrongAnswerNum;
	pullInfo.favNum = data.favNum;

	uni.hideLoading();
}

// 试题统计查询
async function questionStatisQueryWithInit() {
	let {
		data: { typeStatis, markTypeStatis }
	} = await myExerQuestionStatis({ exerId: exerId.value });
	let datas = (typeStatis as any[]).map((d) => {
		return {
			name: dictStore.getValue('QUESTION_TYPE', d.type),
			value: d.count
		};
	});
	questionStatisData.typeStatis = { series: [{ data: datas }] };
	questionStatisData.markTypeStatis = markTypeStatis;
}

// 我的练习查询
async function myExerQuery() {
	const { code, data } = await myExerListpage({
		exerId: exerId.value,
		curPage: listpage.curPage,
		pageSize: listpage.pageSize
	});

	if (code !== 200) {
		return;
	}

	listpage.list = data.list;
	listpage.total = data.total;
}

// 去练习
async function toExer() {
	uni.navigateTo({
		url: `/pages/myExer/myPaper?exerId=${exerId.value}&type=${exerType.value}`
	});
}

// 重新练习
async function reset() {
	if (exerType.value === 11 || exerType.value === 12) {
		return;
	}

	const { code } = await myExerExerReset({
		exerId: exerId.value,
		type: exerType.value
	});

	if (code !== 200) {
		return;
	}

	toExer();
}

function onShow(arg0: () => Promise<void>) {
	throw new Error('Function not implemented.');
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
			.user__main {
				flex: 1;
				display: flex;
				align-items: center;
				margin: 0rpx 50rpx;
				.user__avatar {
					width: 100rpx;
					height: 100rpx;
					margin-right: 50rpx;
				}
				.user__wrapper {
					flex: 1;
					display: flex;
					flex-direction: column;
					.user__outer {
						display: flex;
						justify-content: space-between;
						.user__inner {
							display: flex;
							.user__label {
								font-size: 26rpx;
								color: #8f939c;
								line-height: 48rpx;
							}
							.user__value {
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
				.warn__main {
					padding: 0rpx 30rpx;
					font-size: 22rpx;
					line-height: 42rpx;
					color: #e43d33;
				}
			}
			.exer {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.exer__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.exer__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.exer__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.exer__main {
					padding: 0rpx 30rpx;
					.exer__row {
						height: 60rpx;
						.exer__label {
							font-size: 26rpx;
							color: #8f939c;
							line-height: 60rpx;
						}
						.exer__value {
							font-size: 26rpx;
							color: #333333;
							line-height: 60rpx;
						}
					}
				}
			}

			.my-exer {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.my-exer__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.my-exer__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.my-exer__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.my-exer__main {
					padding: 0rpx 30rpx;
					:deep(.checklist-box) {
						padding: 30rpx 15rpx;
					}
				}
			}

			.question-bank {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;
				.question-bank__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;
					//border: 1rpx solid red;
					.question-bank__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}
					.question-bank__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}
				.question-bank__main {
					position: relative;
					padding-left: 0rpx;
					.question-bank__statis {
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
		display: flex;
		margin: 20rpx 0rpx 50rpx 0rpx;
		.myread-foot__exam-in {
			display: flex;
			flex-direction: row;
			justify-content: center;
			align-items: center;
			width: 100%;
			height: 100rpx;
			border-radius: 50px;
			margin: 0px 10px;
			line-height: 40rpx;
		}
		.myread-foot__exam-in--active {
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
		.myread-foot__exam-in--disable {
			border: 1rpx solid #04c7f2;
			color: #04c7f2;
			background: linear-gradient(to right, #fff 0%, #fff 100%);
		}
	}
}
</style>
