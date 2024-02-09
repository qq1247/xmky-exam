<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">暂停</div>
            <div class="edit-desc">暂停考试</div>
        </template>
        <el-button :type="'warning'" @click="pause">
            {{ form.state === 1 ? '暂停' : '发布' }}
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
    state: 0,
})

// 组件挂载完成后，执行如下方法
onMounted(async () => {
    let { data: { data } } = await http.post("exam/get", { id: route.params.id })
    form.id = data.id
    form.state = data.state
})

// 暂停
async function pause() {
    let { data: { code } } = await http.post("exam/pause", {
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/exam")
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