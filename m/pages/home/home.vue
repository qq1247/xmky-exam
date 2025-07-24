<template>
	<view class="home">
		<view class="home-head">
			<image class="home-head__bg" src="@/static/img/home-bg.png"></image>
			<text class="home-head__sysname">{{ userStore.user.sysName }}</text>
			<text class="home-head__goodluck">祝考试之旅一帆风顺，成就满满！</text>
		</view>
		<view class="home-main">
			<view class="home-main__nav-wrap">
				<view class="home-main__nav">
					<swiper class="home-main__nav-inner">
						<swiper-item>
							<uni-grid :column="navBtnList.length" :show-border="false" :square="false" @change="quickNav">
								<uni-grid-item v-for="(navBtn, index) in navBtnList" :key="index" :index="index">
									<view class="home-main__nav-btn">
										<uni-icons custom-prefix="iconfont" :type="navBtn.icon" color="white" size="50rpx"></uni-icons>
									</view>
									<text class="home-main__nav-name">{{ navBtn.name }}</text>
								</uni-grid-item>
							</uni-grid>
						</swiper-item>
					</swiper>
				</view>
			</view>
			<view class="home-main__bulletin">
				<xzw-notice :list="bulletinList" theme="warning" @goItem="toBulletin" @goMore="toBulletin" />
			</view>
		</view>
		<view class="home-foot">
			<uv-tabs
				:list="[
					{ name: '我的考试', badge: { value: todoExamList?.length } },
					{ name: '我的练习', badge: { value: myExerList?.length } }
				]"
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
			<scroll-view scroll-y="true" class="home-foot__scroll" :style="{ height: taskListHeight + 'px' }">
				<xm-card
					v-if="curTabIndex === 0 && todoExamList?.length"
					v-for="(todoExam, index) in todoExamList"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="todoExam.examName"
					tag-name="考试"
				>
					<template #content>
						<view>
							<text>{{ todoExam.examStartTime }} 至 {{ todoExam.examEndTime }}</text>
						</view>
						<view>
							<text>分数：</text>
							<text class="home-foot__value">{{ todoExam.examPassScore }} / {{ todoExam.examTotalScore }}</text>
							<text>限时：</text>
							<text class="home-foot__value">{{ todoExam.examLimitMinute === 0 ? '无' : todoExam.examLimitMinute + '分钟' }}</text>
						</view>
					</template>
					<template #opt>
						<view class="home-foot__opt">
							<view>
								<text v-if="todoExam.state === 1">距离考试开始：</text>
								<xm-count-down v-if="todoExam.state === 1" :expireTime="todoExam.examStartTime"></xm-count-down>
								<text v-if="todoExam.state === 2">距离考试结束：</text>
								<xm-count-down v-if="todoExam.state === 2" :expireTime="todoExam.answerEndTime"></xm-count-down>
							</view>
							<button type="primary" @click="toExam(todoExam)" class="home-foot__exam-in">进入考试</button>
						</view>
					</template>
				</xm-card>
				<xm-empty v-if="curTabIndex === 0 && !todoExamList?.length"></xm-empty>
				<xm-card
					v-if="curTabIndex === 1 && myExerList.length"
					v-for="(exer, index) in myExerList"
					:key="index"
					:preTxt="(index + 1).toString().padStart(2, '0')"
					:name="exer.name"
					tag-name="练习"
				>
					<template #content>
						<view></view>
					</template>
					<template #opt>
						<view class="home-foot__opt">
							<view></view>
							<button type="primary" @click="toExer(exer)" class="home-foot__exam-in">进入练习</button>
						</view>
					</template>
				</xm-card>
				<xm-empty v-if="curTabIndex === 1 && !myExerList?.length"></xm-empty>
			</scroll-view>
		</view>
	</view>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { myExamListpage } from '@/api/myExam';
import { bulletinListpage } from '@/api/bulletin';
import { exerListpage } from '@/api/exer';

/************************变量定义相关***********************/
const userStore = useUserStore(); // 用户存储
const todoExamList = ref<any[]>(); // 未完成考试列表
const myExerList = ref<any[]>(); // 我的练习列表
const bulletinList = ref<any[]>(); // 公告列表
const curTabIndex = ref(0); // 当前选择标签页
const navBtnList = ref([
	{ name: '考试', icon: 'icon-icon-pen' },
	{ name: '练习', icon: 'icon-icon-pencil' },
	/* { name: '错题', icon: 'icon-icon-top_01' },
	{ name: '收藏', icon: 'icon-icon-top-05' }, */
	{ name: '个人中心', icon: 'icon-icon-people' }
]); // 导航按钮列表
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	if (!userStore.user.id) {
		uni.navigateTo({ url: '/pages/login/login' });
		return;
	}

	myExamQuery();
	myExerQuery();
	bulletinQuery();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.home-foot__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 我的考试查询
async function myExamQuery() {
	let { data } = await myExamListpage({ todo: true, pageSize: 20 });
	todoExamList.value = data.list;
}

// 我的练习查询
async function myExerQuery() {
	let { data } = await exerListpage({ state: 1, todo: true, pageSize: 20 });
	myExerList.value = data.list;
}

// 公告查询
async function bulletinQuery() {
	let { data } = await bulletinListpage({ pageSize: 20 });
	bulletinList.value = data.list;
	if (!bulletinList.value.length) {
		bulletinList.value.push({
			id: 0,
			title: '暂无公告',
			content: '暂无公告',
			startTime: null,
			endTime: null
		});
	}
}

