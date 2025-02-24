<template>
    <div class="my-exam-nav">
        <xmks-sub-nav :nav-list="navList"
            @go="$router.push([0, 2].includes(userStore.type) ? '/exam-list' : '/my-mark-list')"></xmks-sub-nav>
    </div>
</template>
<script lang="ts" setup>
import XmksSubNav from '@/components/nav/xmks-sub-nav.vue'
import { useUserStore } from '@/stores/user';
import type { NavLink } from '@/ts/nav/nav'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute();// 路由
const userStore = useUserStore();// 用户缓存

const navList = ref<NavLink[]>([]);// 导航列表

/************************组件生命周期相关*********************/
onMounted(async () => {
    navList.value.push(
        { 'title': '考试详情', 'url': `/my-mark/read/${route.params.examId}` },
        { 'title': '我的试卷', 'url': `/my-mark/paper/${route.params.examId}` }
    );
})

</script>
<style lang="scss" scoped>
.my-exam-nav {
    flex: 1;
    display: flex;
}
</style>
