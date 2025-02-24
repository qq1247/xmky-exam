<template>
    <div class="xmks-card">
        <div class="xmks-card__head">
            <span v-if="preTxt" class="xmks-card__pre-txt">{{ preTxt }}</span>
            <span class="xmks-card__head">{{ title }}</span>
            <div v-if="tagName" class="xmks-card__tag">
                <span>{{ tagName }}</span>
            </div>
        </div>
        <div class="xmks-card__main">
            <slot></slot>
        </div>
        <div v-if="btns?.length" class="xmks-card__foot">
            <span v-for="(btn, index) in btns" :key="index" :data-name="btn.name" class="xmks-card__btn"
                @click="btn.event">
                <i :class="`iconfont ${btn.icon} `"></i>
            </span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { CardBtn } from '@/ts/common/card-btn'

/************************变量定义相关***********************/
withDefaults(
    defineProps<{
        preTxt?: string; //前缀文本
        title: string; //标题文本
        tagName?: string; //标签名称
        btns?: CardBtn[];// 按钮组
    }>(),
    {
        preTxt: "",
        tagName: "",
    }
);
</script>

<style lang="scss" scoped>
.xmks-card {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 385px;
    min-height: 220px;
    padding: 30px 20px 20px 20px;
    // margin: 0px 20px 20px 0px;
    background: #ffffff;
    border-radius: 15px;
    cursor: pointer;

    .xmks-card__head {
        display: flex;
        justify-content: center;

        .xmks-card__pre-txt {
            display: inline-block;
            width: 24px;
            height: 18px;
            line-height: 18px;
            border-radius: 4px;
            text-align: center;
            font-size: 14px;
            color: #ffffff;
            margin-right: 10px;
            background: linear-gradient(to right, #04c7f2 0%, #259ff8 100%);
        }

        .xmks-card__head {
            display: inline-block;
            height: 18px;
            line-height: 18px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-size: 16px;
            color: #333333;
        }

        .xmks-card__tag {
            display: inline-block;
            margin-left: 5px;
            padding: 0px 8px;
            font-size: 14px;
            border: 1px solid transparent;
            border-radius: 4px;
            background-clip: padding-box, border-box;
            background-origin: padding-box, border-box;
            height: 18px;
            line-height: 16px;
            background-image: linear-gradient(to bottom, #fff, #fff), linear-gradient(180deg, #259ff8, #04c7f2);
            color: #04c7f2;
        }
    }

    .xmks-card__main {
        flex: 1;
        display: flex;
        flex-direction: column;
    }

    &:hover {
        .xmks-card__foot {
            height: 58px;
            overflow: initial;
            // border-top: 1px solid #E5E5E5;
        }
    }

    .xmks-card__foot {
        position: absolute;
        display: flex;
        justify-content: center;
        align-items: center;
        left: 0px;
        right: 0px;
        bottom: 0px;
        background-color: #FFFFFF;
        height: 0px;
        border-radius: 0px 0px 15px 15px;
        transition: all .1s ease-in-out;
        overflow: hidden;

        .xmks-card__btn {
            display: inline-block;
            width: 38px;
            height: 38px;
            line-height: 38px;
            margin: 0px 10px;
            text-align: center;
            border-radius: 50%;
            border: 1px solid #E5E5E5;
            color: #8F939C;
            position: relative;
            cursor: pointer;
            z-index: 1;

            &::before {
                content: "";
                display: block;
                position: absolute;
                bottom: -10px;
                left: 50%;
                transform: translateX(-50%);
                border-width: 0 5px 10px 5px;
                border-style: solid;
                border-color: transparent transparent #04C7F2;
                opacity: 0;
            }

            &::after {
                content: attr(data-name);
                display: block;
                position: absolute;
                bottom: -45px;
                transform: translateX(-50%);
                left: 50%;
                height: 36px;
                width: 100px;
                line-height: 36px;
                background-image: linear-gradient(to right, #04C7F2, #259FF8);
                color: white;
                border-radius: 6px;
                font-size: 14px;
                opacity: 0;
            }

            &:hover {
                border: 1px solid #04C7F2;
                background-image: linear-gradient(to right, #04C7F2, #259FF8);
                color: white;

                &::before,
                &::after {
                    opacity: 1;
                }
            }
        }
    }
}
</style>
