<template>
    <div class="exam-publish">
        <div class="exam-publish__head">
            <span class="exam-name">{{ form.name }}</span>
        </div>
        <el-scrollbar height="calc(100vh - 364px)" class="exam-publish__mian">
            <div class="exam-info">
                <div class="exam-info__illust">
                    <span class="exam-info__illust-title">考试概览</span>
                    <span class="exam-info__illust-desc">♦ 抢分冲刺 | 绝地反击</span>
                </div>
                <div class="exam-info__outer">
                    <div class="exam-info__inner">
                        <div class="exam-info__tag">
                            <span class="iconfont icon-fabu-10 exam-info__tag-icon"></span>
                            <span class="exam-info__tag-txt">{{ dictStore.getValue('LOGIN_TYPE', form.loginType)
                                }}</span>
                        </div>
                        <div class="exam-info__tag">
                            <span class="iconfont icon-fabu-11 exam-info__tag-icon"></span>
                            <span class="exam-info__tag-txt">
                                {{ dictStore.getValue('PAPER_GEN_TYPE', form.genType) }}
                            </span>
                        </div>
                        <div class="exam-info__tag">
                            <span class="iconfont icon-fabu-12 exam-info__tag-icon"></span>
                            <span class="exam-info__tag-txt">
                                {{ dictStore.getValue('PAPER_MARK_TYPE', form.markType) }}试卷
                            </span>
                        </div>
                    </div>
                    <div>
                        <span class="exam-info__lab">防止作弊：
                            <span class="exam-info__value">
                                {{ form.sxes.length ? '' : '无' }}
                                {{ form.sxes.indexOf(1) !== -1 ? '试题乱序' : '' }}
                                {{ form.sxes.indexOf(2) !== -1 ? '选项乱序' : '' }}
                            </span>
                        </span>
                    </div>
                    <div class="exam-info__row">
                        <span class="exam-info__lab">考试时间：
                            <span class="exam-info__value">
                                {{ form.examTimes[0].substring(0, 16) }} -
                                {{ form.examTimes[1].substring(0, 16) }}
                            </span>
                        </span>
                        <span class="exam-info__lab" v-if="form.markType === 2">阅卷时间：
                            <span class="exam-info__value">
                                {{ form.markTimes[0].substring(0, 16) }} -
                                {{ form.markTimes[1].substring(0, 16) }}
                            </span>
                        </span>
                    </div>
                    <div class="exam-info__row1">
                        <div class="exam-info__column">
                            <span class="exam-info__lab">及格分数</span>
                            <span class="exam-info__value">
                                {{ form.passScore }} / {{ form.totalScore }}分
                            </span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">限时答题</span>
                            <span class="exam-info__value">
                                {{ form.limitMinute === 0 ? '无' : `${form.limitMinute}分钟` }}
                            </span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">考试人数</span>
                            <span class="exam-info__value">{{ form.examUserNum }}人</span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">协助阅卷</span>
                            <span class="exam-info__value">{{ form.markUserNum }}人</span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">公布成绩</span>
                            <span class="exam-info__value">
                                {{ dictStore.getValue('SCORE_STATE', form.scoreState) }}
                            </span>
                        </div>
                        <div class="exam-info__column">
                            <span class="exam-info__lab">公布排名</span>
                            <span class="exam-info__value">
                                {{ dictStore.getValue('STATE_ON', form.rankState) }}
                            </span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="exam-statis">
                <span class="exam-statis__title">试题数量：</span>
                <div class="exam-statis__inner">
                    <span class="exam-statis__lab">客观题：</span>
                    <span class="exam-statis__value">{{ form.objectiveQuestionNum }} 道</span>
                    <span class="exam-statis__lab">主观题： </span>
                    <span class="exam-statis__value">{{ form.subjectiveQuestionNum }} 道 </span>
                    <span class="exam-statis__lab">章节：</span>
                    <span class="exam-statis__value">{{ form.chapterNum }} 章</span>
                </div>
                <div class="exam-card__wrap">
                    <div class="exam-card exam-card--single-choice">
                        <span class="exam-card__title">单选题</span>
                        <span class="exam-card__num">{{ form.singleChoiceNum }}<span
                                class="exam-card__unit">道</span></span>
                    </div>
                    <div class="exam-card exam-card--multiple-choice">
                        <span class="exam-card__title">多选题</span>
                        <span class="exam-card__num">{{ form.multipleChoiceNum }}<span
                                class="exam-card__unit">道</span></span>
                    </div>
                    <div class="exam-card exam-card--fill-blank">
                        <span class="exam-card__title">填空题</span>
                        <span class="exam-card__num">{{ form.fillblankNum }}<span
                                class="exam-card__unit">道</span></span>
                    </div>
                    <div class="exam-card exam-card--judge">
                        <span class="exam-card__title">判断题</span>
                        <span class="exam-card__num">{{ form.judgeNum }}<span class="exam-card__unit">道</span></span>
                    </div>
                    <div class="exam-card exam-card--qa">
                        <span class="exam-card__title">问答题</span>
                        <span class="exam-card__num">{{ form.qaNum }}<span class="exam-card__unit">道</span></span>
                    </div>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>
<script lang="ts" setup>
import { useDictStore } from '@/stores/dict'
import { useExamStore } from '@/stores/exam'

/************************变量定义相关***********************/
const dictStore = useDictStore() // 字典缓存
const form = useExamStore() // 表单

