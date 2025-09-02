<template>
	<view class="center">
		<view class="center-head">
			<image class="center-head__bg" src="@/static/img/home-bg.png"></image>
			<view class="center-head__wrap">
				<image class="center-head__avatar" src="@/static/img/user-avatar.png"></image>
				<view class="center-head__inner">
					<view class="center-head__name">{{ user.name }}</view>
					<view class="center-head__orgname">{{ user.orgName }}</view>
				</view>
			</view>
		</view>
		<view class="center-main">
			<scroll-view scroll-y="true" class="center-main__scroll" :style="{ height: taskListHeight + 'px' }">
				<uni-list :border="false" class="center-main__list">
					<uni-list-item
						:show-extra-icon="true"
						showArrow
						:extra-icon="{ customPrefix: 'iconfont', color: '#999', size: '18', type: 'icon-tongzhi' }"
						title="通知公告"
						to="/pages/bulletin/bulletin"
					/>
					<uni-list-item
						:show-extra-icon="true"
						showArrow
						:extra-icon="{ customPrefix: 'iconfont', color: '#999', size: '18', type: 'icon-gerenzhongxin2-03' }"
						title="修改密码"
						to="/pages/center/pwd"
					/>
					<uni-list-item
						:show-extra-icon="true"
						showArrow
						:extra-icon="{ customPrefix: 'iconfont', color: '#999', size: '18', type: 'icon-gerenzhongxin2-03' }"
						title="服务支持"
						to="/pages/center/custom"
					/>
					<uni-list-item
						v-if="custom.title"
						:show-extra-icon="true"
						showArrow
						:extra-icon="{ customPrefix: 'iconfont', color: '#999', size: '18', type: 'icon-gerenzhongxin1' }"
						:title="custom.title"
						to="/pages/center/custom"
					/>
				</uni-list>
			</scroll-view>
		</view>
		<view class="center-foot">
			<button class="center-foot__out" type="primary" @click="out">退出登录</button>
		</view>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { User } from '@/ts/user.d';
import { userGet } from '@/api/user';
import { loginOut } from '@/api/login';

/************************变量定义相关***********************/
const userStore = useUserStore(); // 用户存储
const user = reactive<User>({
	id: null, // 用户信息
	name: '',
	loginName: '',
	orgName: ''
});
const custom = reactive({
	title: '', // 自定义内容
	content: ''
});
const taskListHeight = ref(0); // 下侧列表沾满剩余空间

/************************组件生命周期相关*********************/
onShow(async () => {
	if (!userStore.user.id) {
		uni.navigateTo({ url: '/pages/login/login' });
		return;
	}

	userQuery();
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.center-main__scroll')
		.boundingClientRect((data: any) => {
			taskListHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

/************************事件相关*****************************/
// 用户查询
async function userQuery() {
	let { data } = await userGet();
	user.id = data.id;
	user.name = data.name;
	user.loginName = data.loginName;
	user.orgName = data.orgName;
}

// 退出登录
async function out() {
	await loginOut();
	uni.redirectTo({ url: '/pages/login/login' });
}
</script>
<style lang="scss" scoped>
.center {
	display: flex;
	flex-direction: column;
	height: inherit;
	.center-head {
		display: flex;
		align-items: end;
		height: 320rpx;
		.center-head__wrap {
			display: flex;
			padding: 30rpx;
			.center-head__avatar {
				margin: 0rpx 20rpx;
				height: 130rpx;
				width: 130rpx;
				z-index: 0;
			}
			.center-head__inner {
				display: flex;
				flex-direction: column;
				justify-content: center;
				z-index: 0;
				.center-head__name {
					font-weight: bold;
					font-size: 36rpx;
					line-height: 60rpx;
					color: #ffffff;
				}
				.center-head__orgname {
					font-size: 26rpx;
					line-height: 60rpx;
					color: #ffffff;
				}
			}
		}
		.center-head__bg {
			position: absolute;
			width: 750rpx;
			height: 320rpx;
		}
	}
	.center-main {
		padding: 20rpx;
		:deep(.center-main__list) {
			background-color: initial;
			// #ifdef MP-WEIXIN
			.uni-list {
				background-color: initial; // .center-main__list和.uni-list，在h5是平级，在wx是上下级
			}
			// #endif
			.uni-list-item {
				min-height: 105rpx;
				margin: 10rpx 0rpx;
				border-radius: 16rpx;
				.uni-list-item__content-title {
					font-size: 32rpx;
					color: #222222;
				}
				.uni-list--border {
					display: none;
				}
			}
		}
	}
	.center-foot {
		height: 200rpx;
		.center-foot__out {
			margin-top: 30rpx;
			width: 628rpx;
			height: 100rpx;
			line-height: 100rpx;
			border-radius: 50px;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}
}
</style>
