<template>
	<view class="xmky-comment">
		<uv-avatar :src="`${host}/file/download?id=${userAvatarFileId}`"></uv-avatar>
		<view class="xmky-comment__outer">
			<view class="xmky-comment__inner">
				<view class="xmky-comment__name">{{ props.userName }}</view>
				<view class="xmky-comment__like" @click="emit('like')">
					<uni-icons v-if="isLike" custom-prefix="iconfont" type="icon-lianxi-68" color="#04C7F2" size="38rpx"></uni-icons>
					<uni-icons v-else custom-prefix="iconfont" type="icon-lianxi-67" color="#303133" size="38rpx"></uni-icons>
					<text class="xmky-comment__like-txt">{{ props.likeNum }}</text>
				</view>
			</view>
			<view class="xmky-comment__comment">
				<text v-if="props.replyUserName">
					回复@
					<text class="xmky-comment__name">{{ props.replyUserName }}：</text>
				</text>
				<text>{{ props.content }}</text>
			</view>
			<view class="xmky-comment__opt">
				<view class="xmky-comment__time">{{ props.updateTime }}</view>
				<view class="xmky-comment__reply" @click="popup.open()">
					<!-- <uni-icons custom-prefix="iconfont" type="icon-lianxi-69" color="#303133" size="32rpx"></uni-icons> -->
					<text class="xmky-comment__reply-txt">回复</text>
				</view>
			</view>
			<slot></slot>
		</view>
		<xmky-popup ref="popup" name="分享思路" class="popup">
			<view class="popup__txt">
				@回复
				<text class="popup__name">{{ props.userName }}</text>
			</view>
			<uni-easyinput
				type="textarea"
				v-model="replyContent"
				:maxlength="128"
				autoHeight
				placeholder="我是这样解题的..."
				:styles="{ borderColor: '#04C7F2' }"
				primaryColor="#04C7F2"
				class="popup__input"
			></uni-easyinput>
			<button type="primary" class="popup__btn" @click="send">发送</button>
		</xmky-popup>
	</view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

/************************变量定义相关***********************/
const emit = defineEmits<{
	// 定义事件
	(e: 'like'): void;
	(e: 'reply', content: string): void;
}>();
const props = defineProps({
	userName: { type: [String], required: false, default: '' }, // 评论用户姓名
	userAvatarFileId: { type: [Number, null], required: false, default: null }, // 评论用户头像附件ID
	replyUserName: { type: [String, null], required: false, default: '' }, // 回复用户姓名
	replyUserAvatarFileId: { type: [Number, null], required: false, default: null }, // 回复用户头像附件ID
	content: { type: [String], required: false, default: '' }, // 评论
	updateTime: { type: [String], required: false, default: '' }, // 更新时间
	likeNum: { type: [Number, null], required: false, default: 0 }, // 点赞数量
	isLike: { type: [Boolean], required: false, default: false } // 点赞数量
});

const reply = ref(false);
const replyContent = ref('');
const popup = ref();
const host = ref(uni.getStorageSync('BASE_URL'));

function send() {
	if (!replyContent.value) {
		return;
	}
	emit('reply', replyContent.value);
	replyContent.value = '';
	popup.value.close();
}
</script>

<style lang="scss" scoped>
.xmky-comment {
	display: flex;
	margin-top: 20rpx;
	margin-right: 20rpx;
	padding-bottom: 10rpx;
	border-bottom: 1px solid #e5e5e5;
	&:last-child {
		border-bottom: initial;
	}
	.xmky-comment__outer {
		flex: 1;
		margin-left: 10rpx;
		// margin-right: 20rpx;
		.xmky-comment__inner {
			display: flex;
			justify-content: space-between;
			align-items: center;
			.xmky-comment__like {
				width: 100rpx;
				.xmky-comment__like-txt {
					margin-left: 5rpx;
					font-size: 30rpx;
					color: #8f939c;
				}
			}
		}
		.xmky-comment__name {
			font-size: 30rpx;
			color: #8f939c;
			// line-height: 22rpx;
		}
		.xmky-comment__comment {
			font-size: 30rpx;
			color: #303133;
			line-height: 48rpx;
		}
		.xmky-comment__opt {
			display: flex;
			align-items: center;
			margin-top: 5rpx;
			.xmky-comment__time {
				font-size: 30rpx;
				color: #8f939c;
			}
			.xmky-comment__reply {
				margin-left: 20rpx;
				.xmky-comment__reply-txt {
					margin-left: 5rpx;
					font-size: 30rpx;
					color: #04c7f2;
				}
			}
		}
	}
	.popup {
		display: flex;
		.popup__txt {
			margin-bottom: 20rpx;
			font-size: 30rpx;
			color: #303133;
		}
		.popup__name {
			color: #8f939c;
		}
		.popup__input {
		}
		.popup__btn {
			margin-top: 20rpx;
			// width: 628rpx;
			height: 100rpx;
			line-height: 100rpx;
			border-radius: 50px;
			background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
		}
	}
}
</style>