</script>
<style scoped lang="scss">
.exam-publish {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 30px;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;

    .exam-publish__head {
        margin-top: 30px;

        .exam-name {
            font-size: 20px;
            color: #333333;
            line-height: 45px;
        }
    }

    .exam-publish__mian {
        .exam-info {
            display: flex;
            padding: 20px 0px 30px 0px;
            border-top: 1px solid #F2F2F2;
            border-bottom: 1px solid #F2F2F2;

            .exam-info__illust {
                display: flex;
                flex-direction: column;
                width: 315px;
                height: 182px;
                padding: 60px 0px 0px 30px;
                border-radius: 15px;
                background: url('/img/exam/step/exam-publish/overview-bg.jpg') no-repeat;

                .exam-info__illust-title {
                    font-size: 32px;
                    font-weight: bold;
                }

                .exam-info__illust-desc {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    width: 130px;
                    padding: 2px;
                    margin-top: 10px;
                    border-radius: 4px;
                    font-size: 11px;
                    background: linear-gradient(to right, #D1F9FA, #3EDAFA);
                }

            }

            .exam-info__outer {
                flex: 1;
                margin-left: 20px;

                .exam-info__inner {
                    display: flex;
                    margin-bottom: 15px;

                    .exam-info__tag {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        position: relative;
                        margin-right: 25px;
                        width: 124px;
                        height: 38px;
                        padding: 0px 12px;
                        background: #D4EEFE;
                        border-radius: 6px 6px 6px 6px;
                        border: 1px solid #B8E5FF;

                        &::before {
                            content: '';
                            position: absolute;
                            left: 45px;
                            width: 2px;
                            height: 25px;
                            background-color: #B8E5FF;
                        }

                        .exam-info__tag-icon {
                            font-size: 20px;
                            color: #1EA1EE;
                        }

                        .exam-info__tag-txt {
                            font-size: 12px;
                            color: #1EA1EE;
                            line-height: 15px;
                        }
                    }

                }

                .exam-info__lab {
                    font-size: 14px;
                    color: #999999;
                    line-height: 30px;

                }

                .exam-info__value {
                    font-size: 14px;
                    color: #333333;
                }

                .exam-info__row {
                    display: flex;
                    justify-content: space-between;
                    padding-right: 36px;
                }

                .exam-info__row1 {
                    display: grid;
                    grid-template-columns: repeat(6, 1fr);
                    margin-top: 10px;

                    .exam-info__column {
                        display: flex;
                        position: relative;
                        flex-direction: column;
                        align-items: center;

                        &::before {
                            content: '';
                            position: absolute;
                            left: 0px;
                            top: 50%;
                            transform: translateY(-50%);
                            width: 1px;
                            height: 46px;
                            background-color: #E5E5E5;
                        }

                        &:last-child {
                            &::after {
                                content: '';
                                position: absolute;
                                right: 0px;
                                top: 50%;
                                transform: translateY(-50%);
                                width: 1px;
                                height: 46px;
                                background-color: #E5E5E5;
                            }
                        }
                    }
                }
            }
        }

        .exam-statis {
            display: flex;
            flex-direction: column;
            padding-top: 20px;

            .exam-statis__title {
                font-size: 16px;
                color: #333333;
                line-height: 45px;
            }


            .exam-statis__inner {
                .exam-statis__lab {
                    font-size: 14px;
                    color: #8F939C;
                    line-height: 36px;
                }

                .exam-statis__value {
                    margin-right: 40px;
                    font-size: 14px;
                    color: #139FF6;
                    line-height: 36px;
                }
            }

            .exam-card__wrap {
                display: grid;
                grid-template-columns: repeat(5, 1fr);
                gap: 20px 20px;
                margin-top: 30px;

                .exam-card {
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    position: relative;
                    height: 120px;
                    background: url('/img/exam/step/exam-publish/statis-bg.png') no-repeat;
                    background-position: left bottom;
                    padding-left: 46px;
                    border-radius: 15px 15px 15px 15px;

                    &::before {
                        content: '';
                        position: absolute;
                        left: 46px;
                        top: 30px;
                        width: 18px;
                        height: 15px;
                        border-radius: 2px 2px 2px 2px;
                    }

                    .exam-card__title {
                        margin-left: 40px;
                        font-size: 16px;
                        color: #8F939C;
                        line-height: 38px;
                    }

                    .exam-card__num {
                        margin-left: 40px;
                        font-weight: bold;
                        font-size: 24px;
                        color: #333333;
                        line-height: 38px;
                    }

                    .exam-card__unit {
                        font-weight: initial;
                        font-size: 14px;
                        color: #333333;
                        line-height: 38px;
                    }

                    &.exam-card--single-choice {
                        background-color: #D4EEFE;

                        &::before {
                            background-color: #139FF6;
                        }
                    }

                    &.exam-card--multiple-choice {
                        background-color: #C9EEF6;

                        &::before {
                            background-color: #06BCE3;
                        }
                    }

                    &.exam-card--fill-blank {
                        background-color: #D7D4ED;

                        &::before {
                            background-color: #978CEC;
                        }
                    }

                    &.exam-card--judge {
                        background-color: #EFDCF3;

                        &::before {
                            background-color: #DD8CEC;
                        }
                    }

                    &.exam-card--qa {
                        background-color: #F7E2D2;

                        &::before {
                            background-color: #F9B08C;
                        }
                    }
                }
            }
        }
    }

}
</style>