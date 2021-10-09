<!--
 * @Description: 敏感词库
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-28 10:29:46
 * @LastEditors: Che
 * @LastEditTime: 2021-10-08 15:29:57
-->

<template>
  <div class="container">
    <el-form :model="sensitiveForm" ref="sensitiveForm">
      <el-form-item label="黑名单" label-width="300px" prop="orgName">
        <el-select
          v-model="sensitiveForm.blackList"
          remote
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="请输入敏感词"
          @change="selectChange($event, 'blackList')"
        >
        </el-select>
      </el-form-item>
      <el-form-item label="白名单" label-width="300px" prop="orgName">
        <el-select
          v-model="sensitiveForm.whiteList"
          remote
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="请输入解封词"
          @change="selectChange($event, 'whiteList')"
        >
        </el-select>
      </el-form-item>
      <el-form-item label label-width="300px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { sensitiveEdit, sensitiveGet } from 'api/base'
export default {
  data() {
    return {
      sensitiveForm: {
        id: null,
        blackList: [],
        whiteList: [],
      },
    }
  },
  mounted() {
    this.getWordList()
  },
  methods: {
    async getWordList() {
      const { data } = await sensitiveGet({})
      if (data) {
        this.sensitiveForm.id = data.id || null
        this.sensitiveForm.blackList = data.blackList
          ? data.blackList.split('\n')
          : []
        this.sensitiveForm.whiteList = data.whiteList
          ? data.whiteList.split('\n')
          : []
      }
    },
    async setting() {
      const res = await sensitiveEdit({
        id: this.sensitiveForm.id,
        blackList: [this.sensitiveForm.blackList.join('\n')],
        whiteList: [this.sensitiveForm.whiteList.join('\n')],
      })
      res?.code === 200 &&
        (this.$message.success('设置成功！'), this.getWordList())
    },
    selectChange(event, name) {
      const filterArray = event.filter((item) => item.trim())
      this.sensitiveForm[name] = filterArray
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  align-items: flex-start;
}
/deep/ .el-input {
  width: 400px;
}
</style>
