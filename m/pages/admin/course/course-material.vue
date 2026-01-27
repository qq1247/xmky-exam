<template>
	<view class="course-material">
		<view class="course-material__head"></view>
		<view class="course-material__main">
			<scroll-view scroll-y="true" class="course-material__scroll" :style="{ height: scrollHeight + 'px' }">
				<view v-for="(courseMaterial, index) in listpage.list" :key="index" class="list">
					<view class="list__wrap">
						<view class="list__title">{{ courseMaterial.name }}</view>
						<view class="list__outer">
							<view class="list__tag list__tag--type">{{ courseMaterial.videoTime }}</view>
							<view class="list__tag llist__tag--mark-type">试题{{ courseMaterial.questionNum }}道</view>
							<view class="list__tag list__tag--score">第{{ courseMaterial.no }}小节</view>
							<!-- <view class="list__tag list__tag--user-name">{{ courseMaterial.updateUserName }}</view> -->
						</view>
					</view>
					<view class="list__nav" @click="">
						<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
					</view>
				</view>
				<uni-load-more
					v-if="listpage.list?.length"
					:status="listpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="query(true)"
				></uni-load-more>
				<xmky-empty v-if="!listpage.list?.length"></xmky-empty>
			</scroll-view>
		</view>
		<view class="course-material__foot"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { courseMaterialListpage } from '@/api/course-material';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const queryForm = reactive({
	courseId: null
});
const listpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 课程资料列表
const scrollHeight = ref(0); // 列表沾满剩余空间
queryForm;
/************************组件生命周期相关*********************/
onLoad(async (option) => {
	queryForm.courseId = option.courseId;
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.course-material__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top;
		})
		.exec();
});

/************************事件相关*****************************/
// 试题列表查询
async function query(append: boolean) {
	listpage.status = 'loading';
	listpage.curPage = append ? listpage.curPage + 1 : 1;

	let { data } = await courseMaterialListpage({
		...queryForm,
		curPage: listpage.curPage,
		pageSize: listpage.pageSize
	});

	if (append) {
		data.list.length && listpage.list.push(...data.list);
	} else {
		listpage.list = data.list;
	}
	listpage.total = data.total;
	listpage.status = listpage.list.length < listpage.total ? 'more' : 'no-more';
}

// 试题预览
function toView(id: number) {
	uni.navigateTo({ url: `/pages/admin/courseMaterial-bank/courseMaterial-view?id=${id}` });
}
</script>
<style lang="scss" scoped>
.course-material {
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.course-material__head {
		:deep(.query) {
			padding: 0px;
			margin-bottom: 10rpx;
			// #ifdef MP-WEIXIN
			.uni-searchbar {
				padding: 0px;
				margin-bottom: 10rpx;
			}
			// #endif
			.uni-searchbar__box {
				height: 86rpx;
			}
		}
	}
	.course-material__main {
		flex: 1;
		margin-top: 10rpx;
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
		.course-material__scroll {
			padding-bottom: max(20px, env(safe-area-inset-bottom, 20px));
		}
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
	.course-material__foot {
	}
}
</style>
