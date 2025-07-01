<template>
    <div class="exam-user-select">
        <div class="exam-user-select__main">
            <div class="user-select__wrap">
                <div class="user-select">
                    <div class="user-select__tip">
                        <span class="iconfont icon-tubiaoziti-10 user-select__tip-icon"></span>
                        <div class="user-select__tip-wrap">
                            <span class="user-select__tip-title">
                                已选<span class="user-select__tip-num">{{ form.orgIds.length }}</span>机构、
                                已选<span class="user-select__tip-num">{{ form.userIds.length }}</span>用户
                            </span>
                            <span class=" user-select__tip-desc">只能选择可管理的机构或用户</span>
                        </div>
                    </div>
                    <el-form v-if="form.loginType === 1" ref="formRef" :model="form" :rules="formRules" label-width="0">
                        <el-form-item label="" prop="orgIds">
                            <xmks-select v-model="form.orgIds" url="org/listpage" :params="{}" search-parm-name="name"
                                option-label="name" option-value="id" :options="orgs" :multiple="true" clearable
                                :page-size="100" search-placeholder="请输入机构名称进行筛选" placeholder="请输入机构名称进行筛选">
                                <template #default="{ option }">
                                    {{ option.name }} - {{ option.orgName }}
                                </template>
                            </xmks-select>
                        </el-form-item>
                        <!-- state：不查询冻结用户 -->
                        <el-form-item label="" prop="userIds">
                            <xmks-select v-model="form.userIds" url="user/listpage" :params="{ state: 1, type: 1 }"
                                search-parm-name="name" option-label="name" option-value="id" :options="users"
                                :multiple="true" clearable :page-size="100" search-placeholder="请输入机构名称或用户名称进行筛选"
                                placeholder="请输入机构名称或用户名称进行筛选">
                                <template #default="{ option }">
                                    {{ option.name }} - {{ option.orgName }}
                                </template>
                            </xmks-select>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>
        <div class="exam-user-select__side">
            <div class="user-add">
                <span class="user-add__title">尚未添加考试用户？</span>
                <span class="user-add__desc">{{ userStore.type === 0 ? '快去添加吧！' : '请联系管理员' }}</span>
                <el-button v-if="userStore.type === 0" type="" class="user-add__btn" @click="toUserAdd">
                    <span class="iconfont icon-tubiaoziti2-02 user-add__btn-icon"></span>
                    <span class="user-add__btn-txt">添加</span>
                </el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useExamStore } from '@/stores/exam'
import xmksSelect from '@/components/xmks-select.vue'
import { userListpage } from '@/api/base/user'
import { useUserStore } from '@/stores/user'
import { orgListpage } from '@/api/base/org'

/************************变量定义相关***********************/
defineExpose({ next })
const userStore = useUserStore()
const formRef = ref<FormInstance>()// 表单引用
const form = useExamStore()
const formRules = reactive<FormRules>({// 表单校验规则

})

const users = ref<any[]>([]) // 考试用户
const orgs = ref<any[]>([]) // 机构

/************************组件生命周期相关*********************/
onMounted(async () => {
    if (form.userIds.length) {
        let curPage = 1
        const pageSize = 100
        while (true) {
            const { data: { data } } = await userListpage({
                ids: form.userIds.join(),
                curPage: curPage++,
                pageSize: pageSize,
            })

            users.value.push(...data.list)
            if (users.value.length >= data.total) {// 分批次取出
                break
            }
        }
    }
    if (form.orgIds.length) {
        let curPage = 1
        const pageSize = 100
        while (true) {
            const { data: { data } } = await orgListpage({
                ids: form.orgIds.join(),
                curPage: curPage++,
                pageSize: pageSize,
            })

            orgs.value.push(...data.list)
            if (orgs.value.length >= data.total) {// 分批次取出
                break
            }
        }
    }
})

/************************事件相关*****************************/
function toUserAdd() {
    window.open('/base-nav/exam-user-list', '_blank')
}

// 下一步
async function next() {
    if (form.userIds.length === 0 && form.orgIds.length === 0) {
        ElMessage.error(`请选择机构或用户`)
        return false
    }

    return true

    // try {
    //     await formRef.value?.validate()
    //     return true
    // } catch (e) {
    //     ElMessage.error(`部分信息填写错误，请检查`)
    //     return false
    // }
}

</script>

<style lang="scss" scoped>
.exam-user-select {
    flex: 1;
    display: flex;
    background-color: #ffffff;
    border-radius: 15px 15px 15px 15px;
    height: calc(100vh - 240px);

    .exam-user-select__main {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px;
        border-right: 1px dashed #E5E5E5;
        height: initial;

        .user-select__wrap {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            margin: 20px;
            border-radius: 15px 15px 15px 15px;
            transform: translateY(-30px);

            .user-select {
                display: flex;
                flex-direction: column;
                width: 680px;

                .user-select__tip {
                    display: flex;
                    margin-bottom: 20px;

                    .user-select__tip-icon {
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        width: 45px;
                        height: 45px;
                        padding: 5px;
                        font-size: 28px;
                        color: #cccccc;
                        border: 1px dashed #E5E5E5;
                    }

                    .user-select__tip-wrap {
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;
                        margin-left: 20px;

                        .user-select__tip-title {
                            font-size: 14px;
                            color: #000000;

                            .user-select__tip-num {
                                color: #1EA1EE;
                                padding: 0px 4px;
                            }
                        }

                        .user-select__tip-desc {
                            font-size: 14px;
                            color: #999999;
                        }
                    }
                }

            }

        }
    }

    .exam-user-select__side {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 300px;

        .user-add {
            display: flex;
            flex-direction: column;
            align-items: center;
            transform: translateY(-65px);

            .user-add__title {
                font-size: 14px;
                color: #000000;
                line-height: 45px;
            }

            .user-add__desc {
                font-size: 14px;
                color: #000000;
                line-height: 45px;
            }

            .user-add__btn {
                height: 40px;
                margin-top: 20px;
                padding: 0px 30px;
                border-radius: 6px;
                border: 0px;
                background-image: linear-gradient(to right, #04C7F2, #259FF8);

                .user-add__btn-icon {
                    color: #FFFFFF;
                    font-size: 16px;
                    margin-right: 4px;
                }

                .user-add__btn-txt {
                    color: #FFFFFF;
                    font-size: 14px;
                }
            }
        }


    }
}
</style>
