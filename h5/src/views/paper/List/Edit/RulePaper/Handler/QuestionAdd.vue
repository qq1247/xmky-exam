<template>
  <div class="handler-content">
    <el-form
      size="small"
      ref="questionForm"
      :model="questionForm"
      :rules="questionForm.rules"
      label-width="100px"
    >
      <el-form-item label="选择题库" prop="questionTypeId">
        <CustomSelect
          :multiple="false"
          placeholder="请选择试题分类"
          :value="questionForm.questionTypeId"
          :total="questionForm.total"
          @change="selectQuestionType"
          @input="searchQuestionType"
          @currentChange="getMoreQuestionType"
          @visibleChange="getQuestionType"
        >
          <el-option
            v-for="item in questionForm.questionTypes"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </CustomSelect>
      </el-form-item>
      <el-form-item label="选择类型">
        <div class="types">
          <div
            :class="[
              'type-btn',
              questionForm.questionType === btn.type ? 'type-btn-active' : '',
            ]"
            :key="btn.type"
            @click="selectType(btn.type)"
            v-for="btn in typeButtons"
          >
            <i :class="btn.icon"></i>
            {{ btn.name }}
            <i class="common common-subscript sub-script"></i>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="快捷操作">
        <div class="template-btn">
          <Upload
            type="word"
            ref="templateUpload"
            @success="templateSuccess"
            @remove="templateRemove"
          >
          </Upload>
          <div class="template-item" @click="questionTemplate">
            <i class="common common-word-template"></i>
            <p>下载试题模板</p>
          </div>
        </div>
      </el-form-item>
    </el-form>
    <div class="wrap-title">编辑区域</div>
    <EditModule
      ref="editModule"
      publish
      @add="add"
      key="editModule"
      :questionType="questionForm.questionType"
      :questionTypeId="questionForm.questionTypeId"
    ></EditModule>
  </div>
</template>

<script>
import Upload from 'components/Upload'
import CustomSelect from 'components/CustomSelect.vue'
import EditModule from 'components/EditQuestion/EditModule.vue'
import {
  questionTypeListPage,
  questionAdd,
  questionTemplate,
  questionImport,
} from 'api/question'
import { progressBarGet } from 'api/common'
export default {
  components: { Upload, CustomSelect, EditModule },
  data() {
    return {
      questionForm: {
        total: 1,
        curPage: 1,
        questionTypeId: null,
        questionTypes: [],
        questionType: 1,
        rules: {
          questionTypeId: [
            { required: true, message: '请选择试题分类', trigger: 'blur' },
          ],
        },
      },
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
      questionDocIds: [],
      isAnalysis: false,
      percentage: 0,
    }
  },
  mounted() {},
  methods: {
    // 选择试题类型
    selectType(e) {
      this.questionForm.questionType = e
    },
    // 获取试题分类
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListPage({
        name,
        curPage,
        pageSize: 5,
      })
      this.questionForm.questionTypes = typeList.data.list
      this.questionForm.total = typeList.data.total
    },
    // 根据name 查询试题分类
    searchQuestionType(name) {
      this.getQuestionType(1, name)
    },
    // 获取更多试题分类
    getMoreQuestionType(curPage, name) {
      this.getQuestionType(curPage, name)
    },
    // 选择试题分类
    selectQuestionType(questionTypeId) {
      this.$set(this.questionForm, 'questionTypeId', questionTypeId)
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
      this.$refs['questionForm'].validate(async (valid) => {
        if (!valid) {
          this.templateClear('templateUpload')
          return false
        }

        if (this.questionDocIds.length === 0) {
          this.$message.warning('请选择需解析的文件')
          return
        }

        const res = await questionImport({
          fileId: this.questionDocIds[0].response.data.fileIds,
          questionTypeId: this.questionForm.questionTypeId,
          state: 1,
        }).catch(() => {
          this.isAnalysis = false
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
      })
    },
    // 导出试题
    questionExport() {
      this.$message.warning('暂未开放！')
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
      this.questionDocIds = fileList
      this.questionImport()
    },
    // 上传试题模板失败
    templateClear(ref) {
      this.questionDocIds = []
      this.$refs[ref].clear()
    },
    // 删除试题模板
    templateRemove(file, fileList) {
      this.questionDocIds = []
    },
    // 添加试题
    add(params) {
      this.$refs['questionForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }
        const res = await questionAdd(params)
        if (res?.code === 200) {
          this.$refs.editModule.resetData()
        } else {
          this.$message.success('添加失败！')
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.handler-content {
  padding: 50px 0 10px;
}

.types {
  display: flex;
  flex-wrap: wrap;
}
.type-btn {
  width: 110px;
  height: 40px;
  margin: 7px 1%;
  line-height: 40px;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  user-select: none;
  border-radius: 3px;
  border: 1px solid #eeeeff;
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

.template-btn {
  display: flex;
  justify-content: space-around;
}

.template-item {
  width: 120px;
  height: 70px;
  border: 1px solid #ccc;
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

.wrap-title {
  width: 100%;
  line-height: 40px;
  background: #efefef;
  font-weight: 600;
  padding-left: 15px;
  margin-bottom: 10px;
}
</style>
