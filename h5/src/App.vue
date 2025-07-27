<template>
    <RouterView />
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { checkBrowserCompatibility, getDownloadLinks } from '@/util/browserCheck';

onMounted(() => {
    const { isSupported, browserName, currentVersion } = checkBrowserCompatibility();
    console.log(`浏览器支持情况：${isSupported}, 浏览器名称：${browserName}, 当前版本：${currentVersion}`);
    if (!isSupported) {
        const links = getDownloadLinks();
        alert(`您的${browserName}浏览器版本(${currentVersion})过低，推荐下载新版：${Object.values(links).join('\n')}`);
    }
});
</script>
