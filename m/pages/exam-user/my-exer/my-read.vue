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
						<view>1、自组练习抽题：优先题库中未抽取到的试题，题数不足再补练习中未答试题，再补练习中已答试题，确保练习内容覆盖全面</view>
						<view>2、单次练习5分钟无操作，将视为挂机，不计入有效练习时长</view>
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
						<qiun-data-charts canvas-id="chart-1" type="line" :opts="exerTimeStatisOpts" :chartData="exerTimeStatisData.data" :ontouch="true" :canvas2d="true" />
						<!-- <view class="exer__row">
							<text class="exer__label">练习名称：</text>
							<text class="exer__value">{{ exer.name }}</text>
						</view>
						<view class="exer__row">
							<text class="exer__label">发布状态：</text>
							<text class="exer__value">{{ dictStore.getValue('STATE_PS', exer.state) }}</text>
						</view> -->
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
							<qiun-data-charts canvas-id="chart-2" type="pie" :opts="questionStatisOpts" :chartData="questionStatisData.typeStatis" :canvas2d="true" />
						</view>
					</view>
				</view>
				<view class="my-exer-list">
					<view class="my-exer-list__head">
						<view class="my-exer-list__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="my-exer-list__title">最近练习</text>
					</view>
					<view class="my-exer-list__main">
						<uni-list border-full>
							<uni-list-item v-for="(myExer, index) in listpage.list" :key="index" showArrow :to="`/pages/myExer/myPaper?exerId=${exerId}&id=${myExer.id}`">
								<template #body>
									<view>
										<view>
											<text class="my-exer-list__name">{{ myExer.name }}</text>
										</view>
										<view class="my-exer-list__sub">
											<text class="my-exer-list__lable">进度：</text>
											<text class="my-exer-list__value">{{ myExer.answerNum }}/{{ myExer.questionNum }}</text>
											<text class="my-exer-list__lable">正确率：</text>
											<text class="my-exer-list__value">
												{{ new Decimal(myExer.correctAnswerNum).dividedBy(myExer.answerNum).times(100).toDecimalPlaces(0).toNumber() || 0 }}%
											</text>
										</view>
									</view>
								</template>
							</uni-list-item>
						</uni-list>
					</view>
				</view>
				<view class="my-exer">
					<view class="my-exer__head">
						<view class="my-exer__icon">
							<uni-icons customPrefix="iconfont" type="icon-wodekaoshi" color="white" size="26rpx"></uni-icons>
						</view>
						<text class="my-exer__title">创建练习</text>
					</view>
					<view class="my-exer__main">
						<uni-forms ref="formRef" :label-width="75" :model="form" class="form">
							<uni-forms-item label="类型：" name="type" required>
								<uni-data-select
									v-model="form.type"
									:localdata="[
										{
											value: 1,
											text: `自组（${
												(myExerStatis.free?.[0]?.count ?? 0) +
												(myExerStatis.free?.[1]?.count ?? 0) +
												(myExerStatis.free?.[2]?.count ?? 0) +
												(myExerStatis.free?.[3]?.count ?? 0) +
												(myExerStatis.free?.[4]?.count ?? 0)
											}题）`
										},
										{
											value: 2,
											text: `未练（${
												(myExerStatis.unExer?.[0]?.count ?? 0) +
												(myExerStatis.unExer?.[1]?.count ?? 0) +
												(myExerStatis.unExer?.[2]?.count ?? 0) +
												(myExerStatis.unExer?.[3]?.count ?? 0) +
												(myExerStatis.unExer?.[4]?.count ?? 0)
											}题）`
										},
										{
											value: 3,
											text: `错题（${
												(myExerStatis.wrong?.[0]?.count ?? 0) +
												(myExerStatis.wrong?.[1]?.count ?? 0) +
												(myExerStatis.wrong?.[2]?.count ?? 0) +
												(myExerStatis.wrong?.[3]?.count ?? 0) +
												(myExerStatis.wrong?.[4]?.count ?? 0)
											}题）`
										},
										{
											value: 4,
											text: `收藏（${
												(myExerStatis.fav?.[0]?.count ?? 0) +
												(myExerStatis.fav?.[1]?.count ?? 0) +
												(myExerStatis.fav?.[2]?.count ?? 0) +
												(myExerStatis.fav?.[3]?.count ?? 0) +
												(myExerStatis.fav?.[4]?.count ?? 0)
											}题）`
										}
									]"
									@change="toggleExer"
								></uni-data-select>
							</uni-forms-item>
							<uni-forms-item label="单选题：" name="singleNum" required>
								<view class="my-exer__row">
									<xmky-number v-model="form.singleNum" :min="0" :max="questionMaxNum.singleNum" @input="nameUpdate" />
									<text>，共{{ questionMaxNum.singleNum }}题</text>
								</view>
							</uni-forms-item>
							<uni-forms-item label="多选题：" name="multipleNum" required>
								<view class="my-exer__row">
									<xmky-number v-model="form.multipleNum" :min="0" :max="questionMaxNum.multipleNum" @input="nameUpdate" />
									<text class="my-exer__label">，共{{ questionMaxNum.multipleNum }}题</text>
								</view>
							</uni-forms-item>
							<uni-forms-item label="填空题：" name="fillBlankNum" required>
								<view class="my-exer__row">
									<xmky-number v-model="form.fillBlankNum" :min="0" :max="questionMaxNum.fillBlankNum" @input="nameUpdate" />
									<text class="my-exer__label">，共{{ questionMaxNum.fillBlankNum }}题</text>
								</view>
							</uni-forms-item>
							<uni-forms-item label="判断题：" name="judgeNum" required>
								<view class="my-exer__row">
									<xmky-number v-model="form.judgeNum" :min="0" :max="questionMaxNum.judgeNum" @input="nameUpdate" />
									<text class="my-exer__label">，共{{ questionMaxNum.judgeNum }}题</text>
								</view>
							</uni-forms-item>
							<uni-forms-item label="问答题：" name="qaNum" required>
								<view class="my-exer__row">
									<xmky-number v-model="form.qaNum" :min="0" :max="questionMaxNum.qaNum" @input="nameUpdate" />
									<text class="my-exer__label">，共{{ questionMaxNum.qaNum }}题</text>
								</view>
							</uni-forms-item>
							<uni-forms-item label="名称：" name="loginName" required>
								<view class="my-exer__row">
									<uni-easyinput v-model="form.name" placeholder="请输入名称"></uni-easyinput>
								</view>
							</uni-forms-item>
						</uni-forms>
						<button :loading="loading" :disabled="loading" class="my-exer__btn my-exer__btn--active" type="primary" @click="toExer">
							<text>创建练习</text>
						</button>
					</view>
				</view>
			</scroll-view>
		</view>
		<view class="myread-foot"></view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { User } from '@/ts/user.d';
