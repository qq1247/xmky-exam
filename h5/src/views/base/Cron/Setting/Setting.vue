<template>
  <el-form
    ref="editForm"
    :model="editForm"
    :rules="editForm.rules"
    label-width="100px"
  >
    <el-form-item label="名称" prop="name">
      <el-input v-model="editForm.name" placeholder="请输入名称" />
    </el-form-item>
    <el-form-item label="实现类" prop="jobClass">
      <el-input v-model="editForm.jobClass" placeholder="请输入实现类" />
    </el-form-item>
    <el-form-item label="cron表达式" prop="cron">
      <el-input v-model="editForm.cron" placeholder="请输入cron表达式" />
    </el-form-item>
    <el-form-item>
      <el-button v-if="!id" type="primary" @click="add">添加</el-button>
      <el-button v-if="id" type="primary" @click="edit">修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { cronAdd, cronEdit, cronGet } from 'api/base'

export default {
  data() {
    return {
      id: null,
      editForm: {
        // 修改表单
        id: null, // 主键
        name: null, // 名称
        jobClass: null, // 实现类
        cron: null, // 表达式
        show: false, // 是否显示页面
        rules: {
          // 校验
          name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
          jobClass: [
            { required: true, message: '请输入实现类', trigger: 'blur' }
          ],
          cron: [{ required: true, message: '请输入表达式', trigger: 'blur' }]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await cronGet({ id: this.id })
      this.$nextTick(() => {
        this.id = res.data.id
        this.editForm.name = res.data.name
        this.editForm.jobClass = res.data.jobClass
        this.editForm.cron = res.data.cron
      })
    }
  },
  methods: {
    add() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }
        const { code, msg } = await cronAdd({
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron
        })
        if (code !== 200) {
          this.$message.error(msg)
          return
        }

        this.$router.back()
      })
    },
    edit() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await cronEdit({
          id: this.id,
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron
        })
        if (code !== 200) {
          this.$message.error(msg)
          return
        }

        this.$router.back()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.form-footer {
  padding-left: 60px;
}
</style>
