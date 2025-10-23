<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="exer-set">
        <xmks-edit-card title="练习" desc="练习">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                    <el-form-item label="题库" prop="questionBankIds">
                        <xmks-select v-model="form.questionBankIds" url="questionBank/listpage" :params="{}"
                            search-parm-name="name" option-value="id" option-label="name" :multiple="true"
                            :page-size="100" search-placeholder="请输入题库名称进行筛选" :options="questionBanks"
                            :disabled="form.id" placeholder="请选择题库">
                            <template #default="{ option }">
                                {{ option.name }} （单选{{ option.singleNum }} / 多选{{ option.multipleNum }} /
                                客观填空{{ option.multipleNum }} / 判断{{ option.judgeNum }} / 客观问答{{
                                    option.qaObjNum }}）
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="机构及用户" prop="orgIds">
                        <xmks-select v-model="form.orgIds" url="org/listpage" :params="{}" search-parm-name="name"
                            option-label="name" option-value="id" :options="orgs" :multiple="true" clearable
                            :page-size="100" search-placeholder="请输入机构名称进行筛选" placeholder="请选择机构">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.orgName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item label="" prop="userIds">
                        <xmks-select v-model="form.userIds" url="user/listpage" :params="{ state: 1, type: 1 }"
                            search-parm-name="name" option-label="name" option-value="id" :options="users"
                            :multiple="true" clearable :page-size="100" search-placeholder="请输入机构名称或用户名称进行筛选"
                            placeholder="请选择用户">
                            <template #default="{ option }">
                                {{ option.name }} - {{ option.orgName }}
                            </template>
                        </xmks-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="$route.path.indexOf('add') !== -1" type="primary" class="form__btn"
                            @click="add">添加</el-button>
                        <el-button v-else type="primary" class="form__btn" @click="edit">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="发布练习" desc="考试期间请暂停练习，以免考试用户可以边考边查询答案。">
            <template #card-main>
                <el-form size="large" class="form">
                    <el-radio-group v-model="form.state">
                        <el-radio v-for="(dict, index) in dictStore.getList('STATE_PS')" :key="index"
                            :value="parseInt(dict.dictKey)">
                            {{ dict.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="state" style="margin-bottom: 30px;">保存设置</el-button>
            </template>
        </xmks-edit-card>
        <xmks-edit-card v-if="form.id" title="删除练习" desc="删除练习">
            <template #card-side>
                <el-button type="primary" class="form__btn" :class="{ 'form__btn--warn': delConfirm }" @click="del"
                    style="margin-bottom: 14px;">删除练习</el-button>
            </template>
        </xmks-edit-card>
        <!-- <xmks-edit-card v-if="form.id" title="允许评论" desc="解题技巧互评，用同伴视角发现解题盲点">
            <template #card-main>
                <el-form size="large" class="form">
                    <el-radio-group v-model="form.rmkState">
                        <el-radio v-for="(dict, index) in dictStore.getList('STATE_YN')" :key="index"
                            :value="parseInt(dict.dictKey)">
                            {{ dict.dictValue }}
                        </el-radio>
                    </el-radio-group>
                </el-form>
            </template>
            <template #card-side>
                <el-button type="primary" class="form__btn" @click="rmk" style="margin-bottom: 30px;">保存设置</el-button>
            </template>
        </xmks-edit-card> -->
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { exerAdd, exerDel, exerEdit, exerGet, exerState } from '@/api/exam/exer'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import xmksSelect from '@/components/xmks-select.vue'
import { useDictStore } from '@/stores/dict'
import type { Exer } from '@/ts/exam/exer'
import { type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/************************变量定义相关***********************/
const route = useRoute()// 路由
const router = useRouter()// 路由
const dictStore = useDictStore()// 字典缓存
const formRef = ref<FormInstance>()// 表单引用
const form = reactive<Exer>({// 表单
    id: null,
    name: '',
    questionBankIds: [],
    userIds: [],
    orgIds: [],
    state: 1,
})
const formRules = reactive<FormRules>({// 表单规则
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 1, max: 16, message: '长度介于1-16', trigger: 'blur' },
    ],
    questionBankIds: [
        { required: true, message: '请选择题库', trigger: 'blur' },
    ],
    userIds: [
        {
            validator: (rule: any, value: number[], callback: any) => {
                if (!form.userIds.length && !form.orgIds?.length) {
                    return callback(new Error("机构或用户最少选一个"))
                }
                return callback()
            }
        },
    ],
    orgIds: [
        { required: false, message: '请选择练习机构', trigger: 'blur' },
    ],
})
const delConfirm = ref(false) // 删除确认
const questionBanks = ref<Record<string, unknown>[]>([]) // 题库列表
const users = ref<Record<string, unknown>[]>([]) // 用户列表
const orgs = ref<Record<string, unknown>[]>([]) // 机构列表

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (route.path.indexOf('add') !== -1) {// 添加

    } else {// 修改
        const { data: { data } } = await exerGet({ id: route.params.id })
        form.id = data.id
        form.name = data.name
        form.questionBankIds = data.questionBanks.map((questionBank: any) => questionBank.id)
        form.userIds = data.users.map((user: any) => user.id)
        form.orgIds = data.orgs.map((org: any) => org.id)
        form.state = data.state

        questionBanks.value = data.questionBanks
        users.value = data.users
        orgs.value = data.orgs
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

// 发布
async function state() {
    const { data: { code } } = await exerState({ id: form.id })
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
