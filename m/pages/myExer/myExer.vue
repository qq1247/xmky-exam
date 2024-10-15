<template>
	<view class="myexer">
		<view class="myexer-head">
			<uni-search-bar
				v-model="queryForm.examName"
				bgColor="#fff"
				clearButton="auto"
				cancelButton="none"
				radius="16"
				placeholder="请输入练习名称"
				@confirm="
					myExerQuery(false);
					todoExerQuery(false);
				"
				@cancel="
					myExerQuery(false);
					todoExerQuery(false);
				"
			></uni-search-bar>
		</view>
		<view class="myexer-main">
			<uv-tabs
				:list="[
					{ name: '全部', badge: { value: myExerListpage.total } },
					{ name: '进行中', badge: { value: todoExerListpage.total } }
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
			<scroll-view scroll-y="true" class="myexer-main__scroll" :style="{ height: taskListHeight + 'px' }">
				<xm-card
					v-if="curTabIndex === 0"
					v-for="(myexer, index) in myExerListpage.list"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="myexer.name"
					tag-name="练习"
				>
					<template #content>
						<view>
							<text>{{ myexer.startTime }} 至 {{ myexer.endTime }}</text>
						</view>
					</template>
					<template #opt>
						<view class="myexer-main__opt">
							<view>
								<view class="myexer-main__state">
									<uni-icons
										customPrefix="iconfont"
										:type="['icon-kaoshiyemiantubiao-06', 'icon-kaoshiyemiantubiao-05', 'icon-kaoshiyemiantubiao-04'][myexer.state - 1]"
										:color="['#8f939c', '#ff5d15', '#18bc38'][myexer.state - 1]"
										size="26rpx"
									></uni-icons>
									<text
										:class="[
											'myexer-main__state-name',
											{ 'myexer-main__state-name--warn': myexer.state === 2 },
											{ 'myexer-main__state-name--succ': myexer.state === 3 }
										]"
									>
										{{ dictStore.getValue('EXAM_STATE', myexer.state) }}
									</text>
								</view>
								<view class="myexer-main__state">
									<uni-icons
										customPrefix="iconfont"
										:type="['icon-kaoshiyemiantubiao-01', 'icon-kaoshiyemiantubiao-02', 'icon-kaoshiyemiantubiao-03'][myexer.markState - 1]"
										:color="['#8f939c', '#ff5d15', '#18bc38'][myexer.markState - 1]"
										size="26rpx"
									></uni-icons>
									<text
										:class="[
											'myexer-main__state-name',
											{ 'myexer-main__state-name--warn': myexer.markState === 2 },
											{ 'myexer-main__state-name--succ': myexer.markState === 3 }
										]"
									>
										{{ dictStore.getValue('MARK_STATE', myexer.markState) }}
									</text>
								</view>
							</view>
							<button type="primary" @click="toExer(myexer)" class="myexer-main__exam-in">查看详情</button>
						</view>
					</template>
				</xm-card>
				<uni-load-more
					v-if="curTabIndex === 0 && myExerListpage.list?.length"
					:status="myExerListpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="myExerQuery(true)"
				></uni-load-more>
				<xm-empty v-if="curTabIndex === 0 && !myExerListpage.list?.length"></xm-empty>

				<xm-card
					v-if="curTabIndex === 1"
					v-for="(todoExer, index) in todoExerListpage.list"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="todoExer.name"
					tag-name="练习"
				>
					<template #content>
						<view>
							<text>{{ todoExer.startTime }} 至 {{ todoExer.endTime }}</text>
						</view>
					</template>
					<template #opt>
						<view class="myexer-main__opt">
							<view>
								<text>距离练习开始：</text>
								<xm-count-down :expireTime="todoExer.startTime"></xm-count-down>
							</view>
							<button type="primary" @click="toExer(todoExer)" class="myexer-main__exam-in">进入练习</button>
						</view>
					</template>
				</xm-card>
				<uni-load-more
					v-if="curTabIndex === 1 && todoExerListpage.list?.length"
					:status="todoExerListpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="todoExerQuery(true)"
				></uni-load-more>
				<xm-empty v-if="curTabIndex === 1 && !todoExerListpage.list?.length"></xm-empty>
			</scroll-view>
		</view>
		<view class="myexer-bottom"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { MyExer } from '@/ts/myexer.d';
