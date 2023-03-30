<template>
    <span :style="`color: ${props.color};`">{{preTxt}} {{ d > 0 ? d + '天' : '' }}{{ h > 0 ? h : 0 }}小时{{ m > 0 ? m : 0 }}分{{ s > 0 ? s : 0}}秒</span>
</template>

<script lang="ts" setup>
import http from '@/request';
import dayjs from 'dayjs';
import { computed, onMounted, ref, watch } from 'vue';

// 定义变量
const emit = defineEmits(['end', 'remind'])
const props = withDefaults(defineProps<{
    expireTime?: Date // 过期时间
    preTxt?: string // 前置文本
    remind?: number // 提醒（单位：秒）
    color?: string// 颜色
}>(), {
    preTxt: '',
    color: 'var(--el-color-primary)'
})

const expireTime = ref(props.expireTime) // 过期时间
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
        let { data: { data } } = await http.post("login/sysTime", {  })
        curTime.value = dayjs(data, 'YYYY-MM-DD HH:mm:ss').toDate()
        // console.log('服务时间：', dayjs(curTime.value).format('YYYY-MM-DD HH:mm:ss'))
    } else {
        curTime.value = dayjs(curTime.value).add(1, 'second').toDate()
        times.value--
        // console.log('本地时间：', dayjs(curTime.value).format('YYYY-MM-DD HH:mm:ss'))
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