<template>
  <div class="content">
    <!-- 答题卡 -->
    <div class="paper-router">
      <el-divider>答题卡</el-divider>
      <div class="router-content">
        <Draggable
          v-model="examInfo.examRules"
          tag="div"
          group="questionGroup"
          chosen-class="drag-question-active"
          animation="300"
          @change="ruleSort"
        >
          <div
            v-for="(examRule, index) in examInfo.examRules"
            :key="index"
            :style="{ display: examRule.type === 1 ? 'block' : 'inline-block' }"
          >
            <div v-if="examRule.type === 1" class="router-title">
              {{ examRule.chapterName }}
            </div>
            <div v-else>
              <a
                :class="['router-index']"
              >
                {{ examRule.no }}
              </a>
            </div>
          </div>
        </Draggable>
      </div>
    </div>
    <!-- 规则 -->
    <el-scrollbar class="paper-content">
      <div class="top">
        <div class="top-title">
          <el-input
            :value="examInfo.exam.paperName"
            @input="(value) => paperNameUpdate(value)"
            placeholder="请输入试卷名称"
            maxlength="16"
          ></el-input>
          <div class="top-score">
            {{ totalScore }}分
            <i class="common common-fenshudixian fenshudixian"></i>
          </div>
        </div>
        <div class="top-handler">
          <el-button
            icon="el-icon-edit-outline"
            size="mini"
            round
            @click="_ruleAdd"
            >规则添加</el-button
          >
          <el-button
            icon="el-icon-plus"
            size="mini"
            @click="
              chapterAddForRule({ name: '点击这里输入章节名称', txt: '' })
            "
            round
            >章节添加</el-button
          >
          <el-button icon="el-icon-delete" size="mini" round @click="paperClear"
            >试卷重置</el-button
          >
        </div>
      </div>
      <el-form ref="editForm" :model="examInfo" :rules="editForm.rules">
        <div
          class="content-center"
          v-for="(examRule, index) of examInfo.examRules"
          :key="index"
        >
          <div v-if="examRule.type === 1" class="chapter">
            <el-input
              :value="examRule.chapterName"
              @input="(value) => chapterNameUpdateForRule({ index, value })"
              placeholder="请输入名称"
              maxlength="16"
            />
            <el-input
              :value="examRule.chapterTxt"
              @input="(value) => chapterTxtUpdateForRule({ index, value })"
              :rows="2"
              placeholder="请输入描述"
              type="textarea"
              :autosize="true"
              maxlength="128"
            />
              <el-button
                class="btn"
                icon="el-icon-close"
                round
                size="mini"
                type="danger"
                @click="chapterDelForRule(index)"
                >删除</el-button>
          </div>
          <div v-if="examRule.type === 2">
            <el-form-item
              :prop="`examRules.${index}.questionTypeId`"
              :rules="editForm.rules.questionTypeId"
              style="display: inline-block; padding-left: 20px; width: 150px;"
            >
              <CustomSelect
                :value="examRule.questionTypeId"
                url="questionType/listpage"
                :params="{}"
                optionLabel="name"
                optionValue="id"
                searchParmName="name"
                @change="examRuleQuestionTypeIdUpdate({ index, value: $event })"
              >
                <template #customText="{ item }">
                  <span style="float: left">{{ item.name }}</span>
                  <span
                    style="float: right; color: #8492a6; font-size: 13px"
                    >{{ item.questionNum }}题</span
                  >
                </template>
              </CustomSelect>
            </el-form-item>
            <el-form-item 
              :prop="`examRules.${index}.questionType`"
              :rules="editForm.rules.questionType"
              style="display: inline-block; padding-left: 10px; width: 100px;">
              <el-select
                :value="examRule.questionType"
                clearable
                @change="_examRuleQuestionTypeUpdate({ index, value: $event })"
                placeholder="试题类型"
                size="mini"
              >
                <el-option
                  v-for="dict in $tools.getDicts('QUESTION_TYPE')"
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                />
              </el-select>
            </el-form-item>
            <el-form-item 
              v-if="examRule.questionType === 3 || examRule.questionType === 5"
              :prop="`examRules.${index}.markType`"
              :rules="editForm.rules.markType"
              style="display: inline-block; padding-left: 10px; width: 100px;">
              <el-select
                :value="examRule.markType"
                clearable
                placeholder="阅卷类型"
                size="mini"
                :disabled="true"
              >
                <el-option
                  v-for="dict in $tools.getDicts('PAPER_MARK_TYPE')"
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                />
              </el-select>
            </el-form-item>
            <el-form-item 
              v-if="examRule.markType === 1 && (examRule.questionType === 3 || examRule.questionType === 5)"
              :prop="`examRules.${index}.markOptions`"
              style="display: inline-block; padding-left: 10px; width: 160px;"
              >
              <el-select
                :value="examRule.markOptions"
                clearable
                placeholder="阅卷选项"
                size="mini"
                :multiple="true"
                :collapse-tags="true"
                @change="examRuleMarkOptionsUpdate({ index, value: $event })"
              >
                <el-option
                  v-for="dict in $tools.getDicts('QUESTION_MARK_OPTIONS')"
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              :prop="`examRules.${index}.num`"
              :rules="editForm.rules.num"
              style="display: inline-block; padding-left: 10px;"
            > 添加
            <el-input-number
                :value="examRule.num"
                :min="1"
                :max="100"
                size="small"
                @change="(value) => examRuleNumUpdate({ index, value })"
              ></el-input-number
              >题
            </el-form-item>
            <el-form-item
              :prop="`examRules.${index}.score`"
              :rules="editForm.rules.score"
              style="display: inline-block; "
            >，每题
            <el-input-number
                :value="examRule.score"
                :min="0.5"
                :max="20"
                size="small"
                @change="(value) => _examRuleScoreUpdate({ index, value })"
              ></el-input-number
              >分
            </el-form-item>
            <el-form-item
              v-if="examRule.questionType === 2"
              :prop="`examRules.${index}.scores.0`"
              :rules="editForm.rules.scores"
              style="display: inline-block; padding-left: 10px;"
            >，漏选
              <el-input-number
                :value="examRule.scores[0]"
                :min="0"
                :max="20"
                size="small"
                @change="(value) => _examRuleScoresUpdate({ index, value })"
              ></el-input-number
              >分
            </el-form-item>
            <el-button
              v-if="index > 0" 
              class="btn2"
              icon="el-icon-close"
              round
              size="mini"
              type="danger"
              @click="_ruleDel(index)"
              >删除{{index}}</el-button>
          </div>
        </div>
      </el-form>
      <div style="height: 50px"></div>
    </el-scrollbar>
  </div>
