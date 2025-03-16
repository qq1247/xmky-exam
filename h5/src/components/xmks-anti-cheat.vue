<template>
    <slot></slot>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';
import _ from 'lodash'
import { onBeforeRouteLeave } from 'vue-router';
import { addListener, launch, removeListener, stop } from 'devtools-detector';

/************************变量定义相关***********************/
const emit = defineEmits<{
    (e: 'screenSwitch', type: number): void
}>()
const props = defineProps({
    screenSwitch: { type: [Boolean], required: false, default: true }, // 允许切屏
    hotkey: { type: [Boolean], required: false, default: true }, // 允许快捷键
    debug: { type: [Boolean], required: false, default: true }, // 允许浏览器调试
    pageExit: { type: [Boolean], required: false, default: true }, // 允许页面退出
});

const screenSwitchNum = ref(0) // 切屏次数

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (!props.hotkey) {
        document.addEventListener('contextmenu', handleContextMenu)
        document.addEventListener('keydown', handleKeyDown)
        document.addEventListener('copy', handleCopy)
        document.addEventListener('cut', handleCopy)
        document.addEventListener('paste', handleCopy)
        document.addEventListener('touchstart', handleTouch)
    }
    if (!props.debug) {
        addListener(isOpen => {
            handleDebug(isOpen)
        });
        launch();
    }

    if (!props.pageExit) {
        window.addEventListener('popstate', handlePopState)
    }

    if (!props.screenSwitch) {
        window.addEventListener('blur', handleWindowBlur)
        document.addEventListener('visibilitychange', handleVisibilityChange)
    }
})

onBeforeUnmount(() => {
    if (!props.hotkey) {
        document.removeEventListener('contextmenu', handleContextMenu)
        document.removeEventListener('keydown', handleKeyDown)
        document.removeEventListener('copy', handleCopy)
        document.removeEventListener('cut', handleCopy)
        document.removeEventListener('paste', handleCopy)
        document.removeEventListener('touchstart', handleTouch)
    }

    if (!props.debug) {
        stop()
        removeListener(handleDebug)
    }

    if (!props.pageExit) {
        window.removeEventListener('popstate', handlePopState)
    }

    if (!props.screenSwitch) {
        window.removeEventListener('blur', handleWindowBlur)
        document.removeEventListener('visibilitychange', handleVisibilityChange)
    }

})

onBeforeRouteLeave(() => {
    if (!props.pageExit) {
        console.log('不允许离开页面')
        return false
    }
})

/************************事件相关*****************************/
const handleContextMenu = (e: MouseEvent) => {// 右键菜单
    console.log('右键菜单')
    e.preventDefault()
}

const handleKeyDown = (e: KeyboardEvent) => {// 快捷键
    if ((e.ctrlKey || e.metaKey) && ['c', 'x', 'v'].includes(e.key.toLowerCase())) {
        console.log('不允许复制粘贴')
        e.preventDefault()
    }
    if (e.key === 'F12' || e.code === 'F12') {
        console.log('不允许F12')
        e.preventDefault()
        return false
    }
    if (e.key === 'F5' || e.code === 'F5') {
        console.log('不允许刷新')
        e.preventDefault()
        return false
    }
    if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.code === 'KeyI') {
        console.log('不允许开发者工具')
        e.preventDefault()
    }
}

const handleCopy = (e: ClipboardEvent) => {// 复制
    console.log('不允许复制')
    e.preventDefault()
}

const handleTouch = (e: TouchEvent) => {// 移动端长按
    e.preventDefault()
}

const handleDebug = (debug: boolean) => {// 调试页面
    if (debug) {
        console.log('不允许调试')
        window.location.replace('about:blank');
    }
}

const handlePopState = () => { // 浏览器前进后对
    console.log('不允许前进后退')
    history.pushState(null, '', document.URL);
};

const handleWindowBlur = () => {// 浏览器外点击
    if (document.visibilityState === 'visible') {
        handleScreenSwitch()
    }
}
const handleVisibilityChange = () => {// 标签页切换
    if (document.visibilityState === 'hidden') {
        handleScreenSwitch()
    }
}

const handleScreenSwitch = _.debounce(() => {// 切屏（防抖）
    console.log('不允许切屏')
    emit('screenSwitch', ++screenSwitchNum.value)
}, 1000)
</script>

<style scoped></style>