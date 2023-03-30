<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">删除</div>
            <div class="edit-desc">删除题库</div>
        </template>
        <el-button :type="form.doubleConfirm ? 'danger' : 'warning'" @click="del">
            {{ form.doubleConfirm ? '再次确认' : '删除' }}
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
    doubleConfirm: false
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    form.id = parseInt(route.params.id as string)
})

// 删除
async function del() {
    if (!form.doubleConfirm) {
        form.doubleConfirm = true
        return
    }

    let { data: { code } } = await http.post("questionType/del", {
        id: form.id,
    })
    if (code !== 200) {
        return
    }

    router.push("/questionType")

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