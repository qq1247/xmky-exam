<template>
	<kevy-result-page 
		v-if="type" 
		:type="type" 
		:title="title" 
		:description="description" 
		:details="details"
		primaryBtnText="返回首页" 
		secondaryBtnText="返回模拟" 
		primarColor="#2979ff"
		@primaryBtnClick="toHome"
		@secondaryBtnClick="toBack"
		></kevy-result-page>
</template>

<script lang="ts" setup>
	import { ref } from 'vue'
	import { onLoad } from "@dcloudio/uni-app"

	// 定义变量
	const type = ref('success')
	const title = ref('模拟结束')
	const description = ref('以下是对本次模拟的评估')
	const details = ref([])
	
	// 页面加载完毕，执行如下方法
	onLoad(async (option) => {
		details.value.push({
			label: '练习试题',
			value: option.total + '道',
			bold: true,
		})
		details.value.push({
			label: '答题时长',
			value: option.diffMinute + '分钟',
			bold: true,
		})
		details.value.push({
			label: '客观题答对（不含填空问答）',
			value: option.trueNum + '道',
			bold: true,
		})
		details.value.push({
			label: '客观题答错（不含填空问答）',
			value: option.falseNum + '道',
			bold: true,
		})
	})
	
	// 返回首页
	function toHome() {
		uni.switchTab({
			url: '/'
		})
	}
	
	// 返回上一页
	function toBack() {
		uni.navigateBack()
	}
</script>

<style lang="scss" scoped>
</style>