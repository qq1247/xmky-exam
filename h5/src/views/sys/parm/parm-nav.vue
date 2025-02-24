<template>
    <div class="parm-nav">
        <xmks-sub-nav :nav-list="navList" @go="$router.push('/sys-nav/parm-list')"></xmks-sub-nav>
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
    navList.value.push(
        { 'title': '设置', 'url': `/parm-nav/set/${route.params.id}` },
    );
})

</script>
<style lang="scss" scoped>
.parm-nav {
    flex: 1;
    display: flex;
}
</style>
