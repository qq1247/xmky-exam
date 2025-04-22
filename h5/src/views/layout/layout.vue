<template>
    <div class="layout">
        <div class="layout__head">
            <div class="nav__wrap">
                <div class="nav">
                    <div class="nav-logo">
                        <img :src="logoUrl" class="nav-logo__img">
                        <span class="nav-logo__name">{{ userStore.sysName }}</span>
                    </div>
                    <el-menu :default-active="curActiveMenu" mode="horizontal" :router="true" :ellipsis="false"
                        class="nav-menu">
                        <el-menu-item index="/home">首页</el-menu-item>
                        <el-menu-item v-if="userStore.type === 0 || userStore.type === 2"
                            index="/question-bank-list">题库</el-menu-item>
                        <el-menu-item v-if="userStore.type === 0 || userStore.type === 2"
                            index="/exer-list">练习</el-menu-item>
                        <el-menu-item v-if="userStore.type === 1" index="/my-exer-list">练习</el-menu-item>
                        <el-menu-item v-if="userStore.type === 0 || userStore.type === 2"
                            index="/exam-list">考试</el-menu-item>
                        <el-menu-item v-if="userStore.type === 3" index="/my-mark-list">阅卷</el-menu-item>
                        <el-menu-item v-if="userStore.type === 1 || userStore.type === 4"
                            index="/my-exam-list">考试</el-menu-item>
                        <el-menu-item v-if="userStore.type === 0 || userStore.type === 2"
                            index="/base-nav/exam-user-list">用户</el-menu-item>
                        <el-menu-item v-if="userStore.type === 0" index="/sys-nav/bulletin-list">系统</el-menu-item>
                    </el-menu>
                    <el-dropdown @command="dropdownCmd" :teleported="false" class="nav-user__wrap">
                        <div class="nav-user">
                            <span class="iconfont icon-rentouxiang nav-user__img"></span>
                            <span class="nav-user__user-name">{{ userStore.name }}</span>
                            <span class="iconfont icon-xiala nav-user__icon"></span>
                        </div>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="pwd"><span
                                        class="iconfont icon-edit"></span>修改密码</el-dropdown-item>
                                <el-dropdown-item command="out"><span
                                        class="iconfont icon-login-out"></span>退出</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
            <div class="carousel__wrap">
                <div class="carousel">
                    <div class="carousel__inner">
                        <span class="carousel__title" v-html="customStore.title"></span>
                        <span class="carousel__desc" v-html="customStore.content"></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="layout__main">
            <RouterView></RouterView>
        </div>
    </div>
</template>
<script lang="ts" setup>
import http from '@/request'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCustomStore } from '@/stores/custom'
import { computed, onMounted } from 'vue'

/************************变量定义相关***********************/
const router = useRouter() // 路由
const route = useRoute() // 路由
const userStore = useUserStore() // 用户缓存
const customStore = useCustomStore() // 自定义缓存
const logoUrl = `${http.defaults.baseURL}login/logo` // logo地址

/************************组件生命周期相关*********************/
onMounted(async () => {
})


/************************计算属性相关*************************/
const curActiveMenu = computed(() => {
    if (route.path.startsWith('/base-nav')) {// 子菜单在不同的路由，需要特殊处理一下
        return '/base-nav/org-list'
    }
    if (route.path.startsWith('/sys-nav')) {
        return '/sys-nav/bulletin-list'
    }

    return route.path
})

/************************事件相关*****************************/
// 下拉菜单命令
async function dropdownCmd(command: string) {
    if (command === 'pwd') {
        router.push('/pwd')
        return
    }

    if (command === 'out') {
        await http.post('login/out', {})
        router.push('/login')
        return
    }
}
</script>
<style lang="scss" scoped>
.layout {
    flex: 1;
    display: flex;
    flex-direction: column;

    .layout__head {
        display: flex;
        height: 200px;
        position: relative;

        .nav__wrap {
            display: flex;
            justify-content: center;
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 64px;
            background-image: linear-gradient(to right, #1e72ad, #6a97ad);
            z-index: 1;
            opacity: 0.8;

            .nav {
                display: flex;
                flex-direction: row;
                align-items: center;
                width: 1200px;

                .nav-logo {
                    display: flex;
                    align-items: center;

                    .nav-logo__img {
                        height: 35px;
                    }

                    .nav-logo__name {
                        margin-left: 5px;
                        font-weight: bold;
                        font-size: 18px;
                        color: #FFFFFF;
                        line-height: 63px;
                    }
                }

                :deep(.nav-menu) {
                    flex: 1;
                    display: flex;
                    justify-content: center;
                    background-color: initial;
                    border-bottom: initial;
                    height: 40px;

                    .el-menu-item {
                        padding: 0px;
                        margin: 0px 20px;
                        color: #ffffff !important;
                        border-radius: 2px 2px 2px 2px;

                        &.is-active {
                            color: #ffffff;
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
                                background-color: #FFFFFF;
                                border-radius: 2px 2px 2px 2px;
                            }
                        }

                        &:hover {
                            background-color: initial;
                        }

                    }

                    .el-sub-menu {
                        .el-sub-menu__title {
                            color: #ffffff;

                            &:hover {
                                background-color: initial;
                                color: #ffffff;
                            }
                        }
                    }
                }

                :deep(.nav-user__wrap) {
                    outline: none;
                    cursor: pointer;

                    .nav-user {
                        display: flex;
                        align-items: center;
                        outline: none;

                        .nav-user__img {
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

                        .nav-user__user-name {
                            margin-left: 10px;
                            font-size: 14px;
                            color: #FFFFFF;
                            line-height: 48px;
                        }

                        .nav-user__icon {
                            color: #ffffff;
                            margin-left: 5px;
                        }
                    }
                }
            }
        }

        .carousel__wrap {
            flex: 1;
            height: 200px; // 不设置，页面切换高度会变化
            display: flex;
            justify-content: center;
            background-image: linear-gradient(to right, #2A9EF8, #37CFF8);
            position: relative;
            overflow: hidden;

            &::after {
                content: "";
                position: absolute;
                background-repeat: no-repeat;
                background-size: contain;
                background-image: url("/img/login/login-bg1.png");
                top: 0;
                right: 0;
                width: 370px;
                height: 470px;
            }


            .carousel {
                display: flex;
                align-items: center;
                position: relative;
                width: 1200px;
                padding-top: 64px;

                .carousel__inner {
                    display: flex;
                    flex-direction: column;

                    .carousel__title {
                        font-weight: bold;
                        font-size: 24px;
                        color: #FFFFFF;
                        line-height: 50px;
                    }

                    .carousel__desc {
                        font-size: 14px;
                        color: #FFFFFF;
                    }
                }

                &::after {
                    content: "";
                    position: absolute;
                    background-repeat: no-repeat;
                    background-size: contain;
                    background-image: url("/img/login/login_illust.png");
                    top: 0;
                    right: 0;
                    width: 660px;
                    height: 350px;
                    transform: perspective(800px) rotateX(0deg) rotateY(180deg);
                }
            }
        }
    }

    .layout__main {
        flex: 1;
        display: flex;
        justify-content: center;
    }
}
</style>
