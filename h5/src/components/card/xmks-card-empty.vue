<template>
    <div class="xmks-card-empty">
        <span class="iconfont xmks-card-empty__icon" :class="icon"></span>
        <span class="xmks-card-empty__txt">{{ name }}</span>
    </div>
</template>

<script lang="ts" setup>
/************************变量定义相关***********************/
withDefaults(
    defineProps<{
        name: string; // 名称
        icon: string; // 图标
    }>(),
    {
    }
);
</script>

<style lang="scss" scoped>
.xmks-card-empty {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    min-width: 385px;
    min-height: 220px;
    background: #ffffff;
    border-radius: 15px;
    cursor: pointer;

    &:hover {
        .xmks-card-empty__icon {
            background-color: #B6E1FC;
            border: 1px solid #B6E1FC;
            color: #fff;
        }

        .xmks-card-empty__txt {
            color: #B6E1FC;
        }
    }

    .xmks-card-empty__icon {
        border: 1px dashed #B6E1FC;
        color: #B6E1FC;
        font-size: 34px;
        padding: 40px;
        border-radius: 50%;
        transition: all .3s ease-in-out;
    }

    .xmks-card-empty__txt {
        font-size: 14px;
        color: #999999;
        line-height: 48px;
    }




}
</style>
