<template>
    <div class="xmky-comment">
        <el-avatar v-if="userAvatarFileId" :size="38" :src="`${downloadUrl}?id=${userAvatarFileId}`"></el-avatar>
        <span v-else class="iconfont icon-rentouxiang xmky-comment__avatar"></span>
        <div class="xmky-comment__outer">
            <div class="xmky-comment__name">{{ props.userName }}</div>
            <div class="xmky-comment__comment">
                <span>
                    <template v-if="props.replyUserName">回复@<span class="xmky-comment__name">{{ props.replyUserName
                            }}：</span></template>
                    {{ props.content }}
                </span>
            </div>
            <div class="xmky-comment__opt">
                <div class="xmky-comment__time">{{ props.updateTime }}</div>
                <div class="xmky-comment__opt-inner">
                    <div @click="emit('like')" class="xmky-comment__like">
                        <span
                            :class="['iconfont', 'xmky-comment__like-icon', isLike ? 'icon-lianxi-68 xmky-comment__like-icon--active' : 'icon-lianxi-67']"></span>
                        <span class="xmky-comment__like-value">{{ props.likeNum }}</span>
                    </div>
                    <div @click="reply = true" class="xmky-comment__reply">
                        <span class="iconfont icon-lianxi-69 xmky-comment__reply-icon"></span>
                        <span class="xmky-comment__reply-txt">回复</span>
                    </div>
                </div>
            </div>
            <div v-if="reply" class="xmky-comment__my-reply">
                <el-input v-model="replyContent" type="textarea" maxlength="128" style="width: 100%"
                    placeholder="我是这样解题的..." show-word-limit resize="none" :autosize="{ minRows: 3, maxRows: 3 }"
                    class="xmky-comment__textarea" />
                <div class="xmky-comment__inner">
                    <el-button type="primary" @click="emit('reply', replyContent); reply = false; replyContent = ''"
                        class="xmky-comment__btn">分享思路</el-button>
                    <!-- <el-checkbox :true-value="true" :false-value="false" class="xmky-comment__checkbox">匿名</el-checkbox> -->
                </div>
            </div>
            <slot></slot>
        </div>
    </div>
</template>
<script lang="ts" setup>
import http from '@/request';
import { ref } from 'vue';

/************************变量定义相关***********************/
const emit = defineEmits<{// 定义事件
    (e: 'like'): void
    (e: 'reply', content: string): void
}>()
const props = defineProps({
    userName: { type: [String], required: false, default: '' }, // 评论用户姓名
    userAvatarFileId: { type: [Number, null], required: false, default: null }, // 评论用户头像附件ID
    replyUserName: { type: [String, null], required: false, default: '' }, // 回复用户姓名
    replyUserAvatarFileId: { type: [Number, null], required: false, default: null }, // 回复用户头像附件ID
    content: { type: [String], required: false, default: '' }, // 评论
    updateTime: { type: [String], required: false, default: '' }, // 更新时间
    likeNum: { type: [Number, null], required: false, default: 0 }, // 点赞数量
    isLike: { type: [Boolean], required: false, default: false }, // 点赞数量
})

const downloadUrl = `${http.defaults.baseURL}file/download`// 下载地址
const reply = ref(false)
const replyContent = ref('')
</script>
<style lang="scss">
.xmky-comment {
    display: flex;
    margin-top: 20px;

    .xmky-comment__avatar {
        color: #77A2DB;
        font-size: 27px;
        width: 36px;
        height: 36px;
        background-color: #C5DBFF;
        border-radius: 50%;
        display: inline-flex;
        justify-content: center;
        align-items: center;

        &:focus {
            outline: none;
        }
    }

    .xmky-comment__outer {
        flex: 1;
        display: flex;
        flex-direction: column;
        margin-left: 10px;
        border-bottom: 1px solid #E5E5E5;

        .xmky-comment__name {
            font-size: 14px;
            color: #999999;
            line-height: 20px;
        }

        .xmky-comment__comment {
            font-size: 14px;
            color: #333333;
            line-height: 24px;
            margin-top: 4px;
        }

        .xmky-comment__opt {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 4px;
            margin-bottom: 10px;

            .xmky-comment__time {
                font-size: 14px;
                color: #999999;
            }

            .xmky-comment__opt-inner {
                display: flex;

                .xmky-comment__like {
                    display: flex;
                    align-items: center;
                    color: #999999;
                    cursor: pointer;

                    .xmky-comment__like-icon {
                        font-size: 18px;

                        &.xmky-comment__like-icon--active {
                            color: #1EA1EE;
                        }
                    }

                    .xmky-comment__like-value {
                        font-size: 16px;
                        margin-left: 5px;
                    }
                }

                .xmky-comment__reply {
                    display: flex;
                    align-items: center;
                    margin-left: 30px;
                    margin-right: 30px;
                    font-size: 14px;
                    color: #999999;
                    cursor: pointer;

                    .xmky-comment__reply-icon {
                        font-size: 18px;
                    }

                    .xmky-comment__reply-txt {
                        color: #1EA1EE;
                        font-size: 14px;
                        margin-left: 5px;
                    }
                }
            }
        }

        .xmky-comment__my-reply {
            .xmky-comment__textarea {
                .el-textarea__inner {
                    padding: 15px;
                    border-radius: 6px;
                }

                .el-input__count {
                    font-size: 13px;
                }
            }

            .xmky-comment__inner {
                display: flex;
                margin: 15px 0px;

                .xmky-comment__btn {
                    width: 85px;
                    height: 32px;
                    border-radius: 6px;
                    border: 0px;
                    color: #FFFFFF;
                    font-size: 14px;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                }

                .xmky-comment__checkbox {
                    margin-left: 20px;
                }
            }
        }
    }
}
</style>