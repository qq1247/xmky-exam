<template>
  <el-form
    ref="editForm"
    :model="editForm"
    :rules="editForm.rules"
    label-width="100px"
  >
    <el-form-item label="索引" prop="dictIndex">
      <el-input v-model="editForm.dictIndex" placeholder="请输入索引" />
    </el-form-item>
    <el-form-item label="键" prop="dictKey">
      <el-input v-model="editForm.dictKey" placeholder="请输入键" />
    </el-form-item>
    <el-form-item label="值" prop="dictValue">
      <el-input v-model="editForm.dictValue" placeholder="请输入值" />
    </el-form-item>
    <el-form-item label="排序" prop="no">
      <el-input-number v-model.number="editForm.no" :max="100" :min="1" />
    </el-form-item>
    <el-form-item>
      <el-button v-if="!id" type="primary" @click="add">添加</el-button>
      <el-button v-if="id" type="primary" @click="edit">修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { dictAdd, dictEdit, dictGet } from 'api/base'

export default {
  data() {
    return {
      id: null,
      editForm: {
        dictIndex: null, // 索引
        dictKey: null, // key
        dictValue: null, // 值
        no: null, // 排序
        show: false, // 是否显示页面
        rules: {
          // 校验
          dictIndex: [
            { required: true, message: '请输入排序', trigger: 'blur' }
          ],
          dictKey: [{ required: true, message: '请输入排序', trigger: 'blur' }],
          dictValue: [
            { required: true, message: '请输入排序', trigger: 'blur' }
          ],
          no: [{ required: true, message: '请输入排序', trigger: 'blur' }]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await dictGet({ id: this.id })

      this.$nextTick(() => {
        this.editForm.dictIndex = res.data.dictIndex
        this.editForm.dictKey = res.data.dictKey
        this.editForm.dictValue = res.data.dictValue
        this.editForm.no = res.data.no
      })
    }
  },
  methods: {
    // 添加
    add() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await dictAdd({
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
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

        const { code, msg } = await dictEdit({
          id: this.id,
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
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
