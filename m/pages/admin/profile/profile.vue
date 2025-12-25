<template>
	<xmky-layout :tabs="tabbarStore.admin">
		<view class="profile">
			<view class="profile__head">
				<image class="profile__bg" src="@/static/img/home-bg.png"></image>
				<view class="avatar">
					<!-- <image class="avatar__wrap" src="@/static/img/user-avatar.png"></image> -->
					<uni-file-picker
						v-model="avatarUrl"
						limit="1"
						:del-icon="false"
						disable-preview
						:auto-upload="false"
						:imageStyles="{
							height: 72,
							width: 72,
							border: {
								color: '#fff',
								width: 2,
								style: 'solid',
								radius: '50%'
							}
						}"
						file-mediatype="image"
						@select="upload"
					>
						<uni-icons v-if="!avatarUrl.length" custom-prefix="iconfont" type="icon-rentouxiang" color="#77a2db" size="88rpx"></uni-icons>
					</uni-file-picker>
					<view class="avatar__inner">
						<view class="avatar__name">{{ userStore.name }}</view>
						<view class="avatar__orgname">{{ userStore.role === 'ADMIN' ? '管理员' : userStore.role === 'SUB_ADMIN' ? '子管理员' : '未知' }}</view>
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
							to="/pages/admin/profile/pwd"
						/>
						<uni-list-item
							:show-extra-icon="true"
							showArrow
							:extra-icon="{ customPrefix: 'iconfont', color: '#999', size: '18', type: 'icon-gerenzhongxin2-03' }"
							title="服务支持"
							to="/pages/admin/profile/custom"
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
import { loginAvatar, loginOut } from '@/api/login';
import { useTabbarStore } from '@/stores/tabbar';
import { loginSysTime } from '@/api/login';

/************************变量定义相关***********************/
const tabbarStore = useTabbarStore();
const userStore = useUserStore(); // 用户存储
const scrollHeight = ref(0); // 下侧列表沾满剩余空间
const avatarUrl = ref([]);
const host = ref(uni.getStorageSync('BASE_URL'));

/************************组件生命周期相关*********************/
onShow(async () => {
	if (!userStore.id) {
		uni.navigateTo({ url: '/pages/login/login' });
		return;
	}

	if (userStore.avatarFileId) {
		avatarUrl.value[0] = {
			url: `${host.value}/file/download?id=${userStore.avatarFileId}`
		};
	}
});

onReady(() => {
	uni.createSelectorQuery()
		.select('.profile__scroll')
		.boundingClientRect((data: any) => {
			scrollHeight.value = uni.getWindowInfo().windowHeight - data.top - 50;
		})
		.exec();
});

// 上传头像
async function upload(e) {
	const file = e.tempFiles[0];
	if (!file) return;

	await loginSysTime(); // bug修复，上传时访问令牌正好过期，调用不了刷新令牌
	uni.uploadFile({
		url: `${host.value}/file/upload`,
		filePath: file.path,
		name: 'files',
		header: {
			Authorization: userStore.accessToken
		},
		success: async (uploadFileRes) => {
			const jsonObj = JSON.parse(uploadFileRes.data);
			if (jsonObj.code !== 200) {
				uni.showToast({ title: jsonObj.msg, icon: 'error' });
				return;
			}

			const { code } = await loginAvatar({ avatarFileId: jsonObj.data.fileIds });
			if (code !== 200) {
				return;
			}

			userStore.avatarFileId = jsonObj.data.fileIds;
		}
	});
}

/************************事件相关*****************************/
// 退出登录
async function out() {
	await loginOut({});
	userStore.reset();
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
			:deep(.file-picker__box-content) {
				background-color: #c5dbff;
			}
			.avatar__wrap {
				margin: 0rpx 20rpx;
				height: 130rpx;
				width: 130rpx;
				z-index: 0;
			}
			.avatar__inner {
				display: flex;
				flex-direction: column;
				justify-content: profile;
				z-index: 0;
				margin-left: 20rpx;
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