// 公告查询
function toBulletin(bulletin: any) {
	if (bulletin && bulletin.id) {
		uni.navigateTo({ url: `/pages/bulletin/bulletinDetail?id=${bulletin.id}` });
	} else {
		uni.navigateTo({ url: `/pages/bulletin/bulletin` });
	}
}

// 去考试
async function toExam(todoExam: any) {
	uni.navigateTo({ url: `/pages/myExam/myRead?examId=${todoExam.examId}` });
}

// 去练习
async function toExer(myExer: any) {
	uni.navigateTo({ url: `/pages/myExer/myRead?exerId=${myExer.id}` });
}

// 快速导航
function quickNav({ detail: { index } }) {
	if (index === 0) {
		uni.switchTab({
			url: '/pages/myExam/myExam'
		});
		return;
	}
	if (index === 1) {
		uni.switchTab({
			url: '/pages/myExer/myExer'
		});
		return;
	}
	if (index === 4) {
		uni.switchTab({
			url: '/pages/center/center'
		});
		return;
	}

	uni.showToast({ title: '暂未开放', icon: 'error' });
}
</script>
<style lang="scss" scoped>
.home {
	display: flex;
	flex-direction: column;
	height: inherit;
	.home-head {
		display: flex;
		flex-direction: column;
		align-items: center;
		height: 320rpx;
		.home-head__bg {
			position: absolute;
			width: 750rpx;
			height: 320rpx;
		}
		.home-head__sysname {
			z-index: 0;
			font-size: 48rpx;
			font-weight: bold;
			color: #ffffff;
			margin-top: 50rpx;
		}
		.home-head__goodluck {
			z-index: 0;
			font-size: 30rpx;
			color: #ffffff;
			margin-top: 30rpx;
		}
	}
	.home-main {
		display: flex;
		flex-direction: column;
		padding: 0rpx 20rpx;
		.home-main__nav-wrap {
			height: 150rpx;
			position: relative;
			.home-main__nav {
				display: flex;
				position: absolute;
				height: 220rpx;
				width: 710rpx;
				left: 50%;
				transform: translateX(-50%);
				top: -90rpx;
				background: linear-gradient(to bottom, #dbf9ff 0%, #ffffff 100%);
				box-shadow: 0rpx 10rpx 20rpx 0rpx #dee1e4;
				border-radius: 16rpx;
				.home-main__nav-inner {
					height: 160rpx;
					flex: 1;
					margin: 40rpx 5rpx 0rpx 5rpx;
				}
				// #ifdef MP-WEIXIN
				:deep(.uni-grid) {
					uni-grid-item {
						// 比h5多一层元素：uni-grid-item
						&:nth-of-type(1) .home-main__nav-btn {
							background: linear-gradient(to bottom, #00a0e9 0%, #5cd4ff 100%);
						}
						&:nth-of-type(2) .home-main__nav-btn {
							background: linear-gradient(to bottom, #05b77f 0%, #22d29a 100%);
						}
						&:nth-of-type(3) .home-main__nav-btn {
							background: linear-gradient(to bottom, #ec8c4c 0%, #f4ae5c 100%);
						}
						&:nth-of-type(4) .home-main__nav-btn {
							background: linear-gradient(to bottom, #5d54f2 0%, #7594ee 100%);
						}
						&:nth-of-type(5) .home-main__nav-btn {
							background: linear-gradient(to bottom, #f445a0 0%, #f99c9c 100%);
						}
					}
				}
				// #endif
				:deep(.uni-grid-item) {
					&:nth-of-type(1) .home-main__nav-btn {
						background: linear-gradient(to bottom, #00a0e9 0%, #5cd4ff 100%);
					}
					&:nth-of-type(2) .home-main__nav-btn {
						background: linear-gradient(to bottom, #05b77f 0%, #22d29a 100%);
					}
					&:nth-of-type(3) .home-main__nav-btn {
						background: linear-gradient(to bottom, #ec8c4c 0%, #f4ae5c 100%);
					}
					&:nth-of-type(4) .home-main__nav-btn {
						background: linear-gradient(to bottom, #5d54f2 0%, #7594ee 100%);
					}
					&:nth-of-type(5) .home-main__nav-btn {
						background: linear-gradient(to bottom, #f445a0 0%, #f99c9c 100%);
					}
					.uni-grid-item__box {
						align-items: center;
						.home-main__nav-btn {
							display: flex;
							justify-content: center;
							align-items: center;
							width: 100rpx;
							height: 100rpx;
							border-radius: 50%;
							margin-bottom: 20rpx;
						}
						.home-main__nav-name {
							font-size: 26rpx;
							color: #8f939c;
							font-weight: bold;
						}
					}
				}
			}
		}
		.home-main__bulletin {
			:deep(.xzw_notice) {
				height: 88rpx;
				.swiperIn {
					font-weight: initial;
				}
			}
		}
	}
	.home-foot {
		flex: 1;
		display: flex;
		flex-direction: column;
		padding: 0rpx 30rpx;
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

		.home-foot__scroll {
			.home-foot__opt {
				flex: 1;
				display: flex;
				justify-content: space-between;
				align-items: center;
				.home-foot__exam-in {
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
			.home-foot__value {
				color: #333;
				margin-right: 30rpx;
			}
		}
	}
}
</style>
