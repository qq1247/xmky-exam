<template>
	<xmky-layout :tabs="tabbarStore.markUser">
		<view class="my-mark">
			<view class="my-mark__head">
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
			<view class="my-mark__main">
				<scroll-view scroll-y="true" class="my-mark__scroll" :style="{ height: scrollHeight + 'px' }">
					<xmky-card v-for="(myMark, index) in listpage.list" :key="index" :preTxt="(index + 1).toString().padStart(2, '0')" :name="myMark.examName" tag-name="考试">
						<template #content>
							<view>
								<text>组卷方式：</text>
								<text class="my-mark__value">{{ dictStore.getValue('PAPER_GEN_TYPE', myMark.examGenType) }}</text>
								<text>登录方式：</text>
								<text class="my-mark__value">{{ dictStore.getValue('LOGIN_TYPE', myMark.examLoginType) }}</text>
							</view>
							<view>
								<text>阅卷方式：</text>
								<text class="my-mark__value">{{ dictStore.getValue('MARK_STATE', myMark.examMarkState) }}</text>
								<text>发布状态：</text>
								<text class="my-mark__value">{{ dictStore.getValue('STATE_PS', myMark.examState) }}</text>
							</view>
							<view>
								<text>考试时间：</text>
								<text class="my-mark__value">{{ myMark.examStartTime.substring(0, 16) }} - {{ myMark.examEndTime.substring(0, 16) }}</text>
							</view>
							<view>
								<text>阅卷时间：</text>
								<text v-if="myMark.examMarkType === 2" class="my-mark__value">
									{{ myMark.examMarkStartTime.substring(0, 16) }} - {{ myMark.examMarkEndTime.substring(0, 16) }}
								</text>
								<text v-else class="my-mark__value">考试结束自动阅卷</text>
							</view>
							<view>
								<text>试卷：</text>
								<text class="my-mark__value">{{ dictStore.getValue('PAPER_MARK_TYPE', myMark.examMarkType) }}</text>
								<text>及格分数：</text>
								<text class="my-mark__value">{{ myMark.examPassScore || '-' }}/{{ myMark.examTotalScore }}</text>
							</view>
							<view>
								<text>限时：</text>
								<text class="my-mark__value">{{ myMark.examLimitMinute ? myMark.examLimitMinute + '分钟' : '无' }}</text>
								<text>考试人数：</text>
								<text class="my-mark__value">{{ myMark.examUserNum }}人</text>
							</view>
							<view>
								<text>防作弊：</text>
								<text class="my-mark__value">{{ myMark.examSxes.length > 0 ? '是' : '否' }}</text>
								<text>查询成绩：</text>
								<text class="my-mark__value">{{ dictStore.getValue('SCORE_STATE', myMark.examScoreState) }}</text>
							</view>
							<view>
								<text>排名：</text>
								<text class="my-mark__value">{{ dictStore.getValue('STATE_ON', myMark.examRankState) }}</text>
								<text>协助阅卷：</text>
								<text v-if="myMark.examMarkUserNum" class="my-mark__value">{{ myMark.examMarkUserNum }}人</text>
								<text v-else class="my-mark__value">无</text>
							</view>
						</template>
						<template #opt>
							<view class="my-mark__opt">
								<view>
									<view class="my-mark__state"></view>
									<view class="my-mark__state"></view>
								</view>
								<button type="primary" @click="toMark(myMark.examId)" class="my-mark__btn">进入阅卷</button>
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
			<view class="my-mark__foot"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { Page } from '@/ts/page.d';
import { myMarkListpage } from '@/api/my-mark';
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
		.select('.my-mark__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 考试列表查询
async function query(append: boolean) {
	listpage.status = 'loading';
	listpage.curPage = append ? listpage.curPage + 1 : 1;

	let { data } = await myMarkListpage({
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

// 去阅卷页面
async function toMark(id: number) {
	uni.navigateTo({ url: `/pages/mark-user/my-mark/my-read?examId=${id}` });
}
</script>
<style lang="scss" scoped>
.my-mark {
	display: flex;
	flex-direction: column;
	padding: 20rpx;
	.my-mark__head {
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
	.my-mark__main {
		.my-mark__scroll {
			.my-mark__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.my-mark__state {
					display: inline-block;
					margin-right: 40rpx;
					.my-mark__state-name {
						margin-left: 4rpx;
						font-size: 26rpx;
						color: #8f939c;
					}
					.my-mark__state-name--warn {
						color: #ff5d15;
					}
					.my-mark__state-name--succ {
						color: #18bc38;
					}
				}
				.my-mark__btn {
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
			.my-mark__value {
				color: #333;
				margin-right: 40rpx;
			}
		}
	}
}
</style>
