<template>
    <div class="grid">
        <div class="grid-tag">
            <slot name="tag"></slot>
        </div>
        <div class="grid-title">
            <slot name="title">标题</slot>
        </div>
        <div class="grid-content">
            <slot name="content">
                <div>内容</div>
            </slot>
        </div>
        <div class="grid-btn">
            <template v-if="(menu.length <= 6)">
                <span v-for="i in (menu.length)" :data-name="menu[i - 1].name"
                    @click="menu[i - 1].event">
                    <i :class="`iconfont ${menu[i - 1].icon}`"></i>
                </span>
            </template>
            <template v-else>
                <span v-for="i in 5" :data-name="menu[i - 1].name" @click="menu[i - 1].event">
                    <i :class="`iconfont ${menu[i - 1].icon}`"></i>
                </span>
                <span data-name="更多" class="last-span">
                    <i class="iconfont icon-more-row"></i>
                    <div class="grid-btn-more">
                        <div v-for="i in (menu.length - 5)" class="grid-btn-more-btn"
                            @click="menu[i - 1 + 5].event">
                            <i :class="`iconfont ${menu[i - 1 + 5].icon}`"></i>
                            {{ menu[i - 1 + 5].name }}
                        </div>
                    </div>
                </span>
            </template>
        </div>
    </div>
</template>

<script lang="ts" setup>import type { Menu } from '@/stores/exam';

// 定义变量
const emit = defineEmits(['callback'])
defineProps<{
    menu: Menu[]
}>()

</script>
<style lang="scss" scoped>
.grid {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 0px 20px 20px 0px;
    padding: 30px 30px 20px 30px;
    background-color: white;
    width: 380px;
    height: 200px;
    position: relative;
    .grid-tag {
        position: absolute;
        right: 20px;
        top: 10px;
    }

    .grid-title {
        word-break: keep-all;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        margin: 8px;
        font-weight: bold;
        color: var(--el-text-color-regular);
    }

    .grid-content {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        flex: 1;
        width: 100%;
        text-align: center;
    }

    .grid-btn {
        height: 35px;

        span {
            display: inline-block;
            width: 35px;
            height: 35px;
            line-height: 35px;
            margin: 0px 5px;
            text-align: center;
            border-radius: 50%;
            border: 1px solid #eff3f7;
            transition: all .3s ease-in-out;
            position: relative;
            font-size: 12px;
            color: var(--el-text-color-secondary);
            cursor: pointer;

            &::before {
                content: "";
                display: block;
                position: absolute;
                z-index: 100;
                bottom: -18px;
                left: 50%;
                transform: translateX(-50%);
                border-width: 0 5px 10px 5px;
                border-style: solid;
                border-color: transparent transparent var(--el-color-primary);
                opacity: 0;
            }

            &::after {
                content: attr(data-name);
                display: block;
                position: absolute;
                z-index: 100;
                bottom: -45px;
                transform: translateX(-50%);
                left: 50%;
                width: 80px;
                height: 30px;
                line-height: 30px;
                background: var(--el-color-primary);
                color: white;
                border-radius: 3px;
                font-size: 12px;
                opacity: 0;
            }

            &:hover {
                border: 1px solid var(--el-color-primary);
                background: var(--el-color-primary);
                color: white;

                &::before,
                &::after {
                    opacity: 1;
                }
            }

            .grid-btn-more {
                background: #4a5766;
                width: 120px;
                color: #fff;
                border-radius: 3px;
                font-size: 12px;
                position: absolute;
                left: 60px;
                top: 50%;
                transform: translateY(-50%);
                opacity: 0;
                transition: all .3s ease-in-out;
                z-index: 1;

                &::before {
                    content: "";
                    display: block;
                    position: absolute;
                    z-index: 100;
                    left: -10px;
                    top: 50%;
                    transform: translateY(-50%);
                    border-width: 5px 10px 5px 0;
                    border-style: solid;
                    border-color: transparent #4a5766 transparent transparent;
                }

                .grid-btn-more-btn {
                    padding: 12px 0;
                    border-bottom: 1px solid #65707d;
                    font-size: 12px;
                    line-height: 1;

                    i {
                        padding-right: 5px;
                    }

                    &:hover {
                        background: var(--el-color-primary);
                        color: #fff;
                    }
                }
            }

            &.last-span {

                &:hover {

                    &::after,
                    &::before {
                        display: none;
                    }

                    .grid-btn-more {
                        opacity: 1;
                        position: absolute;
                        left: 50px;
                    }
                }
            }
        }
    }
}
</style>