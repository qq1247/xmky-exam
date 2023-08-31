<template>
	<uni-card :is-full="true">
		<uni-title type="h1" align="center" :title="bulletin.title"></uni-title>
		<uni-title class="h5" type="h5" color="#666" :title="bulletin.content"></uni-title>
	</uni-card>
</template>

<script lang="ts" setup>
	import { onLoad } from "@dcloudio/uni-app"
	import http from '@/utils/request'
	import { reactive } from "vue";
	import { Bulletin } from "../../ts";

	// 定义变量
	const bulletin = reactive({} as Bulletin)

	// 页面加载完毕，执行如下方法
	onLoad(async (option) => {
		let id = option.id
		let { data } = await http.post("bulletin/get", { id })
		bulletin.title = data.title
		bulletin.content = data.content
	})
</script>

<style lang="scss" scoped>
</style>