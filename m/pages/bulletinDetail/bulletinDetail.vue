<template>
	<view class="container">
		<u-navbar title="公告详情" :placeholder="true" autoBack :border-bottom="false"></u-navbar>
		
		<view class="bulletin-title">{{bulletinDetail.title}}</view>
		<view class="bulletin-time">
			{{bulletinDetail.endTime}}
		</view>
		<div v-html="bulletinDetail.content" class="bulletin-detail"></div>
	</view>
</template>

<script>
	import {
		bulletinListPage
	} from '@/api/common.js'
	export default {
		data() {
			return {
				bulletinDetail: {}
			};
		},
		onLoad(options) {
			const index = Number(options.index)
			this.getBulletinList(index)
		},
		methods: {
			// 获取公告列表
			async getBulletinList(index) {
				const {
					data: {
						list
					}
				} = await bulletinListPage({
					curPage: 1,
					pageSize: 100
				})
				const bulletinList = list.reduce((acc, cur) => {
					if (cur.showType === 2 || cur.showType === 1) {
						acc.push(cur)
					}
					return acc
				}, [])
				this.bulletinDetail = bulletinList[index]
			},
		}
	}
</script>

<style lang="scss" scoped>
.container{
	background: #fff;
	padding: 20rpx;
}

.bulletin-title {
	font-size: 32rpx;
	font-weight: 600;
	padding: 50rpx 0 50rpx;
}

.bulletin-time {
	color: #ddd;
	font-size: 28rpx;
	text-align: right;
	align-self: flex-end;
}

.bulletin-detail {
	width: 710rpx;
	margin: 20rpx auto;
	border-radius: 16rpx;
	padding: 20rpx;
}
</style>
