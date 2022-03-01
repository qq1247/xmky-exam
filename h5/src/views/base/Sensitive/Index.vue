<!--
 * @Description: 敏感词库
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-28 10:29:46
 * @LastEditors: Che
 * @LastEditTime: 2021-11-03 16:14:54
-->

<template>
  <div class="container param-container">
    <div class="param-option">
      <div class="param-title">敏感词设置</div>
      <el-form :model="sensitiveForm" ref="sensitiveForm">
        <el-form-item label="黑名单" label-width="150px" prop="orgName">
          <el-select
            v-model="sensitiveForm.blackList"
            remote
            multiple
            clearable
            filterable
            allow-create
            default-first-option
            placeholder="请输入敏感词"
            @change="selectChange($event, 'blackList')"
          >
          </el-select>
        </el-form-item>
        <el-form-item label="白名单" label-width="150px" prop="orgName">
          <el-select
            v-model="sensitiveForm.whiteList"
            remote
            multiple
            clearable
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
/deep/ .el-input {
  width: 400px;
}
.param-container {
  display: flex;
  align-items: flex-start;
  .param-option {
    width: 1024px;
    margin: 0 auto 20px;
    padding: 0 30px;
    background: #fff;
    border-radius: 10px;
    border: 1px solid #ececec;
  }
  .param-title {
    width: 100%;
    height: 50px;
    line-height: 50px;
    font-weight: 600;
    border-bottom: 1px solid #ececec;
    margin-bottom: 20px;
  }
}
</style>
