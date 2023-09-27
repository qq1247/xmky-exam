<template>
    <el-card class="mark-setting" shadow="never">
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100">
            <el-form-item label="考试用户：" prop="examUserIds">
                <Select
                    v-model="form.examUserIds"
                    url="user/listpage"
                    :params="{ state: 1 }"
                    search-parm-name="name"
                    option-label="name"
                    option-value="id"
                    :options="examUsers"
                    :multiple="true"
                    clearable
                    searchPlaceholder="请输入机构名称或用户名称进行筛选"
                    >
                    <template #default="{ option }">
                        {{ option.name }} - {{option.orgName}}
                    </template>
                </Select>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useExamStore } from '@/stores/exam';
import Select from '@/components/Select.vue'
import http from '@/request';

// 定义变量
defineExpose({ next });
const form = useExamStore() // 表单
const formRef = ref<FormInstance>()// 表单引用
const formRules = reactive<FormRules>({// 表单校验规则
    examUserIds: [
        { required: true, message: '请选择考试用户', trigger: 'blur' },
    ],
})
const examUsers = ref([] as any[]) // 考试用户

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    if (form.examUserIds.length) {
        let curPage = 1
        let pageSize = 100
        while (true) {
            let { data: { data } } = await http.post("user/listpage", { 
                ids: form.examUserIds.join(),
                curPage: curPage++,
                pageSize: pageSize,
            })
    
            examUsers.value.push(...data.list)
            if (examUsers.value.length >= data.total) {// 分批次取出
                break
            }
        }
    }
})

// 下一步
async function next() {
    if (!formRef.value) return false
    return await formRef.value.validate((valid, fields) => {
        return valid
    })
}
</script>
  
<style lang="scss" scoped>
.mark-setting {
    flex: 1;
    overflow: visible;
    .el-alert {
        padding: 0;
        height: 24px;
    }
}
</style>