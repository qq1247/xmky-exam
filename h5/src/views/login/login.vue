<template>
    <div class="login">
        <div class="login__head">
            <img :src="logoWhiteUrl" class="login__logo">
            <span class="login__sysname">{{ userStore.sysName }}</span>
        </div>
        <div class="login__main">
            <div class="login__main-wrap">
                <div class="login-illust">
                    <span class="login-illust__title">&nbsp;</span>
                    <span class="login-illust__desc">&nbsp;</span>
                    <img src="/img/login/login_illust.png" class="login-illust__img">
                </div>
                <div class="login-win-wrap">
                    <div class="login-win">
                        <div class="login-win__head">
                            <img :src="logoUrl" class="login-win__logo">
                            <span class="login-win__sysname">{{ userStore.sysName }}</span>
                            <span class="login-win__welcome">欢迎登录</span>
                        </div>
                        <el-form ref="formRef" :model="form" :rules="formRules" class="login-win__main" size="large">
                            <el-form-item label="" prop="loginName">
                                <el-input v-model.trim="form.loginName" placeholder="请输入账号" class="login-win__input">
                                    <template #prefix>
                                        <span class="iconfont icon-zhanghaodenglu login-win__input-icon"></span>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-form-item label="" prop="pwd">
                                <el-input v-model.trim="form.pwd" type="password" show-password placeholder="请输入密码"
                                    class="login-win__input">
                                    <template #prefix>
                                        <span class="iconfont icon-mima54 login-win__input-icon"></span>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" class="login-win__btn" @click="login">登录</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useDictStore } from '@/stores/dict'
import { useCustomStore } from '@/stores/custom'
import { loginIn, loginEnt, loginCustom } from '@/api/login'
import { dictIndexList } from '@/api/sys/dict'
import http from '@/request'

/************************变量定义相关***********************/
const router = useRouter()// 路由
const dictStore = useDictStore()// 字典缓存
const userStore = useUserStore()// 用户缓存
const customStore = useCustomStore() // 自定义缓存
const logoUrl = ref(`${http.defaults.baseURL}login/logo`)// logo链接
const logoWhiteUrl = ref(`${http.defaults.baseURL}login/logo`)// logo链接
const form = reactive({// 表单
    loginName: '',
    pwd: '',
})
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单规则
    loginName: [
        { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    pwd: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ],
})
const keyDown = (e: KeyboardEvent) => {// 回车登录
    if (e.key === 'Enter') {
        login()
    }
}

/************************组件生命周期相关*********************/
onMounted(async () => {
    init()
    window.addEventListener('keydown', keyDown)
})

onUnmounted(() => {
    window.removeEventListener('keydown', keyDown, false)
})

/************************事件相关*****************************/
// 初始化
async function init() {
    // 缓存系统信息
    const { data: { data } } = await loginEnt({
        loginName: form.loginName,
        pwd: form.pwd
    })
    userStore.sysName = data.name

    // 缓存自定义信息
    const { data: { data: custom } } = await loginCustom({})
    customStore.title = custom.title
    customStore.content = custom.content.replaceAll('\n', '<br/>')

    // 更新浏览器标题和图标
    document.title = userStore.sysName
    const favicon = document.querySelector('link[rel="icon"]') as HTMLLinkElement;
    favicon.href = logoUrl.value;
}

// 登录
async function login() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 登录
    const { data: { code, data } } = await loginIn({
        loginName: form.loginName,
        pwd: form.pwd
    })
    if (code !== 200) {
        return
    }

    // 令牌缓存
    userStore.id = data.userId
    userStore.name = data.userName
    userStore.type = data.type
    userStore.accessToken = data.accessToken

    // 字典缓存
    const { data: { data: dictData } } = await dictIndexList({})
    dictStore.dicts = dictData

    // 跳转到首页
    router.push("/home")
}

</script>

<style lang="scss" scoped>
.login {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
    background-image: linear-gradient(to right, #82D1FD, #69C0FE);

    &::before {
        content: "";
        position: absolute;
        background-repeat: no-repeat;
        background-size: contain;
        background-image: url('/img/login/login-bg2.png');
        bottom: 0;
        left: 0;
        width: 1116px;
        height: 552px;
    }

    &::after {
        content: "";
        position: absolute;
        background-repeat: no-repeat;
        background-size: contain;
        background-image: url('/img/login/login-bg1.png');
        top: 0;
        right: 0;
        width: 370px;
        height: 470px;
    }

    .login__head {
        display: flex;
        align-items: center;
        margin: 0px 50px;
        height: 100px;

        .login__logo {
            width: 28px;
            height: 28px;
        }

        .login__sysname {
            margin-left: 6px;
            font-weight: bold;
            font-size: 22px;
            color: #FFFFFF;
        }
    }

    .login__main {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;

        .login__main-wrap {
            display: flex;
            justify-content: center;

            .login-illust {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                transform: translateY(-50px);
                width: 55%;
                margin-right: 120px;

                .login-illust__title {
                    font-size: 30px;
                    color: #FFFFFF;
                    line-height: 48px;
                }

                .login-illust__desc {
                    font-size: 16px;
                    color: #FFFFFF;
                    line-height: 48px;
                }

                .login-illust__img {
                    width: 660px;
                    height: 448px;
                }
            }

            .login-win-wrap {
                flex: 1;
                display: flex;
                align-items: center;
                transform: translateY(-50px);
                z-index: 1;

                .login-win {
                    display: flex;
                    flex-direction: column;
                    align-items: center;

                    width: 455px;
                    height: 550px;
                    padding: 30px;
                    box-shadow: 4px 0px 16px 0px #92C2DD;
                    border-radius: 10px 10px 10px 10px;
                    border: 1px solid #FFFFFF;
                    background: #D2ECFF;

                    .login-win__head {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        margin-top: 20px;

                        .login-win__logo {
                            width: 54px;
                            height: 54px;
                        }

                        .login-win__sysname {
                            font-weight: bold;
                            font-size: 24px;
                            color: #0D9DF6;
                            line-height: 48px;
                        }

                        .login-win__welcome {
                            font-size: 16px;
                            color: #333333;
                            line-height: 48px;
                        }
                    }

                    .login-win__main {
                        flex: 1;
                        width: 310px;
                        margin-top: 30px;

                        .el-form-item {
                            margin-bottom: 40px;


                            :deep(.el-form-item__content) {
                                display: flex;
                                justify-content: center;

                                .login-win__input {
                                    height: 45px;

                                    .el-input__wrapper {
                                        border-radius: 22px 22px 22px 22px;
                                        border: 1px solid #AEBACF;
                                        background-color: #D2ECFF;

                                        .el-input__inner {
                                            font-size: 16px;
                                        }

                                        .login-win__input-icon {
                                            font-size: 18px;

                                            &::after {
                                                content: "";
                                                width: 1px;
                                                height: 16px;
                                                margin: 0px 10px;
                                                border-right: 1px solid #AEBACF;
                                                background-color: #AEBACF;
                                            }
                                        }
                                    }
                                }

                                .login-win__btn {
                                    width: 100%;
                                    height: 48px;
                                    border-radius: 24px;
                                    border: 0px;
                                    color: #FFFFFF;
                                    font-size: 18px;
                                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
</style>
