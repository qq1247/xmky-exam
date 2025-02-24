<template>
    <div class="exam-list">
        <div class="exam-list__head">
            <el-form :model="queryForm" :inline="true" size="large" class="query">
                <el-form-item label="">
                    <el-input v-model="queryForm.name" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.genType" placeholder="组卷方式" clearable size="large">
                        <el-option v-for="dict in dictStore.getList('PAPER_GEN_TYPE')" :key="dict.dictKey"
                            :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                    </el-select>
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.loginType" placeholder="登录方式" clearable size="large"
                        style="width: 200px;">
                        <el-option v-for="dict in dictStore.getList('LOGIN_TYPE')" :key="dict.dictKey"
                            :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                    </el-select>
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.markState" placeholder="阅卷状态" clearable size="large"
                        style="width: 200px;">
                        <el-option v-for="dict in dictStore.getList('MARK_STATE')" :key="dict.dictKey"
                            :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                    </el-select>
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.state" placeholder="发布状态" clearable size="large"
                        style="width: 200px;">
                        <el-option v-for="dict in dictStore.getList('STATE_PS')" :key="dict.dictKey"
                            :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                    </el-select>
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.markType" placeholder="阅卷方式" clearable size="large"
                        style="width: 200px;">
                        <el-option v-for="dict in dictStore.getList('PAPER_MARK_TYPE')" :key="dict.dictKey"
                            :label="dict.dictValue" :value="parseInt(dict.dictKey)" />
                    </el-select>
                </el-form-item>
                <el-form-item label="">
                    <el-select v-model="queryForm.isLimit" placeholder="是否限时" clearable size="large"
                        style="width: 200px;">
                        <el-option label="有" :value="1" />
                        <el-option label="无" :value="0" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" class="query__btn" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="exam-list__main">
            <el-scrollbar max-height="calc(100vh - 360px)">
                <xmks-card-add name="添加考试" @click="$router.push('/exam/add')"></xmks-card-add>
                <xmks-card-data v-for="exam in listpage.list" :key="exam.id" :title="exam.name" tag-name="考试"
                    :btns="btns(exam)" class="exam">
                    <div class="exam__tag-wrap">
                        <span class="exam__tag exam__tag--gen-type">
                            {{ dictStore.getValue("PAPER_GEN_TYPE", exam.genType) }}
                        </span>
                        <span class="exam__tag exam__tag--login-type">
                            {{ dictStore.getValue("LOGIN_TYPE", exam.loginType) }}
                        </span>
                        <span class="exam__tag exam__tag--mark-state">
                            {{ dictStore.getValue("MARK_STATE", exam.markState) }}
                        </span>
                        <span class="exam__tag exam__tag--state">
                            {{ dictStore.getValue("STATE_PS", exam.state) }}
                        </span>
                    </div>
                    <div class="exam__row">
                        <span class="exam__exam-time">
                            考试时间：<span class="exam__time">{{ exam.startTime.substring(0, 16) }} - {{
                                exam.endTime.substring(0, 16)
                            }}</span>
                        </span>
                    </div>
                    <div class="exam__row">
                        <span v-if="exam.markType === 2" class="exam__exam-time">
                            阅卷时间：<span class="exam__time">{{ exam.markStartTime.substring(0, 16) }} - {{
                                exam.markEndTime.substring(0, 16) }}</span>
                        </span>
                        <span v-else class="exam__exam-time">
                            阅卷时间：考试结束自动阅卷
                        </span>
                    </div>
                    <div class="exam__row1">
                        <div class="exam__inner">
                            <span class="exam__value">
                                {{ dictStore.getValue("PAPER_MARK_TYPE", exam.markType) }}<span
                                    class="exam__unit"></span>
                            </span>
                            <span class="exam__lab">试卷</span>
                        </div>
                        <div class="exam__inner">
                            <span class="exam__value">
                                {{ exam.passScore || '-' }}/{{ exam.totalScore }}<span class="exam__unit">分</span>
                            </span>
                            <span class="exam__lab">及格分数</span>
                        </div>
                        <div class="exam__inner">
                            <span v-if="exam.limitMinute" class="exam__value">
                                60<span class="exam__unit">分钟</span>
                            </span>
                            <span v-else class="exam__value">无</span>
                            <span class="exam__lab">限时</span>
                        </div>
                        <div class="exam__inner">
                            <span class="exam__value">
                                {{ exam.userNum }}<span class="exam__unit">人</span>
                            </span>
                            <span class="exam__lab">考试人数</span>
                        </div>
                        <div class="exam__inner">
                            <span class="exam__value">
                                {{ exam.sxes.length > 0 ? '是' : '否' }}<span class="exam__unit"></span>
                            </span>
                            <span class="exam__lab">防作弊</span>
                        </div>
                        <div class="exam__inner">
                            <span class="exam__value">
                                {{ dictStore.getValue('SCORE_STATE', exam.scoreState) }}<span class="exam__unit"></span>
                            </span>
                            <span class="exam__lab">查询成绩</span>
                        </div>
                        <div class="exam__inner">
                            <span class="exam__value">
                                {{ dictStore.getValue('STATE_ON', exam.rankState) }}<span class="exam__unit"></span>
                            </span>
                            <span class="exam__lab">排名</span>
                        </div>
                        <div class="exam__inner">
                            <span v-if="exam.markUserNum" class="exam__value">
                                {{ exam.markUserNum }}<span class="exam__unit">人</span>
                            </span>
                            <span v-else class="exam__value">无</span>
                            <span class="exam__lab">协助阅卷</span>
                        </div>
                    </div>
                </xmks-card-data>
            </el-scrollbar>
        </div>
        <div class="exam-list__foot">
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next" :hide-on-single-page="true" size="large"
                class="pagination" @size-change="query" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>
    </div>