import { PaperStatis } from '@/ts/paper.d';
import { userGet } from '@/api/user';
import { myExerListpage, myExerGet, myExerTrackList, myExerAdd } from '@/api/my-exer';
import { loginSysTime } from '@/api/login';
import { exerListpage } from '@/api/exer';
import { Page } from '@/ts/page.d';
import dayjs from 'dayjs';
import Decimal from 'decimal.js';

/************************变量定义相关***********************/
// 变量定义
const exerId = ref(0);
const form = reactive<any>({
	exerId: null,
	type: 1,
	singleNum: 0,
	multipleNum: 0,
	fillBlankNum: 0,
	judgeNum: 0,
	qaNum: 0
});
const questionMaxNum = reactive({
	singleNum: 0,
	multipleNum: 0,
	fillBlankNum: 0,
	judgeNum: 0,
	qaNum: 0
});
const myreadMainHeight = ref(0);
const user = reactive<User>({
	id: null,
	name: '',
	loginName: '',
	orgName: ''
}); // 用户
const listpage = reactive<Page<any>>({
	// 我的练习分页列表
	curPage: 1,
	pageSize: 20,
	total: 0,
	list: []
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

const myExerStatis = reactive({
	free: [],
	unExer: [],
	wrong: [],
	fav: []
});

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	exerId.value = options.exerId;
	form.exerId = options.exerId;
	userQuery();
	questionBankStatisQuery();
	exerTimeStatisQuery();
	myExerQuery();

	await myExerStatisQuery();
	toggleExer();
});
onReady(() => {
	uni.createSelectorQuery()
		.select('.myread-main__scroll')
		.boundingClientRect((data: any) => {
			myreadMainHeight.value = uni.getWindowInfo().windowHeight - data.top - 10;
		})
		.exec();
});

