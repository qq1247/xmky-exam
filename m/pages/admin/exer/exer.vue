<template>
	<xmky-layout :tabs="tabbarStore.admin">
		<view class="exer">
			<view class="exer__head">
				<uni-search-bar
					v-model="queryForm.name"
					bgColor="#fff"
					radius="10"
					placeholder="请输入练习名称"
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
			<view class="exer__main">
				<scroll-view scroll-y="true" class="exer__scroll" :style="{ height: scrollHeight + 'px' }">
					<xmky-card v-for="(exer, index) in listpage.list" :key="index" :preTxt="(index + 1).toString().padStart(2, '0')" :name="exer.name" tag-name="练习">
						<template #content>
							<view class="exer__row">
								<text>练习已</text>
								<text class="exer__value">{{ dictStore.getValue('STATE_PS', exer.state) }}</text>
							</view>
							<view>
								<text>题库选</text>
								<text class="exer__value">{{ exer.questionBankIds.length }}个</text>
								<text>试题共</text>
								<text class="exer__value">{{ exer.objectiveNum + exer.subjectiveNum }}道</text>
								<text>机构选</text>
								<text class="exer__value">{{ exer.orgIds.length }}个</text>
								<text>用户选</text>
								<text class="exer__value">{{ exer.userIds.length }}个</text>
							</view>
						</template>
						<template #opt>
							<view class="exer__opt">
								<view>
									<view class="exer__state">{{ exer.createUserName }}</view>
									<view class="exer__state">{{ exer.updateTime }}</view>
								</view>
								<button type="primary" @click="toExerStatis(exer.id)" class="exer__btn">进入练习</button>
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
			<view class="exer__foot"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { exerListpage } from '@/api/exer';
import { useDictStore } from '@/stores/dict';
import { useTabbarStore } from '@/stores/tabbar';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
const dictStore = useDictStore();
const queryForm = reactive({
	name: '' // 练习名称
});
const listpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 练习列表
const scrollHeight = ref(0); // 列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.exer__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 练习列表查询
async function query(append: boolean) {
	listpage.status = 'loading';
	listpage.curPage = append ? listpage.curPage + 1 : 1;

	let { data } = await exerListpage({
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

// 去练习统计页面
async function toExerStatis(id: number) {
	uni.navigateTo({ url: `/pages/admin/exer/exer-statis?exerId=${id}` });
}
</script>
<style lang="scss" scoped>
.exer {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.exer__head {
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
	.exer__main {
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
		.exer__scroll {
			.exer__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.exer__state {
					display: inline-block;
					margin-right: 40rpx;
					.exer__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.exer__state-name--warn {
						color: #ff5d15;
					}
					.exer__state-name--succ {
						color: #18bc38;
					}
				}
				.exer__btn {
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
			.exer__row {
				display: flex;
			}
			.exer__value {
				color: #333;
				margin-right: 20rpx;
			}
		}
	}
}
</style>
