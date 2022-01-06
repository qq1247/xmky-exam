<!--
 * @Description: 添加试题
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-19 14:22:34
 * @LastEditors: Che
 * @LastEditTime: 2021-11-11 18:06:49
-->
<template>
  <div>
    <div id="question_driver_types">
      <div class="left-top">添加题型</div>
      <div
        :class="[
          'type-btn',
          questionType === btn.type ? 'type-btn type-btn-active' : 'type-btn',
        ]"
        :key="btn.type"
        @click="updateType(btn.type)"
        v-for="btn in typeButtons"
      >
        <i :class="btn.icon"></i>
        {{ btn.name }}
        <i class="common common-subscript sub-script"></i>
      </div>
    </div>
    <div class="splitLine"></div>
    <div
      id="question_driver_template"
      class="handler-btn"
      :key="handler.type"
      @click="otherHandler(handler.type)"
      v-for="handler in handlerButtons"
    >
      <i :class="handler.icon"></i>
      {{ handler.name }}
    </div>
    <!-- 上传试题模板 -->
    <el-dialog
      :visible.sync="handlerForm.fileShow"
      :show-close="false"
      width="40%"
      title="上传并解析试题"
      :close-on-click-modal="false"
      @close="templateClear('templateUpload')"
    >
      <el-form :model="handlerForm" ref="handlerForm">
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
          :loading="handlerForm.isAnalysis"
          type="primary"
          >{{ percentage ? `解析中 ${percentage}%` : '解析试题' }}</el-button
        >
        <el-button @click="handlerForm.fileShow = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { questionTemplate, questionImport, questionPublish } from 'api/question'
import { progressBarGet } from 'api/common'
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
      typeButtons: [
        // 左侧按钮组1
        {
          type: 1,
          name: '单选题',
          icon: 'common common-radio',
        },
        {
          type: 2,
          name: '多选题',
          icon: 'common common-checkbox',
        },
        {
          type: 3,
          name: '填空题',
          icon: 'common common-cloze',
        },
        {
          type: 4,
          name: '判断题',
          icon: 'common common-judge',
        },
        {
          type: 5,
          name: '问答题',
          icon: 'common common-ask',
        },
      ],
      handlerButtons: [
        // 左侧按钮组2
        {
          type: 1,
          name: '试题模板',
          icon: 'common common-template-down',
        },
        {
          type: 2,
          name: '试题导入',
          icon: 'common common-template-up',
        },
        {
          type: 3,
          name: '一键发布',
          icon: 'common common-publish',
        },
        /* {
          name: "清空试题",
          img: require("assets/img/icon/clear-icon.png")
        } */
      ],
      handlerForm: {
        fileShow: false,
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
      if (this.handlerForm.questionDocIds.length === 0) {
        this.$message.warning('请选择需解析的文件')
        return
      }
      this.handlerForm.isAnalysis = true
      const res = await questionImport({
        fileId: this.handlerForm.questionDocIds[0].response.data.fileIds,
        questionTypeId: this.$parent.$parent.queryForm.questionTypeId,
      }).catch(() => {
        this.handlerForm.isAnalysis = false
      })
      if (res?.code == 200) {
        this.percentage = 1
        await this.getProgress(res.data)
        if (this.percentage === 100) {
          this.$message.success('解析成功！')
          this.handlerForm.isAnalysis = false
          this.handlerForm.fileShow = false
          this.templateClear('templateUpload')
          this.$parent.$parent.query()
          this.$tools.delay().then(() => {
            this.percentage = 0
          })
        } else {
          this.handlerForm.isAnalysis = false
        }
      } else {
        this.$message.error(res.msg)
      }
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
      this.handlerForm.questionDocIds = fileList
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
    otherHandler(type) {
      switch (type) {
        case 1:
          this.questionTemplate()
          break
        case 2:
          this.handlerForm.fileShow = true
          break
        case 3:
          const parentData = this.$parent.$parent.$data
          if (!['', '2'].includes(parentData.queryForm.state)) {
            this.$message.warning('请选择草稿状态再发布！')
            return
          }
          this.$confirm('请选择发布方式？', '发布方式', {
            distinguishCancelAndClose: true,
            confirmButtonText: '全部发布',
            cancelButtonText: '单页发布',
          })
            .then(async () => {
              await questionPublish({
                questionTypeId: parentData.queryForm.questionTypeId,
              })
              this.$parent.$parent.search()
            })
            .catch(async (action) => {
              if (action === 'cancel') {
                const ids = parentData.list.questionList.reduce((acc, cur) => {
                  cur.state === 2 && acc.push(cur.id)
                  return acc
                }, [])
                if (!ids.length) return false
                await questionPublish({
                  ids,
                })
                this.$parent.$parent.search()
              }
            })
          break
        default:
          break
      }
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
.type-btn {
  width: 110px;
  height: 40px;
  margin: 7px auto;
  line-height: 40px;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  user-select: none;
  border-radius: 3px;
  border: 1px solid #eeeeff;
  &:nth-of-type(2) {
    margin-top: 50px;
  }
  &:hover {
    border: 1px solid #0095e5;
  }
  i {
    font-size: 16px;
    &:first-child {
      display: block;
      width: 22px;
      height: 22px;
      line-height: 22px;
      text-align: center;
      margin: 0 10px;
      color: #0095e5;
    }
    &:last-child {
      font-size: 22px;
      position: absolute;
      bottom: -11px;
      right: -1px;
      color: #0095e5;
      display: none;
    }
  }
}
.type-btn-active {
  border: 1px solid #0095e5;
  color: #0095e5;
  i:last-child {
    display: block;
  }
}
.splitLine {
  background: #eee;
  width: 100%;
  height: 1px;
  margin: 16px auto;
}
.handler-btn {
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
  user-select: none;
  border-radius: 3px;
  &:hover {
    background: #0095e5;
    color: #ffff;
  }
  i {
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    font-size: 16px;
    display: block;
    margin-right: 10px;
  }
}
</style>
