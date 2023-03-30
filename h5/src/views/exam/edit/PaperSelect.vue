<template>
    <el-card shadow="never" class="paper-select">
        <div class="paper-select-panel" style="background-color: #0094e4;">
            <span class="iconfont icon-kongbaiwendang"></span>
            <div class="paper-select-panel-title">空白试卷</div>
            <div class="paper-select-panel-desc">从零开始创建一张试卷</div>
            <el-button round @click="emit('select', 'blank')">创建</el-button>
        </div>
        <div class="paper-select-panel" style="background-color: #09c9be;">
            <span class="iconfont icon-kuaisuxiadan"></span>
            <div class="paper-select-panel-title">快速生成试卷</div>
            <div class="paper-select-panel-desc">即时编辑试题，快速导入</div>
            <el-button round @click="emit('select', 'quick')">创建</el-button>
        </div>
        <div class="paper-select-panel" style="background-color: #6a77f9;">
            <span class="iconfont icon-shiti"></span>
            <div class="paper-select-panel-title">随机试卷</div>
            <div class="paper-select-panel-desc">配置抽题规则，每个人试卷不一样</div>
            <el-button round @click="emit('select', 'random')">创建</el-button>
        </div>
    </el-card>
</template>

<script lang="ts" setup>
// 定义变量
const emit = defineEmits<{
    (e: 'select', type: string): void
}>()
</script>

<style lang="scss" scoped>
.paper-select {
    :deep(.el-card__body) {
        display: flex;

        .paper-select-panel {
            flex: 1;
            height: 200px;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
            margin: 5px;
            border-radius: 8px;
            color: white;

            .iconfont {
                font-size: 48px;
                margin-bottom: 10px;
            }

            .paper-select-panel-title {
                font-weight: bold;
                margin-bottom: 10px;
            }

            .paper-select-panel-desc {
                font-size: 13px;
                margin-bottom: 10px;
            }

            .el-button {
                padding: 18px 40px;
                span {
                    color: var(--el-color-primary);
                }
            }
        }
    }
}
</style>