</template>
<script>
import Draggable from 'vuedraggable'
import CustomSelect from 'components/CustomSelect.vue'
import { mapState } from 'vuex'
import { mapGetters } from 'vuex'
import { mapMutations } from 'vuex'
export default {
  components: {
    Draggable,
    CustomSelect,
  },
  data() {
    return {
      editForm: {
        rules: {
          questionTypeId: [{ required: true, message: '请选择题库', trigger: 'change' }],
          questionType: [{ required: true, message: '请选择试题分类', trigger: 'change' }],
          markType: [{ required: true, message: '请选择试题分类', trigger: 'change' }],
          num: [{
            trigger: 'change', 
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请输入题数'))
              }
              if (!/^[0-9]*$/.test(value)) {
                return callback(new Error('请输入正整数'))
              }
              if (value > 100) {
                return callback(new Error('最大100题'))
              }
              return callback()
          }}],
          score: [{
            trigger: 'change', 
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请输入分数'))
              }
              if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
              }
              if (value > 20) {
                return callback(new Error('最大20分'))
              }
              return callback()
          }}],
          scores: [{
            trigger: 'change', 
            validator: (rule, value, callback) => {
              console.log(value)
              if (value === null || value === undefined) {
                return callback(new Error('请输入分数'))
              }
              if (!/^(([1-9]{1}\d*)|(0{1}))(\.\d{1,2})?$/.test(value)) {
                return callback(new Error('正数，最多两位小数'))
              }
              if (value > 20) {
                return callback(new Error('最大20分'))
              }
              return callback()
          }}],
        },
      },
    }
  },
  computed: {
    ...mapState('exam', ['examInfo']),
    ...mapGetters('exam', ['totalScore']),
  },
  mounted() {
    this.genTypeUpdate(2)
    let hasRule = this.examInfo.examRules.some(examQuestion => examQuestion.type === 2)
    if (!hasRule) {
      this.ruleAdd()
    }
  },
  methods: {
    ...mapMutations('exam', [
      'genTypeUpdate',
      'paperNameUpdate',
      'paperClear',
      'chapterNameUpdateForRule',
      'chapterTxtUpdateForRule',
      'chapterAddForRule',
      'chapterDelForRule',
      'ruleAdd',
      'ruleDel',
      'ruleSort',
      'examRuleQuestionTypeIdUpdate',
      'examRuleQuestionTypeUpdate',
      'examRuleNumUpdate',
      'examRuleScoreUpdate',
      'examRuleScoresUpdate',
      'examRuleMarkTypeUpdate',
      'examRuleMarkOptionsUpdate',
    ]),
    // 跳转到对应的试题
    toHref(index) {
      document
        .getElementById(`question-${index}`)
        .parentElement.parentElement.scrollIntoView()
    },
    // 规则添加
    _ruleAdd() {
      this.ruleAdd()
      this.ruleSort()
    },
    _ruleDel(index) {
      this.ruleDel(index)
      this.ruleSort()
    },
    // 试题类型修改
    _examRuleQuestionTypeUpdate({ index, value}) {
      this.examRuleQuestionTypeUpdate({ index, value})

      let examRule = this.examInfo.examRules[index]
      if (examRule.questionType == 2) {// 如果是多选，设置漏选分数
        this.examRuleScoresUpdate({
          index, 
          value: [examRule.score / 2]
        })
      } else {// 否则清空子分数
        this.examRuleScoresUpdate({
          index, 
          value: []
        })
      }

      this.examRuleMarkOptionsUpdate({ index, value: []}) // 清空阅卷选项
    },
    // 分数修改
    _examRuleScoreUpdate({ index, value}) {
      this.examRuleScoreUpdate({ index, value})
      let examRule = this.examInfo.examRules[index]
      if (examRule.questionType === 2) {// 如果是多选，漏选分值超出范围，则恢复
        if (examRule.score <= examRule.scores[0]) {
          this.examRuleScoresUpdate({ index, value: [examRule.score / 2]});
        }
      }
    },
    // 子分数修改
    async _examRuleScoresUpdate({ index, value}) {
      let examRule = this.examInfo.examRules[index]
      if (examRule.questionType === 2) {// 如果是多选，漏选分值超出范围，则恢复
        this.examRuleScoresUpdate({ index, value: [value]});
        await this.$nextTick()
        if (examRule.score <= value) {
          this.examRuleScoresUpdate({ index, value: [examRule.score / 2]});
        } else {
          this.examRuleScoresUpdate({ index, value: [value]});
        }
      }
    },

    // 下一步
    next() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }
        this.$parent.activeIndex++
      })
    },
  },
}
</script>

