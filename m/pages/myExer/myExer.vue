<template>
	<view class="myexer">
		<view class="myexer-head">
			<uni-search-bar
				v-model="queryForm.name"
				bgColor="#fff"
				clearButton="auto"
				cancelButton="none"
				radius="16"
				placeholder="请输入练习名称"
				@confirm="query(false)"
				@cancel="query(false)"
			></uni-search-bar>
		</view>
		<view class="myexer-main">
			<scroll-view scroll-y="true" class="myexer-main__scroll" :style="{ height: taskListHeight + 'px' }">
				<xm-card v-for="(exer, index) in listpage.list" :key="index" :preTxt="(index + 1).toString().padStart(2, '0')" :name="exer.name" tag-name="练习">
					<template #content>
						<view class="myexer-main__head">
							<text>主观：</text>
							<text class="myexer-main__value">{{ exer.objectiveNum }}</text>
							<text>客观：</text>
							<text class="myexer-main__value">{{ exer.subjectiveNum }}</text>
						</view>
						<view>
							<text>单选：</text>
							<text class="myexer-main__value">{{ exer.singleNum }}</text>
							<text>多选：</text>
							<text class="myexer-main__value">{{ exer.multipleNum }}</text>
							<text>填空：</text>
							<text class="myexer-main__value">{{ exer.fillBlankObjNum + exer.fillBlankSubNum }}</text>
							<text>判断：</text>
							<text class="myexer-main__value">{{ exer.judgeNum }}</text>
							<text>问答：</text>
							<text class="myexer-main__value">{{ exer.qaObjNum + exer.qaSubNum }}</text>
						</view>
					</template>
					<template #opt>
						<view class="myexer-main__opt">
							<view>
								<view class="myexer-main__state"></view>
								<view class="myexer-main__state"></view>
							</view>
							<button type="primary" @click="toExer(exer)" class="myexer-main__exer-in">进入练习</button>
						</view>
					</template>
				</xm-card>
				<uni-load-more
					v-if="listpage.list?.length"
					:status="listpage.status"
					:contentText="{ contentdown: '点击查看更多', contentrefresh: '加载中', contentnomore: '没有更多数据了' }"
					@clickLoadMore="query(true)"
				></uni-load-more>
				<xm-empty v-if="!listpage.list?.length"></xm-empty>
			</scroll-view>
		</view>
		<view class="myexer-bottom"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { exerListpage } from '@/api/exer';
import { Exer } from '@/ts/exer.d';

/************************变量定义相关***********************/
const queryForm = reactive({
	name: '' // 练习名称
});
const listpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 我的练习列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.myexer-main__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
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

// 去练习
async function toExer(exer: Exer) {
	uni.navigateTo({ url: `/pages/myExer/myRead?exerId=${exer.id}` });
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
				.myexer-main__exer-in {
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
			.myexer-main__head {
				display: flex;
				justify-content: center;
			}
			.myexer-main__value {
				color: #333;
				margin-right: 20rpx;
			}
		}
	}
}
</style>
