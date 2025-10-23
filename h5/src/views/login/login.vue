<template>
    <div class="login">
        <div class="login__head">
            <img :src="logoWhiteUrl" class="login__logo">
            <span class="login__sysname">{{ parmStore.sysName }}</span>
        </div>
        <div class="login__main">
            <div class="login__main-wrap">
                <div class="login-illust">
                    <span class="login-illust__title"></span>
                    <span class="login-illust__desc"></span>
                    <img src="/img/login/login_illust.png" class="login-illust__img">
                </div>
                <div class="login-win-wrap">
                    <div class="login-win">
                        <div class="login-win__head">
                            <img :src="logoUrl" class="login-win__logo">
                            <span class="login-win__sysname">{{ parmStore.sysName }}</span>
                            <span class="login-win__welcome">欢迎登录</span>
                        </div>
                        <el-form v-if="loginType == 1" ref="formRef" :model="form" :rules="formRules"
                            class="login-win__main" size="large">
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
                                <div class="login-win__btn-group">
                                    <span @click="loginType = 2" class="login-win__switch‌-btn">临时登录</span>
                                    <el-button type="primary" class="login-win__login-btn" @click="login">登录</el-button>
                                </div>
                            </el-form-item>
                        </el-form>
                        <el-form v-if="loginType == 2" ref="anonFormRef" :model="anonForm" :rules="anonFormRules"
                            class="login-win__main" size="large">
                            <el-form-item label="" prop="userName">
                                <el-input v-model.trim="anonForm.userName" placeholder="请输入姓名和手机号"
                                    class="login-win__input">
                                    <template #prefix>
                                        <span class="iconfont icon-fabu-11 login-win__input-icon"></span>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-form-item label="" prop="examName">
                                <el-input v-model.trim="anonForm.examName" placeholder="请输入考试名称"
                                    class="login-win__input">
                                    <template #prefix>
                                        <span class="iconfont icon-tubiaoziti-35 login-win__input-icon"></span>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-form-item>
                                <div class="login-win__btn-group">
                                    <span @click="loginType = 1" class="login-win__switch‌-btn">账号登录</span>
                                    <el-button type="primary" class="login-win__login-btn"
                                        @click="anonLogin">登录</el-button>
                                </div>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </div>
        </div>
        <div class="login__foot">
            <p v-if="parmStore.icp" v-html="parmStore.icp" class="copyright"></p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { examExamGet } from '@/api/exam/exam'
import { loginIn, loginNoLogin, loginParm, loginEncrypt, loginSysTime } from '@/api/login'
import { myExamGeneratePaper } from '@/api/my/my-exam'
import { dictIndexList } from '@/api/sys/dict'
import http from '@/request'
import { useDictStore } from '@/stores/dict'
import { useParmStore } from '@/stores/parm'
import { useUserStore } from '@/stores/user'
import { escape2Html } from '@/util/htmlUtil'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import JSEncrypt from 'jsencrypt'
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

/************************变量定义相关***********************/
const router = useRouter()// 路由
const dictStore = useDictStore()// 字典缓存
const userStore = useUserStore()// 用户缓存
const parmStore = useParmStore() // 参数缓存
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

const anonForm = reactive({// 表单
    userName: '',
    examName: '',
})
const anonFormRef = ref<FormInstance>()// 表单引用
const anonFormRules = reactive<FormRules>({// 表单规则
    userName: [
        { required: true, message: '请输入姓名和手机号', trigger: 'blur' },
        { min: 2, max: 16, message: '长度介于2-16', trigger: 'blur' }
    ],
    examName: [
        { required: true, message: '请输入考试名称', trigger: 'blur' },
    ],
})

const loginType = ref(1)// 登录类型（1：账号登录；2：匿名登录）

