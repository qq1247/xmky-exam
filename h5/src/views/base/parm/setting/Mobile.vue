<template>
    <el-card class="service" shadow="never">
        <template #header>
            <div class="service-title">移动端设置</div>
            <div class="service-desc">用于生成移动端扫码答题的链接</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="移动端访问地址" prop="host">
                <el-input v-model="form.host" placeholder="如：http://192.168.1.99:8080 或 https://exam.xxx.com/m" />
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
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';

// 定义变量
const form = reactive({// 表单
    host: '',// 服务名称
})
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    host: [
        { required: true, message: '请输入移动端访问地址', trigger: 'blur' },
        { min: 1, max: 128, message: '长度介于1-128', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    let { data: { data } } = await http.post("parm/get")
    form.host = data.mHost
})

// 保存
async function save() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, msg } } = await http.post("parm/m", { ...form })
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