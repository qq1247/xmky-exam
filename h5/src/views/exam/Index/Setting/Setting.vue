<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 11:08:59
-->
<template>
  <el-form
    :model="examForm"
    :rules="examForm.rules"
    ref="examForm"
    label-width="100px"
  >
    <el-form-item label="名称" prop="examName">
      <el-input
        placeholder="请输入分类名称"
        v-model="examForm.examName"
      ></el-input>
    </el-form-item>
    <el-form-item>
      <el-button @click="addOrEdit" type="primary">{{
        id ? '修改' : '添加'
      }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { examTypeEdit, examTypeAdd, examTypeGet } from 'api/exam'

export default {
  data() {
    return {
      id: null,
      examForm: {
        edit: false,
        examName: '',
        rules: {
          examName: [
            { required: true, message: '请输入分类名称', trigger: 'blur' },
          ],
        },
      },
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await examTypeGet({ id: this.id })
      this.$nextTick(() => {
        this.examForm.examName = res.data.name
      })
    }
  },
  methods: {
    // 添加 || 修改分类名称
    addOrEdit() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.examForm.edit) {
          res = await examTypeEdit({
            id: this.id,
            name: this.examForm.examName,
          })
        } else {
          res = await examTypeAdd({
            name: this.examForm.examName,
          })
        }

        if (res?.code == 200) {
          this.$message.success(!this.id ? '添加成功！' : '修改成功！')
          this.$router.back()
        } else {
          this.$message.error(!this.id ? '添加失败！' : '修改失败！')
        }
      })
    },
  },
}
</script>
