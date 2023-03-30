<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">账号冻结</div>
            <div class="edit-desc">账号冻结</div>
        </template>
        <el-switch 
            v-model="form.state"
            :active-value="2"
            :inactive-value="1"
            @click="frozen"
            />
    </el-card>
</template>

<script lang="ts" setup>
import { reactive, onMounted } from 'vue'
import http from '@/request'
import { useRouter, useRoute } from 'vue-router'

// 定义变量
const route = useRoute()
const router = useRouter()
const form = reactive({// 表单
    id: null,
    state: null,
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("user/get", { id: route.params.id })
    form.id = data.id
    form.state = data.state
})

// 账号冻结
async function frozen() {
    let { data: { code, data } } = await http.post("user/frozen", {...form})

    if (code !== 200) {
        return
    }

    router.push("/user")

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