const keyDown = (e: KeyboardEvent) => {// 回车登录
    if (e.key === 'Enter') {
        if (loginType.value === 1) {
            login()
        } else {
            anonLogin()
        }
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
    const { data: { data } } = await loginParm({})
    parmStore.sysName = data.sysName
    parmStore.customTitle = data.customTitle
    parmStore.customContent = data.customContent.replaceAll('\n', '<br/>')
    parmStore.icp = escape2Html(data.icp || '')

    // 更新浏览器标题和图标
    document.title = parmStore.sysName
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

    const { data: { code: _code, data: _encrypt } } = await loginEncrypt({ loginName: form.loginName })
    if (_code !== 200) {
        return
    }
    const encrypt = new JSEncrypt();
    encrypt.setPublicKey(_encrypt.publicKey);
    const encryptedPwd = encrypt.encrypt(`${_encrypt.nonce}:${form.pwd}`);
    if (!encryptedPwd) {
        ElMessage.warning('生成秘钥失败')
        return
    }

    // 登录
    const { data: { code, data } } = await loginIn({
        loginName: form.loginName,
        pwd: encryptedPwd
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


// 匿名登录
async function anonLogin() {
    // 数据校验
    try {
        await anonFormRef.value?.validate()
    } catch (e) {
        return
    }

    const { data: { code, data: examData } } = await examExamGet({
        name: anonForm.examName,
    })
    if (code !== 200) {
        return
    }
    if (!examData.id) {
        ElMessage.warning('未找到该考试，请重新输入')
        return
    }
    if (examData.loginType === 1) {
        ElMessage.warning('企业内部考试，请使用账号密码登录')
        return
    }

    const { data: { data: curTime } } = await loginSysTime({})
    if (examData.startTime > curTime) {
        ElMessage.warning('考试未开始')
        return
    }
    if (examData.endTime < curTime) {
        ElMessage.warning('考试已结束')
        return
    }

    // 登录
    const { data: { code: code1, data: userData } } = await loginNoLogin({
        name: anonForm.userName
    })
    if (code1 !== 200) {
        return
    }

    // 令牌缓存
    userStore.id = userData.userId
    userStore.name = userData.userName
    userStore.type = userData.type
    userStore.accessToken = userData.accessToken

    // 字典缓存
    const { data: { data: dict } } = await dictIndexList({})
    dictStore.dicts = dict

    // 生成试卷
    const { data: { code: myExamCode } } = await myExamGeneratePaper({ examId: examData.id });
    if (myExamCode !== 200) {
        return;
    }

    // 进入试卷页面
    router.push(`/my-exam/read/${examData.id}`)
}

</script>

<style lang="scss" scoped>
.login {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-image: linear-gradient(to right, #82D1FD, #69C0FE);

    &::before {
        content: "";
        position: absolute;
        background-repeat: no-repeat;
        background-size: contain;
        background-image: url('/img/login/login-bg2.png');
        bottom: 0;
        left: 0;
        width: 800px;
        height: 380px;
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

                        .el-form-item {
                            margin-top: 40px;

                            &:first-child {
                                margin-top: 30px;
                            }

                            &:last-child {
                                margin-top: 20px;
                            }


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

                                .login-win__btn-group {
                                    display: flex;
                                    flex-direction: column;
                                    align-items: flex-end;
                                    width: 100%;

                                    .login-win__switch‌-btn {
                                        line-height: initial;
                                        margin: 0px 10px 10px 0px;
                                        cursor: pointer;
                                        color: #0D9DF6;

                                        &:hover {
                                            color: #04C7F2;
                                        }
                                    }

                                    .login-win__login-btn {
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

    .login__foot {
        position: relative;

        .copyright {
            position: absolute;
            left: 0px;
            right: 0px;
            bottom: 0px;
            margin: initial;
            padding: 4px 0px;
            text-align: center;
            font-size: 14px;
            color: #FFF;
            background-color: rgba(13, 157, 246, 0.2);

            :deep(a) {
                color: #FFF;
                text-decoration: none;

                &:hover,
                &:active {
                    text-decoration: underline;
                    color: #0D9DF6;
                }
            }
        }
    }
}
</style>