<style lang="scss" scoped>
/deep/ .el-input-number--small {
  width: 45px;
}
/deep/ .el-input-number--small .el-input-number__increase,
/deep/ .el-input-number--small .el-input-number__decrease {
  display: none;
  width: 45px;
}
/deep/ .el-input--small .el-input__inner {
  border: none;
  border-radius: 0;
  border-bottom: 1px solid #557587;
}
/deep/ .el-input--small .el-input__inner {
  width: 45px;
  padding-left: 0;
  padding-right: 0;
}
.content-center :hover {
  padding-bottom: 0px;
  .question-opt {
    display: block;
  }
  .btn {
    display: inline-block;
  }
  .btn2 {
    display: inline-block;
  }
}
.btn:hover {
  padding-bottom: 5px;
}
.btn2:hover {
  padding-bottom: 5px;
}
.btn {
  padding: 5px 5px; 
  margin-left: 10px;
  position: absolute;
  right: 20px;
  bottom: 0px;
  display: none;
}
.btn2 {
  padding: 5px 5px; 
  margin-left: 10px;
  display: none;
}
.question-opt {
  color: #557587;
  height: 32px;
  margin-top: 10px;
  display: none;
  /deep/ .el-input {
    width: 50px;
  }
  /deep/ .el-input-number--small {
    width: 50px;
  }
  /deep/ .el-input-number--small .el-input-number__increase,
  /deep/ .el-input-number--small .el-input-number__decrease {
    display: none;
    width: 50px;
  }
  /deep/ .el-input--small .el-input__inner {
    border: none;
    border-radius: 0;
    border-bottom: 1px solid #557587;
  }
  /deep/ .el-input--small .el-input__inner {
    width: 50px;
    padding-left: 0;
    padding-right: 0;
  }
}
.paper-router {
  width: 240px;
  background: #fff;
  position: relative;
  padding: 15px;
  /deep/ .el-divider__text.is-center {
    color: #596a76;
  }
  .total-score {
    background: #0094e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 40px;
    left: 0;
    z-index: 100;
  }
}
.router-content {
  padding: 10px;
  .router-title {
    line-height: 40px;
    font-size: 14px;
    font-weight: 600;
    color: #0c2e41;
  }

  .router-index {
    position: relative;
    width: 28px;
    height: 28px;
    color: #41baff;
    line-height: 28px;
    font-weight: 600;
    background: #e3f4fe;
    text-align: center;
    display: inline-block;
    margin-bottom: 10px;
    margin-right: 10px;
    border-radius: 3px;
    text-decoration: none;
    border-radius: 6px;
    cursor: pointer;
    user-select: none;
    &:nth-child(10n) {
      margin-right: 0;
    }
  }

  .router-active,
  a:hover {
    background: #0094e4;
    // border: 1px solid #7fc2f6;
    color: #fff;
  }
}
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 240px);
  margin: 0 auto;
}
.paper-handler {
  width: 400px;
  background: #fff;
  position: relative;
  border-radius: 8px;
  .paper-title {
    line-height: 40px;
    background: #fff;
    width: 100%;
    height: 40px;
    font-size: 16px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding: 0 20px;
    border-radius: 8px 8px 0 0;
    border-bottom: 1px solid #eee;
    &::before {
      content: '';
      display: inline-block;
      position: relative;
      top: 5px;
      left: -10px;
      width: 2px;
      height: 20px;
      background: #0094e5;
    }
  }
}