</template>
<script setup lang="ts">
import { reactive, onMounted, computed, } from 'vue'
import type { Listpage } from '@/ts/common/listpage'
import xmksCardData from '@/components/card/xmks-card-data.vue'
import xmksCardAdd from '@/components/card/xmks-card-add.vue'
import { examListpage } from '@/api/exam/exam'
import { useDictStore } from '@/stores/dict'
import { useRouter } from 'vue-router'
import type { CardBtn } from '@/ts/common/card-btn'

/************************变量定义相关***********************/
const router = useRouter();
const dictStore = useDictStore();// 字典缓存
const queryForm = reactive({// 查询表单
    name: '',
    genType: null,
    loginType: null,
    markState: null,
    state: null,
    markType: null,
    isLimit: null,
})
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,
    pageSize: 5,
    total: 0,
    list: [],
})

/************************组件生命周期相关*********************/
onMounted(() => {
    query()
})

/************************计算属性相关*************************/
const btns = computed(() => (exam: any) => {
    const _btns: CardBtn[] = [{
        name: '设置',
        icon: 'icon-liebiao-01',
        event: () => router.push(`/exam/set/${exam.id}`)
    }, {
        name: '统计',
        icon: 'icon-liebiao-02',
        event: () => router.push(`/exam/statis/${exam.id}`)
    }]

    if (exam.markType === 2) {
        _btns.push({
            name: '阅卷',
            icon: 'icon-icon-05',
            event: () => router.push(`/my-mark/read/${exam.id}`)
        })
    }

    return _btns
})

/************************事件相关*****************************/
// 查询
async function query() {
    const { data: { code, data } } = await examListpage({
        ...queryForm,
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
    })

    if (code !== 200) {
        return
    }

    listpage.list = data.list
    listpage.total = data.total
}

</script>
<style lang="scss" scoped>
.exam-list {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;

    .exam-list__head {
        .query {
            .el-form-item {
                width: 126px;

                &:last-child {
                    width: initial;
                    margin-right: 0px;
                }
            }

            .query__btn {
                height: 40px;
                padding: 0px 30px;
                border-radius: 6px;
                border: 0px;
                color: #FFFFFF;
                font-size: 16px;
                background-image: linear-gradient(to right, #04C7F2, #259FF8);
            }
        }
    }

    :deep(.exam-list__main) {
        flex: 1;

        .el-scrollbar__view {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px 20px;


            .exam {
                display: flex;
                flex-direction: column;

                .exam__tag-wrap {
                    display: flex;
                    justify-content: center;
                    margin-top: 15px;

                    .exam__tag {
                        font-size: 12px;
                        padding: 2px 4px;
                        border-radius: 4px 4px 4px 4px;
                        margin-right: 10px;

                        &.exam__tag--gen-type {
                            color: #0D9DF6;
                            background: #E4F6FF;
                            border: 1px solid #A1E0FF;
                        }

                        &.exam__tag--login-type {
                            color: #FC8113;
                            background: #FDEDD9;
                            border: 1px solid #FFC791;
                        }

                        &.exam__tag--mark-state {
                            color: #FE7068;
                            background: #FFE6E6;
                            border: 1px solid #FFCAC7;
                        }

                        &.exam__tag--state {
                            color: #1AC693;
                            background: #E8F9F4;
                            border: 1px solid #AFE7D6;
                        }
                    }

                }

                .exam__row {
                    margin-top: 10px;

                    .exam__exam-time {
                        font-size: 14px;
                        color: #999999;

                        .exam__time {
                            font-size: 14px;
                            color: #333333;
                        }
                    }
                }

                .exam__row1 {
                    display: grid;
                    grid-template-rows: repeat(2, 1fr);
                    grid-template-columns: repeat(4, 1fr);
                    margin-top: 15px;
                    height: 90px;
                    background: #EFF5FA;
                    border-radius: 6px 6px 6px 6px;

                    .exam__inner {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        position: relative;

                        &::after {
                            content: "";
                            position: absolute;
                            display: block;
                            right: 0;
                            width: 1px;
                            height: 33px;
                            background-color: #E5E5E5;
                        }

                        &:last-child {
                            &::after {
                                display: none;
                            }
                        }

                        &:nth-last-child(5) {
                            &::after {
                                display: none;
                            }
                        }


                        .exam__value {
                            font-size: 14px;
                            color: #333333;

                            .exam__unit {
                                font-size: 12px;
                                color: #8F939C;
                            }
                        }

                        .exam__lab {
                            font-size: 12px;
                            color: #8F939C;
                            margin-top: 2px;
                        }
                    }
                }
            }
        }
    }

    .exam-list__foot {
        display: flex;
        justify-content: center;

        :deep(.pagination) {
            margin-top: 10px;

            .btn-prev,
            .btn-next {
                width: 38px;
                height: 38px;
                background-color: #FFFFFF;
                border-radius: 4px 4px 4px 4px;

                .el-icon {
                    font-size: 20px;
                }
            }

            .number {
                background-color: #FFFFFF;
                border-radius: 4px 4px 4px 4px;
            }

            .el-pager {
                li {
                    width: 38px;
                    height: 38px;
                    border-radius: 4px 4px 4px 4px;
                    font-size: 16px;

                    &.is-active {
                        background-image: linear-gradient(to right, #04C7F2, #259FF8);
                    }
                }
            }
        }
    }
}
</style>
