<template>
	<view class="custom">
		<view class="custom__main-title">
			<text :decode="true">{{ custom.title }}</text>
		</view>
		<view class="custom__sub-title">
			<text :decode="true"></text>
		</view>
		<view class="custom__content">
			<text :decode="true">{{ custom.content }}</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { loginCustom, loginOut } from '@/api/login';

/************************变量定义相关***********************/
const custom = reactive({
	title: '', // 自定义内容
	content: ''
});

/************************组件生命周期相关*********************/
onLoad(async () => {
	customQuery();
});

/************************事件相关*****************************/
// 自定义查询
async function customQuery() {
	let { data } = await loginCustom();
	custom.title = data.title;
	custom.content = data.content;
}
</script>

<style lang="scss" scoped>
.custom {
	height: inherit;
	background-color: #fff;
	padding: 30rpx;
	.custom__main-title {
		font-size: 32rpx;
		color: #333333;
		line-height: 46rpx;
		text-align: center;
	}
	.custom__sub-title {
		font-size: 26rpx;
		color: #8f939c;
		text-align: center;
		line-height: 46rpx;
	}
	.custom__content {
		margin-top: 20rpx;
		font-size: 26rpx;
		color: #8f939c;
		line-height: 40rpx;
		line-height: 46rpx;
	}
}
</style>
