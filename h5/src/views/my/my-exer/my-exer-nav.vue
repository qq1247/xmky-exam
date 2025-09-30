<template>
    <div class="my-exer-nav">
        <xmks-sub-nav :nav-list="navList" @go="$router.push('/my-exer-list')"></xmks-sub-nav>
    </div>
</template>
<script lang="ts" setup>
import XmksSubNav from '@/components/nav/xmks-sub-nav.vue'
import type { NavLink } from '@/ts/nav/nav'
import { nextTick, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute();// 路由
const navList = ref<NavLink[]>([]);// 导航列表

/************************组件生命周期相关*********************/
onMounted(async () => {
    navList.value.push(
        { 'title': '练习详情', 'url': `/my-exer/read/${route.params.exerId}` },
    );
})

/************************监听相关*****************************/
watch(() => route.path, (newPath, oldPath) => {
    nextTick(() => {
        navList.value = [
            { 'title': '练习详情', 'url': `/my-exer/read/${route.params.exerId}` },
        ]
        if (newPath.includes('/my-exer/paper')) {
            navList.value[1] = { 'title': '我的练习', 'url': `/my-exer/paper/${route.params.exerId}/${route.params.id}` }
        }
    })
})
</script>
<style lang="scss" scoped>
.my-exer-nav {
    flex: 1;
    display: flex;
}
</style>
