<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="exer-set">
        <xmks-edit-card title="练习" desc="练习">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item label="题库" prop="questionBankId">
                        <xmks-select v-model="form.questionBankId" url="questionBank/listpage" :params="{}"
                            search-parm-name="name" option-value="id" option-label="name" :multiple="false"
                            search-placeholder="请输入题库名称进行筛选" :options="questionBanks" placeholder="请选择题库"
                            class="paper-question__question-bank">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.questionNum }}题
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="练习时间">
                        <el-form-item label="" prop="startTime">
                            <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
                                placeholder="请选择开始时间" />
                        </el-form-item>
                        <div class="form__divide">-</div>
                        <el-form-item label="" prop="endTime">
                            <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
                                placeholder="请选择结束时间" />
                        </el-form-item>
                    </el-form-item>
                    <el-form-item label="考试用户" prop="userIds">
                        <xmks-select v-model="form.userIds" url="user/listpage" :params="{}" search-parm-name="name"
                            option-value="id" option-label="name" :multiple="true" search-placeholder="请输入机构名称或用户名称进行筛选"
                            :options="users" placeholder="请选择考试用户" class="paper-question__question-bank">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.questionNum }}题
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <!-- <el-form-item label="允许评论" prop="rmkState">
                        <el-checkbox v-model="form.rmkState" :true-label="1" :false-label="2">是</el-checkbox>
                    </el-form-item> -->
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除练习" desc="删除练习">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除练习</el-button>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { userListpage } from '@/api/base/user'
import { exerAdd, exerDel, exerEdit, exerGet } from '@/api/exam/exer'
import { questionBankListpage } from '@/api/exam/question-bank'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import xmksSelect from '@/components/xmks-select.vue'
import type { Exer } from '@/ts/exam/exer'
import { type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Exer>({
    id: null,
    name: '',
    startTime: '',
    endTime: '',
    questionBankId: undefined,
    userIds: [],
    rmkState: 1,
})

const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    questionBankId: [
        { required: true, message: '请选择题库', trigger: 'blur' },
    ],
    startTime: [
        { required: true, message: '请选择开始时间', trigger: 'blur' },
    ],
    endTime: [
        { required: true, message: '请选择结束时间', trigger: 'blur' },
        {
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (form.startTime > form.endTime) {
                    return callback(new Error('不能大于开始时间'))
                }
                return callback()
            }
        }
    ],
    userIds: [
        { required: true, message: '请选择考试用户', trigger: 'blur' },
    ],
})
const questionBanks = ref<any[]>([]) // 题库列表
const users = ref<any[]>([]) // 用户列表
const delConfirm = ref(false) // 删除确认

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await exerGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.questionBankId = data.questionBankId
        form.startTime = data.startTime
        form.endTime = data.endTime
        form.userIds = data.userIds
        form.rmkState = data.rmkState

        const { data: { data: data2 } } = await questionBankListpage({
            id: form.questionBankId,
            curPage: 1,
            pageSize: 1,
        })
        questionBanks.value.push(...data2.list)

        if (form.userIds.length) {
            let curPage = 1
            const pageSize = 100
            while (true) {
                const { data: { data } } = await userListpage({
                    ids: form.userIds.join(),
                    //state: 1, 只查询1就丢了2的数据
                    curPage: curPage++,
                    pageSize: pageSize,
                })

                users.value.push(...data.list)
                if (users.value.length >= data.total) {
                    break
                }
            }
        }
    }
})

/************************事件相关*****************************/
// 添加
async function add() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 添加
    const { data: { code } } = await exerAdd({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/exer-list")
}

// 修改
async function edit() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 修改
    const { data: { code } } = await exerEdit({ ...form })
    if (code !== 200) {
        return
    }

    router.push("/exer-list")
}

// 删除
async function del() {
    if (!delConfirm.value) {
        delConfirm.value = true
        return
    }

    const { data: { code } } = await exerDel({ id: form.id })
    if (code !== 200) {
        return
    }

    router.push("/exer-list")
}

</script>

<style lang="scss" scoped>
.exer-set {
    display: flex;
    flex-direction: column;
    width: 1200px;
    background-color: #FFFFFF;
    margin: 20px 0px;
    padding: 40px 30px 0px 30px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;

        .form__divide {
            display: flex;
            justify-content: center;
            width: 50px;
        }
    }

    .form__btn {
        height: 38px;
        padding: 0px 30px;
        border-radius: 6px;
        border: 0px;
        color: #FFFFFF;
        font-size: 14px;
        background-image: linear-gradient(to right, #04C7F2, #259FF8);
    }

    .form__btn--secondary {
        color: #04C7F2;
        border: 1px solid #04C7F2;
        background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
    }

    .form__btn--warn {
        color: #FF5D15;
        border: 1px solid #FF5D15;
        background-image: linear-gradient(to right, #FFFFFF, #FFFFFF);
    }
}
</style>
