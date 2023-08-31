# cc-noticeBar


#### 使用方法 
```使用方法
<!-- 默认颜色#333公告栏 -->
<view class="header">默认颜色公告栏</view>
<!-- noticeList：通知数组  @click:公告栏条目点击事件-->
<cc-noticeBar :noticeList="noticeList" @click="itemClick"></cc-noticeBar>

<!-- 橄榄色公告栏 -->
<view class="header">橄榄色公告栏</view>
<!-- noticeList：通知数组 colors：设置文字颜色  @click:公告栏条目点击事件 -->
<cc-noticeBar :noticeList="noticeList" colors="olive" @click="itemClick"></cc-noticeBar>	

```

#### HTML代码实现部分
```html
<template>
	<view class="content">


		<!-- 默认颜色#333公告栏 -->
		<view class="header">默认颜色公告栏</view>
		<!-- noticeList：通知数组  @click:公告栏条目点击事件-->
		<cc-noticeBar :noticeList="noticeList" @click="itemClick"></cc-noticeBar>

		<!-- 橄榄色公告栏 -->
		<view class="header">橄榄色公告栏</view>
		<!-- noticeList：通知数组 colors：设置文字颜色  @click:公告栏条目点击事件 -->
		<cc-noticeBar :noticeList="noticeList" colors="olive" @click="itemClick"></cc-noticeBar>

		<!-- 橙色公告栏 -->
		<view class="header">橙色背景公告栏</view>
		<!-- noticeList：通知数组 colors：设置文字颜色  @click:公告栏条目点击事件 -->
		<cc-noticeBar :noticeList="noticeList" colors="orange" @click="itemClick"></cc-noticeBar>

		<!-- 粉色公告栏 -->
		<view class="header">粉色背景公告栏</view>
		<!-- noticeList：通知数组 colors：设置文字颜色  @click:公告栏条目点击事件 -->
		<cc-noticeBar :noticeList="noticeList" colors="#e03997" @click="itemClick"></cc-noticeBar>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				colors: '#fa436a',
				noticeList: [{
						id: 1,
						title: '征程这些伟大精神 串连起中国共产党人的精神谱系'
					},
					{
						id: 2,
						title: '增强水运发展新动能 前5月港口货物吞吐量增长7.9%'
					},
					{
						id: 3,
						title: '多地持续高温 各地采取措施积极应对'
					},
					{
						id: 4,
						title: '中非经贸博览会见证中非合作深度'
					},
					{
						id: 5,
						title: '国安家安得民心 保驾护航促治兴'
					}
				],



			}
		},

		methods: {

			itemClick: function(item) {

				console.log("点击公告栏条目item = " + JSON.stringify(item));
				uni.showModal({
					title: '点击公告栏条目',
					content: "点击公告栏条目item = " + JSON.stringify(item)
				})
			},

		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;

	}

	.header {

		margin-left: 3%;
		width: 94%;
		line-height: 30px;
		font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
		font-weight: 550;
		height: 30px;
		font-size: 14px;
		margin-top: 10px;

	}
</style>



```