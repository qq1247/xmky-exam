<template>
  <el-form
    ref="editForm"
    :model="editForm"
    :rules="editForm.rules"
    label-width="100px"
  >
    <el-form-item label="名称" prop="name">
      <el-input v-model="editForm.name" placeholder="请输入名称" maxlength="8"/>
    </el-form-item>
    <el-form-item>
      <el-button v-if="!id" type="primary" @click="add">添加</el-button>
      <el-button v-if="id" type="primary" @click="edit">修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {
  questionTypeEdit,
  questionTypeAdd,
  questionTypeGet,
} from 'api/question'
export default {
  data() {
    return {
      id: null,
      editForm: {
        name: '',
        rules: {
          name: [
            { required: true, message: '请输入题库名称', trigger: 'blur' },
          ],
        },
      },
    }
  },
  async mounted() {
    this.id = this.$route.params.questionTypeId
    if (Number(this.id)) {
      const res = await questionTypeGet({ id: this.id })
      this.$nextTick(() => {
        this.editForm.name = res.data.name
      })
    }
  },
  methods: {
    // 题库添加
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        await questionTypeAdd({
          name: this.editForm.name,
        }).then((res) => {
          this.$message.success('添加成功')
          this.$tools.switchTab('QuestionIndexSetting', {
            questionTypeId: res.data,
            tab: '1',
          })
        })
      })
    },
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        await questionTypeEdit({
          id: this.id,
          name: this.editForm.name,
        }).then(() => {
          this.$message.success('修改成功')
        })
      })
    },
  },
}
</script>
