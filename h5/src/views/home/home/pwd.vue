<template>
    <el-scrollbar max-height="calc(100vh - 190px)" class="pwd">
        <xmks-edit-card title="修改密码" desc="修改密码">
            <template #card-main>
                <el-form ref="formRef" :model="form" :rules="formRules" label-width="100" size="large" class="form">
                    <el-form-item label="旧密码" prop="oldPwd">
                        <el-input v-model.trim="form.oldPwd" type="password" placeholder="请输入旧密码" />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPwd">
                        <el-input v-model.trim="form.newPwd" type="password" placeholder="请输入新密码" />
                    </el-form-item>
                    <el-form-item label="再次确认" prop="newPwd2">
                        <el-input v-model.trim="form.newPwd2" type="password" placeholder="请输入新密码" />
                    </el-form-item>
                    <el-form-item>
                        <el-button class="form__btn" @click="pwd">修改</el-button>
                    </el-form-item>
                </el-form>
            </template>
        </xmks-edit-card>
    </el-scrollbar>
</template>

<script lang="ts" setup>
import { loginPwd } from '@/api/login'
import XmksEditCard from '@/components/card/xmks-card-edit.vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

/************************变量定义相关***********************/
const formRef = ref<FormInstance>()// 表单引用
const form = reactive({
    oldPwd: '',
    newPwd: '',
    newPwd2: '',
})
const formRules = reactive<FormRules>({// 表单规则
    oldPwd: [
        { required: true, message: '请输入旧密码', trigger: 'blur' },
    ],
    newPwd: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度介于6-20', trigger: 'blur' },
    ],
    newPwd2: [{
        trigger: 'blur',
        validator: (rule: any, value: any, callback: any) => {
            if (!value) {
                return callback(new Error('请输入新密码'))
            }
            if (form.newPwd != form.newPwd2) {
                return callback(new Error('两次密码不一致'))
            }
            return callback()
        }
    }],
})

/************************组件生命周期相关*********************/
onMounted(async () => {
})

/************************事件相关*****************************/
// 密码修改
async function pwd() {
    // 数据校验
    try {
        await formRef.value?.validate()
    } catch (e) {
        return
    }

    // 密码修改
    const { data: { code } } = await loginPwd({ ...form })
    if (code !== 200) {
        return
    }
    ElMessage.success('修改成功')
}
</script>

<style lang="scss" scoped>
.pwd {
    display: flex;
    flex-direction: column;
    width: 1200px;
    margin: 20px 0px;
    padding: 0px 0px 0px 0px;
    border-radius: 15px 15px 15px 15px;

    .form {
        margin-top: 20px;
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
