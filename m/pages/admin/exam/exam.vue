<template>
	<xmky-layout :tabs="tabbarStore.admin">
		<view class="exam">
			<view class="exam__head">
				<uni-search-bar
					v-model="queryForm.name"
					bgColor="#fff"
					radius="10"
					placeholder="请输入考试名称"
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
			<view class="exam__main">
				<scroll-view scroll-y="true" class="exam__scroll" :style="{ height: scrollHeight + 'px' }">
					<xmky-card v-for="(exam, index) in listpage.list" :key="index" :preTxt="(index + 1).toString().padStart(2, '0')" :name="exam.name" tag-name="考试">
						<template #content>
							<view>
								<text>组卷方式：</text>
								<text class="exam__value">{{ dictStore.getValue('PAPER_GEN_TYPE', exam.genType) }}</text>
								<text>登录方式：</text>
								<text class="exam__value">{{ dictStore.getValue('LOGIN_TYPE', exam.loginType) }}</text>
							</view>
							<view>
								<text>阅卷方式：</text>
								<text class="exam__value">{{ dictStore.getValue('MARK_STATE', exam.markState) }}</text>
								<text>发布状态：</text>
								<text class="exam__value">{{ dictStore.getValue('STATE_PS', exam.state) }}</text>
							</view>
							<view>
								<text>考试时间：</text>
								<text class="exam__value">{{ exam.startTime.substring(0, 16) }} - {{ exam.endTime.substring(0, 16) }}</text>
							</view>
							<view>
								<text>阅卷时间：</text>
								<text v-if="exam.markType === 2" class="exam__value">{{ exam.markStartTime.substring(0, 16) }} - {{ exam.markEndTime.substring(0, 16) }}</text>
								<text v-else class="exam__value">考试结束自动阅卷</text>
							</view>
							<view>
								<text>试卷：</text>
								<text class="exam__value">{{ dictStore.getValue('PAPER_MARK_TYPE', exam.markType) }}</text>
								<text>及格分数：</text>
								<text class="exam__value">{{ exam.passScore || '-' }}/{{ exam.totalScore }}</text>
							</view>
							<view>
								<text>限时：</text>
								<text class="exam__value">{{ exam.limitMinute ? exam.limitMinute + '分钟' : '无' }}</text>
								<text>考试人数：</text>
								<text class="exam__value">{{ exam.userNum }}人</text>
							</view>
							<view>
								<text>防作弊：</text>
								<text class="exam__value">{{ exam.sxes.length > 0 ? '是' : '否' }}</text>
								<text>查询成绩：</text>
								<text class="exam__value">{{ dictStore.getValue('SCORE_STATE', exam.scoreState) }}</text>
							</view>
							<view>
								<text>排名：</text>
								<text class="exam__value">{{ dictStore.getValue('STATE_ON', exam.rankState) }}</text>
								<text>协助阅卷：</text>
								<text v-if="exam.markUserNum" class="exam__value">{{ exam.markUserNum }}人</text>
								<text v-else class="exam__value">无</text>
							</view>
						</template>
						<template #opt>
							<view class="exam__opt">
								<view>
									<view class="exam__state"></view>
									<view class="exam__state"></view>
								</view>
								<button type="primary" @click="toQuestion(exam.id)" class="exam__btn">进入考试</button>
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
			<view class="exam__foot"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { examListpage } from '@/api/exam';
import { useDictStore } from '@/stores/dict';
import { useTabbarStore } from '@/stores/tabbar';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
const dictStore = useDictStore();
const queryForm = reactive({
	name: '' // 考试名称
});
const listpage = reactive<Page<any>>({
	curPage: 1,
	pageSize: 10,
	total: 0,
	list: [],
	status: 'more'
}); // 考试列表
const scrollHeight = ref(0); // 列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	query(false);
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.exam__scroll')
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

	let { data } = await examListpage({
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
	uni.navigateTo({ url: `/pages/admin/exam/exam-statis?examId=${id}` });
}
</script>
<style lang="scss" scoped>
.exam {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.exam__head {
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
	.exam__main {
		.exam__scroll {
			.exam__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.exam__state {
					display: inline-block;
					margin-right: 40rpx;
					.exam__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.exam__state-name--warn {
						color: #ff5d15;
					}
					.exam__state-name--succ {
						color: #18bc38;
					}
				}
				.exam__btn {
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
			.exam__value {
				color: #333;
				margin-right: 40rpx;
			}
		}
	}
}
</style>
