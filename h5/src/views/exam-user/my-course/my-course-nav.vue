<template>
    <div class="my-course-nav">
        <xmks-sub-nav :nav-list="navList" @go="$router.push('/my-course-list')"></xmks-sub-nav>
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
        { 'title': '课程详情', 'url': `/my-course/read/${route.params.courseId}` },
    );
})

/************************监听相关*****************************/
watch(() => route.path, (newPath, oldPath) => {
    nextTick(() => {
        navList.value = [
            { 'title': '课程详情', 'url': `/my-course/read/${route.params.courseId}` },
        ]
        if (newPath.includes('/my-course/paper')) {
            navList.value[1] = { 'title': '我的课程', 'url': `/my-course/paper/${route.params.courseId}` }
        }
    })
})
</script>
<style lang="scss" scoped>
.my-course-nav {
    flex: 1;
    display: flex;
}
</style>
