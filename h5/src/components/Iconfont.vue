<template>
    <div v-if="(props.width && props.height)" :style="`
    display: flex;
    width: ${props.width}px;
    height: ${props.height}px;
    justify-content: center;
    align-items: center;
    background-color: ${props.backgroundColor};
    border-radius: ${props.radius}px;`">
        <span :class="`iconfont ${props.icon}`" :style="`font-size: ${props.size}px; color: ${props.color}; `">
            <slot></slot>
        </span>
    </div>
    <span v-else :class="`iconfont ${props.icon}`" :style="`font-size: ${props.size}px; color: ${props.color}; `">
        <slot></slot>
    </span>
</template>
<script lang="ts" setup>

const props = defineProps({
    icon: {
        type: String,
        required: true,
        default: '',
    },
    radius: {
        type: Number,
        default: 0,
    },
    size: {
        type: Number,
        default: 14,
    },
    color: {
        type: String,
        default: 'var(--el-text-color-regular)'
    },
    width: {
        type: Number,
        default: 0,
    },
    height: {
        type: Number,
        default: 0,
    },
    backgroundColor: {
        type: String,
        default: 'white'
    }
})
</script>
<style lang="scss" scoped>

</style>