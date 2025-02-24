<template>
    <div class="xmks-card-guide">
        <div class="xmks-card-guide__title">
            <span class="iconfont xmks-card-guide__title-icon" :class="icon"></span>
            <span class="xmks-card-guide__title-txt">{{ title }}</span>
        </div>
        <el-button v-if="showMore" type="" link class="xmks-card__more">
            更多>>
        </el-button>
    </div>
</template>

<script lang="ts" setup>
/************************变量定义相关***********************/
withDefaults(
    defineProps<{
        title: string; // 标题
        icon: string; // 图标
        showMore?: boolean; // 是否显示更多按钮
    }>(),
    {
        showMore: false
    }
);
</script>

<style lang="scss" scoped>
.xmks-card-guide {
    display: flex;
    justify-content: space-between;

    .xmks-card-guide__title {
        display: flex;
        align-items: center;

        .xmks-card-guide__title-icon {
            width: 24px;
            height: 24px;
            line-height: 24px;
            font-size: 20px;
            text-align: center;
            color: #ffffff;
            background: linear-gradient(to right, #04C7F2 0%, #259FF8 100%);
            border-radius: 4px 4px 4px 4px;
        }

        .xmks-card-guide__title-txt {
            margin-left: 5px;
            font-size: 18px;
            color: #333333;
        }
    }

    .xmks-card-guide__more {
        margin-right: 40px;
        font-size: 14px;
        color: #1EA1EE;
    }
}
</style>
