<template>
    <div class="xmks-card-add">
        <span class="iconfont icon-tubiaoziti2-02 xmks-card-add__icon"></span>
        <span class="xmks-card-add__txt">{{ name }}</span>
    </div>
</template>

<script lang="ts" setup>
/************************变量定义相关***********************/
withDefaults(
    defineProps<{
        name: string; // 
    }>(),
    {

    }
);
</script>

<style lang="scss" scoped>
.xmks-card-add {
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
        .xmks-card-add__icon {
            background-color: #0D9DF6;
            border: 1px solid #0D9DF6;
            color: #fff;
        }

        .xmks-card-add__txt {
            color: #0D9DF6;
        }
    }

    .xmks-card-add__icon {
        border: 1px dashed #0D9DF6;
        color: #0D9DF6;
        font-size: 32px;
        padding: 40px;
        border-radius: 50%;
        transition: all .3s ease-in-out;
    }

    .xmks-card-add__txt {
        font-size: 14px;
        color: #999999;
        line-height: 48px;
    }
}
</style>
