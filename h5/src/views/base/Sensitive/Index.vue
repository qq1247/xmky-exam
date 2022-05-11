<template>
  <div class="container">
    <div class="param-title">敏感词设置</div>
    <el-form ref="sensitiveForm" :model="sensitiveForm" label-width="150px">
      <el-form-item label="黑名单" prop="orgName">
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
        />
      </el-form-item>
      <el-form-item label="白名单" prop="orgName">
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
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="setting">设置</el-button>
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
        whiteList: []
      }
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
        whiteList: [this.sensitiveForm.whiteList.join('\n')]
      })
      res?.code === 200 &&
        (this.$message.success('设置成功！'), this.getWordList())
    },
    selectChange(event, name) {
      const filterArray = event.filter((item) => item.trim())
      this.sensitiveForm[name] = filterArray
    }
  }
}
</script>

<style lang="scss" scoped>
/deep/ .el-input {
  width: 400px;
}
.container {
  padding: 0 30px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #ececec;
  margin-top: 16px;
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