.top {
  background: #fff;
  width: calc(1200px - 410px);
  height: 80px;
  padding: 30px 10px 10px 10px;
  color: #333;
  z-index: 100;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  border-radius: 8px 8px 0 0;
  .top-handler {
    display: none;
    position: absolute;
    right: 10px;
    top: 10px;
    /deep/ .el-button {
      padding: 5px 10px;
    }
    z-index: 1; // 滚动试题时不能点击
  }
  .top-title {
    flex: 1;
    text-align: center;
    font-size: 20px;
    vertical-align: middle;
    .top-score {
      position: relative;
      display: flex;
      float: right;
      font-size: 20px;
      flex-direction: column;
      top: -26px;
      right: 28px;
      z-index: 1;
      color: #ff5722;
    }
    .fenshudixian {
      font-size: 53px;
      margin-top: -25px;
      margin-right: -4px;
    }

    /deep/ .el-input__inner {
      text-align: center;
      border: 0;
      font-size: 20px;
      font-weight: bold;
    }
  }
}
/deep/ .el-drawer__header {
  margin-bottom: 0px;
}
.handler-content {
  padding: 10px 10px 10px;
}
.chapter {
  position: relative;
  padding: 15px 0px 0px 17px;
  /deep/ .el-input__inner {
    border: none;
    font-size: 18px;
  }
  /deep/ .el-textarea__inner {
    border: none;
    color: #557587;
  }
}
</style>
<style lang="scss">
.paper-content {
  background: #fff;
  width: calc(100% - 400px);
  margin-left: 10px;
  border-radius: 8px;
  &:hover {
    .top {
      .top-handler {
        display: inline-block;
      }
    }
  }
  .paper-content {
    overflow: auto;
  }
}
</style>