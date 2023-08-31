<template>
    <text :style="`color: ${props.color};`">{{preTxt}} {{ d > 0 ? d + '天' : '' }}{{ h }}小时{{ m }}分{{ s }}秒</text>
</template>

<script lang="ts" setup>
import http from '@/utils/request.js';
import { computed, onMounted, ref, watch } from 'vue';

// 定义变量
const emit = defineEmits(['end', 'remind'])
const props = withDefaults(defineProps<{
    expireTime?: Date // 到期时间
    preTxt?: string // 前置文本
    remind?: number // 剩余多久提醒（单位：秒）
    color?: string// 文字颜色
}>(), {
    preTxt: '',
    color: '$uni-base-color'
})

const expireTime = ref(props.expireTime) // 到期时间
const curTime = ref(new Date())// 当前时间
const times = ref(0) // 剩余次数

// 监听属性
watch(() => props.expireTime, () => {
    expireTime.value = props.expireTime
})

// 计算属性
const d = computed(() => !expireTime.value ? '-' : Math.floor((expireTime.value.getTime() - curTime.value.getTime()) / 1000 / 60 / 60 / 24))
const h = computed(() => !expireTime.value ? '-' : Math.floor(((expireTime.value.getTime() - curTime.value.getTime()) / 1000 / 60 / 60) % 24))
const m = computed(() => !expireTime.value ? '-' : Math.floor(((expireTime.value.getTime() - curTime.value.getTime()) / 1000 / 60 ) % 60))
const s = computed(() => !expireTime.value ? '-' : Math.floor(((expireTime.value.getTime() - curTime.value.getTime()) / 1000 ) % 60))

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    synTime()
})

/**
 * 同步服务器时间
 * 每隔30秒同步一次服务器时间；30秒内使用本地浏览器计时；30秒内会有误差，但影响不大
 */ 
async function synTime() {
    // 如果没有过期时间，继续等待
    if (!expireTime.value) {
        setTimeout(synTime, 1000)
        return
    }

    // 每间隔30秒同步一次服务器时间
    if (times.value <= 0) {
        times.value = 30
        let { data } = await http.post("login/sysTime", {  })
        curTime.value = new Date(data.replaceAll('-', '/'))
        //console.log('服务时间：', data.replaceAll('-', '/'))
    } else {
		curTime.value = new Date(curTime.value.getTime() + 1000)
        times.value--
        //console.log('本地时间：', curTime.value, !expireTime.value ? '-' : Math.floor(((expireTime.value.getTime() - curTime.value.getTime()) / 1000 ) % 60), s.value)
    }
    
    // 如果有提醒，触发提醒事件
    if (props.remind) {
        if  (curTime.value.getTime() + (props.remind * 1000) >= expireTime.value.getTime()) {
            // console.log('倒计时事件：remind', curTime.value, expireTime.value)
            emit('remind')
        }
    }

    // 如果时间已到，触发事件，让上层处理
    if  (curTime.value.getTime() >= expireTime.value.getTime()) {
        // console.log('倒计时事件：end', curTime.value, expireTime.value)
        emit('end')
        return
    }

    setTimeout(synTime, 1000)
    return
}
</script>

<style lang="scss" scoped>
span {
    font-weight: bold;
    font-size: 14px;
    color: var(--el-color-primary);
    margin: auto;
    display: block;
    text-align: center;
}
</style>