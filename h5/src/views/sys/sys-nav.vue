<template>
    <div class="sys-nav">
        <div class="sys-nav__head">
            <el-menu :default-active="$route.path" mode="horizontal" :router="true" class="sys-nav__menu">
                <el-menu-item v-if="userStore.type === 0" index="/sys-nav/bulletin-list">公告管理</el-menu-item>
                <el-menu-item v-if="userStore.type === 0" index="/sys-nav/parm-list">系统配置</el-menu-item>
                <el-menu-item v-if="userStore.type === 0" index="/sys-nav/cron-list">定时任务</el-menu-item>
                <el-menu-item v-if="userStore.type === 0" index="/sys-nav/dict-list">字典</el-menu-item>
            </el-menu>
        </div>
        <div class="sys-nav__main">
            <router-view></router-view>
        </div>
    </div>

</template>
<script lang="ts" setup>
import { useUserStore } from '@/stores/user'

/************************变量定义相关***********************/
const userStore = useUserStore()// 用户缓存


</script>

<style scoped lang="scss">
.sys-nav {
    width: 1200px;
    display: flex;
    flex-direction: column;

    .sys-nav__head {
        display: flex;
        margin: 20px 0px 10px 0px;

        :deep(.sys-nav__menu) {
            flex: 1;
            display: flex;
            justify-content: flex-start;
            background-color: initial;
            border-bottom: initial;
            height: 40px;

            .el-menu-item {
                padding: 0px;
                margin: 0px 20px;
                color: #333333 !important;
                border-radius: 2px 2px 2px 2px;




                &:hover {
                    background-color: initial;
                }

            }

            .is-active {
                color: #333333;
                background-color: initial;
                border-bottom: 0px solid #409EFF;
                position: relative;

                &::after {
                    content: "";
                    position: absolute;
                    bottom: 0px;
                    left: 50%;
                    transform: translateX(-50%);
                    width: 14px;
                    height: 4px;
                    background-color: #0D9DF6;
                    border-radius: 2px 2px 2px 2px;
                }
            }
        }
    }

    .sys-nav__main {
        flex: 1;
        display: flex;
        background-color: #FFFFFF;
        border-radius: 15px 15px 15px 15px;
        margin-bottom: 20px;
    }
}
</style>
