<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">密码重置</div>
            <div class="edit-desc">
                恢复默认密码，一般在用户忘记密码时使用。
            </div>
        </template>
        <el-button type="danger" @click="pwdInit">
            重置
        </el-button>
    </el-card>
</template>

<script lang="ts" setup>
import { reactive, onMounted } from 'vue'
import http from '@/request'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus';

// 定义变量
const route = useRoute()
const router = useRouter()
const form = reactive({// 表单
    id: 0,
})

// 组件挂载完成后，执行如下方法
onMounted(() => {
    form.id = parseInt(route.params.id as string)
})

// 密码初始化
async function pwdInit() {
    let { data: { code, data } } = await http.post("user/pwdInit", {...form})

    if (code !== 200) {
        return
    }

    ElMessage({
        message: `密码初始化：${data.initPwd}`,
        type: 'success',
        showClose: true
    })

    router.push("/subAdmin")

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