<template>
    <el-container class="home-container">
        <el-header>
            <Header></Header>
        </el-header>
        <el-main>
            <Main></Main>
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import Header from './Header.vue';
import Main from './Main.vue';
</script>

<style>
/** 定制盒模型 */
* {
    box-sizing: border-box;
}

/** 定制分页 */
.el-pagination.is-background .btn-next,
.el-pagination.is-background .btn-prev,
.el-pagination.is-background .el-pager li {
    background-color: white;
}

.el-pagination.is-background .btn-next.is-disabled,
.el-pagination.is-background .btn-next:disabled,
.el-pagination.is-background .btn-prev.is-disabled,
.el-pagination.is-background .btn-prev:disabled,
.el-pagination.is-background .el-pager li.is-disabled,
.el-pagination.is-background .el-pager li:disabled {
    background-color: white;
}

/** 定制表格 */
.el-table th.el-table__cell {
    background-color: var(--el-border-color);
}

.el-table .cell {
    color: var(--el-text-color-regular);
    font-weight: bold;
}

.el-table td.el-table__cell div {
    font-size: 13px;
    font-weight: bold;
    color: var(--el-text-color-secondary);
}

.el-table td.el-table__cell div .iconfont {
    cursor: pointer;
    margin: 0px 10px;
    font-weight: initial;
}

/** 定制单选 */
.el-radio__input.is-checked .el-radio__inner::after {
    transform: translate(-50%, -50%) scale(1.5);
}

.el-radio__inner::after {
    background-color: var(--el-color-primary);
}

.el-radio__input.is-checked .el-radio__inner {
    background-color: white;
}
</style>

<style lang="scss" scoped>
.home-container {
    height: 100vh;
}

.el-header {
    padding: 0px;
    height: initial;
}

.el-main {
    padding: 0;
    display: flex;
    justify-content: center;
}
</style>