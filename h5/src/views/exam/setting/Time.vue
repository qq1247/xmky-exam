<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">时间变更</div>
            <div class="edit-desc">提前或延后考试时间、阅卷时间</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" inline label-width="100" size="large">
            <el-form-item label="" prop="timeType">
                <el-select v-model="form.timeType" clearable placeholder="请选择时间选项">
                    <el-option label="考试开始时间" :value="1" />
                    <el-option label="考试结束时间" :value="2" />
                    <el-option v-if="form.markType === 2" label="阅卷开始时间" :value="3" />
                    <el-option v-if="form.markType === 2" label="阅卷结束时间" :value="4" />
                </el-select>
            </el-form-item>
            <el-form-item label="" prop="add">
                <el-select v-model="form.add" clearable placeholder="请选择操作选项">
                    <el-option label="提前" :value="1" />
                    <el-option label="延后" :value="2" />
                </el-select>
            </el-form-item>
            <el-form-item label="" prop="minute">
                <el-input-number v-model="form.minute" :min="1" :step="10" :precision="0"></el-input-number>&nbsp;&nbsp;分钟
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="setTime">设置</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>
  
<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import http from '@/request'
import { useRouter, useRoute } from 'vue-router'

// 定义变量
const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({// 表单
    id: 0, // 主键
    timeType: 2,// 时间类型
    add: 2, // 添加
    minute: 10, // 分钟数
    markType: 1, // 阅卷类型（用于控制显示阅卷时间）
})
const formRules = reactive<FormRules>({// 表单校验规则
    timeType: [
        { required: true, message: '请选择时间类型', trigger: 'blur' },
    ],
    add: [
        { required: true, message: '请选择操作类型', trigger: 'blur' },
    ],
    minute: [
        { required: true, message: '请输入分钟数', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    let { data: { data } } = await http.post("exam/get", { id: route.params.id })
    form.id = data.id
    form.markType = data.markType 
})

// 时间变更
async function setTime() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("exam/time", {
            id: form.id,
            timeType: form.timeType,
            minute: form.add === 1 ? -form.minute : form.minute
        })
        if (code !== 200) {
            return
        }

        router.push("/exam")
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