<template>
	<view class="xmky-layout">
		<slot></slot>
		<xmky-tab-bar :current="curIndex" :tabs="props.tabs"></xmky-tab-bar>
	</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

/************************变量定义相关***********************/
const props = defineProps<{
	tabs: {
		pagePath: string;
		text: string;
		icon: string;
	}[];
}>();

const curIndex = ref(0);

/************************组件生命周期相关*********************/
onMounted(() => {
	updateCurrentIndex();
});

function updateCurrentIndex() {
	const pages = getCurrentPages();
	const currentRoute = pages[pages.length - 1].route; // 如 'pages/exam-user/home/home'
	const fullPath = '/' + currentRoute;
	const idx = props.tabs.findIndex((tab) => tab.pagePath === fullPath);
	curIndex.value = idx >= 0 ? idx : 0;
}
</script>

<style lang="scss" scoped>
.xmky-layout {
	min-height: calc(100vh - 44px);
	background-color: #f3f6f9;
	padding-bottom: 60px; /* 防止内容被 tabBar 遮挡 */
	padding: inherit;
}
</style>
