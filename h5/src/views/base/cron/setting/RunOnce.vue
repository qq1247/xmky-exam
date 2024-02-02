<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">执行一次</div>
            <div class="edit-desc">执行一次</div>
        </template>
        <el-button type="warning" @click="runOnce">
            执行
        </el-button>
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
    id: 0,
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    form.id = parseInt(route.params.id as string)
})

// 执行一次
async function runOnce() {
    let { data: { code, data } } = await http.post("cron/runOnceTask", {...form})
    if (code !== 200) {
        return
    }

    router.push("/cron")
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