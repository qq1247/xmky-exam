<template>
	<view class="bulletin-detail">
		<view class="bulletin-detail__main-title">
			<text :decode="true">{{ bulletin.title }}</text>
		</view>
		<view class="bulletin-detail__sub-title">
			<text :decode="true">{{ bulletin.startTime }}</text>
		</view>
		<view class="bulletin-detail__content">
			<text :decode="true">{{ bulletin.content }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { Bulletin } from '@/ts/bulletin.d';
import { bulletinGet } from '@/api/bulletin';

/************************变量定义相关***********************/
const bulletin = reactive<Bulletin>({
	id: null,
	title: '',
	content: '',
	startTime: '',
	endTime: ''
});

/************************组件生命周期相关*********************/
onLoad(async (options) => {
	bulletin.id = options.id;
	await bulletinQuery();
});

/************************事件相关*****************************/
// 公告列表查询
async function bulletinQuery() {
	let { data } = await bulletinGet({ id: bulletin.id });
	bulletin.id = data.id;
	bulletin.title = data.title;
	bulletin.content = data.content;
	bulletin.startTime = data.startTime;
	bulletin.endTime = data.endTime;
}
</script>

<style lang="scss" scoped>
.bulletin-detail {
	height: inherit;
	background-color: #fff;
	padding: 30rpx;
	.bulletin-detail__main-title {
		font-size: 32rpx;
		color: #333333;
		line-height: 46rpx;
		text-align: center;
	}
	.bulletin-detail__sub-title {
		font-size: 26rpx;
		color: #8f939c;
		text-align: center;
		line-height: 46rpx;
	}
	.bulletin-detail__content {
		margin-top: 20rpx;
		font-size: 26rpx;
		color: #8f939c;
		line-height: 40rpx;
		line-height: 46rpx;
	}
}
</style>
