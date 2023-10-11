<template>
    <div class="grid">
        <span :class="`iconfont ${ props.icon }`"></span>
        <div class="grid-content">{{ name }}</div>
    </div>
</template>

<script lang="ts" setup>
// 定义变量
const props = withDefaults(defineProps<{
    name: string,
    icon?: string,
}>(), {
    icon: 'icon-plus',
})

</script>
<style lang="scss" scoped>
.grid {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0px 20px 20px 0px;
    padding: 40px 30px 20px 30px;
    background-color: white;
    width: 380px;
    height: 200px;
    cursor: pointer;

    &:hover {
        color: (--el-color-primary);

        .iconfont {
            background-color: var(--el-color-primary);
            border: 1px solid var(--el-color-primary);
            color: white;
        }
        .grid-content {
            color: var(--el-color-primary);
        }
    }

    .iconfont {
        display: inline-block;
        width: 100px;
        height: 100px;
        line-height: 100px;
        text-align: center;
        border-radius: 50%;
        border: 1px solid var(--el-text-color-secondary);
        font-size: 45px;
        color: var(--el-text-color-regular);
        margin-bottom: 10px;
        transition: all .3s ease-in-out;
    }

    .grid-content {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        text-align: center;
    }

}
</style>