/************************计算属性相关*************************/
const exerTimeStatisData = reactive({
	data: {} // 不要单独更新某个属性，要作为一个整体，插件限制
});
const exerTimeStatisOpts = reactive({
	color: ['#4692D8', '#06BCE3', '#978CEC', '#DD8CEC', '#85e3a4'],
	padding: [15, 10, 0, 15],
	enableScroll: true,
	legend: { show: false },
	xAxis: {
		disableGrid: true,
		scrollShow: true,
		itemCount: 4
	},
	yAxis: {
		gridType: 'dash',
		dashLength: 2
	},
	extra: {
		line: {
			type: 'straight',
			width: 2,
			activeType: 'hollow'
		}
	}
});

const loading = ref(false);

/************************事件相关*****************************/
// 用户查询
async function userQuery() {
	let { data } = await userGet();
	user.id = data.id;
	user.name = data.name;
	user.loginName = data.loginName;
	user.orgName = data.orgName;
}

// 题库统计查询
async function questionBankStatisQuery() {
	let { code, data } = await exerListpage({
		exerId: exerId.value,
		curPage: 1,
		pageSize: 1
	});

	let datas = [];
	datas.push({ name: '单选题', value: data.list[0].singleNum });
	datas.push({ name: '多选题', value: data.list[0].multipleNum });
	datas.push({ name: '填空题', value: data.list[0].fillBlankObjNum + data.list[0].fillBlankSubNum });
	datas.push({ name: '判断题', value: data.list[0].judgeNum });
	datas.push({ name: '问答题', value: data.list[0].qaSubNum });
	questionStatisData.typeStatis = { series: [{ data: datas }] };

	questionStatisData.markTypeStatis.subjective = data.list[0].subjectiveNum;
	questionStatisData.markTypeStatis.objective = data.list[0].objectiveNum;
}

