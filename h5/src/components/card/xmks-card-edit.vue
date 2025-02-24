<template>
    <view class="xmks-edit-card">
        <div class="xmks-edit-card__main">
            <div class="card-head">
                <span class="card-head__title">{{ title }}</span>
                <span v-html="desc" class="card-head__desc"></span>
                <slot name="card-head"></slot>
            </div>
            <div class="card-main">
                <slot name="card-main"></slot>
            </div>
        </div>
        <view class="xmks-edit-card__side">
            <slot name="card-side"></slot>
        </view>
    </view>
</template>

<script lang="ts" setup>
/************************变量定义相关***********************/
withDefaults(
    defineProps<{
        title: string; // 标题
        desc?: string; // 描述
    }>(),
    {
        desc: "",
    }
);
</script>

<style lang="scss" scoped>
.xmks-edit-card {
    margin-bottom: 20px;
    display: flex;
    background-color: #F8FAFC;
    border-radius: 15px 15px 15px 15px;

    .xmks-edit-card__main {
        flex: 1;
        display: flex;
        flex-direction: column;
        padding: 30px 30px 20px 30px;

        .card-head {
            flex: 1;
            display: flex;
            align-items: flex-end;
            position: relative;
            // border-bottom: 1px solid #E5E5E5;
            padding: 0px 0px 0px 15px;

            &::after {
                content: "";
                position: absolute;
                left: 5px;
                top: 50%;
                transform: translateY(-50%);
                width: 4px;
                height: 16px;
                border-radius: 2px 2px 2px 2px;
                background: #3AA8EF;
            }

            .card-head__title {
                font-weight: bold;
                font-size: 16px;
                color: #333333;
            }

            .card-head__desc {
                margin-left: 5px;
                margin-right: 30px;
                font-size: 12px;
                color: #999999;
            }
        }

        .card-main {
            flex: 1;
        }
    }

    .xmks-edit-card__side {
        width: 200px;
        padding-right: 30px;
        display: flex;
        justify-content: flex-end;
        align-items: flex-end;
    }
}
</style>
