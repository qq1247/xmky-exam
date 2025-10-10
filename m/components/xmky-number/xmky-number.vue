<template>
	<view class="xm-number">
		<view class="xm-number__btn" :class="{ 'xm-number__btn--disabled': isMin }" @click="decrease">
			<text>-</text>
		</view>
		<input class="xm-number__input" type="number" :value="innerValue" @input="onInput" @blur="onBlur" />
		<view class="xm-number__btn" :class="{ 'xm-number__btn--disabled': isMax }" @click="increase">
			<text>+</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref, watch, computed } from 'vue';

/************************ 变量定义相关 ***********************/
const props = withDefaults(
	defineProps<{
		modelValue: number;
		min: number;
		max: number;
		step: number;
	}>(),
	{
		modelValue: 0,
		min: 0,
		max: 100,
		step: 10
	}
);

const emit = defineEmits<{
	(e: 'update:modelValue', value: number): void;
	(e: 'input', value: number): void;
}>();

const innerValue = ref(props.modelValue);

/************************监听相关*****************************/
watch(
	() => props.modelValue,
	(newVal) => {
		if (newVal !== innerValue.value) {
			innerValue.value = clamp(newVal, props.min, props.max);
		}
	}
);
watch(
	() => [props.min, props.max],
	([newMin, newMax]) => {
		const clamped = clamp(innerValue.value, newMin, newMax);
		innerValue.value = clamped;
		emit('update:modelValue', clamped);
		emit('input', clamped);
	},
	{ immediate: true } // 立即执行一次，确保初始值合法
);

/************************计算属性相关*************************/
const isMin = computed(() => innerValue.value <= props.min);
const isMax = computed(() => innerValue.value >= props.max);

/************************事件相关*****************************/
// 工具函数：限制值在 [min, max] 范围内
function clamp(val: number, min: number, max: number): number {
	if (isNaN(val)) return min;
	return Math.min(Math.max(Math.floor(val), min), max);
}

// 增加
function increase() {
	if (isMax.value) return;
	const next = innerValue.value + props.step;
	innerValue.value = clamp(next, props.min, props.max);
	emit('update:modelValue', innerValue.value);
	emit('input', innerValue.value);
}

// 减少
function decrease() {
	if (isMin.value) return;
	const next = innerValue.value - props.step;
	innerValue.value = clamp(next, props.min, props.max);
	emit('update:modelValue', innerValue.value);
	emit('input', innerValue.value);
}

// 输入处理
function onInput(e: any) {
	const raw = e.detail.value;
	if (raw === '') {
		innerValue.value = NaN; // 允许空（用户可能正在删除），blur 时再修正
		return;
	}
	const num = Number(raw);
	innerValue.value = num; // 不立即 clamp，允许用户输入中间状态（如输 999），blur 时再修正
}

// 失去焦点时校验并修正
function onBlur() {
	const originalValue = innerValue.value;
	let val = originalValue;

	if (isNaN(val) || val === null || val === undefined) {
		val = props.min;
	}

	const clamped = clamp(val, props.min, props.max);
	innerValue.value = clamped;

	emit('update:modelValue', clamped);
	emit('input', clamped);
}
</script>

<style lang="scss" scoped>
.xm-number {
	display: flex;
	align-items: center;
	width: 200rpx; // 可根据需要调整
	height: 50rpx;
	border-radius: 8rpx;
	overflow: hidden;
	margin-right: 10rpx;
	.xm-number__btn {
		width: 50rpx;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		background-color: #f5f5f5;
		font-size: 32rpx;
		&--disabled {
			color: #ccc;
			background-color: #fafafa;
		}
	}

	.xm-number__input {
		flex: 1;
		text-align: center;
		font-size: 28rpx;
		height: 100%;
		padding: 0;
		margin: 0;
		width: 100rpx;
	}
}
</style>
