<template>
	<view class="my-user">
		<view class="my-user__head"></view>
		<view class="my-user__main">
			<scroll-view scroll-y="true" class="my-user__scroll" :style="{ height: scrollHeight + 'px' }">
				<view v-for="(mark, index) in markList" :key="index" class="list">
					<view class="list__wrap">
						<view class="list__title">{{ mark.examUserName }}</view>
						<view class="list__outer">
							<view class="list__tag list__tag--type">{{ mark.markUserName }}</view>
							<view class="list__tag llist__tag--mark-type">
								{{ mark.myExamState === 1 ? dictStore.getValue('EXAM_STATE', mark.myExamState) : dictStore.getValue('MARK_STATE', mark.myExamMarkState) }}
							</view>
						</view>
					</view>
					<view class="list__nav" @click="toPaper(pageParm.examId, mark.examUserId)">
						<uni-icons customPrefix="iconfont" type="icon-you" color="#8F939C" size="28rpx"></uni-icons>
					</view>
				</view>
				<xmky-empty v-if="!markList?.length"></xmky-empty>
			</scroll-view>
		</view>
		<view class="my-user__foot"></view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad, onReady, onShow } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { myMarkMarkList } from '@/api/my-mark';
import { useDictStore } from '@/stores/dict';

/************************变量定义相关***********************/
const dictStore = useDictStore();
const pageParm = reactive({
	examId: null
});
const markList = ref<any[]>([]); // 阅卷列表
const scrollHeight = ref(0); // 列表沾满剩余空间
/************************组件生命周期相关*********************/
onLoad(async (option) => {
	pageParm.examId = option.examId;
});
onShow(async () => {
	markListQuery();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.my-user__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top;
		})
		.exec();
});

/************************事件相关*****************************/
// 阅卷列表查询
async function markListQuery() {
	const { data } = await myMarkMarkList({ examId: pageParm.examId });
	markList.value = data;
}

// 试题预览
function toPaper(examId: number, userId: number) {
	uni.navigateTo({ url: `/pages/mark-user/my-mark/my-paper?examId=${examId}&userId=${userId}` });
}
</script>
<style lang="scss" scoped>
.my-user {
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.my-user__head {
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
	.my-user__main {
		flex: 1;
		margin-top: 10rpx;
		overflow: hidden;
		border-radius: 30rpx 30rpx 30rpx 30rpx;
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
	.my-user__foot {
	}
}
</style>
