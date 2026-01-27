<template>
	<xmky-layout :tabs="tabbarStore.admin">
		<view class="course">
			<view class="course__head">
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
			<view class="course__main">
				<scroll-view scroll-y="true" class="course__scroll" :style="{ height: scrollHeight + 'px' }">
					<xmky-card v-for="(course, index) in listpage.list" :key="index" :preTxt="(index + 1).toString().padStart(2, '0')" :name="course.name" tag-name="课程">
						<template #content>
							<view class="course__row">
								<text>资料合计</text>
								<text class="course__value">{{ course.courseMaterialNum }}个</text>
								<text>试题合计</text>
								<text class="course__value">{{ course.questionNum }}道</text>
							</view>
							<view class="course__row">
								<text>机构已选</text>
								<text class="course__value">{{ course.orgIds.length }}个</text>
								<text>用户已选</text>
								<text class="course__value">{{ course.userIds.length }}个</text>
							</view>
						</template>
						<template #opt>
							<view class="course__opt">
								<view>
									<view class="course__state">{{ course.createUserName }} / {{ dictStore.getValue('SHARE_AUTH', course.shareAuth) }}权限</view>
									<!-- <view class="course__state">{{ course.updateTime }}</view> -->
								</view>
								<button type="primary" @click="toCourseMaterial(course.id)" class="course__btn">进入课程</button>
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
			<view class="course__foot"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { courseListpage } from '@/api/course';
import { useTabbarStore } from '@/stores/tabbar';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
const dictStore = useDictStore();
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
}); // 课程列表
const scrollHeight = ref(0); // 列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.course__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 课程列表查询
async function query(append: boolean) {
	listpage.status = 'loading';
	listpage.curPage = append ? listpage.curPage + 1 : 1;

	let { data } = await courseListpage({
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

// 去课程资料页面
async function toCourseMaterial(id: number) {
	uni.navigateTo({ url: `/pages/admin/course/course-material?courseId=${id}` });
}
</script>
<style lang="scss" scoped>
.course {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.course__head {
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
	.course__main {
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
		.course__scroll {
			.course__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.course__state {
					display: inline-block;
					margin-right: 40rpx;
					.course__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.course__state-name--warn {
						color: #ff5d15;
					}
					.course__state-name--succ {
						color: #18bc38;
					}
				}
				.course__btn {
					width: 190rpx;
					height: 66rpx;
					margin: initial;
					border-radius: 33rpx 33rpx 33rpx 33rpx;
					line-height: 66rpx;
					font-size: 29rpx; // 30在鸿蒙下概率性不显最后一个字
					color: #fefeff;
					background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
				}
			}
			.course__row {
				display: flex;
			}
			.course__value {
				color: #333;
				margin-right: 20rpx;
			}
		}
	}
}
</style>
