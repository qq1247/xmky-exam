<template>
  <div class="content">
    <div class="top">
      <div class="top-title">{{ paperName }}</div>
    </div>
    <div class="paper-content">
      <div
        v-for="(item, index) in paperQuestionRules"
        :key="item.id"
        class="content-rule"
      >
        <div class="chapter">
          <div class="chapter-item">
            <el-input
              v-model="item.chapter.name"
              class="chapter-name"
              placeholder="请输入章节名称（最多16个字符）"
              maxlength="16"
              @blur="
                (e) =>
                  editorListener('chapterName', e.target.value, item.chapter)
              "
            />
            <div class="chapter-handler">
              <el-button
                class="btn"
                icon="common common-delete"
                round
                size="mini"
                @click="chapterDel(item.chapter)"
              >删除章节</el-button>
            </div>
          </div>
          <Editor
            id="chapterDesc"
            class="chapter-description"
            placeholder="请输入章节描述"
            :value="item.chapter.description"
            @editorListener="
              (id, value) => editorListener(id, value, item.chapter)
            "
          />
        </div>
        <el-form
          :ref="`ruleForm${index}`"
          class="box-card"
          :model="paperQuestionRules[index]"
          size="small"
        >
          <div v-for="(rule, ruleIndex) in item.rule" :key="rule.id">
            <el-row>
              <el-col :span="3">
                <el-form-item
                  :prop="`rule.${ruleIndex}.questionTypeId`"
                  :rules="rules.questionTypeId"
                >
                  <CustomSelect
                    :ref="`questionSelect${index}${ruleIndex}`"
                    :total="total"
                    :is-auto="false"
                    :multiple="false"
                    placeholder="题库"
                    :value="rule.questionTypeId"
                    @input="searchQuestionType"
                    @change="(e) => selectQuestionType(e, index, ruleIndex)"
                    @currentChange="getMoreQuestionType"
                    @visibleChange="getQuestionType"
                  >
                    <el-option
                      v-for="type in questionTypes"
                      :key="type.id"
                      :label="type.name"
                      :value="type.id"
                    />
                  </CustomSelect>
                </el-form-item>
              </el-col>
              <el-col :span="3" :offset="1">
                <el-form-item
                  :prop="`rule.${ruleIndex}.type`"
                  :rules="rules.type"
                >
                  <el-select v-model="rule.type" clearable placeholder="类型">
                    <el-option
                      v-for="dict in typeDictList"
                      :key="parseInt(dict.dictKey)"
                      :label="dict.dictValue"
                      :value="parseInt(dict.dictKey)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col
                v-if="markType === 1 && [3, 5].includes(rule.type)"
                :span="4"
                :offset="1"
              >
                <el-form-item :prop="`rule.${ruleIndex}.markType`" :rules="rules.markType">
                  <el-select
                    v-model="rule.markType"
                    multiple
                    clearable
                    collapse-tags
                    placeholder="是否智能"
                  >
                    <el-option
                      v-for="dict in markTypeList"
                      :key="parseInt(dict.dictKey)"
                      :label="dict.dictValue"
                      :value="parseInt(dict.dictKey)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6" :offset="1">
                <div style="display: flex; align-items: center">
                  <el-form-item
                    :prop="`rule.${ruleIndex}.totalNumber`"
                    :rules="rules.totalNumber"
                  >
                    添加：<el-input
                      v-model="rule.totalNumber"
                      class="rule-input"
                    />道题，&nbsp;&nbsp;
                  </el-form-item>
                  <el-form-item
                    :prop="`rule.${ruleIndex}.score`"
                    :rules="rules.score"
                  >
                    每题&nbsp;&nbsp;<el-input
                      v-model="rule.score"
                      class="rule-input"
                    />分
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-form-item
              v-if="rule.markType.includes(1) && [3, 5].includes(rule.type)"
              label="分数选项："
              label-width="85px"
            >
              <el-checkbox-group v-model="rule.markOptions">
                <el-tooltip
                  v-if="rule.type === 3"
                  class="item"
                  content="默认答案有顺序"
                  effect="dark"
                  placement="top"
                >
                  <el-checkbox :label="2">答案无顺序</el-checkbox>
                </el-tooltip>
                <el-tooltip
                  class="item"
                  content="默认大小写敏感"
                  effect="dark"
                  placement="top"
                >
                  <el-checkbox :label="3">大小写不敏感</el-checkbox>
                </el-tooltip>
              </el-checkbox-group>
            </el-form-item>
          </div>
        </el-form>
        <el-button
          size="mini"
          type="primary"
          class="rule-btn"
          :disabled="item.rule.length >= 10"
          @click="addQuestionRule(index)"
        >+添加规则</el-button>
        <el-button
          size="mini"
          type="danger"
          class="rule-btn"
          :disabled="item.rule.length <= 1"
          @click="delQuestionRule(index)"
        >-删除规则</el-button>
        <el-button
          style="display: block"
          type="primary"
          @click="updateQuestionRule(index)"
        >更新规则</el-button>
      </div>
    </div>

    <div class="handler-btn" @click="paperChapterAdd">
      <i class="common common-random" />
      <span>随机组合章节</span>
    </div>
  </div>
