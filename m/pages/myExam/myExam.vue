<template>
	<view class="myexam">
		<view class="myexam-head">
			<uni-search-bar
				v-model="queryForm.examName"
				bgColor="#fff"
				clearButton="auto"
				cancelButton="none"
				radius="16"
				placeholder="请输入考试名称"
				@confirm="
					myExamQuery(false);
					todoExamQuery(false);
				"
				@cancel="
					myExamQuery(false);
					todoExamQuery(false);
				"
			></uni-search-bar>
		</view>
		<view class="myexam-main">
			<uv-tabs
				:list="[
					{ name: '全部', badge: { value: myExamListpage.total } },
					{ name: '进行中', badge: { value: todoExamListpage.total } }
				]"
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
			<scroll-view scroll-y="true" class="myexam-main__scroll" :style="{ height: taskListHeight + 'px' }">
				<xm-card
					v-if="curTabIndex === 0"
					v-for="(myExam, index) in myExamListpage.list"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="myExam.examName"
					tag-name="考试"
				>
					<template #content>
						<view>
							<text>{{ myExam.examStartTime }} 至 {{ myExam.examEndTime }}</text>
						</view>
						<view>
							<text>用时：</text>
							<text class="myexam-main__value">{{ diffMinute(myExam.examStartTime, myExam.examEndTime) }}分钟</text>
							<text>分数：</text>
							<text class="myexam-main__value">
								{{ myExam.totalScore == null ? '-' : myExam.totalScore }} / {{ myExam.examTotalScore == null ? '-' : myExam.examTotalScore }}
							</text>
							<text>排名：</text>
							<text class="myexam-main__value">{{ myExam.no == null ? '-' : myExam.no }} / {{ myExam.userNum == null ? '-' : myExam.userNum }}</text>
						</view>
					</template>
					<template #opt>
						<view class="myexam-main__opt">
							<view>
								<view class="myexam-main__state">
									<uni-icons
										customPrefix="iconfont"
										:type="['icon-kaoshiyemiantubiao-06', 'icon-kaoshiyemiantubiao-05', 'icon-kaoshiyemiantubiao-04'][myExam.state - 1]"
										:color="['#8f939c', '#ff5d15', '#18bc38'][myExam.state - 1]"
										size="26rpx"
									></uni-icons>
									<text
										:class="[
											'myexam-main__state-name',
											{ 'myexam-main__state-name--warn': myExam.state === 2 },
											{ 'myexam-main__state-name--succ': myExam.state === 3 }
										]"
									>
										{{ dictStore.getValue('EXAM_STATE', myExam.state) }}
									</text>
								</view>
								<view class="myexam-main__state">
									<uni-icons
										customPrefix="iconfont"
										:type="['icon-kaoshiyemiantubiao-01', 'icon-kaoshiyemiantubiao-02', 'icon-kaoshiyemiantubiao-03'][myExam.markState - 1]"
										:color="['#8f939c', '#ff5d15', '#18bc38'][myExam.markState - 1]"
										size="26rpx"
									></uni-icons>
									<text
										:class="[
											'myexam-main__state-name',
											{ 'myexam-main__state-name--warn': myExam.markState === 2 },
											{ 'myexam-main__state-name--succ': myExam.markState === 3 }
										]"
									>
										{{ dictStore.getValue('MARK_STATE', myExam.markState) }}
									</text>
								</view>
							</view>
							<button type="primary" @click="toExam(myExam)" class="myexam-main__exam-in">
								{{ examing(myExam.state, myExam.markState) ? '进入考试' : '查看详情' }}
							</button>
						</view>
					</template>
				</xm-card>
				<uni-load-more
					v-if="curTabIndex === 0 && myExamListpage.list?.length"
					:status="myExamListpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="myExamQuery(true)"
				></uni-load-more>
				<xm-empty v-if="curTabIndex === 0 && !myExamListpage.list?.length"></xm-empty>

				<xm-card
					v-if="curTabIndex === 1"
					v-for="(todoExam, index) in todoExamListpage.list"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="todoExam.examName"
					tag-name="考试"
				>
					<template #content>
						<view>
							<text>{{ todoExam.examStartTime }} 至 {{ todoExam.examEndTime }}</text>
						</view>
						<view>
							<text>分数：</text>
							<text class="myexam-main__value">{{ todoExam.examPassScore }} / {{ todoExam.examTotalScore }}</text>
							<text>限时：</text>
							<text class="myexam-main__value">{{ todoExam.examLimitMinute === 0 ? '无' : todoExam.examLimitMinute + '分钟' }}</text>
						</view>
					</template>
					<template #opt>
						<view class="myexam-main__opt">
							<view>
								<text>距离考试开始：</text>
								<xm-count-down :expireTime="todoExam.examStartTime"></xm-count-down>
							</view>
							<button type="primary" @click="toExam(todoExam)" class="myexam-main__exam-in">进入考试</button>
						</view>
					</template>
				</xm-card>
				<uni-load-more
					v-if="curTabIndex === 1 && todoExamListpage.list?.length"
					:status="todoExamListpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="todoExamQuery(true)"
				></uni-load-more>
				<xm-empty v-if="curTabIndex === 1 && !todoExamListpage.list?.length"></xm-empty>
			</scroll-view>
		</view>
		<view class="myexam-bottom"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { MyExam } from '@/ts/myExam.d';
