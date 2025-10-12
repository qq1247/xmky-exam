<template>
	<view class="exer-statis-chart">
		<qiun-data-charts
			:style="{ width: width + 'px', height: heigth + 'px' }"
			canvas-id="chart-1"
			type="line"
			:opts="exerTimeStatisOpts"
			:chartData="exerTimeStatisData.data"
			:ontouch="true"
			:canvas2d="true"
			class="chart"
		/>
	</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { onLoad, onReady } from '@dcloudio/uni-app';

/************************变量定义相关***********************/

const exerTimeStatisData = reactive({
	data: {}
});
const exerTimeStatisOpts = reactive({
	color: ['#4692D8', '#06BCE3', '#978CEC', '#DD8CEC', '#85e3a4'],
	padding: [15, 10, 0, 15],
	enableScroll: true,
	legend: { show: false },
	xAxis: {
		disableGrid: true,
		scrollShow: true,
		itemCount: 13
	},
	yAxis: {
		gridType: 'dash',
		dashLength: 2
	},
	extra: {
		line: {
			type: 'straight',
			width: 2,
			activeType: 'hollow'
		}
	},
	rotate: true
});
const width = ref(0);
const heigth = ref(0);

/************************组件生命周期相关*********************/
onReady(() => {
	uni.getSystemInfo({
		success(res) {
			width.value = res.windowWidth;
			heigth.value = res.windowHeight;
		} // 微信小程序需要指定高宽
	});
});
onLoad(async (option) => {
	let tracks = JSON.parse(decodeURIComponent(option.tracks));
	exerTimeStatisData.data = {
		categories: tracks.map((item) => item.ym),
		series: [
			{
				name: '练习时长（分钟）',
				data: tracks.map((item) => item.minuteCount)
			}
		]
	};
});

/************************事件相关*****************************/
</script>
<style lang="scss" scoped>
.exer-statis-chart {
	display: flex;
	flex-direction: column;
	// #ifdef H5
	height: calc(100vh - 44px);
	// #endif
	// #ifdef MP-WEIXIN
	height: calc(100vh - 0px);
	// #endif
	background-color: #fff;
}
</style>
