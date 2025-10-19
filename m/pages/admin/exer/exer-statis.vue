<template>
	<view class="exer-statis">
		<view class="exer-statis__head">
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
		<view class="exer-statis__main">
			<uv-tabs
				:list="[{ name: '练习时长' }, { name: '答错数量' }]"
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
				@change="(item: any) => (curTabIndex = item.index)"
			></uv-tabs>
			<scroll-view scroll-y="true" class="exer-statis__scroll" :style="{ height: taskListHeight + 'px' }">
				<template v-if="curTabIndex === 0">
					<view v-for="(exerTrack, index) in exerTrackListpage.list" :key="index" class="list">
						<view class="list__wrap">
							<view class="list__title">{{ exerTrack.userName }} | {{ exerTrack.orgName }}</view>
							<view class="list__outer">
								<view class="list__tag list__tag--type">当月{{ exerTrack.tracks[exerTrack.tracks.length - 1].minuteCount }}分钟</view>
								<view class="list__tag llist__tag--mark-type">上月{{ exerTrack.tracks[exerTrack.tracks.length - 2].minuteCount }}分钟</view>
								<view class="list__tag list__tag--score">
									近一年{{ exerTrack.tracks.reduce((sum: number, track: any) => sum + (track.minuteCount || 0), 0) }}分钟
								</view>
							</view>
						</view>
						<view class="list__nav" @click="toTrackChart(exerTrack.tracks)">
							<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
						</view>
					</view>
					<uni-load-more
						v-if="exerTrackListpage.list?.length"
						:status="exerTrackListpage.status"
						:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
						@clickLoadMore="exerTrackQuery(true)"
					></uni-load-more>
				</template>
				<template v-if="curTabIndex === 1">
					<view v-for="(exerWrongQuestion, index) in exerWrongQuestionListpage.list" :key="index" class="list">
						<view class="list__wrap">
							<view class="list__title">{{ exerWrongQuestion.userName }} | {{ exerWrongQuestion.orgName }}</view>
							<view class="list__outer">
								<view class="list__tag list__tag--type">
									已练{{ exerWrongQuestion.answeredNum }}道，正确率{{
										exerWrongQuestion.answeredNum
											? Math.round(((exerWrongQuestion.answeredNum - exerWrongQuestion.wrongAnswerNum) / exerWrongQuestion.answeredNum) * 100) + '%'
											: '0%'
									}}
								</view>
								<view class="list__tag llist__tag--mark-type">
									共{{ exerWrongQuestion.totalQuestionNum }}道，完成率{{
										exerWrongQuestion.totalQuestionNum ? Math.round((exerWrongQuestion.answeredNum / exerWrongQuestion.totalQuestionNum) * 100) + '%' : '0%'
									}}
								</view>
							</view>
						</view>
						<view class="list__nav" @click="toWrongQuestionChart(exerWrongQuestion.questions)">
							<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
						</view>
					</view>
					<uni-load-more
						v-if="exerWrongQuestionListpage.list?.length"
						:status="exerWrongQuestionListpage.status"
						:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
						@clickLoadMore="exerWrongQuestionQuery(true)"
					></uni-load-more>
				</template>
			</scroll-view>
		</view>
		<view class="exer-statis-bottom"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { MyExam } from '@/ts/myExam.d';
import { Page } from '@/ts/page.d';
import { reportExerTrackListpage as _exerTrackListpage, reportExerWrongQuestionListpage as _exerWrongQuestionListpage } from '@/api/report';
import { loginSysTime } from '@/api/login';
import dayjs from 'dayjs';

/************************变量定义相关***********************/
const curTabIndex = ref(0); // 当前选择标签页
const dictStore = useDictStore();
const pageParm = reactive({
	exerId: null
});
const exerTrackListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 练习跟踪列表
const exerWrongQuestionListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 练习错题列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onLoad(async (option) => {
	pageParm.exerId = option.exerId;
	exerTrackQuery(false);
	exerWrongQuestionQuery(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.exer-statis__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 20;
		})
		.exec();
});

/************************事件相关*****************************/
// 练习跟踪列表
async function exerTrackQuery(append: boolean) {
	exerTrackListpage.status = 'loading';
	exerTrackListpage.curPage = append ? exerTrackListpage.curPage + 1 : 1;

	const { data: sysTime } = await loginSysTime({});
	const startTime = dayjs(sysTime).subtract(1, 'year').startOf('month').valueOf(); // 去年同月的第一天（去年的今天会丢数据）
	const endTime = dayjs(sysTime).startOf('day').valueOf(); // 今天

	let { data } = await _exerTrackListpage({
		exerId: pageParm.exerId,
		startYm: dayjs(startTime).format('YYYY-MM'),
		endYm: dayjs(endTime).format('YYYY-MM'),
		curPage: exerTrackListpage.curPage,
		pageSize: exerTrackListpage.pageSize
	});

	if (append) {
		data.list.length && exerTrackListpage.list.push(...data.list);
	} else {
		exerTrackListpage.list = data.list;
	}
	exerTrackListpage.total = data.total;
	exerTrackListpage.status = exerTrackListpage.list.length < exerTrackListpage.total ? 'more' : 'no-more';
}
// 练习错题列表查询
async function exerWrongQuestionQuery(append: boolean) {
	exerWrongQuestionListpage.status = 'loading';
	exerWrongQuestionListpage.curPage = append ? exerWrongQuestionListpage.curPage + 1 : 1;

	let { data } = await _exerWrongQuestionListpage({
		exerId: pageParm.exerId,
		curPage: exerWrongQuestionListpage.curPage,
		pageSize: exerWrongQuestionListpage.pageSize
	});

	if (append) {
		data.list.length && exerWrongQuestionListpage.list.push(...data.list);
	} else {
		exerWrongQuestionListpage.list = data.list;
	}
	exerWrongQuestionListpage.total = data.total;
	exerWrongQuestionListpage.status = exerWrongQuestionListpage.list.length < exerWrongQuestionListpage.total ? 'more' : 'no-more';
}

// 打开跟踪图表
async function toTrackChart(tracks: []) {
	const tracksStr = JSON.stringify(tracks);
	uni.navigateTo({ url: `/pages/admin/exer/exer-track-chart?tracks=${tracksStr}` });
}

// 打开错题列表
async function toWrongQuestionChart(tracks: []) {
	uni.setStorageSync('tracks', tracks);
	uni.navigateTo({ url: `/pages/admin/exer/exer-wrong-question` });
}
</script>
<style lang="scss" scoped>
.exer-statis {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	.exer-statis__head {
	}
	.exer-statis__main {
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
		.exer-statis__scroll {
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
		}
	}
}
</style>
