<template>
  <el-form
    ref="examForm"
    :model="examForm"
    :rules="examForm.rules"
    label-width="100px"
  >
    <el-form-item label="名称" prop="examName">
      <el-input v-model="examForm.examName" placeholder="请输入题库名称" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="addOrEdit">{{
        id ? '修改' : '添加'
      }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {
  questionTypeEdit,
  questionTypeAdd,
  questionTypeGet
} from 'api/question'
export default {
  data() {
    return {
      id: null,
      examForm: {
        examName: '',
        rules: {
          examName: [
            { required: true, message: '请输入题库名称', trigger: 'blur' }
          ]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await questionTypeGet({ id: this.id })
      this.$nextTick(() => {
        this.examForm.examName = res.data.name
      })
    }
  },
  methods: {
    // 添加 || 修改分类名称
    addOrEdit() {
      this.$refs['examForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.id) {
          res = await questionTypeEdit({
            id: this.id,
            name: this.examForm.examName
          })
        } else {
          res = await questionTypeAdd({
            name: this.examForm.examName
          })
        }

        if (res?.code === 200) {
          this.$message.success(!this.id ? '添加成功！' : '修改成功！')
          this.$router.back()
        } else {
          this.$message.error(!this.id ? '添加失败！' : '修改失败！')
        }
      })
    }
  }
}
</script>
