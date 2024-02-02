<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">公告信息</div>
            <div class="edit-desc">{{ $route.path.indexOf('bulletin/add') !== -1 ? '添加' : '修改' }}</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="查阅时间" prop="times">
                <el-date-picker 
                    v-model="form.times" 
                    type="datetimerange" 
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    />
            </el-form-item>
            <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入标题"/>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <el-input v-model="form.content" placeholder="请输入内容" :autosize="{ minRows: 2, maxRows: 20 }" type="textarea"/>
            </el-form-item>
            <el-form-item>
                <el-button v-if="$route.path.indexOf('bulletin/add') !== -1" type="primary" @click="add">
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
import dayjs from 'dayjs'

// 定义变量
const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({// 表单
    id: null,
    title: null,
    content: null,
    times: [
            dayjs().add(1, 'day').hour(0).minute(0).second(0).toDate(),
            dayjs().add(1, 'day').hour(23).minute(59).second(59).toDate(),
        ]
})
const formRules = reactive<FormRules>({// 表单校验规则
    times: [{
        trigger: 'change',
        validator: (rule: any, value: Date[], callback: any) => {
            if(!value || !value[0] || !value[1]) {
                return callback(new Error("请选择时间段"))
            }
            if (dayjs(value[0], 'YYYY-MM-DD HH:mm:ss').toDate() >= dayjs(value[1], 'YYYY-MM-DD HH:mm:ss').toDate()) {
                return callback(new Error("开始时间必须小于结束时间"))
            }
            return callback()
        }
    }],
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { min: 1, max: 15, message: '长度介于1-15', trigger: 'blur' },
    ],
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' },
        { min: 1, max: 10000, message: '长度介于1-10000', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (route.path.indexOf('bulletin/add') !== -1) {
        
    } else {
        let { data: { data } } = await http.post("bulletin/get", { id: route.params.id })
        form.id = data.id
        form.times[0] = data.startTime
        form.times[1] = data.endTime
        form.title = data.title
        form.content = data.content
    }
})

// 添加
async function add() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("bulletin/add", {
            startTime: form.times[0], 
            endTime: form.times[1], 
            title: form.title,
            content: form.content,
        })
        if (code !== 200) {
            return
        }

        router.push("/bulletin")
    })
}

// 修改
async function edit() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("bulletin/edit", {
            id: form.id,
            startTime: form.times[0], 
            endTime: form.times[1], 
            title: form.title,
            content: form.content,
        })
        if (code !== 200) {
            return
        }

        router.push("/bulletin")
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