<template>
	<xmky-layout :tabs="tabbarStore.examUser">
		<view class="mycourse">
			<view class="mycourse-head">
				<uni-search-bar
					v-model="queryForm.name"
					bgColor="#fff"
					radius="10"
					placeholder="请输入课程名称"
					class="query"
					@confirm="
						() => {
							query(false);
						}
					"
					@cancel="
						() => {
							queryForm.name = '';
							query(false);
						}
					"
					@clear="
						() => {
							queryForm.name = '';
							query(false);
						}
					"
				></uni-search-bar>
			</view>
			<view class="mycourse-main">
				<scroll-view scroll-y="true" class="mycourse-main__scroll" :style="{ height: taskListHeight + 'px' }">
					<xmky-card v-for="(course, index) in listpage.list" :key="index" :preTxt="(index + 1).toString().padStart(2, '0')" :name="course.name" tag-name="课程">
						<template #content>
							<view class="mycourse-main__head">
								<text>资料合计：</text>
								<text class="mycourse-main__value">{{ course.courseMaterialNum }}个</text>
								<text>试题合计：</text>
								<text class="mycourse-main__value">{{ course.questionNum }}道</text>
							</view>
						</template>
						<template #opt>
							<view class="mycourse-main__opt">
								<view>
									<view class="mycourse-main__state"></view>
									<view class="mycourse-main__state"></view>
								</view>
								<button type="primary" @click="toCourse(course)" class="mycourse-main__course-in">进入课程</button>
							</view>
						</template>
					</xmky-card>
					<uni-load-more
						v-if="listpage.list?.length"
						:status="listpage.status"
						:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
						@clickLoadMore="query(true)"
					></uni-load-more>
					<xmky-empty v-if="!listpage.list?.length"></xmky-empty>
				</scroll-view>
			</view>
			<view class="mycourse-bottom"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { myCourseCourseListpage } from '@/api/my-course';
import { Course } from '@/ts/course.d';
import { useTabbarStore } from '@/stores/tabbar';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
const queryForm = reactive({
	name: '' // 课程名称
});
const listpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 我的课程列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.mycourse-main__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 课程列表查询
async function query(append: boolean) {
	listpage.status = 'loading';
	listpage.curPage = append ? listpage.curPage + 1 : 1;

	let { data } = await myCourseCourseListpage({
		...queryForm,
		state: 1,
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

// 去学习
async function toCourse(course: Course) {
	uni.navigateTo({ url: `/pages/exam-user/my-course/my-read?courseId=${course.id}` });
}
</script>
<style lang="scss" scoped>
.mycourse {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.mycourse-head {
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
	.mycourse-main {
		overflow: hidden;
		border-radius: 30rpx;
		.mycourse-main__scroll {
			padding-bottom: max(20px, env(safe-area-inset-bottom, 20px));
			.mycourse-main__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.mycourse-main__state {
					display: inline-block;
					margin-right: 40rpx;
					.mycourse-main__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.mycourse-main__state-name--warn {
						color: #ff5d15;
					}
					.mycourse-main__state-name--succ {
						color: #18bc38;
					}
				}
				.mycourse-main__course-in {
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
			.mycourse-main__head {
				display: flex;
				justify-content: center;
			}
			.mycourse-main__value {
				color: #333;
				margin-right: 20rpx;
			}
		}
	}
}
</style>
