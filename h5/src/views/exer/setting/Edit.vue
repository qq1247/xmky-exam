<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">练习信息</div>
            <div class="edit-desc">{{ $route.path.indexOf('exer/add') !== -1 ? '添加' : '修改' }}</div>
        </template>
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large">
            <el-form-item label="名称" prop="name">
                <el-input v-model.trim="form.name" placeholder="请输入名称"/>
            </el-form-item>
            <el-form-item label="题库" prop="questionTypeId">
                <Select
                    v-model="form.questionTypeId"
                    url="questionType/listpage"
                    :params="{}" 
                    search-parm-name="name"
                    option-label="name"
                    option-value="id"
                    :options="questionTypes"
                    :multiple="false"
                    clearable
                    searchPlaceholder="请输入题库名称进行筛选"
                    >
                    <template #default="{ option }">
                        {{ option.name }} - {{option.questionNum}}题
                    </template>
                </Select>
            </el-form-item>
            <el-form-item label="时间" prop="exerTimes">
                <el-date-picker 
                    v-model="form.exerTimes" 
                    type="datetimerange" 
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    />
            </el-form-item>
            <el-form-item label="考试用户" prop="userIds">
                <Select
                    v-model="form.userIds"
                    url="user/listpage"
                    :params="{ state: 1 }" 
                    search-parm-name="name"
                    option-label="name"
                    option-value="id"
                    :options="users"
                    :multiple="true"
                    clearable
                    searchPlaceholder="请输入机构名称或用户名称进行筛选"
                    >
                    <template #default="{ option }">
                        {{ option.name }} - {{option.orgName}}
                    </template>
                </Select>
            </el-form-item>
            <el-form-item label="允许评论" prop="rmkState">
                <el-checkbox v-model="form.rmkState" :true-label="1" :false-label="2">是</el-checkbox>
            </el-form-item>
            <el-form-item>
                <el-button v-if="$route.path.indexOf('exer/add') !== -1" type="primary" @click="add">
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
import Select from '@/components/Select.vue'
import dayjs from 'dayjs';

// 定义变量
const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({// 表单
    id: undefined,// 主键
    name: `练习-${dayjs().add(0, 'day').format('YYYY-MM-DD')}`,// 名称
    exerTimes: [
            dayjs().add(0, 'day').hour(0).minute(0).second(0).format('YYYY-MM-DD HH:mm:ss'),
            dayjs().add(0, 'day').hour(23).minute(59).second(59).format('YYYY-MM-DD HH:mm:ss'),
        ] as string[],// 练习时间
    questionTypeId: undefined,// 题库
    userIds: [] as number[],// 用户列表
    rmkState: 1,// 允许评论（1：是；2：否）
})
const formRules = reactive<FormRules>({// 表单校验规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    questionTypeId: [
        { required: true, message: '请选择题库', trigger: 'blur' },
    ],
    exerTimes: [
        { required: true, message: '请选择练习时间', trigger: 'blur' },
    ],
    userIds: [
        { required: true, message: '请选择考试用户', trigger: 'blur' },
    ],
})
const questionTypes = ref([] as any[]) // 题库列表
const users = ref([] as any[]) // 用户列表

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (route.path.indexOf('exer/add') !== -1) {
        
    } else {
        let { data: { data } } = await http.post("exer/get", { id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.questionTypeId = data.questionTypeId
        form.exerTimes = [data.startTime, data.endTime]
        form.userIds = data.userIds
        form.rmkState = data.rmkState

        let { data: { data: data2 } } = await http.post("questionType/listpage", { 
            id: form.questionTypeId,
            curPage: 1,
            pageSize: 1,
        })
        questionTypes.value.push(...data2.list)

        if (form.userIds.length) {
            let curPage = 1
            let pageSize = 100
            while (true) {
                let { data: { data } } = await http.post("user/listpage", { 
                    ids: form.userIds.join(),
                    //state: 1, 只查询1就丢了2的数据
                    curPage: curPage++,
                    pageSize: pageSize,
                })
        
                users.value.push(...data.list)
                if (users.value.length >= data.total) {// 分批次取出
                    break
                }
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

        let { data: { code } } = await http.post("exer/add", {
            ...form,
            startTime: form.exerTimes[0],
            endTime: form.exerTimes[1],
        })

        if (code !== 200) {
            return
        }

        router.push("/exer")
    })
}

// 修改
async function edit() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("exer/edit", {
            ...form,
            startTime: form.exerTimes[0],
            endTime: form.exerTimes[1],
        })

        if (code !== 200) {
            return
        }

        router.push("/exer")
    })
}
</script>
  
<style lang="scss" scoped>
.edit {
    padding: 10px;
    overflow: visible;

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