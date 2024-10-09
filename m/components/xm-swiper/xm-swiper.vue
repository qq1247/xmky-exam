<template>
	<swiper :current="curSwiperIndex" :circular="true" @change="(e: any) => e.detail.source === 'touch' && synIndex(e.detail.current)">
		<swiper-item v-for="(item, index) in swiperItems" :key="index">
			<slot :item="item"></slot>
		</swiper-item>
	</swiper>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue';

/************************变量定义相关***********************/
defineExpose({ pre, next });
const emit = defineEmits<{
	'update:modelValue': number[];
}>();
const props = withDefaults(
	defineProps<{
		modelValue: number;
		items: any[];
	}>(),
	{
		items: () => [{}] // bug：先初始化一个空对象占位，让轮播图先初始化，否则会导致页面叠在一起。
	}
);

const curSwiperIndex = ref(0); // 当前滑块索引
const curItemIndex = ref(0); // 当前项目索引

/************************计算属性相关*************************/
const swiperItems = computed(() => {
	let curItem = props.items[curItemIndex.value];
	let itemLen = props.items.length;

	let nextItemIndex = curItemIndex.value >= itemLen - 1 ? 0 : curItemIndex.value + 1;
	let nextItem = props.items[nextItemIndex];

	let preItemIndex = curItemIndex.value <= 0 ? itemLen - 1 : curItemIndex.value - 1;
	let preItem = props.items[preItemIndex];

	let tempItems = [];
	if (curSwiperIndex.value === 0) {
		tempItems.push(curItem);
		tempItems.push(nextItem);
		tempItems.push(preItem);
	} else if (curSwiperIndex.value === 1) {
		tempItems.push(preItem);
		tempItems.push(curItem);
		tempItems.push(nextItem);
	} else if (curSwiperIndex.value === 2) {
		tempItems.push(nextItem);
		tempItems.push(preItem);
		tempItems.push(curItem);
	}
	return tempItems;
});

/************************监听相关*****************************/
watch(
	() => props.modelValue,
	() => {
		curItemIndex.value = props.modelValue;
	}
);

/************************事件相关*****************************/
/**
 * 同步索引
 * 类似两个齿轮相互带动转动。如：a1b1 a2b2 a3b3 a1b4 a2b1
 *  		  b1
 * 	 a1  	b4  b2
 * 	a3 a2	  b3
 *
 * @param newSwiperIndex 最新滑动索引
 */
function synIndex(newSwiperIndex: number) {
	let itemLen = props.items.length;
	let oldSwiperIndex = curSwiperIndex.value;
	let swipeRight = oldSwiperIndex - newSwiperIndex === -2 || oldSwiperIndex - newSwiperIndex === 1;
	if (swipeRight) {
		curSwiperIndex.value <= 0 ? (curSwiperIndex.value = 2) : curSwiperIndex.value--; // 索引同步右滑
		curItemIndex.value <= 0 ? (curItemIndex.value = itemLen - 1) : curItemIndex.value--;
	} else {
		curSwiperIndex.value >= 2 ? (curSwiperIndex.value = 0) : curSwiperIndex.value++; // 索引同步左滑
		curItemIndex.value >= itemLen - 1 ? (curItemIndex.value = 0) : curItemIndex.value++;
	}

	emit('update:modelValue', curItemIndex.value);
}

// 上一个
async function pre() {
	curItemIndex.value <= 0 ? (curItemIndex.value = props.items.length - 1) : curItemIndex.value--;
	emit('update:modelValue', curItemIndex.value);
}

// 下一个
async function next() {
	curItemIndex.value >= props.items.length - 1 ? (curItemIndex.value = 0) : curItemIndex.value++;
	emit('update:modelValue', curItemIndex.value);
}
</script>

<style lang="scss" scoped></style>
