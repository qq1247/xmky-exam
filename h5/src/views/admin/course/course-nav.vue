<template>
    <div class="course-nav">
        <xmks-sub-nav :nav-list="navList" @go="$router.push('/course-list')"></xmks-sub-nav>
    </div>
</template>
<script lang="ts" setup>
import XmksSubNav from '@/components/nav/xmks-sub-nav.vue'
import type { NavLink } from '@/ts/nav/nav'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute();// 路由
const navList = ref<NavLink[]>([]);// 导航列表

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('/course/add') !== -1) {
        navList.value.push(
            { 'title': '设置', 'url': `/course/add` }
        );
    } else {
        if (route.params.questionBankId !== '0') {
            navList.value.push(
                { 'title': '设置', 'url': `/course/set/${route.params.id || route.params.courseId}` },
            );
        }
        navList.value.push(
            { 'title': '资料列表', 'url': `/course/course-nav/list/${route.params.id || route.params.courseId}` },
        );
    }
})

</script>
<style lang="scss" scoped>
.course-nav {
    flex: 1;
    display: flex;
}
</style>
