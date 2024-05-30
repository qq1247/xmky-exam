<template>
    <el-card class="edit" shadow="never">
        <template #header>
            <div class="edit-title">刷新缓存</div>
            <div class="edit-desc">直接在数据库修改用户、考试等信息时，因为做了缓存处理，点击刷新按钮清理</div>
        </template>
        <el-button type="primary" @click="refresh">刷新</el-button>
    </el-card>
</template>

<script lang="ts" setup>
import http from '@/request'
import { ElMessage } from 'element-plus';

// 刷新
async function refresh() {
    let { data: { code, msg } } = await http.post("cache/refresh", {
        cacheNames: ['USER_CACHE', 'ORG_CACHE', 'PARM_CACHE', 'PROGRESS_BAR_CACHE', 'QUESTION_CACHE', 'EXAM_CACHE', 'MYEXAM_CACHE', 'MYQUESTION_CACHE']
    })
    if (code === 200) {
        ElMessage.success('刷新成功')
    }
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