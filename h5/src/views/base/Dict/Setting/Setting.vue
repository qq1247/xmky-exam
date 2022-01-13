<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 15:40:19
-->
<template>
  <div class="container">
    <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
      <el-form-item label="索引" label-width="120px" prop="dictIndex">
        <el-input
          placeholder="请输入索引"
          v-model="editForm.dictIndex"
        ></el-input>
      </el-form-item>
      <el-form-item label="键" label-width="120px" prop="dictKey">
        <el-input placeholder="请输入键" v-model="editForm.dictKey"></el-input>
      </el-form-item>
      <el-form-item label="值" label-width="120px" prop="dictValue">
        <el-input
          placeholder="请输入值"
          v-model="editForm.dictValue"
        ></el-input>
      </el-form-item>
      <el-form-item label="排序" label-width="120px" prop="no">
        <el-input-number
          :max="100"
          :min="1"
          v-model.number="editForm.no"
        ></el-input-number>
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-button @click="add" type="primary" v-if="!id">添加</el-button>
      <el-button @click="edit" type="primary" v-if="id">修改</el-button>
    </div>
  </div>
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
            { required: true, message: '请输入排序', trigger: 'blur' },
          ],
          dictKey: [{ required: true, message: '请输入排序', trigger: 'blur' }],
          dictValue: [
            { required: true, message: '请输入排序', trigger: 'blur' },
          ],
          no: [{ required: true, message: '请输入排序', trigger: 'blur' }],
        },
      },
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
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await dictAdd({
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
          no: this.editForm.no,
        })
        if (code != 200) {
          this.$message.error(msg)
          return
        }

        this.$router.back()
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await dictEdit({
          id: this.id,
          dictIndex: this.editForm.dictIndex,
          dictKey: this.editForm.dictKey,
          dictValue: this.editForm.dictValue,
          no: this.editForm.no,
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
