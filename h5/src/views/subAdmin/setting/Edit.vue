<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">子管理信息</div>
            <div class="edit-desc">当考试用户过多时，建议分配一些子管理员，分别对考试用户进行管理</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="子管理姓名" prop="name">
                <el-input v-model.trim="form.name" placeholder="请输入子管理姓名"/>
            </el-form-item>
            <el-form-item label="登录账号" prop="loginName">
                <el-input v-model.trim="form.loginName" placeholder="请输入登录账号"/>
            </el-form-item>
            <el-form-item label="拥有权限" prop="">
                <el-tag size="small">组织考试</el-tag>&nbsp;
                <el-tag type="success" size="small">模拟练习</el-tag>&nbsp;
                <el-tag type="warning" size="small">分配阅卷</el-tag>&nbsp;
                <el-tag type="danger" size="small">统计查询</el-tag>&nbsp;
            </el-form-item>
            <el-form-item label="管理用户" prop="userIds">
                <Select
                    v-model="form.userIds"
                    url="user/listpage"
                    :params="{ state: 1, type: 1 }"
                    search-parm-name="name"
                    option-label="name"
                    option-value="id"
                    :options="users"
                    :multiple="true"
                    clearable
                    searchPlaceholder="请输入机构用户名称进行筛选"
                    >
                    <template #default="{ option }">
                        {{ option.name }} - {{option.orgName}}
                    </template>
                </Select>
            </el-form-item>
            <el-form-item>
                <el-button v-if="$route.path.indexOf('subAdmin/add') !== -1" type="primary" @click="add">
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
import type { User } from '@/ts'
import Select from '@/components/Select.vue'

// 定义变量
const route = useRoute()
const router = useRouter()
const form = reactive({ // 表单
    type: 2,
    userIds: [] as number[],
} as User)
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    loginName: [
        { required: true, message: '请输入登录账号', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    name: [
        { required: true, message: '请输入子管理姓名', trigger: 'blur' },
        { min: 1, max: 8, message: '长度介于1-8', trigger: 'blur' },
    ],
    userIds: [
        { required: true, message: '请选择管理用户', trigger: 'blur' },
    ],
})
const users = ref([] as User[])

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (route.path.indexOf('subAdmin/add') !== -1) {
    } else {
        let { data: { data } } = await http.post("user/get", { id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.loginName = data.loginName
        form.userIds = data.userIds

        let curPage = 1
        let pageSize = 100
        while (true) {
            let { data: { data } } = await http.post("user/listpage", { 
                ids: form.userIds.join(),
                state: 1, // 状态正常
                type: 1,// 考试用户
                curPage: curPage++,
                pageSize: pageSize,
            })
    
            users.value.push(...data.list)
            if (users.value.length >= data.total) {// 分批次取出
                break
            }
        }
    }

})

// 添加
async function add() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, data } } = await http.post("user/add", {...form})
        if (code !== 200) {
            return
        }

        ElMessage({
            message: `密码初始化：${data.initPwd}`,
            type: 'success',
            showClose: true
        })
        router.push("/subAdmin")
    })
}

// 修改
async function edit() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, data } } = await http.post("user/edit", {...form})
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

        router.push("/subAdmin")
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