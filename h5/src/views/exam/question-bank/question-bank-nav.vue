<template>
    <div class="exam-user-nav">
        <xmks-sub-nav :nav-list="navList" @go="$router.push('/question-bank-list')"></xmks-sub-nav>
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
    if (route.path.indexOf('/question-bank/add') !== -1) {
        navList.value.push(
            { 'title': '设置', 'url': `/question-bank/add` }
        );
    } else {
        if (route.params.questionBankId !== '0') {
            navList.value.push(
                { 'title': '设置', 'url': `/question-bank/set/${route.params.id || route.params.questionBankId}` },
            );
        }
        navList.value.push(
            { 'title': '试题列表', 'url': `/question-bank/question-nav/list/${route.params.id || route.params.questionBankId}` },
        );
    }
})

</script>
<style lang="scss" scoped>
.exam-user-nav {
    flex: 1;
    display: flex;
}
</style>
