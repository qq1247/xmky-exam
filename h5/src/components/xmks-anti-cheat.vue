<template>
    <slot></slot>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, watch } from 'vue';
import _ from 'lodash'
import { onBeforeRouteLeave } from 'vue-router';
import { addListener, launch, removeListener, stop } from 'devtools-detector';

/************************变量定义相关***********************/
const emit = defineEmits<{
    (e: 'screenSwitch', content: string): void
    (e: 'hotkey', content: string): void
    (e: 'debug', content: string): void
}>()
const props = defineProps({
    screenSwitch: { type: [Boolean], required: false, default: true }, // 允许切屏
    hotkey: { type: [Boolean], required: false, default: true }, // 允许快捷键
    debug: { type: [Boolean], required: false, default: true }, // 允许浏览器调试
    // pageExit: { type: [Boolean], required: false, default: true }, // 允许页面退出
});

/************************组件生命周期相关*********************/
onMounted(async () => {
    // if (!props.pageExit) {
    //     window.addEventListener('popstate', handlePopState)
    // }
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

    // if (!props.pageExit) {
    //     window.removeEventListener('popstate', handlePopState)
    // }

    if (!props.screenSwitch) {
        window.removeEventListener('blur', handleWindowBlur)
        document.removeEventListener('visibilitychange', handleVisibilityChange)
    }

})

onBeforeRouteLeave(() => {
    // if (!props.pageExit) {
    //     console.log('不允许离开页面')
    //     return false
    // }
})

/************************事件相关*****************************/
const handleContextMenu = (e: MouseEvent) => {// 右键菜单
    emit('hotkey', '尝试右键菜单')
    e.preventDefault()
}

const handleKeyDown = (e: KeyboardEvent) => {// 快捷键
    if ((e.ctrlKey || e.metaKey) && ['c', 'x', 'v'].includes(e.key.toLowerCase())) {
        emit('hotkey', '尝试复制粘贴')
        e.preventDefault()
    }
    if (e.key === 'F12' || e.code === 'F12') {
        emit('hotkey', '尝试f12调试')
        e.preventDefault()
        return false
    }
    if (e.key === 'F5' || e.code === 'F5') {
        emit('hotkey', '尝试f5刷新')
        e.preventDefault()
        return false
    }
    if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.code === 'KeyI') {
        emit('hotkey', '尝试ctrl+shift+i调试')
        e.preventDefault()
    }
}

const handleCopy = (e: ClipboardEvent) => {// 复制
    emit('hotkey', '尝试复制粘贴')
    e.preventDefault()
}

const handleTouch = (e: TouchEvent) => {// 移动端长按
    emit('hotkey', '尝试长按页面')
    e.preventDefault()
}

const handleDebug = (debug: boolean) => {// 调试页面
    if (debug) {
        emit('debug', '尝试debug调试')
        window.location.replace('about:blank');
    }
}

// const handlePopState = () => { // 浏览器前进后退
//     console.log('禁止前进后退')
//     history.pushState(null, '', document.URL);
// };

const handleWindowBlur = () => {// 浏览器外点击
    if (document.visibilityState === 'visible') {
        handleScreenSwitch('visible')
    }
}
const handleVisibilityChange = () => {// 标签页切换
    if (document.visibilityState === 'hidden') {
        handleScreenSwitch('hidden')
    }
}

const handleScreenSwitch = _.debounce((type: string) => {// 切屏（防抖）
    if (type === 'visible') {
        emit('screenSwitch', '切屏离开')
    } else {
        emit('screenSwitch', '打开标签页')
    }
}, 2000)

/************************监听相关（放到事件后面，没初始化）*****************************/
watch(
    () => props.hotkey,
    () => {
        if (!props.hotkey) {
            document.addEventListener('contextmenu', handleContextMenu)
            document.addEventListener('keydown', handleKeyDown)
            document.addEventListener('copy', handleCopy)
            document.addEventListener('cut', handleCopy)
            document.addEventListener('paste', handleCopy)
            document.addEventListener('touchstart', handleTouch)
        } else {
            document.removeEventListener('contextmenu', handleContextMenu)
            document.removeEventListener('keydown', handleKeyDown)
            document.removeEventListener('copy', handleCopy)
            document.removeEventListener('cut', handleCopy)
            document.removeEventListener('paste', handleCopy)
            document.removeEventListener('touchstart', handleTouch)
        }
    },
    { immediate: true }
);
watch(
    () => props.debug,
    () => {
        if (!props.debug) {
            addListener(isOpen => {
                handleDebug(isOpen)
            });
            launch();
        } else {
            stop()
            removeListener(handleDebug)
        }
    },
    { immediate: true }
);
watch(
    () => props.screenSwitch,
    () => {
        if (!props.screenSwitch) {
            window.addEventListener('blur', handleWindowBlur)
            document.addEventListener('visibilitychange', handleVisibilityChange)
        } else {
            window.removeEventListener('blur', handleWindowBlur)
            document.removeEventListener('visibilitychange', handleVisibilityChange)
        }
    },
    { immediate: true }
);
</script>

<style scoped></style>