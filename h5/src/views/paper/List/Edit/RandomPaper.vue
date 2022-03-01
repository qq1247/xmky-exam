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
              class="chapter-name"
              placeholder="请输入章节名称（最多16个字符）"
              v-model="item.chapter.name"
              maxlength="16"
              @blur="
                (e) =>
                  editorListener('chapterName', e.target.value, item.chapter)
              "
            ></el-input>
            <div class="chapter-handler">
              <el-button
                @click="delRules(index)"
                class="btn"
                icon="common common-delete"
                round
                size="mini"
                >清空规则</el-button
              >
              <el-button
                @click="chapterDel(item.chapter)"
                class="btn"
                icon="common common-delete"
                round
                size="mini"
                >删除章节</el-button
              >
            </div>
          </div>
          <TinymceEditor
            class="chapter-description"
            placeholder="请输入章节描述"
            :value="item.chapter.description"
            @editorListener="
              (id, value) => editorListener(id, value, item.chapter)
            "
            id="chapterDesc"
          ></TinymceEditor>
        </div>
        <el-card
          class="box-card"
          shadow="never"
          v-for="(rand, randIndex) in item.randChapterRulesList"
          :key="rand.id"
        >
          <div slot="header">
            <span>筛选规则</span>
            <el-button
              style="float: right; margin-top: -2px"
              @click="delRule(index, randIndex)"
              size="mini"
              round
              >删除</el-button
            >
          </div>
          <el-form
            :model="rand"
            :rules="rules"
            :ref="`ruleForm${index}${randIndex}`"
          >
            <el-form-item prop="questionTypeId">
              <CustomSelect
                :ref="`questionSelect${index}${randIndex}`"
                :isAuto="false"
                :multiple="false"
                placeholder="请选择题库"
                :value="rand.questionTypeId"
                :total="total"
                @change="(e) => selectQuestionType(e, index, randIndex)"
                @input="searchQuestionType"
                @currentChange="getMoreQuestionType"
                @visibleChange="getQuestionType"
              >
                <el-option
                  v-for="item in questionTypes"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                ></el-option>
              </CustomSelect>
            </el-form-item>
            <el-row>
              <el-col :span="5"
                ><el-form-item prop="type">
                  <el-select
                    clearable
                    placeholder="请选择类型"
                    v-model="rand.type"
                  >
                    <el-option
                      :key="parseInt(dict.dictKey)"
                      :label="dict.dictValue"
                      :value="parseInt(dict.dictKey)"
                      v-for="dict in typeDictList"
                    ></el-option>
                  </el-select> </el-form-item
              ></el-col>
              <el-col :span="5"
                ><el-form-item>
                  <el-select
                    clearable
                    placeholder="请选择难度"
                    v-model="rand.difficulty"
                  >
                    <el-option
                      :key="parseInt(dict.dictKey)"
                      :label="dict.dictValue"
                      :value="parseInt(dict.dictKey)"
                      v-for="dict in difficultyDictList"
                    ></el-option>
                  </el-select> </el-form-item
              ></el-col>
              <el-col
                :span="5"
                v-if="markType === 2 && [3, 5].includes(rand.type)"
                ><el-form-item prop="ai">
                  <el-select
                    clearable
                    placeholder="请选择是否智能"
                    v-model="rand.ai"
                  >
                    <el-option
                      :key="parseInt(dict.dictKey)"
                      :label="dict.dictValue"
                      :value="parseInt(dict.dictKey)"
                      v-for="dict in aiList"
                    ></el-option>
                  </el-select> </el-form-item
              ></el-col>
              <el-col :span="4"
                ><el-form-item>
                  <el-input
                    placeholder="请输入分值"
                    v-model="rand.queryScore"
                  ></el-input> </el-form-item
              ></el-col>
            </el-row>
            <el-form-item
              label="分数选项："
              label-width="85px"
              v-if="rand.ai === 1 && [3, 5].includes(rand.type)"
            >
              <el-checkbox-group v-model="rand.scoreOptions">
                <el-tooltip
                  v-if="rand.type === 3"
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
            <el-row>
              <el-col :span="1">添加：</el-col>
              <el-col :span="4.5">
                <el-form-item prop="totalNumber">
                  <el-input
                    class="rule-input"
                    v-model="rand.totalNumber"
                  ></el-input
                  >道题，每题&nbsp;&nbsp;&nbsp;&nbsp;
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item prop="score">
                  <el-input class="rule-input" v-model="rand.score"></el-input
                  >分
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="2"
                ><el-form-item>
                  <el-button
                    type="primary"
                    @click="editQuestionRule(index, randIndex)"
                    >{{ !rand.id ? '添加' : '修改' }}</el-button
                  >
                </el-form-item></el-col
              >
              <el-col :span="2"
                ><el-form-item>
                  <el-button
                    type="primary"
                    @click="resetQuestionRule(index, randIndex)"
                    >重置</el-button
                  >
                </el-form-item></el-col
              >
            </el-row>
          </el-form>
        </el-card>
        <div class="handler-btn" @click="addQuestionRule(index)">
          <i class="common common-plus"></i>
          <span>添加筛选</span>
        </div>
      </div>
    </div>

    <div class="handler-btn" @click="paperChapterAdd" v-if="paperState === 2">
      <i class="common common-random"></i>
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
  randChapterRulesAdd,
  randChapterRulesEdit,
  randChapterRulesDel,
  randChapterRulesGet,
  randChapterRulesList,
} from 'api/paper'
import { questionTypeListPage } from 'api/question'
import { getOneDict } from '@/utils/getDict'
import TinymceEditor from 'components/TinymceEditor/Index.vue'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    TinymceEditor,
    CustomSelect,
  },
  data() {
    return {
      paperId: 0,
      paperState: 1,
      paperTypeId: 0,
      markType: 1,
      paperName: '',
      total: 1,
      aiList: [
        {
          dictValue: '智能',
          no: 1,
          dictKey: '1',
        },
        {
          dictValue: '非智能',
          no: 2,
          dictKey: '2',
        },
      ],
      typeDictList: [],
      questionTypes: [],
      difficultyDictList: [],
      chapterForm: {
        id: 0,
        name: '章节名称',
        description: '章节描述',
        rules: {
          name: [
            { required: true, message: '请输入章节名称', trigger: 'blur' },
          ],
        },
      },
      paperQuestionRules: [],
      rules: {
        questionTypeId: [
          { required: true, message: '请选择题库', trigger: 'change' },
        ],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        ai: [{ required: true, message: '请选择是否智能', trigger: 'change' }],
        totalNumber: [
          {
            required: true,
            message: '请填写题目数',
            trigger: 'blur',
          },
        ],
        score: [
          {
            required: true,
            message: '请填写题目分数',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  async created() {
    this.paperId = this.$route.params.id
    if (Number(this.paperId)) {
      const res = await paperGet({ id: this.paperId })
      this.$nextTick(() => {
        this.paperName = res.data.name
        this.paperState = res.data.state
        this.markType = res.data.markType
      })
    }
    this.init()
  },
  methods: {
    init() {
      this.typeDictList = getOneDict('QUESTION_TYPE')
      this.difficultyDictList = getOneDict('QUESTION_DIFFICULTY')
      this.query()
    },
    // 查询组卷规则
    async query() {
      const res = await randChapterRulesList({
        paperId: this.paperId,
      })
      this.paperQuestionRules = res.data

      this.$nextTick(() => {
        if (this.paperQuestionRules.length) {
          this.paperQuestionRules.map((item, index) => {
            if (item.randChapterRulesList.length) {
              item.randChapterRulesList.map((rule, indexRule) => {
                this.$refs[`questionSelect${index}${indexRule}`][0].$refs[
                  'elSelect'
                ].cachedOptions.push({
                  currentLabel: rule.questionTypeName,
                  currentValue: rule.questionTypeId,
                  label: rule.questionTypeName,
                  value: rule.questionTypeId,
                })
              })
            }
          })
        }
      })
    },
    // 添加章节
    async paperChapterAdd() {
      const res = await paperChapterAdd({
        name: this.chapterForm.name,
        description: this.chapterForm.description,
        paperId: this.paperId,
        type: 1,
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
        ...chapterInfo,
      })
    },
    // 删除章节
    chapterDel({ id }) {
      this.$confirm(`删除章节将删除章节内的试题，是否删除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperChapterDel({ chapterId: id })
          this.refreshData(res, '删除章节')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 添加组卷规则
    addQuestionRule(index) {
      this.paperQuestionRules[index].randChapterRulesList.push({
        id: null,
        paperId: 2,
        questionTypeId: null,
        questionTypeName: '',
        type: 1,
        difficulty: 1,
        queryScore: '',
        ai: 1,
        scoreOptions: [],
        totalNumber: '',
        score: '',
      })
    },
    // 获取试题分类
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListPage({
        name,
        curPage,
        pageSize: 5,
      })
      this.questionTypes = typeList.data.list
      this.total = typeList.data.total
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
    selectQuestionType(e, index, randIndex) {
      this.paperQuestionRules[index].randChapterRulesList[
        randIndex
      ].questionTypeId = e
    },
    // 添加 | 修改自由组卷规则
    editQuestionRule(index, randIndex) {
      this.$refs[`ruleForm${index}${randIndex}`][0].validate(async (valid) => {
        if (!valid) {
          return
        }

        const ruleParam =
          this.paperQuestionRules[index].randChapterRulesList[randIndex]

        let res
        const params = {
          paperId: this.paperId,
          questionTypeId: ruleParam.questionTypeId,
          paperQuestionId: this.paperQuestionRules[index].chapter.id,
          ai: ruleParam.ai,
          type: ruleParam.type,
          difficulty: ruleParam.difficulty,
          queryScore: ruleParam.queryScore,
          scoreOptions: ruleParam.scoreOptions,
          totalNumber: ruleParam.totalNumber,
          score: ruleParam.score,
        }

        if (!ruleParam.id) {
          res = await randChapterRulesAdd(params)
        } else {
          res = await randChapterRulesEdit({ id: ruleParam.id, ...params })
        }

        if (res?.code == 200) {
          this.$message.success(!ruleParam.id ? '添加成功！' : '修改成功！')
          this.query()
        } else {
          this.$message.error(!ruleParam.id ? '添加失败！' : '修改失败！')
        }
      })
    },
    // 重置自由组卷规则
    resetQuestionRule(index, randIndex) {
      this.$refs[`ruleForm${index}${randIndex}`][0].resetFields()
    },
    // 删除自由组卷规则
    async delRule(index, randIndex) {
      const rule =
        this.paperQuestionRules[index].randChapterRulesList[randIndex]
      if (!rule.id) {
        this.paperQuestionRules[index].randChapterRulesList.splice(randIndex, 1)
      } else {
        const res = await randChapterRulesDel({
          ids: [`${rule.id}`],
        })
        this.refreshData(res, '删除规则')
      }
    },
    // 清空章节下的所有规则
    async delRules(index) {
      const ids = this.paperQuestionRules[index].randChapterRulesList.reduce(
        (acc, cur) => {
          cur.id && acc.push(cur.id)
          return acc
        },
        []
      )
      if (ids.length) {
        const res = await randChapterRulesDel({
          ids,
        })
        this.refreshData(res, '清空规则')
      } else {
        this.paperQuestionRules[index].randChapterRulesList = []
      }
    },
    // 更新页面数据
    refreshData(res, title) {
      res?.code === 200
        ? (this.$message(`${title}成功！`), this.query())
        : this.$message.error(`${title}失败！`)
    },
  },
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
    background: #0095e5;
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
  box-shadow: 0 0 13px -4px rgba(0, 0, 0, 0.13);
}

.rule-input {
  width: 100px;
  margin-right: 10px;
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
