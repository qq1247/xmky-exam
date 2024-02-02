<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">数据字典信息</div>
            <div class="edit-desc">{{ $route.path.indexOf('dict/add') !== -1 ? '添加' : '修改' }}</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="索引" prop="dictIndex">
                <el-input v-model="form.dictIndex" placeholder="请输入索引"/>
            </el-form-item>
            <el-form-item label="键" prop="dictKey">
                <el-input v-model="form.dictKey" placeholder="请输入键"/>
            </el-form-item>
            <el-form-item label="值" prop="dictValue">
                <el-input v-model="form.dictValue" placeholder="请输入值"/>
            </el-form-item>
            <el-form-item label="排序" prop="no">
                <el-input-number v-model="form.no" :min="1" :max="100" :precision="0" controls-position="right" size="large" />
            </el-form-item>
            <el-form-item>
                <el-button v-if="$route.path.indexOf('dict/add') !== -1" type="primary" @click="add">
                    添加
                </el-button>
                <el-button v-else type="primary" @click="edit">修改</el-button>
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
    id: null,
    dictIndex: null,
    dictKey: null,
    dictValue: null,
    no: 1,
})
const formRules = reactive<FormRules>({// 表单校验规则
    dictIndex: [
        { required: true, message: '请输入索引', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    dictKey: [
        { required: true, message: '请输入键', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    dictValue: [
        { required: true, message: '请输入值', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    no: [
        { required: true, message: '请输入排序', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (route.path.indexOf('dict/add') !== -1) {
        
    } else {
        let { data: { data } } = await http.post("dict/get", { id: route.params.id })
        form.id = data.id
        form.dictIndex = data.dictIndex
        form.dictKey = data.dictKey
        form.dictValue = data.dictValue
        form.no = data.no
    }
})

// 添加
async function add() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("dict/add", {
            dictIndex: form.dictIndex,
            dictKey: form.dictKey,
            dictValue: form.dictValue,
            no: form.no,
        })

        if (code !== 200) {
            return
        }

        router.push("/dict")
    })
}

// 修改
async function edit() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("dict/edit", {
            id: form.id,
            dictIndex: form.dictIndex,
            dictKey: form.dictKey,
            dictValue: form.dictValue,
            no: form.no,
        })

        if (code !== 200) {
            return
        }

        router.push("/dict")
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