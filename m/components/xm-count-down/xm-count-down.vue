<template>
	<text :style="`color: ${props.color};`">{{ preTxt }}{{ d > 0 ? `${d}天` : '' }}{{ padNumber(h) }}小时{{ padNumber(m) }}分{{ padNumber(s) }}秒</text>
</template>

<script lang="ts" setup>
import { loginSysTime } from '@/api/login';
import { onMounted, computed, ref, watch } from 'vue';

/************************变量定义相关***********************/
const emit = defineEmits<{
	end: [];
	remind: [];
	change: [Date];
}>();

const props = withDefaults(
	defineProps<{
		expireTime?: string; // 到期时间 yyyy-MM-dd HH:mm:ss
		preTxt?: string; // 前置文本
		remind?: number; // 剩余多久提醒（单位：秒）
		color?: string; // 文字颜色
	}>(),
	{
		preTxt: '',
		color: '#ff5d15'
	}
);

const expireTime = ref<Date | string>(); // 到期时间（苹果手机不识别横线）
const curTime = ref<Date | string>(); // 当前时间
const times = ref(0); // 剩余次数

/************************组件生命周期相关*********************/
onMounted(async () => {
	synTime();
});

/************************监听相关*****************************/
watch(
	() => props.expireTime,
	() => {
		expireTime.value = props.expireTime ? new Date(props.expireTime.replaceAll('-', '/')) : '';
	},
	{ immediate: true }
);

/************************计算属性相关*************************/
const d = computed(() => {
	if (!(expireTime.value instanceof Date) || !(curTime.value instanceof Date)) {
		return 0;
	}

	return Math.floor((expireTime.value.getTime() - curTime.value.getTime()) / 1000 / 60 / 60 / 24);
});
const h = computed(() => {
	if (!(expireTime.value instanceof Date) || !(curTime.value instanceof Date)) {
		return 0;
	}
	return Math.floor((((expireTime.value as Date).getTime() - (curTime.value as Date).getTime()) / 1000 / 60 / 60) % 24);
});
const m = computed(() => {
	if (!(expireTime.value instanceof Date) || !(curTime.value instanceof Date)) {
		return 0;
	}
	return Math.floor((((expireTime.value as Date).getTime() - (curTime.value as Date).getTime()) / 1000 / 60) % 60);
});
const s = computed(() => {
	if (!(expireTime.value instanceof Date) || !(curTime.value instanceof Date)) {
		return 0;
	}
	return Math.floor((((expireTime.value as Date).getTime() - (curTime.value as Date).getTime()) / 1000) % 60);
});

/************************事件相关*****************************/
// 数字前面补零
function padNumber(num: number) {
	if (num < 0) {
		return '00';
	}
	return num.toString().padStart(2, '0');
}

/**
 * 同步服务器时间
 * 每隔30秒同步一次服务器时间；30秒内使用本地浏览器计时；30秒内会有误差，但影响不大
 */
async function synTime() {
	// 如果没有过期时间，继续等待
	if (!(expireTime.value instanceof Date)) {
		setTimeout(synTime, 1000);
		return;
	}

	// 每间隔30秒同步一次服务器时间
	if (times.value <= 0) {
		times.value = 30;
		let { data } = await loginSysTime();
		curTime.value = new Date(data.replaceAll('-', '/'));
		//console.log('服务时间：', data.replaceAll('-', '/'))
	} else {
		curTime.value = new Date((curTime.value as Date).getTime() + 1000);
		times.value--;
		//console.log('本地时间：', curTime.value, !expireTime.value ? '-' : Math.floor(((expireTime.value.getTime() - curTime.value.getTime()) / 1000 ) % 60), s.value)
	}
	emit('change', curTime.value);

	// 如果有提醒，触发提醒事件
	if (props.remind) {
		if (curTime.value.getTime() + props.remind * 1000 >= expireTime.value.getTime()) {
			// console.log('倒计时事件：remind', curTime.value, expireTime.value)
			emit('remind');
		}
	}

	// 如果时间已到，触发事件，让上层处理
	if (curTime.value.getTime() >= expireTime.value.getTime()) {
		// console.log('倒计时事件：end', curTime.value, expireTime.value)
		emit('end');
		return;
	}

	setTimeout(synTime, 1000);
	return;
}
</script>

<style lang="scss" scoped></style>
