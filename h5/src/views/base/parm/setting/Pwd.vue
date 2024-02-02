<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">默认密码</div>
            <div class="edit-desc">添加新用户或重置密码时，设置密码生成策略。</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="生成策略" prop="type">
                <el-radio-group v-model="form.type">
                    <el-radio :label="1">随机</el-radio>
                    <el-radio :label="2">固定</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.type === 2" label="密码" prop="value">
                <el-input v-model="form.value" placeholder="请输入密码"/>
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
    type: null,
    value: null,
})
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    value: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("parm/get")
    form.type = data.pwdType
    form.value = data.pwdValue
})

// 保存
async function save() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code, msg } } = await http.post("parm/pwd", {...form})
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