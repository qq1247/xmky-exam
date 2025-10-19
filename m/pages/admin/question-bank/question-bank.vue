<template>
	<xmky-layout :tabs="tabbarStore.admin">
		<view class="question-bank">
			<view class="question-bank__head">
				<uni-search-bar
					v-model="queryForm.name"
					bgColor="#fff"
					radius="10"
					placeholder="请输入题库名称"
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
			<view class="question-bank__main">
				<scroll-view scroll-y="true" class="question-bank__scroll" :style="{ height: scrollHeight + 'px' }">
					<xmky-card
						v-for="(questionBank, index) in listpage.list"
						:key="index"
						:preTxt="(index + 1).toString().padStart(2, '0')"
						:name="questionBank.name"
						tag-name="题库"
					>
						<template #content>
							<view class="question-bank__row">
								<text>主观</text>
								<text class="question-bank__value">{{ questionBank.objectiveNum }}道</text>
								<text>客观</text>
								<text class="question-bank__value">{{ questionBank.subjectiveNum }}道</text>
							</view>
							<view>
								<text>单选</text>
								<text class="question-bank__value">{{ questionBank.singleNum }}道</text>
								<text>多选</text>
								<text class="question-bank__value">{{ questionBank.multipleNum }}道</text>
								<text>填空</text>
								<text class="question-bank__value">{{ questionBank.fillBlankObjNum + questionBank.fillBlankSubNum }}道</text>
								<text>判断</text>
								<text class="question-bank__value">{{ questionBank.judgeNum }}道</text>
								<text>问答</text>
								<text class="question-bank__value">{{ questionBank.qaObjNum + questionBank.qaSubNum }}道</text>
							</view>
						</template>
						<template #opt>
							<view class="question-bank__opt">
								<view>
									<view class="question-bank__state">{{ questionBank.createUserName }}</view>
									<view class="question-bank__state">{{ questionBank.updateTime }}</view>
								</view>
								<button type="primary" @click="toQuestion(questionBank.id)" class="question-bank__btn">进入题库</button>
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
			<view class="question-bank__foot"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { questionBankListpage } from '@/api/question-bank';
import { useTabbarStore } from '@/stores/tabbar';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
const queryForm = reactive({
	name: '' // 题库名称
});
const listpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 题库列表
const scrollHeight = ref(0); // 列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.question-bank__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 题库列表查询
async function query(append: boolean) {
	listpage.status = 'loading';
	listpage.curPage = append ? listpage.curPage + 1 : 1;

	let { data } = await questionBankListpage({
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

// 去试题页面
async function toQuestion(id: number) {
	uni.navigateTo({ url: `/pages/admin/question-bank/question?questionBankId=${id}` });
}
</script>
<style lang="scss" scoped>
.question-bank {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.question-bank__head {
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
	.question-bank__main {
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
		.question-bank__scroll {
			.question-bank__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.question-bank__state {
					display: inline-block;
					margin-right: 40rpx;
					.question-bank__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.question-bank__state-name--warn {
						color: #ff5d15;
					}
					.question-bank__state-name--succ {
						color: #18bc38;
					}
				}
				.question-bank__btn {
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
			.question-bank__row {
				display: flex;
			}
			.question-bank__value {
				color: #333;
				margin-right: 20rpx;
			}
		}
	}
}
</style>
