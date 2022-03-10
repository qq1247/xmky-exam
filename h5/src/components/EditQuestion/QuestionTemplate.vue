<!--
 * @Description: 试题模板下载、试题上传解析、试题导出
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-12-28 14:46:05
 * @LastEditors: Che
 * @LastEditTime: 2022-01-18 10:57:38
-->
<template>
  <div class="template-content">
    <el-form :model="handlerForm" ref="handlerForm">
      <Upload
        type="word"
        ref="templateUpload"
        @success="templateSuccess"
        @remove="templateRemove"
      >
      </Upload>
    </el-form>
    <div class="tips">按照试题模板格式填写并上传</div>
    <div class="handler-template">
      <div class="template-item" @click="questionTemplate">
        <i class="common common-word-template"></i>
        <p>下载试题模板</p>
      </div>
      <div class="template-item" @click="questionExport">
        <i class="common common-word-template"></i>
        <p>导出试题模板</p>
      </div>
      <div
        v-if="back"
        class="template-item template-item-active"
        @click="showTemplate"
      >
        <i class="common common-view-list"></i>
        <p>返回试题列表</p>
      </div>
    </div>
  </div>
</template>

<script>
import Upload from 'components/Upload'
import { questionTemplate, questionImport } from 'api/question'
import { progressBarGet } from 'api/common'
export default {
  components: {
    Upload,
  },
  props: {
    back: {
      type: Boolean,
      default: true,
    },
    questionTypeId: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      percentage: 0,
      handlerForm: {
        fileShow: false,
        questionDocIds: [],
        isAnalysis: false,
      },
    }
  },
  methods: {
    showTemplate() {
      this.$emit('showTemplate', false)
    },
    // 获取试题模板
    async questionTemplate() {
      const template = await questionTemplate({}, 'blob')
      const blob = new Blob([template], { type: 'application/msword' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = '试题模板.docx'
      a.click()
      window.URL.revokeObjectURL(url)
    },
    // 解析试题
    async questionImport() {
      if (!this.questionTypeId) {
        this.$message.warning('请选择试题分类')
        this.templateClear('templateUpload')
        return
      }
      const res = await questionImport({
        fileId: this.handlerForm.questionDocIds[0].response.data.fileIds,
        questionTypeId: this.questionTypeId,
        state: 1,
      }).catch(() => {
        this.handlerForm.isAnalysis = false
      })
      if (res?.code == 200) {
        await this.getProgress(res.data)
        if (this.percentage === 100) {
          this.$message.success('解析成功！')
          this.templateClear('templateUpload')
          this.$emit('showTemplate', false)
        }
      } else {
        this.$message.error(res.msg)
      }
    },
    // 导出试题
    questionExport() {
      this.$message('暂未开放！')
    },
    // 获取进度
    async getProgress(id) {
      const percentage = await this.$tools
        .delay()
        .then(() => {
          return progressBarGet({
            id,
          })
        })
        .catch((err) => {
          return err.data
        })

      const loading = this.$loading({
        lock: true,
        text: `试题解析进度：${percentage?.data?.percent || 10}%`,
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.88)',
      })

      if (percentage.code !== 200 || !percentage?.data?.percent) {
        this.percentage = 0
        this.templateClear('templateUpload')
        loading.close()
        return false
      }

      this.percentage = percentage.data.percent

      if (percentage?.data?.percent !== 100) {
        await this.getProgress(id)
      } else {
        loading.close()
        return true
      }
    },
    // 上传试题模板成功
    templateSuccess(res, file, fileList) {
      this.handlerForm.questionDocIds = fileList
      this.questionImport()
    },
    // 上传试题模板失败
    templateClear(ref) {
      this.handlerForm.questionDocIds = []
      this.$refs[ref].clear()
    },
    // 删除试题模板
    templateRemove(file, fileList) {
      this.handlerForm.questionDocIds = []
    },
  },
}
</script>

<style lang="scss" scoped>
.template-content {
  padding: 100px 20px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.handler-template {
  display: flex;
  width: 100%;
  margin-top: 50px;
  justify-content: space-evenly;
  .template-item {
    width: 120px;
    height: 120px;
    border: 1px solid #f1f1f1;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;
    cursor: pointer;
    background: #fff;
    box-shadow: 0 30px 13px -27px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease-in-out;
    &:hover {
      box-shadow: 0 30px 13px -20px rgba(0, 0, 0, 0.06);
      color: #0095e5;
      border: 1px solid #0095e5;
    }
  }
  .template-item-active {
    box-shadow: 0 30px 13px -20px rgba(0, 0, 0, 0.06);
    color: #0095e5;
    border: 1px solid #0095e5;
  }
  .common {
    display: inline-block;
    width: 30px;
    height: 30px;
    line-height: 30px;
    font-size: 36px;
    text-align: center;
  }
  p {
    margin-top: 20px;
  }
}

.tips {
  color: #0095e5;
  font-size: 13px;
  margin-top: 3px;
}
</style>
