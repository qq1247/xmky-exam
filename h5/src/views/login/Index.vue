<template>
    <div class="login">
        <el-form ref="formRef" :model="form" :rules="formRules" class="login-form" size="large">
            <div class="login-form-title">
                欢迎登录
            </div>
            <el-form-item label="" prop="loginName">
                <el-input v-model.trim="form.loginName" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item label="" prop="pwd">
                <el-input v-model.trim="form.pwd" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="login">登录</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import http from "@/request/index"
import { useUserStore } from '@/stores/user';
import { useDictStore } from '@/stores/dict';

// 定义变量
const router = useRouter()
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
const keyDown = (e:KeyboardEvent) => {
  if (e.keyCode == 13) {
    login()
  }
}

// 组件挂载完成后，执行如下方法
onMounted(() => {
    window.addEventListener('keydown', keyDown)
})

// 组件被卸载之后，执行如下方法
onUnmounted(() => {
  window.removeEventListener('keydown', keyDown, false)
})

// 登录
async function login() {
    // 校验数据有效性
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        // 登录
        let { data: { code, data } } = await http.post('login/in', {
            loginName: form.loginName,
            pwd: form.pwd
        })
        if (code !== 200) {
            return
        }

        // 缓存令牌
        const userStore = useUserStore()
        userStore.id = data.userId
        userStore.name = data.userName
        userStore.type = data.type
        userStore.accessToken = data.accessToken

        // 缓存数据字典
        let { data: { data: dictData } } = await http.post('dict/listpage', {
            pageSize: 100
        })
        const dictStore = useDictStore()// 字典缓存
        dictStore.dicts = dictData.list

        // 跳转到首页
        router.push("/home")
    })
}

</script>

<style lang="scss" scoped>
.login {
    display: flex;
    height: 100vh;
    justify-content: center;
    align-items: center;

    .login-form {
        position: relative;
        width: 35%;
        height: auto;
        padding: 60px;
        box-sizing: border-box;
        background: #fff;
        border-radius: 3px;
        box-shadow: 0 0 16px 3px rgb(0 0 0 / 5%);
        margin-bottom: 100px;

        .login-form-title {
            font-size: 22px;
            font-weight: bold;
            color: var(--el-text-color-regular);
            padding-bottom: 20px;
        }

        .el-button {
            width: 100%;
            height: 45px;
        }
    }
}
</style>
