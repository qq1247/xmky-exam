<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">邮箱设置</div>
            <div class="edit-desc">考试群发邮件使用</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="账号" prop="userName">
                <el-input v-model="form.userName" placeholder="示例：1247qq@163.com"/>
            </el-form-item>
            <el-form-item label="授权码" prop="pwd">
                <el-input v-model="form.pwd" placeholder="示例：QIJAMIEGQABCDEFG"/>
            </el-form-item>
            <el-form-item label="主机" prop="host">
                <el-input v-model="form.host" placeholder="示例：smtp.163.com"/>
            </el-form-item>
            <el-form-item label="协议" prop="protocol">
                <el-input v-model="form.protocol" placeholder="示例：smtp"/>
            </el-form-item>
            <el-form-item label="编码" prop="encode">
                <el-input v-model="form.encode" placeholder="示例：utf-8"/>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="save">保存</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script lang="ts" setup>
import { reactive, onMounted, ref } from 'vue'
import http from '@/request'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';

// 定义变量
const route = useRoute()
const router = useRouter()
const form = reactive({// 表单
    userName: null,
    pwd: null,
    host: null,
    protocol: null,
    encode: null,
})
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    userName: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
    pwd: [
        { required: true, message: '请输入授权码', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
    host: [
        { required: true, message: '请输入主机', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
    protocol: [
        { required: true, message: '请输入协议', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
    encode: [
        { required: true, message: '请输入编码', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-32', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("parm/get")
    form.userName = data.emailUserName
    form.pwd = data.emailPwd
    form.host = data.emailHost
    form.protocol = data.emailProtocol
    form.encode = data.emailEncode
})

// 保存
async function save() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, msg } } = await http.post("parm/email", {...form})
        if (code === 200) {
            ElMessage.success('保存成功')
        }
    })
}
</script>

<style lang="scss" scoped>
.edit {
    padding: 10px;

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