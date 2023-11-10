<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">用户信息</div>
            <div class="edit-desc">{{ $route.path.indexOf('user/add') !== -1 ? '添加' : '修改' }}</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="所属机构" prop="orgId">
                <Select
                    v-model="form.orgId"
                    url="org/listpage"
                    :params="{state: 1}"
                    search-parm-name="name"
                    option-label="name"
                    option-value="id"
                    :multiple="false"
                    :options="orgs"
                    placeholder="请选择机构"
                >
                <template #default="{ option }">
                    {{ option.name }} - {{option.parentName}}
                </template>
                </Select>
            </el-form-item>
            <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名"/>
            </el-form-item>
            <el-form-item label="登录账号" prop="loginName">
                <el-input v-model="form.loginName" placeholder="请输入登录账号"/>
            </el-form-item>
            <el-form-item>
                <el-button v-if="$route.path.indexOf('user/add') !== -1" type="primary" @click="add">
                    添加
                </el-button>
                <el-button v-else type="primary" @click="edit">
                    修改
                </el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>
  
<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import http from '@/request/index'
import { useRouter, useRoute } from 'vue-router'
import Select from '@/components/Select.vue'
import type { User, Org } from '@/ts'

// 定义变量
const route = useRoute()
const router = useRouter()
const form = reactive({// 表单
    type: 1,
    orgId: 1,
} as User)
const orgs = ref([] as Org[])
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    loginName: [
        { required: true, message: '请输入登录账号', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 1, max: 8, message: '长度介于1-8', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (route.path.indexOf('user/add') !== -1) {
    } else {
        let { data: { data } } = await http.post("user/get", { id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.loginName = data.loginName
        form.orgId = data.orgId
    }

    let { data: { data } } = await http.post("org/get", { id: form.orgId })
    orgs.value.push(data)
})

// 添加
async function add() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, data } } = await http.post("user/add", {
            loginName: form.loginName,
            name: form.name,
            orgId: form.orgId,
            type: form.type,
        })

        if (code !== 200) {
            return
        }
        ElMessage({
            message: `密码初始化：${data.initPwd}`,
            type: 'success',
            showClose: true
        })
        router.push("/user")
    })
}

// 修改
async function edit() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, data } } = await http.post("user/edit", {
            id: form.id,
            loginName: form.loginName,
            name: form.name,
            orgId: form.orgId,
            type: form.type,
        })

        if (code !== 200) {
            return
        }
        if (data?.initPwd) {
            ElMessage({
                message: `密码初始化：${data.initPwd}`,
                type: 'success',
                showClose: true
            })
        }

        router.push("/user")
    })
}
</script>
  
<style lang="scss" scoped>
.edit {
    padding: 10px;
    height: calc(100vh - 300px);

    :deep(.el-card__header) {
        padding-bottom: 0px;

        .edit-title {
            font-size: 14px;
            font-weight: bold;
            padding-bottom: 5px;
        }

        .edit-desc {
            font-size: 12px;
            color: var(--el-text-color-secondary);
        }
    }

}
</style>