<template>
  <el-form
    ref="editForm"
    :model="editForm"
    :rules="editForm.rules"
    label-width="100px"
  >
    <el-form-item label="所属机构">
      <el-input v-model="editForm.parentName" disabled placeholder />
    </el-form-item>
    <el-form-item label="名称" prop="name">
      <el-input v-model="editForm.name" placeholder="请输入名称" />
    </el-form-item>
    <el-form-item label="排序" prop="no">
      <el-input-number v-model.number="editForm.no" :max="100" :min="1" />
    </el-form-item>
    <el-form-item>
      <el-button
        v-if="editForm.parentId"
        type="primary"
        @click="add"
      >添加</el-button>
      <el-button
        v-if="!editForm.parentId"
        type="primary"
        @click="edit"
      >修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { orgAdd, orgEdit, orgGet } from 'api/base'

export default {
  data() {
    return {
      id: null,
      // 修改表单
      editForm: {
        name: null, // 名称
        no: null, // 排序
        show: false, // 是否显示页面
        parentId: null, // 父ID
        parentName: null, // 父名称
        rules: {
          // 校验
          name: [{ required: true, message: '请输入名称', trigger: 'change' }],
          no: [{ required: true, message: '请输入排序', trigger: 'change' }]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    this.editForm.parentId = this.$route.params.parentId
    if (!this.editForm.parentId) {
      const res = await orgGet({ id: this.id })
      this.editForm.name = res.data.name
      this.editForm.parentName = res.data.parentName
      this.editForm.no = res.data.no
      if (res.code !== 200) {
        this.$message.error(res.msg)
        return
      }
    }
  },
  methods: {
    // 添加 || 修改分类名称
    add() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await orgAdd({
          name: this.editForm.name,
          parentId: this.editForm.parentId,
          no: this.editForm.no
        })

        if (code !== 200) {
          this.$message.error(msg)
          return
        }

        this.$router.back()
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await orgEdit({
          id: this.id,
          name: this.editForm.name,
          no: this.editForm.no
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
