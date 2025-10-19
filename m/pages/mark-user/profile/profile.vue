<template>
	<xmky-layout :tabs="tabbarStore.markUser">
		<view class="profile">
			<view class="profile__head">
				<image class="profile__bg" src="@/static/img/home-bg.png"></image>
				<view class="avatar">
					<image class="avatar__outer" src="@/static/img/user-avatar.png"></image>
					<view class="avatar__inner">
						<view class="avatar__name">{{ user.name }}</view>
						<view class="avatar__orgname">{{ user.orgName }}</view>
					</view>
				</view>
			</view>
			<view class="profile__main">
				<scroll-view scroll-y="true" class="profile__scroll" :style="{ height: scrollHeight + 'px' }">
					<uni-list :border="false" class="list">
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
							to="/pages/mark-user/profile/pwd"
						/>
						<uni-list-item
							:show-extra-icon="true"
							showArrow
							:extra-icon="{ customPrefix: 'iconfont', color: '#999', size: '18', type: 'icon-gerenzhongxin2-03' }"
							title="服务支持"
							to="/pages/mark-user/profile/custom"
						/>
					</uni-list>
					<button class="profile__btn" type="primary" @click="out">退出登录</button>
				</scroll-view>
			</view>
			<view class="profile__foot"></view>
		</view>
	</xmky-layout>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onShow, onReady } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { User } from '@/ts/user.d';
import { userGet } from '@/api/user';
import { loginOut } from '@/api/login';
import { useTabbarStore } from '@/stores/tabbar';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
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
const scrollHeight = ref(0); // 下侧列表沾满剩余空间

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
		.select('.profile__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
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
.profile {
	display: flex;
	flex-direction: column;
	height: inherit;
	.profile__head {
		display: flex;
		align-items: end;
		height: 320rpx;
		.avatar {
			display: flex;
			padding: 30rpx;
			.avatar__outer {
				margin: 0rpx 20rpx;
				height: 130rpx;
				width: 130rpx;
				z-index: 0;
				.avatar__inner {
					display: flex;
					flex-direction: column;
					justify-content: profile;
					z-index: 0;
					.avatar__name {
						font-weight: bold;
						font-size: 36rpx;
						line-height: 60rpx;
						color: #ffffff;
					}
					.avatar__orgname {
						font-size: 26rpx;
						line-height: 60rpx;
						color: #ffffff;
					}
				}
			}
		}
		.profile__bg {
			position: absolute;
			width: 750rpx;
			height: 320rpx;
		}
	}
	.profile__main {
		padding: 20rpx;
		:deep(.list) {
			background-color: initial;
			// #ifdef MP-WEIXIN
			.uni-list {
				background-color: initial; // .profile__main__list和.uni-list，在h5是平级，在wx是上下级
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
		.profile__btn {
			margin-top: 30rpx;
			width: 100%;
			height: 100rpx;
			line-height: 100rpx;
			border-radius: 50px;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}
	.profile__foot {
	}
}
</style>
