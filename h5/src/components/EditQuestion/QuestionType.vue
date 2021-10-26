<!--
 * @Description: 添加试题
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-10-19 14:22:34
 * @LastEditors: Che
 * @LastEditTime: 2021-10-20 13:52:12
-->
<template>
  <div>
    <div id="question_driver_types">
      <div class="left-top">添加题型</div>
      <div
        :class="[
          'left-center',
          questionType === btn.type
            ? 'left-center left-center-active'
            : 'left-center',
        ]"
        :key="btn.type"
        @click="updateType(btn.type)"
        v-for="btn in typeButtonGroup"
      >
        <img :src="btn.img" />
        {{ btn.name }}
        <img src="../../assets/img/icon/active-icon.png" />
      </div>
    </div>
    <div class="splitLine"></div>
    <div id="question_driver_template">
      <div class="left-bottom" @click="questionTemplate">
        <img src="../../assets/img/icon/template-icon.png" />
        试题模板
      </div>
      <div class="left-bottom" @click="fileForm.show = true">
        <img src="../../assets/img/icon/import-icon.png" />
        试题导入
      </div>
    </div>
    <!-- 上传试题模板 -->
    <el-dialog
      :visible.sync="fileForm.show"
      :show-close="false"
      width="40%"
      title="上传并解析试题"
      :close-on-click-modal="false"
      @close="templateClear('templateUpload')"
    >
      <el-form :model="fileForm" ref="fileForm">
        <Upload
          type="word"
          ref="templateUpload"
          @success="templateSucess"
          @remove="templateRemove"
        />
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button
          @click="questionImport"
          :loading="fileForm.isAnalysis"
          type="primary"
          >{{ percentage ? `解析中 ${percentage}%` : '解析试题' }}</el-button
        >
        <el-button @click="fileForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { questionTemplate, questionImport } from 'api/question'
import { myExamAiProgress } from 'api/my'
import Upload from 'components/Upload'
export default {
  components: { Upload },
  props: {
    questionType: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      percentage: 0,
      typeButtonGroup: [
        // 左侧按钮组1
        {
          type: 1,
          name: '单选题',
          img: require('assets/img/icon/radio-icon.png'),
        },
        {
          type: 2,
          name: '多选题',
          img: require('assets/img/icon/checkbox-icon.png'),
        },
        {
          type: 3,
          name: '填空题',
          img: require('assets/img/icon/blanks-icon.png'),
        },
        {
          type: 4,
          name: '判断题',
          img: require('assets/img/icon/judge-icon.png'),
        },
        {
          type: 5,
          name: '问答题',
          img: require('assets/img/icon/ask-icon.png'),
        },
      ],
      handlerButtonGroup: [
        // 左侧按钮组2
        {
          name: '试题模板',
          img: require('assets/img/icon/template-icon.png'),
        },
        {
          name: '试题导入',
          img: require('assets/img/icon/import-icon.png'),
        },
        /* {
          name: "试题导出",
          img: require("assets/img/icon/export-icon.png")
        },
        {
          name: "清空试题",
          img: require("assets/img/icon/clear-icon.png")
        } */
      ],
      fileForm: {
        show: false,
        questionDocIds: [],
        isAnalysis: false,
      },
    }
  },
  mounted() {},
  methods: {
    // 更新类型
    updateType(value) {
      this.$emit('updateType', value)
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
      if (this.fileForm.questionDocIds.length === 0) {
        this.$message.warning('请选择需解析的文件')
        return
      }
      this.fileForm.isAnalysis = true
      const res = await questionImport({
        fileId: this.fileForm.questionDocIds[0].response.data.fileIds,
        questionTypeId: this.$parent.$parent.queryForm.questionTypeId,
      }).catch(() => {
        this.fileForm.isAnalysis = false
      })
      if (res?.code == 200) {
        this.percentage = 1
        await this.getProgress(res.data)
        if (this.percentage == 100) {
          this.$message.success('解析成功！')
          this.fileForm.isAnalysis = false
          this.fileForm.show = false
          this.templateClear('templateUpload')
          this.$parent.$parent.query()
          this.$tools.delay().then(() => {
            this.percentage = 0
          })
          return
        } else {
          this.fileForm.isAnalysis = false
        }
      } else {
        this.$message.error(res.msg)
      }
    },
    // 获取进度
    async getProgress(id) {
      const percentage = await this.$tools.delay().then(() => {
        return myExamAiProgress({
          id,
        })
      })

      if (percentage.code !== 200 || !percentage?.data?.percent) {
        this.percentage = 0
        return false
      }

      this.percentage = percentage.data.percent

      if (percentage?.data?.percent !== 100) {
        await this.getProgress(id)
      } else {
        return true
      }
    },
    // 上传试题模板成功
    templateSucess(res, file, fileList) {
      this.fileForm.questionDocIds = fileList
    },
    // 上传试题模板失败
    templateClear(ref) {
      this.fileForm.questionDocIds = []
      this.$refs[ref].clear()
    },
    // 删除试题模板
    templateRemove(file, fileList) {
      this.fileForm.questionDocIds = []
    },
  },
}
</script>

<style lang="scss" scoped>
.left-top {
  background: #0095e5;
  width: 100%;
  height: 40px;
  line-height: 40px;
  text-align: left;
  padding-left: 10px;
  font-size: 14px;
  color: #fff;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 100;
}
.left-center {
  width: 110px;
  height: 40px;
  border: 1px solid #eeeeff;
  margin: 7px auto;
  line-height: 40px;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  &:nth-of-type(2) {
    margin-top: 50px;
  }
  &:hover {
    border: 1px solid #0095e5;
    img:last-child {
      display: block;
    }
  }
  img {
    &:first-child {
      width: 22px;
      height: 22px;
      margin: 0 10px;
    }
    &:last-child {
      width: 28px;
      height: 22px;
      position: absolute;
      bottom: -1px;
      right: 0px;
      display: none;
    }
  }
}
.left-center-active {
  border: 1px solid #0095e5;
  img:last-child {
    display: block;
  }
}
.splitLine {
  background: #eee;
  width: 100%;
  height: 1px;
  margin: 16px auto;
}
.left-bottom {
  width: 110px;
  height: 40px;
  background: #eee;
  margin: 7px auto;
  text-align: center;
  line-height: 40px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  &:hover {
    border: 1px solid #0095e5;
    color: #0095e5;
  }
  img {
    width: 16px;
    height: 16px;
    margin-right: 10px;
  }
}
</style>
