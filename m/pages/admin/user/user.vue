<template>
	<xmky-layout :tabs="tabbarStore.admin">
		<view class="user">
			<view class="user__main">
				<uv-tabs
					:list="
						userStore.user.type === 0
							? [{ name: '机构管理' }, { name: '考试用户' }, { name: '阅卷用户' }, { name: '子管理员' }]
							: [{ name: '机构管理' }, { name: '考试用户' }, { name: '阅卷用户' }]
					"
					:current="curTabIndex"
					:scrollable="false"
					lineHeight="4rpx"
					lineWidth="160rpx"
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
				<scroll-view scroll-y="true" class="user__scroll" :style="{ height: taskListHeight + 'px' }">
					<template v-if="curTabIndex === 0">
						<view v-for="(org, index) in orgListpage.list" :key="index" class="list">
							<view class="list__wrap">
								<view class="list__title">{{ org.name }}</view>
							</view>
							<!-- <view class="list__nav" @click="">
								<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
							</view> -->
						</view>
						<uni-load-more
							v-if="orgListpage.list?.length"
							:status="orgListpage.status"
							:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
							@clickLoadMore="orgQuery(true)"
						></uni-load-more>
						<xmky-empty v-if="!orgListpage.list?.length"></xmky-empty>
					</template>
					<template v-if="curTabIndex === 1">
						<view v-for="(examUser, index) in examUserListpage.list" :key="index" class="list">
							<view class="list__wrap">
								<view class="list__title">{{ examUser.name }} | {{ examUser.loginName }}</view>
								<view class="list__outer">
									<view class="list__tag list__tag--type">{{ examUser.orgName }}</view>
									<view class="list__tag llist__tag--mark-type">{{ dictStore.getValue('STATE_NF', examUser.state) }}</view>
								</view>
							</view>
							<!-- <view class="list__nav" @click="">
								<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
							</view> -->
						</view>
						<uni-load-more
							v-if="examUserListpage.list?.length"
							:status="examUserListpage.status"
							:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
							@clickLoadMore="examUserQuery(true)"
						></uni-load-more>
						<xmky-empty v-if="!examUserListpage.list?.length"></xmky-empty>
					</template>
					<template v-if="curTabIndex === 2">
						<view v-for="(markUser, index) in markUserListpage.list" :key="index" class="list">
							<view class="list__wrap">
								<view class="list__title">{{ markUser.name }} | {{ markUser.loginName }}</view>
								<view class="list__outer"></view>
							</view>
							<!-- <view class="list__nav" @click="">
								<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
							</view> -->
						</view>
						<uni-load-more
							v-if="markUserListpage.list?.length"
							:status="markUserListpage.status"
							:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
							@clickLoadMore="orgQuery(true)"
						></uni-load-more>
						<xmky-empty v-if="!markUserListpage.list?.length"></xmky-empty>
					</template>
					<template v-if="curTabIndex === 3">
						<view v-for="(subAdmin, index) in subAdminListpage.list" :key="index" class="list">
							<view class="list__wrap">
								<view class="list__title">{{ subAdmin.name }} | {{ subAdmin.loginName }}</view>
								<view class="list__outer"></view>
							</view>
							<!-- <view class="list__nav" @click="">
								<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
							</view> -->
						</view>
						<uni-load-more
							v-if="subAdminListpage.list?.length"
							:status="subAdminListpage.status"
							:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
							@clickLoadMore="orgQuery(true)"
						></uni-load-more>
						<xmky-empty v-if="!subAdminListpage.list?.length"></xmky-empty>
					</template>
				</scroll-view>
			</view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { useDictStore } from '@/stores/dict';
import { MyExam } from '@/ts/myExam.d';
import { Page } from '@/ts/page.d';
import { userListpage as _userListpage } from '@/api/user';
import { orgListpage as _orgListpage } from '@/api/org';
import { loginSysTime } from '@/api/login';
import dayjs from 'dayjs';
import { useTabbarStore } from '@/stores/tabbar';
import { useUserStore } from '@/stores/user';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
const curTabIndex = ref(0); // 当前选择标签页
const dictStore = useDictStore();
const userStore = useUserStore();
const orgListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 机构列表
const examUserListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 2,
	total: 0,
	list: [],
	status: 'more'
}); // 考试用户列表
const markUserListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 阅卷用户列表
const subAdminListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 子管理员列表

const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onLoad(async () => {
	orgQuery(false);
	examUserQuery(false);
	markUserQuery(false);
	if (userStore.user.type === 0) {
		subAdminQuery(false);
	}
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.user__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 机构列表
async function orgQuery(append: boolean) {
	orgListpage.status = 'loading';
	orgListpage.curPage = append ? orgListpage.curPage + 1 : 1;

	let { data } = await _orgListpage({
		curPage: orgListpage.curPage,
		pageSize: orgListpage.pageSize
	});

	if (append) {
		data.list.length && orgListpage.list.push(...data.list);
	} else {
		orgListpage.list = data.list;
	}
	orgListpage.total = data.total;
	orgListpage.status = orgListpage.list.length < orgListpage.total ? 'more' : 'no-more';
}

// 考试用户列表
async function examUserQuery(append: boolean) {
	examUserListpage.status = 'loading';
	examUserListpage.curPage = append ? examUserListpage.curPage + 1 : 1;

	let { data } = await _userListpage({
		type: 1,
		curPage: examUserListpage.curPage,
		pageSize: examUserListpage.pageSize
	});

	if (append) {
		data.list.length && examUserListpage.list.push(...data.list);
	} else {
		examUserListpage.list = data.list;
	}
	examUserListpage.total = data.total;
	examUserListpage.status = examUserListpage.list.length < examUserListpage.total ? 'more' : 'no-more';
}

// 阅卷用户列表
async function markUserQuery(append: boolean) {
	markUserListpage.status = 'loading';
	markUserListpage.curPage = append ? markUserListpage.curPage + 1 : 1;

	let { data } = await _userListpage({
		type: 3,
		curPage: markUserListpage.curPage,
		pageSize: markUserListpage.pageSize
	});

	if (append) {
		data.list.length && markUserListpage.list.push(...data.list);
	} else {
		markUserListpage.list = data.list;
	}
	markUserListpage.total = data.total;
	markUserListpage.status = markUserListpage.list.length < markUserListpage.total ? 'more' : 'no-more';
}

// 子管理员列表
async function subAdminQuery(append: boolean) {
	subAdminListpage.status = 'loading';
	subAdminListpage.curPage = append ? subAdminListpage.curPage + 1 : 1;

	let { data } = await _userListpage({
		type: 2,
		curPage: subAdminListpage.curPage,
		pageSize: subAdminListpage.pageSize
	});

	if (append) {
		data.list.length && subAdminListpage.list.push(...data.list);
	} else {
		subAdminListpage.list = data.list;
	}
	subAdminListpage.total = data.total;
	subAdminListpage.status = subAdminListpage.list.length < subAdminListpage.total ? 'more' : 'no-more';
}
</script>
<style lang="scss" scoped>
.user {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	.user__head {
	}
	.user__main {
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
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
		.user__scroll {
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
