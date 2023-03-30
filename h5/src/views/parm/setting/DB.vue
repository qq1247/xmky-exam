<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">备份目录</div>
            <div class="edit-desc">备份目录</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="备份目录" prop="bakDir">
                <el-input v-model="form.bakDir" placeholder="请输入备份目录"/>
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
    bakDir: null,
})
const formRef = ref<FormInstance>() // 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    bakDir: [
        { required: true, message: '请输入备份目录', trigger: 'blur' },
        { min: 1, max: 32, message: '长度介于1-64', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("parm/get")
    form.bakDir = data.dbBakDir
})

// 保存
async function save() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("parm/db", {...form})
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