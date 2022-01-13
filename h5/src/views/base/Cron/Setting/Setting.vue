<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 15:50:34
-->
<template>
  <div class="container">
    <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
      <el-form-item label="名称" label-width="120px" prop="name">
        <el-input placeholder="请输入名称" v-model="editForm.name"></el-input>
      </el-form-item>
      <el-form-item label="实现类" label-width="120px" prop="jobClass">
        <el-input
          placeholder="请输入实现类"
          v-model="editForm.jobClass"
        ></el-input>
      </el-form-item>
      <el-form-item label="cron表达式" label-width="120px" prop="cron">
        <el-input
          placeholder="请输入cron表达式"
          v-model="editForm.cron"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-button @click="add" type="primary" v-if="!id">添加</el-button>
      <el-button @click="edit" type="primary" v-if="id">修改</el-button>
    </div>
  </div>
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
            { required: true, message: '请输入实现类', trigger: 'blur' },
          ],
          cron: [{ required: true, message: '请输入表达式', trigger: 'blur' }],
        },
      },
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
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }
        const { code, msg } = await cronAdd({
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron,
        })
        if (code != 200) {
          this.$message.error(msg)
          return
        }

        this.$router.back()
      })
    },
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await cronEdit({
          id: this.id,
          name: this.editForm.name,
          jobClass: this.editForm.jobClass,
          cron: this.editForm.cron,
        })
        if (code != 200) {
          this.$message.error(msg)
          return
        }

        this.$router.back()
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
