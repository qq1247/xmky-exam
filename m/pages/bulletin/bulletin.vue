<template>
	<view class="bulletin">
		<view class="bulletin-head">
			<uni-search-bar
				v-model="queryForm.bulletinName"
				bgColor="#fff"
				clearButton="auto"
				cancelButton="none"
				radius="16"
				placeholder="请输入公告名称"
				@confirm="bulletinQuery(false)"
				@cancel="bulletinQuery(false)"
			></uni-search-bar>
		</view>
		<view class="bulletin-main">
			<uv-tabs
				:list="[{ name: '全部', badge: { value: bulletinListpage.total } }]"
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
			<scroll-view scroll-y="true" class="bulletin-main__scroll" :style="{ height: taskListHeight + 'px' }">
				<xmky-card
					v-if="curTabIndex === 0"
					v-for="(bulletin, index) in bulletinListpage.list"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="bulletin.title"
					tag-name="公告"
				>
					<template #content>
						<view>
							<text>{{ truncate(bulletin.content) }}</text>
						</view>
					</template>
					<template #opt>
						<view class="bulletin-main__opt">
							<view>
								<text>{{ bulletin.startTime }}</text>
							</view>
							<text @click="toBulletin(bulletin)" class="bulletin-main__in">查看详情 >></text>
						</view>
					</template>
				</xmky-card>
				<uni-load-more
					v-if="curTabIndex === 0 && bulletinListpage.list?.length"
					:status="bulletinListpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="bulletinQuery(true)"
				></uni-load-more>
				<xmky-empty v-if="curTabIndex === 0 && !bulletinListpage.list?.length"></xmky-empty>
			</scroll-view>
		</view>
		<view class="bulletin-bottom"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { bulletinListpage as _bulletinListpage } from '@/api/bulletin';

/************************变量定义相关***********************/
const curTabIndex = ref(0); // 当前选择标签页
const queryForm = reactive({
	bulletinName: '' // 公告名称
});
const bulletinListpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 我的公告列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onLoad(() => {
	bulletinQuery(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.bulletin-main__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************计算属性相关*************************/
const truncate = computed(() => (content: string) => content.length <= 10 ? content : content.replaceAll('\n', '').slice(0, 20) + '...'); // 截取

/************************事件相关*****************************/
// 公告列表查询
async function bulletinQuery(append: boolean) {
	bulletinListpage.status = 'loading';
	bulletinListpage.curPage = append ? bulletinListpage.curPage + 1 : 1;

	let { data } = await _bulletinListpage({
		...queryForm,
		curPage: bulletinListpage.curPage,
		pageSize: bulletinListpage.pageSize
	});

	if (append) {
		data.list.length && bulletinListpage.list.push(...data.list);
	} else {
		bulletinListpage.list = data.list;
	}
	bulletinListpage.total = data.total;
	bulletinListpage.status = bulletinListpage.list.length < bulletinListpage.total ? 'more' : 'no-more';
}

// 去公告
async function toBulletin(bulletin: any) {
	uni.navigateTo({ url: `/pages/bulletin/bulletinDetail?id=${bulletin.id}` });
}
</script>
<style lang="scss" scoped>
.bulletin {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.bulletin-head {
	}
	.bulletin-main {
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
		.bulletin-main__scroll {
			:deep(.xmky-card__content) {
				margin-top: 0rpx;
			}
			.bulletin-main__opt {
				height: 50rpx;
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.bulletin-main__in {
					font-size: 26rpx;
					color: #0d9df6;
				}
			}
		}
	}
}
</style>
