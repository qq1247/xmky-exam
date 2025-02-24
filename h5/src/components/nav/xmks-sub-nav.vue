<template>
    <div class="xmks-sub-nav">
        <div class="xmks-sub-nav__head">
            <div class="sub-nav">
                <div class="sub-nav__back">
                    <el-button type='' link class="sub-nav__btn" @click="emit('go')">
                        <span class="iconfont icon-fanhui sub-nav__btn-icon"></span>
                        <span class="sub-nav__btn-txt">返回</span>
                    </el-button>
                </div>
                <el-menu :default-active="$route.path" mode="horizontal" :router="true" class="sub-nav__menu">
                    <el-menu-item v-for="(nav, index) in navList" :index="nav.url" :key="index">
                        {{ nav.title }}
                    </el-menu-item>
                </el-menu>
                <el-dropdown @command="dropdownCmd" :teleported="false" class="user">
                    <div class="user__inner">
                        <span class="iconfont icon-rentouxiang user__img"></span>
                        <span class="user__name">{{ userStore.name }}</span>
                        <span class="iconfont icon-xiala user__icon"></span>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="out"><span
                                    class="iconfont icon-login-out"></span>退出</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
        <div class="xmks-sub-nav__main">
            <RouterView></RouterView>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { useUserStore } from '@/stores/user'
import { onMounted, } from 'vue'
import type { NavLink } from '@/ts/nav/nav'
import { useRouter } from 'vue-router'
import http from '@/request'

/************************变量定义相关***********************/

defineProps({
    navList: { type: [Array<NavLink>], required: true, default: () => [] },// 导航列表
})
const emit = defineEmits<{
    (e: 'go'): void
}>()

const router = useRouter()// 路由
const userStore = useUserStore() // 用户缓存

/************************组件生命周期相关*********************/
onMounted(async () => {
})

/************************事件相关*****************************/
// 下拉菜单命令
async function dropdownCmd(command: string) {
    if (command === 'out') {
        await http.post('login/out', {})
        router.push('/login')
        return
    }
}
</script>
<style lang="scss" scoped>
.xmks-sub-nav {
    flex: 1;
    display: flex;
    flex-direction: column;

    .xmks-sub-nav__head {
        display: flex;
        justify-content: center;
        height: 64px;
        background-color: #FFFFFF;

        .sub-nav {
            display: flex;
            align-items: center;
            width: 1200px;

            .sub-nav__back {
                .sub-nav__btn {
                    .sub-nav__btn-icon {
                        font-size: 18px;
                    }

                    .sub-nav__btn-txt {
                        font-size: 16px;
                        margin-left: 5px;
                    }
                }
            }

            :deep(.sub-nav__menu) {
                flex: 1;
                display: flex;
                justify-content: center;
                background-color: initial;
                border-bottom: initial;
                height: 40px;

                .el-menu-item {
                    padding: 0px;
                    margin: 0px 20px;
                    // color: #1EA1EE !important;
                    border-radius: 2px 2px 2px 2px;

                    &.is-active {
                        color: #1EA1EE !important;
                        background-color: initial;
                        border-bottom: 0px;

                        &::after {
                            content: "";
                            position: absolute;
                            bottom: 0px;
                            left: 50%;
                            transform: translateX(-50%);
                            width: 14px;
                            height: 4px;
                            background-color: #1EA1EE;
                            border-radius: 2px 2px 2px 2px;
                        }
                    }

                    &:hover {
                        background-color: initial;
                    }

                }

                .el-sub-menu {
                    .el-sub-menu__title {
                        color: #1EA1EE;

                        &:hover {
                            background-color: initial;
                            color: #1EA1EE;
                        }
                    }
                }
            }

            :deep(.user) {
                outline: none;
                cursor: pointer;

                .user__inner {
                    display: flex;
                    align-items: center;
                    outline: none;

                    .user__img {
                        color: #77A2DB;
                        font-size: 27px;
                        width: 36px;
                        height: 36px;
                        background-color: #C5DBFF;
                        border-radius: 50%;
                        display: flex;
                        justify-content: center;
                        align-items: center;

                        &:focus {
                            outline: none;
                        }
                    }

                    .user__name {
                        margin-left: 10px;
                        font-size: 14px;
                        line-height: 48px;
                        color: #999999;
                    }

                    .user__icon {
                        color: #999999;
                        margin-left: 5px;
                    }
                }
            }
        }

    }

    .xmks-sub-nav__main {
        display: flex;
        justify-content: center;
    }
}
</style>