</template>
<script>
import {
  paperGet,
  paperChapterAdd,
  paperChapterEdit,
  paperChapterDel,
  paperQuestionRuleUpdate,
  paperQuestionRuleList
} from 'api/paper'
import { questionTypeListpage } from 'api/question'
import { getOneDict } from '@/utils/getDict'
import Editor from 'components/Editor/Index.vue'
import CustomSelect from 'components/CustomSelect.vue'
import { getQuick } from '@/utils/storage'
export default {
  components: {
    Editor,
    CustomSelect
  },
  data() {
    return {
      paperId: 0,
      paperTypeId: 0,
      markType: 1,
      paperName: '',
      total: 1,
      markTypeList: [
        {
          dictValue: '智能',
          no: 1,
          dictKey: '1'
        }
        /* {
          dictValue: '非智能',
          no: 2,
          dictKey: '2',
        }, */
      ],
      typeDictList: [],
      questionTypes: [],
      chapterForm: {
        id: 0,
        name: '章节名称',
        description: '章节描述',
        rules: {
          name: [
            { required: true, message: '请输入章节名称', trigger: 'blur' }
          ]
        }
      },
      paperQuestionRules: [],
      rules: {
        questionTypeId: [
          { required: true, message: '请选择题库', trigger: 'change' }
        ],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        markType: [
          {
            required: true,
            message: '请选择是否智能',
            trigger: 'change',
            type: 'array'
          }
        ],
        totalNumber: [
          {
            required: true,
            message: '请填写题目数',
            trigger: 'blur'
          }
        ],
        score: [
          {
            required: true,
            message: '请填写题目分数',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().id
    await this.init()
    if (Number(this.paperId)) {
      const res = await paperGet({ id: this.paperId })
      this.$nextTick(() => {
        this.paperName = res.data.name
        this.markType = res.data.markType
        this.showSelectContent()
      })
    }
  },
  methods: {
    async init() {
      this.typeDictList = getOneDict('QUESTION_TYPE')
      await this.query()
    },
    // 查询组卷规则
    async query() {
      const res = await paperQuestionRuleList({
        id: this.paperId
      })

      this.paperQuestionRules = res.data
    },
    // 添加章节
    async paperChapterAdd() {
      const res = await paperChapterAdd({
        name: this.chapterForm.name,
        description: this.chapterForm.description,
        paperId: this.paperId,
        type: 1
      })
      this.refreshData(res, '添加章节')
    },
    // 编辑章节
    editorListener(id, value, chapter) {
      const chapterInfo = {}
      if (id === 'chapterName') {
        chapterInfo.name = value
        chapterInfo.description = chapter.description
      } else {
        chapterInfo.name = chapter.name
        chapterInfo.description = value
      }
      paperChapterEdit({
        chapterId: chapter.id,
        ...chapterInfo
      })
    },
    // 删除章节
    chapterDel({ id }) {
      this.$confirm(`删除章节将删除章节内的试题，是否删除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          const res = await paperChapterDel({ chapterId: id })
          this.refreshData(res, '删除章节')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 添加组卷规则
    addQuestionRule(index) {
      this.paperQuestionRules[index].rule.push({
        id: null,
        paperId: 2,
        questionTypeId: null,
        questionTypeName: '',
        type: 1,
        markType: [],
        markOptions: [],
        totalNumber: '',
        score: ''
      })
    },
    // 删除自由组卷规则
    delQuestionRule(index) {
      this.paperQuestionRules[index].rule.pop()
    },
    // 添加 | 修改自由组卷规则
    updateQuestionRule(index) {
      if (!this.paperQuestionRules[index]?.rule.length) {
        this.$message.warning('规则不能为空')
        return
      }
      this.$refs[`ruleForm${index}`][0].validate(async(valid) => {
        if (!valid) {
          return
        }
        const ruleParam = this.paperQuestionRules[index].rule.reduce(
          (acc, cur) => {
            /* ;[3, 5].includes(cur.type)
              ? this.markType === 1
                ? acc.markTypes.push('1')
                : acc.markTypes.push(cur.markType.join(','))
              : acc.markTypes.push('1') */
            acc.markTypes.push('1')
            ;[3, 5].includes(cur.type)
              ? acc.markOptions.push(cur.markOptions.join(','))
              : acc.markOptions.push(cur.type === 2 ? '1' : '')
            acc.nums.push(cur.totalNumber)
            acc.types.push(cur.type)
            acc.scores.push(cur.score)
            acc.questionTypeIds.push(cur.questionTypeId)

            return acc
          },
          {
            markTypes: [],
            nums: [],
            types: [],
            scores: [],
            markOptions: [],
            questionTypeIds: []
          }
        )

        const res = await paperQuestionRuleUpdate({
          paperId: this.paperId,
          chapterId: this.paperQuestionRules[index].chapter.id,
          ...ruleParam
        })

        if (res?.code === 200) {
          this.$message.success('更新成功！')
          this.query()
          this.showSelectContent()
        } else {
          this.$message.success('更新失败！')
        }
      })
    },
    // 获取题库
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListpage({
        name,
        curPage,
        pageSize: 5
      })
      this.questionTypes = typeList.data.list
      this.total = typeList.data.total
    },
    // 根据name 查询题库
    searchQuestionType(name) {
      this.getQuestionType(1, name)
    },
    // 获取更多题库
    getMoreQuestionType(curPage, name) {
      this.getQuestionType(curPage, name)
    },
    // 选择题库
    selectQuestionType(e, index, ruleIndex) {
      this.paperQuestionRules[index].rule[ruleIndex].questionTypeId = e
    },
    // 回显下拉框内容
    showSelectContent() {
      if (this.paperQuestionRules.length) {
        this.paperQuestionRules.map((item, index) => {
          if (item.rule.length) {
            item.rule.map((rule, ruleIndex) => {
              this.$refs[`questionSelect${index}${ruleIndex}`][0].$refs[
                'elSelect'
              ].cachedOptions.push({
                currentLabel: rule.questionTypeName,
                currentValue: rule.questionTypeId,
                label: rule.questionTypeName,
                value: rule.questionTypeId
              })
              this.$refs[`questionSelect${index}${ruleIndex}`][0].$refs[
                'elSelect'
              ].selectedLabel = rule.questionTypeName
            })
          }
        })
      }
    },
    // 更新页面数据
    refreshData(res, title) {
      res?.code === 200
        ? (this.$message(`${title}成功！`), this.query())
        : this.$message.error(`${title}失败！`)
    }
  }
}
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  flex-direction: column;
  width: 1200px;
  margin: 0 auto;
}

.top {
  background: #fff;
  width: 1200px;
  height: 40px;
  color: #333;
  position: fixed;
  z-index: 100;
  font-weight: 600;
  padding-left: 30px;
  display: flex;
  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 14px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0094e5;
  }
  .top-title {
    flex: 1;
    line-height: 40px;
  }
}

.paper-content {
  width: 100%;
  padding-top: 50px;
}

.content-rule {
  padding: 10px;
  margin-top: 10px;
  background: #fff;
  border-radius: 10px;
}

.chapter {
  display: flex;
  flex-direction: column;
  .chapter-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .chapter-name {
      flex: 1;
      margin-right: 20px;
      /deep/ .el-input__inner {
        border: 1px solid #fff;
        padding: 0 10px;
      }
      /deep/ .el-input__inner:hover,
      /deep/ .el-input__inner:focus {
        border: 1px solid #c0c4cc;
      }
    }
    /deep/.el-button {
      opacity: 0;
      .common {
        font-size: 12px;
        margin-right: 5px;
      }
    }
    &:hover {
      .el-button {
        opacity: 1;
      }
    }
  }
  /deep/.tinymce-box .tinymce-content {
    line-height: 30px;
    border: 1px solid transparent;
  }
}

.rule {
  padding: 10px;
}

.box-card {
  margin: 10px 20px 0;
}

.rule-input {
  width: 50px;
  margin-right: 10px;
  /deep/ .el-input__inner {
    border: none;
    border-bottom: 1px solid #dcdfe6;
    padding: 0px;
    text-align: center;
  }
}

.rule-btn {
  margin: 10px 0 20px 20px;
}

.handler-btn {
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 5px;
  margin-top: 10px;
  cursor: pointer;
  i {
    font-size: 25px;
    margin-right: 10px;
  }
}
</style>
