<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">启动停止</div>
            <div class="edit-desc">启动停止</div>
        </template>
        <el-switch 
            v-model="form.state"
            :active-value="1"
            :inactive-value="2"
            @click="start"
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
    state: null
})

// 组件挂载完成后，执行如下方法
onMounted(async() => {
    let { data: { data } } = await http.post("cron/get", { id: route.params.id })
    form.id = data.id
    form.state = data.state
})

// 启动停止
async function start() {
    if (form.state === 1) {
        let { data: { code } } = await http.post("cron/startTask", {...form})
        if (code !== 200) {
            return
        }
    } else {
        let { data: { code } } = await http.post("cron/stopTask", {...form})
        if (code !== 200) {
            return
        }
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