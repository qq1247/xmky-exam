<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">协助阅卷</div>
            <div class="edit-desc">协助管理员阅卷</div>
        </template>
        <el-form v-if="form.markType === 2" ref="formRef" :model="form" :rules="formRules" size="large">
            <el-form-item label="协助阅卷：" prop="markUserIds">
                <Select
                    v-model="form.markUserIds"
                    url="user/listpage"
                    :params="{ type: 3 }"
                    search-parm-name="name"
                    option-label="name"
                    option-value="id"
                    :options="markUsers"
                    :multiple="true"
                    clearable
                    searchPlaceholder="请输入机构名称或用户名称进行筛选"
                    >
                    <template #default="{ option }">
                        {{ option.name }} - {{option.orgName}}
                    </template>
                </Select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="assist">保存</el-button>
            </el-form-item>
        </el-form>
        <el-empty v-else description="无需阅卷" />
    </el-card>
</template>
  
<script lang="ts" setup>
import { reactive, ref, onMounted, onBeforeMount } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import http from '@/request'
import { useRouter, useRoute } from 'vue-router'
import type { User } from '@/ts'
import Select from '@/components/Select.vue'

// 定义变量
const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({// 表单
    id: 0, // 主键
    markType: 0, // 阅卷方式（1：客观题；2：主观题；）
    markUserIds: [] as number[],// 阅卷用户IDS
})
const markUsers = ref([] as User[])
const formRules = reactive<FormRules>({// 表单校验规则
    markUserIds: [
        //{ required: true, message: '请选择阅卷用户', trigger: 'blur' },
    ],
})

// 组件挂载完成后，执行如下方法

onMounted(async () => {
    form.id = parseInt(route.params.id as string)

    let { data: { data } } = await http.post("exam/get", { id: form.id })
    form.markType = data.markType

    let { data: { data : data2 } } = await http.post("exam/markUserList", { id: form.id })
    markUsers.value = data2

    form.markUserIds = markUsers.value.map((markUser: User) => {
        return markUser.id
    })
})

// 阅卷协助
async function assist() {
    if (!formRef.value) return
    await formRef.value.validate(async (valid, fields) => {
        if (!valid) {
            return
        }

        let { data: { code } } = await http.post("exam/assist", { ...form })
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
    :deep(.el-card__body) {
        min-height: 500px;
    }

}
</style>