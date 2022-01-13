<!--
 * @Description: 试卷分类设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-04 16:27:19
-->
<template>
  <div class="container">
    <el-form
      :model="examForm"
      :rules="examForm.rules"
      ref="examForm"
      label-width="60px"
    >
      <el-form-item label="名称" prop="examName">
        <el-input
          placeholder="请输入分类名称"
          v-model="examForm.examName"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-button @click="addOrEdit" type="primary">{{
        id ? '修改' : '添加'
      }}</el-button>
    </div>
  </div>
</template>

<script>
import { paperTypeEdit, paperTypeAdd, paperTypeGet } from 'api/paper'

export default {
  data() {
    return {
      id: null,
      examForm: {
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
      const res = await paperTypeGet({ id: this.id })
      this.$nextTick(() => {
        this.examForm.examName = res.data.name
      })
    }
  },
  methods: {
    // 添加 || 修改试卷名称
    addOrEdit() {
      this.$refs['examForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        let res

        if (this.id) {
          res = await paperTypeEdit({
            id: this.id,
            name: this.examForm.examName,
          })
        } else {
          res = await paperTypeAdd({
            name: this.examForm.examName,
          })
        }

        if (res?.code == 200) {
          this.$message.success(!this.edit ? '添加成功！' : '修改成功！')
          this.$router.back()
        } else {
          this.$message.error(!this.id ? '添加失败！' : '修改失败！')
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  padding-left: 60px;
}
</style>