// 练习时间统计（近一年）
async function exerTimeStatisQuery() {
	const { data: sysTime } = await loginSysTime({});
	const [datePart] = sysTime.split(' ');
	const [y, m, d] = datePart.split('-').map(Number);
	const now = new Date(y, m - 1, d);
	const startYear = now.getFullYear() - 1;
	const startMonth = now.getMonth(); // 0~11
	const startTimeDate = new Date(startYear, startMonth, 1);
	const endTimeDate = new Date(now.getFullYear(), now.getMonth(), 1);
	const categories: string[] = [];
	const monthMap: Record<string, number> = {};
	let current = new Date(startTimeDate);
	while (current <= endTimeDate) {
		const ym = `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}`;
		categories.push(ym);
		monthMap[ym] = 0;
		current.setMonth(current.getMonth() + 1);
	}

	const { data: myExerTracks } = await myExerTrackList({
		exerId: exerId.value,
		startDate: `${startYear}-${String(startMonth + 1).padStart(2, '0')}-01`, // 去年同月的第一天（去年的今天会丢数据）
		endDate: datePart // 今天
	});

	myExerTracks.forEach((myExerTrack: any) => {
		const ym = myExerTrack.ymd.substring(0, 7);
		monthMap[ym] += myExerTrack.minuteCount;
	});

	exerTimeStatisData.data = {
		categories,
		series: [
			{
				name: '练习时长（分钟）',
				data: categories.map((ym) => monthMap[ym] || 0)
			}
		]
	};
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

// 我的练习统计查询
async function myExerStatisQuery() {
	const { data } = await myExerGet({ exerId: exerId.value });
	myExerStatis.free = data.free;
	myExerStatis.unExer = data.unExer;
	myExerStatis.wrong = data.wrong;
	myExerStatis.fav = data.fav;
}

// 切换练习
function toggleExer() {
	if (form.type === 1) {
		questionMaxNum.singleNum = myExerStatis.free?.[0]?.count ?? 0;
		questionMaxNum.multipleNum = myExerStatis.free?.[1]?.count ?? 0;
		questionMaxNum.fillBlankNum = myExerStatis.free?.[2]?.count ?? 0;
		questionMaxNum.judgeNum = myExerStatis.free?.[3]?.count ?? 0;
		questionMaxNum.qaNum = myExerStatis.free?.[4]?.count ?? 0;
	} else if (form.type === 2) {
		questionMaxNum.singleNum = myExerStatis.unExer?.[0]?.count ?? 0;
		questionMaxNum.multipleNum = myExerStatis.unExer?.[1]?.count ?? 0;
		questionMaxNum.fillBlankNum = myExerStatis.unExer?.[2]?.count ?? 0;
		questionMaxNum.judgeNum = myExerStatis.unExer?.[3]?.count ?? 0;
		questionMaxNum.qaNum = myExerStatis.unExer?.[4]?.count ?? 0;
	} else if (form.type === 3) {
		questionMaxNum.singleNum = myExerStatis.wrong?.[0]?.count ?? 0;
		questionMaxNum.multipleNum = myExerStatis.wrong?.[1]?.count ?? 0;
		questionMaxNum.fillBlankNum = myExerStatis.wrong?.[2]?.count ?? 0;
		questionMaxNum.judgeNum = myExerStatis.wrong?.[3]?.count ?? 0;
		questionMaxNum.qaNum = myExerStatis.wrong?.[4]?.count ?? 0;
	} else if (form.type === 4) {
		questionMaxNum.singleNum = myExerStatis.fav?.[0]?.count ?? 0;
		questionMaxNum.multipleNum = myExerStatis.fav?.[1]?.count ?? 0;
		questionMaxNum.fillBlankNum = myExerStatis.fav?.[2]?.count ?? 0;
		questionMaxNum.judgeNum = myExerStatis.fav?.[3]?.count ?? 0;
		questionMaxNum.qaNum = myExerStatis.fav?.[4]?.count ?? 0;
	}

	nameUpdate();
}

// 名称更新
function nameUpdate() {
	const typeName = form.type === 1 ? '自组' : form.type === 2 ? '未练' : form.type === 3 ? '错题' : '收藏';
	form.name = `${typeName}_单${form.singleNum || 0}多${form.multipleNum || 0}填${form.fillBlankNum || 0}判${form.judgeNum || 0}问${form.qaNum || 0}_${dayjs().format(
		'YYYYMMDD'
	)}`;
}

// 去练习
async function toExer() {
	loading.value = true;
	const { code, data } = await myExerAdd({ ...form });
	if (code !== 200) {
		loading.value = false; // 错误显示，正确就跳转了
		return;
	}

	uni.navigateTo({
		url: `/pages/myExer/myPaper?exerId=${exerId.value}&id=${data}`
	});
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
			.my-exer-list {
				display: flex;
				flex-direction: column;
				padding: 30rpx 0rpx;
				margin-bottom: 20rpx;
				background-color: white;
				box-shadow: 0rpx 10rpx 20rpx 0rpx rgba(0, 0, 0, 0.2);
				border-radius: 16rpx;
				overflow: hidden;

				.my-exer-list__head {
					display: flex;
					align-items: center;
					padding: 0rpx 30rpx;
					height: 80rpx;

					//border: 1rpx solid red;
					.my-exer-list__icon {
						display: flex;
						justify-content: center;
						align-items: center;
						height: 42rpx;
						width: 42rpx;
						background: linear-gradient(to bottom right, #04c7f2 0%, #259ff8 100%);
						border-radius: 8rpx 8rpx 8rpx 8rpx;
					}

					.my-exer-list__title {
						margin-left: 20rpx;
						font-weight: bold;
						font-size: 32rpx;
						color: #333333;
					}
				}

				.my-exer-list__main {
					.my-exer-list__name {
						font-size: 26rpx;
						color: #3b4144;
					}
					.my-exer-list__sub {
						margin-top: 4rpx;
					}
					.my-exer-list__lable {
						font-size: 24rpx;
						color: #999;
					}
					.my-exer-list__value {
						font-size: 24rpx;
						color: #3b4144;
						margin-right: 30rpx;
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
					.form {
					}
					.my-exer__row {
						display: flex;
						align-items: center;
						.my-exer__value {
							width: 200rpx;
						}
					}

					.my-exer__btn {
						display: flex;
						flex-direction: row;
						justify-content: center;
						align-items: center;
						width: 100%;
						height: 100rpx;
						border-radius: 50px;
						line-height: 40rpx;
					}

					.my-exer__btn--active {
						background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
					}

					.my-exer__btn--disable {
						border: 1rpx solid #04c7f2;
						color: #04c7f2;
						background: linear-gradient(to right, #fff 0%, #fff 100%);
					}

					// display: flex;
					// flex-direction: column;

					// .my-exer__row {
					// 	display: flex;
					// 	height: 60rpx;
					// 	.my-exer__label {
					// 		font-size: 26rpx;
					// 		color: #8f939c;
					// 		line-height: 60rpx;
					// 	}
					// 	.my-exer__value {
					// 		font-size: 26rpx;
					// 		color: #333333;
					// 		line-height: 60rpx;
					// 	}
					// }
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
	}
}
</style>
