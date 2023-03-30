<template>
    <el-card class="service" shadow="never">
        <template #header>
            <div class="service-title">自定义内容</div>
            <div class="service-desc">变更首页右下角的内容</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入标题"/>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <el-input v-model="form.content" placeholder="请输入内容" :autosize="{ minRows: 2 }" type="textarea"/>
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
import { ElMessage, type FormInstance, type FormRules, type UploadFile, type UploadFiles, type UploadRawFile } from 'element-plus';

// 定义变量
const form = reactive({// 表单
    title: null,// 服务名称
    content: null,// 服务内容
})
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { min: 1, max: 8, message: '长度介于1-16', trigger: 'blur' },
    ],
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { min: 1, max: 128, message: '长度介于1-128', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("parm/get")
    form.title = data.customTitle
    form.content = data.customContent
})

// 保存
async function save() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, msg } } = await http.post("parm/custom", {...form})
        if (code === 200) {
            ElMessage.success('保存成功')
        }
    })
}

</script>

<style lang="scss" scoped>
.service {
    padding: 10px;

    :deep(.el-card__header) {
        padding-bottom: 0px;

        .service-title {
            font-size: 14px;
            font-weight: bold;
            padding-bottom: 5px;
        }

        .service-desc {
            font-size: 12px;
            color: var(--el-text-color-secondary);
        }
    }
}
</style>