import { Page } from '@/ts/page.d';
import { myExamListpage as _myExamListpage } from '@/api/myExam';

/************************变量定义相关***********************/
const curTabIndex = ref(0); // 当前选择标签页
const dictStore = useDictStore();
const queryForm = reactive({
	examName: '' // 考试名称
});
const todoExamListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 未完成考试列表
const myExamListpage = reactive<Page<MyExam>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 我的考试列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	todoExamQuery(false); //onLoad只加载一次
	myExamQuery(false);
});
onReady(() => {
	uni.getSystemInfo({
		success(res) {
			uni.createSelectorQuery()
				.select('.myexam-main__scroll')
				.boundingClientRect((data: any) => {
					taskListHeight.value = res.windowHeight - data.top;
				})
				.exec();
		}
	});
});

/************************计算属性相关*************************/
const examing = computed(() => (state: number, markState: number) => (state === 1 && markState != 3) || state === 2); // 考试中

const diffMinute = computed(() => (startTime: string, endTime: string) => {
	if (!startTime || !endTime) {
		return '-';
	}
	return Math.ceil((new Date(endTime.replaceAll('-', '/')).getTime() - new Date(startTime.replaceAll('-', '/')).getTime()) / 1000 / 60);
}); // 相差分钟

/************************事件相关*****************************/
// 未完成考试列表查询
async function todoExamQuery(append: boolean) {
	todoExamListpage.status = 'loading';
	todoExamListpage.curPage = append ? todoExamListpage.curPage + 1 : 1;

	let { data } = await _myExamListpage({
		todo: true,
		...queryForm,
		curPage: todoExamListpage.curPage,
		pageSize: todoExamListpage.pageSize
	});

	if (append) {
		data.list.length && todoExamListpage.list.push(...data.list);
	} else {
		todoExamListpage.list = data.list;
	}
	todoExamListpage.total = data.total;
	todoExamListpage.status = todoExamListpage.list.length < todoExamListpage.total ? 'more' : 'no-more';
}
// 考试列表查询
async function myExamQuery(append: boolean) {
	myExamListpage.status = 'loading';
	myExamListpage.curPage = append ? myExamListpage.curPage + 1 : 1;

	let { data } = await _myExamListpage({
		...queryForm,
		curPage: myExamListpage.curPage,
		pageSize: myExamListpage.pageSize
	});

	if (append) {
		data.list.length && myExamListpage.list.push(...data.list);
	} else {
		myExamListpage.list = data.list;
	}
	myExamListpage.total = data.total;
	myExamListpage.status = myExamListpage.list.length < myExamListpage.total ? 'more' : 'no-more';
}

// 去考试
async function toExam(myExam: MyExam) {
	uni.navigateTo({ url: `/pages/myExam/myRead?examId=${myExam.examId}` });
}
</script>
<style lang="scss" scoped>
.myexam {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.myexam-head {
	}
	.myexam-main {
		// 小程序没有uni-scroll-view-content
		// #ifdef H5
		:deep(.uni-scroll-view-content) {
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
		.myexam-main__scroll {
			.myexam-main__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.myexam-main__state {
					display: inline-block;
					margin-right: 40rpx;
					.myexam-main__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.myexam-main__state-name--warn {
						color: #ff5d15;
					}
					.myexam-main__state-name--succ {
						color: #18bc38;
					}
				}
				.myexam-main__exam-in {
					width: 180rpx;
					height: 66rpx;
					margin: initial;
					border-radius: 33rpx 33rpx 33rpx 33rpx;
					line-height: 66rpx;
					font-size: 30rpx;
					color: #fefeff;
					background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
				}
			}
			.myexam-main__value {
				color: #333;
				margin-right: 30rpx;
			}
		}
	}
}
</style>