import { Page } from '@/ts/page.d';
import { myExerListpage as _myExerListpage } from '@/api/myexer';
import { loginSysTime } from '@/api/login';

/************************变量定义相关***********************/
const curTabIndex = ref(0); // 当前选择标签页
const dictStore = useDictStore();
const queryForm = reactive({
	examName: '' // 练习名称
});
const todoExerListpage = reactive<Page<MyExer>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 未完成练习列表
const myExerListpage = reactive<Page<MyExer>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 我的练习列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	todoExerQuery(false); //onLoad只加载一次
	myExerQuery(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.myexer-main__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************计算属性相关*************************/
// 相差分钟
const diffMinute = computed(() => (startTime: string, endTime: string) => {
	if (!startTime || !endTime) {
		return '-';
	}
	return Math.ceil((new Date(endTime.replaceAll('-', '/')).getTime() - new Date(startTime.replaceAll('-', '/')).getTime()) / 1000 / 60);
});

/************************事件相关*****************************/
// 未完成练习列表查询
async function todoExerQuery(append: boolean) {
	todoExerListpage.status = 'loading';
	todoExerListpage.curPage = append ? todoExerListpage.curPage + 1 : 1;

	let { data } = await _myExerListpage({
		todo: true,
		...queryForm,
		curPage: todoExerListpage.curPage,
		pageSize: todoExerListpage.pageSize
	});

	if (append) {
		data.list.length && todoExerListpage.list.push(...data.list);
	} else {
		todoExerListpage.list = data.list;
	}
	todoExerListpage.total = data.total;
	todoExerListpage.status = todoExerListpage.list.length < todoExerListpage.total ? 'more' : 'no-more';
}
// 练习列表查询
async function myExerQuery(append: boolean) {
	myExerListpage.status = 'loading';
	myExerListpage.curPage = append ? myExerListpage.curPage + 1 : 1;

	let { data } = await _myExerListpage({
		...queryForm,
		curPage: myExerListpage.curPage,
		pageSize: myExerListpage.pageSize
	});

	if (append) {
		data.list.length && myExerListpage.list.push(...data.list);
	} else {
		myExerListpage.list = data.list;
	}
	myExerListpage.total = data.total;
	myExerListpage.status = myExerListpage.list.length < myExerListpage.total ? 'more' : 'no-more';
}

// 去练习
async function toExer(myExer: MyExer) {
	let { data } = await loginSysTime();
	let curTime = new Date(data.replaceAll('-', '/'));
	let exerStartTime = new Date(myExer.startTime.replaceAll('-', '/'));
	if (exerStartTime.getTime() > curTime.getTime()) {
		uni.showToast({ title: '练习未开始，请等待...', icon: 'error' });
		return;
	}

	let exerEndTime = new Date(myExer.endTime.replaceAll('-', '/'));
	if (curTime.getTime() > exerEndTime.getTime()) {
		uni.showToast({ title: '练习已结束...', icon: 'error' });
		return;
	}

	uni.navigateTo({ url: `/pages/myExer/myPaper?exerId=${myExer.id}` });
}
</script>
<style lang="scss" scoped>
.myexer {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.myexer-head {
	}
	.myexer-main {
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
		.myexer-main__scroll {
			.myexer-main__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.myexer-main__state {
					display: inline-block;
					margin-right: 40rpx;
					.myexer-main__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.myexer-main__state-name--warn {
						color: #ff5d15;
					}
					.myexer-main__state-name--succ {
						color: #18bc38;
					}
				}
				.myexer-main__exam-in {
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
			.myexer-main__value {
				color: #333;
				margin-right: 30rpx;
			}
		}
	}
}
</style>