<template>
  <div class="container">
    <!-- 导航 -->
    <div class="head">
      <el-link
        class="head-left"
        :underline="false"
        @click="goBack"
        icon="el-icon-back"
        >返回</el-link
      >
      <span>{{ paperName }}</span>
      <el-link class="head-right" :underline="false">
        <i class="common common-explain"></i>
      </el-link>
    </div>
    <!-- 内容 -->
    <div class="content">
      <div class="content-left">
        <el-scrollbar
          wrap-style="overflow-x:hidden;"
          style="height: 100%"
          ref="leftScroll"
        >
          <div class="left-top">添加章节</div>
          <el-form
            :model="chapterForm"
            :rules="chapterForm.rules"
            ref="chapterForm"
            label-width="90px"
            class="chapter-form"
            size="mini"
          >
            <el-form-item label="题目类型" prop="name">
              <el-input
                placeholder="请输入章节名称"
                v-model.trim="chapterForm.name"
              ></el-input>
            </el-form-item>
            <el-form-item label="描述">
              <el-input
                placeholder="请输入描述"
                v-model="chapterForm.description"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="paperChapterAdd"
                >添加</el-button
              >
              <el-button
                type="primary"
                v-if="chapterForm.edit"
                @click="paperChapterEdit"
                >修改</el-button
              >
            </el-form-item>
          </el-form>
          <div class="left-top">添加试题</div>
          <el-form
            :model="queryForm"
            label-width="70px"
            class="paper-form"
            :inline="true"
            v-model="labelPosition"
            size="mini"
          >
            <el-form-item>
              <el-input
                placeholder="请查询试题分类"
                v-model="queryForm.questionTypeName"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-input
                placeholder="请输入题干"
                v-model="queryForm.title"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-select placeholder="请选择类型" v-model="queryForm.type">
                <el-option
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                  v-for="dict in queryForm.typeDictList"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select
                placeholder="请选择难度"
                v-model="queryForm.difficulty"
              >
                <el-option
                  :key="parseInt(dict.dictKey)"
                  :label="dict.dictValue"
                  :value="parseInt(dict.dictKey)"
                  v-for="dict in queryForm.difficultyDictList"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input
                placeholder="请输入分值"
                v-model="queryForm.score"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-input
                placeholder="请输入编号"
                v-model="queryForm.id"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="randomQueryQuestion()"
                >随机查询</el-button
              >
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="queryQuestion()"
                >查询</el-button
              >
            </el-form-item>
          </el-form>
          <div class="drags">
            <Draggable
              tag="div"
              class="drag-content"
              :list="paperList"
              :sort="false"
              :group="{ name: 'paper', put: false }"
              chosenClass="drag-active"
              animation="300"
              @end="sourceEnd"
              @choose="sourceChoose"
              :disabled="paperQuestion.lenght == 0 ? true : false"
              v-if="paperList.length > 0"
            >
              <transition-group>
                <div
                  class="drag-item"
                  v-for="item in paperList"
                  :key="item.id"
                  :id="item.id"
                >
                  <div
                    class="item-title"
                    v-html="`${item.id}、${item.title}`"
                  ></div>
                  <el-tag effect="dark" size="mini" type="warning">
                    {{ item.typeName }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="danger">
                    {{ item.difficultyName }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="warning"
                    >{{ item.score }}分</el-tag
                  >
                  <el-tag effect="plain" size="mini" type="info">
                    {{ item.updateUserName }}
                  </el-tag>
                </div>
              </transition-group>
            </Draggable>
            <div v-if="paperList.length == 0" class="data-null">
              <img class="data-img" src="../../assets/img/data-null.png" alt />
              <span class="data-tip">暂无此类型题目</span>
            </div>
          </div>
          <el-pagination
            background
            small
            layout="prev, pager, next"
            prev-text="上一页"
            next-text="下一页"
            hide-on-single-page
            :total="total"
            :page-size="pageSize"
            :current-page="curPage"
            @current-change="pageChange"
            :pager-count="5"
          ></el-pagination>
        </el-scrollbar>
      </div>

      <div class="content-center">
        <div class="paper-title">{{ paperName }}</div>
        <!-- <div class="paper-intro">{{}}</div> -->

        <div class="center-drag">
          <Draggable
            tag="div"
            :list="paperQuestion"
            group="paperParent"
            chosenClass="drag-question-active"
            animation="300"
            v-if="paperQuestion.length > 0"
            @update="chapterUpdate"
          >
            <div
              class="drag-item drag-content drag-parent"
              v-for="(item, index) in paperQuestion"
              :key="index"
            >
              <div class="chapter">
                <div class="chapter-item">
                  <div class="item-title">{{ item.chapter.name }}</div>
                  <div>
                    <el-button
                      @click="chapterEdit(item.chapter)"
                      class="btn"
                      icon="common common-edit"
                      round
                      size="mini"
                      >编辑</el-button
                    >
                    <el-button
                      @click="chapterDel(item.chapter)"
                      class="btn"
                      icon="common common-delete"
                      round
                      size="mini"
                      >删除</el-button
                    >
                    <el-button
                      @click="chapterClear(item.chapter, index)"
                      class="btn"
                      icon="common common-clear"
                      round
                      size="mini"
                      >清空试题</el-button
                    >
                    <el-button
                      @click="chapteFold(index)"
                      class="btn"
                      icon="common common-fold"
                      round
                      size="mini"
                      >折叠</el-button
                    >
                  </div>
                </div>
                <div class="chapter-description">
                  {{ item.chapter.description }}
                </div>
              </div>

              <Draggable
                tag="div"
                v-if="item.chapter.show"
                :class="[
                  'drag-content',
                  item.questionList.length != 0 ? 'drag-children' : '',
                ]"
                :list="item.questionList"
                group="paper"
                chosenClass="drag-question-active"
                animation="300"
                :data-id="item.chapter.id"
              >
                <template v-if="item.questionList.length > 0">
                  <div
                    class="children-content"
                    v-for="(child, index) in item.questionList"
                    :key="child.id"
                    :id="`p-${child.id}`"
                  >
                    <p v-html="index + 1 + '、' + child.title"></p>

                    <!-- 单选 -->
                    <template v-if="child.type === 1">
                      <el-radio-group
                        class="children-option"
                        v-model="child.answers"
                      >
                        <el-radio
                          class="option-item"
                          disabled
                          :key="index"
                          :label="String.fromCharCode(65 + index)"
                          v-for="(option, index) in child.options"
                        >
                          <div
                            class="flex-items-center"
                            v-html="
                              `${String.fromCharCode(65 + index)}、${option}`
                            "
                          ></div>
                        </el-radio>
                      </el-radio-group>
                    </template>

                    <!-- 多选 -->
                    <template v-if="child.type === 2">
                      <el-checkbox-group
                        class="children-option"
                        v-model="child.answers"
                      >
                        <el-checkbox
                          class="option-item"
                          disabled
                          :key="index"
                          :label="String.fromCharCode(65 + index)"
                          v-for="(option, index) in child.options"
                        >
                          <div
                            class="flex-items-center"
                            v-html="
                              `${String.fromCharCode(65 + index)}、${option}`
                            "
                          ></div>
                        </el-checkbox>
                      </el-checkbox-group>
                    </template>

                    <!-- 填空 -->
                    <!-- <template v-if="child.type === 3">
                      <div
                        class="option-item-text"
                        :key="index"
                        v-for="(option, index) in child.answers"
                      ></div>
                    </template> -->

                    <!-- 判断 -->
                    <template v-if="child.type === 4">
                      <el-radio-group
                        class="children-option"
                        v-model="child.answer"
                      >
                        <el-radio
                          class="option-item"
                          disabled
                          :key="index"
                          :label="option"
                          v-for="(option, index) in ['对', '错']"
                          >{{ option }}</el-radio
                        >
                      </el-radio-group>
                    </template>

                    <!-- 问答 -->
                    <!-- <template v-if="child.type === 5">
                      <div class="option-item-text">{{ child.answer }}</div>
                    </template> -->

                    <div class="children-analysis">
                      <el-row :gutter="10">
                        <template v-if="[1, 2, 4].includes(child.type)">
                          <el-col :span="2.5"> 【答案】： </el-col>
                          <el-col :span="21">
                            <div v-html="`${child.answers[0].answer}`"></div>
                          </el-col>
                        </template>
                        <template v-if="child.type === 3">
                          <el-col :span="2.5">【答案】：</el-col>
                          <el-col :span="21">
                            <div
                              v-for="(answer, index) in child.answers"
                              :key="answer.id"
                              class="answers-item"
                            >
                              <span>{{ `填空${index + 1}、` }}</span>
                              <span
                                class="answers-tag"
                                v-for="(ans, index) in answer.answer"
                                :key="index"
                                >{{ ans }}</span
                              >
                            </div>
                          </el-col>
                        </template>
                        <template v-if="child.type === 5">
                          <el-col :span="2.5"> 【答案】： </el-col>
                          <el-col :span="21">
                            <template v-if="child.ai === 1">
                              <div
                                v-for="(answer, index) in child.answers"
                                :key="answer.id"
                                class="answers-item"
                              >
                                <span>{{ `关键词${index + 1}、` }}</span>
                                <span
                                  class="answers-tag"
                                  v-for="(ans, index) in answer.answer"
                                  :key="index"
                                  >{{ ans }}</span
                                >
                              </div>
                            </template>
                            <div
                              v-if="child.ai === 2"
                              v-html="`${child.answers[0].answer}`"
                            ></div>
                          </el-col>
                        </template>
                      </el-row>
                      <el-row :gutter="10">
                        <el-col :span="2.5"> 【解析】： </el-col>
                        <el-col :span="21">
                          <div v-html="`${child.analysis}`"></div>
                        </el-col>
                      </el-row>
                    </div>

                    <div class="children-footer">
                      <div class="children-tags">
                        <el-tag effect="dark" size="mini" type="warning">
                          {{ child.typeName }}
                        </el-tag>
                        <el-tag effect="plain" size="mini" type="danger">
                          {{ child.difficultyName }}
                        </el-tag>
                        <el-tag effect="plain" size="mini" type="warning"
                          >{{ child.score }}分</el-tag
                        >
                        <el-tag effect="plain" size="mini" type="info">
                          {{ child.updateUserName }}
                        </el-tag>
                      </div>
                      <div class="children-buts">
                        <el-button
                          @click="setting(child)"
                          class="btn"
                          icon="el-icon-setting"
                          round
                          size="mini"
                          >设置</el-button
                        >
                        <el-button
                          @click="del(child.paperQuestionId)"
                          class="btn"
                          icon="el-icon-delete"
                          round
                          size="mini"
                          >删除</el-button
                        >
                      </div>
                    </div>
                  </div>
                </template>
                <div v-if="item.questionList.length == 0" class="data-null">
                  <i class="common common-drag data-img"></i>
                  <span class="data-tip">拖拽题目到此处</span>
                </div>
              </Draggable>
            </div>
          </Draggable>
          <div v-if="paperQuestion.length == 0" class="data-null">
            <img class="data-img" src="../../assets/img/data-null.png" alt />
            <span class="data-tip">暂无试卷信息</span>
          </div>
        </div>
      </div>

      <div class="content-right">
        <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
          <el-collapse v-model="collapseShow" v-if="paperQuestion.length > 0">
            <el-collapse-item
              v-for="(item, index) in paperQuestion"
              :key="item.id"
              :title="item.chapter.name"
              :name="index"
            >
              <div class="route-href">
                <div
                  :class="[
                    'href-item',
                    hrefPointer === `#p-${child.id}` ? 'href-item-active' : '',
                  ]"
                  v-for="(child, index) in item.questionList"
                  :key="child.id"
                >
                  <span @click="toHref(child.id)">{{ index + 1 }}</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
          <div v-if="paperQuestion.length == 0" class="data-null">
            <img class="data-img" src="../../assets/img/data-null.png" alt />
            <span class="data-tip">暂无题目导航信息</span>
          </div>
        </el-scrollbar>
      </div>
    </div>

    <el-dialog
      :visible.sync="settingForm.show"
      :show-close="false"
      width="40%"
      title="分值选项设置"
      ref="settingForm"
      :close-on-click-modal="false"
      @close="resetData('settingForm')"
    >
      <el-form
        :model="settingForm"
        :rules="settingForm.rules"
        ref="settingForm"
        label-width="60px"
      >
        <el-form-item label="分值" prop="score">
          <el-input-number
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
            v-model.number="settingForm.score"
            mini
          ></el-input-number>
        </el-form-item>

        <template v-if="settingForm.ai === 1">
          <template v-if="settingForm.type === 3 || settingForm.type === 5">
            <el-form-item
              v-for="(answer, index) in settingForm.answers"
              :key="index"
              :label="settingForm.type === 3 ? '填空' : '关键词'"
              :prop="`answers.${index}.score`"
              :rules="settingForm.rules.aiScore"
              :show-message="settingForm.ai === 1 ? true : false"
            >
              <el-input v-if="settingForm.ai === 1" v-model="answer.score">
                <template slot="append">分</template>
              </el-input>
            </el-form-item>
          </template>
        </template>

        <template v-if="settingForm.ai === 1">
          <el-row v-if="settingForm.type === 2">
            <el-col :span="5">
              <el-form-item>
                <el-checkbox-group v-model="settingForm.scoreOptions">
                  <el-tooltip
                    class="item"
                    content="默认全对得分"
                    effect="dark"
                    placement="top"
                  >
                    <el-checkbox label="1">漏选得分</el-checkbox>
                  </el-tooltip>
                </el-checkbox-group>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item
                v-if="settingForm.scoreOptions.length > 0"
                prop="multipScore"
                class="ai-score"
                :show-message="settingForm.ai === 1 ? true : false"
              >
                <el-input
                  v-if="settingForm.ai === 1"
                  v-model="settingForm.multipScore"
                >
                  <template slot="append">分</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item v-if="settingForm.type === 3">
            <el-checkbox-group v-model="settingForm.scoreOptions">
              <el-tooltip
                class="item"
                content="默认答案有顺序"
                effect="dark"
                placement="top"
              >
                <el-checkbox label="2">答案无顺序</el-checkbox>
              </el-tooltip>
              <el-tooltip
                class="item"
                content="默认大小写敏感"
                effect="dark"
                placement="top"
              >
                <el-checkbox label="3">大小写不敏感</el-checkbox>
              </el-tooltip>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item v-if="settingForm.type === 5">
            <el-checkbox-group v-model="settingForm.scoreOptions">
              <el-tooltip
                class="item"
                content="大小写不敏感"
                effect="dark"
                placement="top"
              >
                <el-checkbox label="3">大小写不敏感</el-checkbox>
              </el-tooltip>
            </el-checkbox-group>
          </el-form-item>
        </template>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="setScore" type="primary">设置</el-button>
        <el-button @click="settingForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { dictListPage } from '@/api/base'
import {
  paperQuestionList,
  paperChapterAdd,
  paperChapterEdit,
  paperChapterDel,
  paperQuestionClear,
  paperQuestionDel,
  paperUpdateScore,
  paperQuestionAdd,
} from '@/api/paper'
import { questionListPage, randomListPage } from '@/api/question'
import Draggable from 'vuedraggable'
export default {
  components: {
    Draggable,
  },
  data() {
    const validateAiScore = (rule, value, callback) => {
      if (this.settingForm.ai === 2) {
        return callback()
      }
      if (value === '') {
        return callback(new Error('请填写分数'))
      }
      if (value > this.settingForm.score || value <= 0) {
        return callback(new Error(`请填写合理分数`))
      }
      return callback()
    }

    const validateMultipScore = (rule, value, callback) => {
      if (
        this.settingForm.ai === 2 ||
        this.settingForm.scoreOptions.length === 0
      ) {
        this.settingForm.multipScore = ''
        return callback()
      }

      if (value === '') {
        return callback(new Error('请填写分数'))
      }
      if (value > this.settingForm.score || value <= 0) {
        return callback(new Error(`请填写合理分数`))
      }
      return callback()
    }
    return {
      labelPosition: 'left',
      hrefPointer: '',
      paperId: 0,
      paperTypeId: 0,
      paperName: '',
      pageSize: 5,
      curPage: 1,
      total: 0,
      collapseShow: 0,
      chapterForm: {
        id: 0,
        name: '',
        description: '',
        edit: false,
        rules: {
          name: [
            { required: true, message: '请输入章节名称', trigger: 'blur' },
          ],
        },
      },
      queryForm: {
        id: '', // 编号
        title: '', // 题干
        type: null, // 类型
        difficulty: null, // 难度
        score: '', // 分数
        questionTypeName: '', // 试题分类name
        questionTypeId: 1, // 试题分类id
        difficultyDictList: [], // 难度列表
        typeDictList: [], // 类型列表
      },
      paperList: [],
      paperQuestion: [],
      settingForm: {
        show: false,
        paperQuestionId: null,
        type: 1,
        score: 1,
        answers: [],
        scoreOptions: [],
        multipScore: '',
        rules: {
          score: [{ required: true, message: '请输入分值', trigger: 'change' }],
          aiScore: [{ validator: validateAiScore }],
          multipScore: [{ validator: validateMultipScore }],
        },
      },
    }
  },
  created() {
    const { id, name } = this.$route.query
    this.paperId = id
    this.paperName = name
    this.init()
  },
  methods: {
    // 返回
    goBack() {
      this.$router.back()
    },
    // 初始化
    async init() {
      await this.query()
      await this.queryQuestion()
      // 初始化类型下拉框
      const typeDictData = await dictListPage({
        dictIndex: 'QUESTION_TYPE',
      })
      this.queryForm.typeDictList = typeDictData.data.list
      // 初始化难度下拉框
      const difficultyDictData = await dictListPage({
        dictIndex: 'QUESTION_DIFFICULTY',
      })
      this.queryForm.difficultyDictList = difficultyDictData.data.list
    },
    // 查询试卷信息
    async query() {
      try {
        const res = await paperQuestionList({
          id: this.paperId,
        })
        res.data.map((item) => {
          item.chapter.show = true
          item.questionList.map((question) => {})
        })
        this.paperQuestion = [...res.data]
      } catch (error) {
        this.$tools.message(error.msg, 'error')
      }
    },
    // 查询试题
    async queryQuestion() {
      const res = await questionListPage({
        id: this.queryForm.id,
        type: this.queryForm.type,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.score,
        scoreEnd: this.queryForm.score,
        exPaperId: this.paperId,
        state: 1,
        curPage: this.curPage,
        pageSize: this.pageSize,
      }).catch((err) => {})
      res?.code === 200
        ? ((this.paperList = res.data.list), (this.total = res.data.total))
        : this.$tools.message('请刷新重新获取试题！', 'error')
    },
    // 随机查询试题
    async randomQueryQuestion() {
      const res = await randomListPage({
        id: this.queryForm.id,
        type: this.queryForm.type,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.score,
        scoreEnd: this.queryForm.score,
        exPaperId: this.paperId,
        // state: 1,
        curPage: 1,
        pageSize: this.pageSize,
      })
      res?.code === 200
        ? (this.paperList = res.data.list)
        : this.$tools.message('请刷新重新获取试题！', 'error')
    },
    // 添加章节
    paperChapterAdd() {
      this.$refs['chapterForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        const res = await paperChapterAdd({
          name: this.chapterForm.name,
          description: this.chapterForm.description,
          paperId: this.paperId,
          type: 1,
        }).catch((err) => {})
        this.refreshData(res, '添加章节')
      })
    },
    // 回显章节信息
    chapterEdit({ id, name, description }) {
      this.chapterForm.id = id
      this.chapterForm.name = name
      this.chapterForm.description = description
      this.$refs.leftScroll.wrap.scrollTop = 0
      this.chapterForm.edit = true
    },
    // 编辑章节
    async paperChapterEdit() {
      const res = await paperChapterEdit({
        id: this.chapterForm.id,
        name: this.chapterForm.name,
        description: this.chapterForm.description,
      }).catch((err) => {})
      this.refreshData(res, '编辑章节')
    },
    // 删除章节
    chapterDel({ id }) {
      this.$confirm(`删除章节将删除章节内的试题，是否删除？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperChapterDel({ id }).catch((err) => {})
          this.refreshData(res, '删除章节')
        })
        .catch(() => {})
    },
    // 章节折叠
    chapteFold(index) {
      this.paperQuestion[index].chapter.show =
        !this.paperQuestion[index].chapter.show
    },
    // 清空试卷试题
    async chapterClear({ id }, index) {
      if (this.paperQuestion[index].questionList.length == 0) {
        this.$tools.message('试题已清空，请重新添加试题！', 'warning')
        return
      }
      this.$confirm(`确认清空章节下的所有试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperQuestionClear({ chapterId: id }).catch(
            (err) => {}
          )
          this.refreshData(res, '清空试题')
        })
        .catch(() => {})
    },
    // 删除试题
    del(paperQuestionId) {
      this.$confirm(`确认删除该试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperQuestionDel({ paperQuestionId }).catch(
            (err) => {}
          )
          this.refreshData(res, '删除试题')
        })
        .catch(() => {})
    },
    // 更新页面数据
    refreshData(res, title) {
      res?.code === 200
        ? (this.$tools.message(`${title}成功！`),
          this.query(),
          this.$tools.resetData(this, 'chapterForm'),
          this.queryQuestion())
        : this.$tools.message(`${title}失败！`, 'error')
    },
    // 设置分数
    setting(data) {
      this.settingForm.paperQuestionId = data.paperQuestionId
      this.settingForm.type = data.type
      this.settingForm.ai = data.ai
      this.settingForm.score = data.score
      this.settingForm.scoreOptions = data.scoreOptions
        ? data.scoreOptions.split(',')
        : []
      this.settingForm.answers = data.answers
      this.settingForm.multipScore =
        data.type === 2 && data.ai === 1 && data.scoreOptions.length > 0
          ? data.answers[0].score
          : ''
      this.settingForm.show = true
    },
    // 设置分数
    setScore() {
      let paperQuestionAnswerId = [],
        paperQuestionAnswerScore = []
      if (this.settingForm.ai === 1) {
        paperQuestionAnswerId = this.settingForm.answers.reduce((acc, cur) => {
          acc.push(cur.id)
          return acc
        }, [])

        paperQuestionAnswerScore = this.settingForm.answers.reduce(
          (acc, cur) => {
            acc.push(cur.score)
            return acc
          },
          []
        )
      }

      this.$refs['settingForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const updateScore = await paperUpdateScore({
          paperQuestionId: this.settingForm.paperQuestionId,
          score: this.settingForm.score,
          paperQuestionAnswerId: paperQuestionAnswerId,
          paperQuestionAnswerScore: paperQuestionAnswerScore,
        }).catch((err) => {})

        const updateScoreOptions = await paperUpdateScoreOptions({
          paperQuestionId: this.settingForm.paperQuestionId,
          scoreOptions: this.settingForm.scoreOptions,
        }).catch((err) => {})

        if (updateScore?.code === 200 && updateScoreOptions?.code === 200) {
          this.$tools.message('编辑成功！')
          this.settingForm.show = false
          this.query()
        }
      })
    },
    // 选择拖拽原题
    sourceChoose(e) {
      if (this.paperQuestion.length == 0) {
        this.$tools.message('请添加章节！', 'error')
        return
      }
    },
    // 拖拽原题结束
    async sourceEnd(e) {
      const chapterId = e.to.dataset.id
      const questionIds = e.item.id
      const res = await paperQuestionAdd({
        chapterId,
        questionIds,
      }).catch((err) => {})
      if (res?.code !== 200) return false
      this.query()
      this.queryQuestion()
    },
    // 章节移动
    chapterUpdate(e) {
      console.log(e)
    },
    pageChange(val = 1) {
      this.curPage = val
      this.queryQuestion(val)
    },
    // 定位锚点
    toHref(id) {
      this.hrefPointer = `#p-${id}`
      document.documentElement.scrollTop =
        document.querySelector(this.hrefPointer).offsetTop - 50
    },
    // 重置数据
    resetData(name) {
      this.$tools.resetData(this, name)
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 0;
  padding-bottom: 0;
}

.head {
  width: 100%;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 5000;
  .head-left {
    color: #fff;
  }
  .head-right {
    color: #fff;
    .common {
      font-size: 20px;
    }
  }
}

.content {
  width: 100%;
  padding: 0 20px 10px;
  margin: 70px auto 0;
}

.drag-content {
  width: 100%;
  border: 1px solid #d8d8d8;
  border-radius: 5px;
}

.drag-active {
  transform: scale(0.98);
  border: 1px solid #0094e5;
  border-radius: 5px;
  border-bottom: 1px solid #0094e5 !important;
  transition: all 0.15s ease-in-out;
}

.drag-question-active {
  border: 1px solid #0094e5 !important;
  box-shadow: 0 0 16px 2px rgba(0, 148, 229, 0.15);
  transition: all 0.15s ease-in-out;
}

.content-left {
  width: 300px;
  background: #fff;
  position: fixed;
  top: 70px;
  left: 20px;
  bottom: 60px;
  z-index: 100;
  .left-top {
    width: 100%;
    height: 45px;
    line-height: 45px;
    text-align: left;
    padding-left: 10px;
    font-size: 14px;
    color: #333;
    position: relative;
    border-bottom: 1px solid #d8d8d8;
    &::before {
      content: '';
      display: block;
      position: absolute;
      top: 15px;
      left: 0;
      width: 2px;
      height: 15px;
      background: rgb(30, 159, 255);
    }
  }
  .chapter-form {
    padding: 15px 10px 0 10px;
    border-bottom: 10px solid #f2f4f5;
    .el-form-item {
      margin-bottom: 15px;
    }
  }
  .paper-form {
    padding: 15px 0 0 10px;
    .el-form-item,
    .el-button {
      width: 135px;
    }
    .el-form-item {
      margin-bottom: 15px;
    }
  }
  .drags {
    padding: 10px;
    .drag-item {
      border-bottom: 1px solid #e8e8e8;
      padding: 15px;
      font-size: 14px;
      cursor: move;
    }
    .item-title {
      margin-bottom: 10px;
      display: flex;
      align-items: center;
    }
    .el-tag {
      margin-left: 10px;
    }
  }
}

.content-center {
  background: #fff;
  margin-left: 310px;
  width: calc(100% - 560px);
  .center-drag {
    width: 100%;
    padding: 10px;
    .drag-item {
      cursor: move;
      margin-bottom: 10px;
      border-radius: 5px;
    }
  }
  .chapter {
    display: flex;
    flex-direction: column;
    padding: 0 10px;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .item-title {
        font-size: 16px;
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
    .chapter-description {
      font-size: 14px;
      color: #999;
      padding-bottom: 10px;
      margin-top: -5px;
    }
  }
  .drag-parent {
    padding: 10px;
  }
  .drag-children {
    border: none;
  }
  .paper-title {
    font-size: 16px;
    color: #333;
    padding: 20px 0 10px 10px;
  }
  .paper-intro {
    font-size: 12px;
    color: #666;
    padding: 0 10px 15px;
    border-bottom: 1px solid #d8d8d8;
  }
  .item-title {
    line-height: 40px;
  }
  .children-content {
    border: 1px solid #d8d8d8;
    font-size: 14px;
    box-sizing: border-box;
    &:not(:last-child) {
      border-bottom: none;
    }
    p {
      line-height: 40px;
      padding-left: 10px;
      background: #e5f4fc;
      display: flex;
      align-items: center;
    }
  }
  .children-option {
    padding: 10px 0 0 25px;
  }
  .option-item,
  .flex-items-center {
    display: flex;
    justify-items: center;
    line-height: 30px;
  }
  /deep/ .el-radio__input,
  /deep/ .el-checkbox__input {
    padding-top: 7px;
  }
  .option-item-text {
    border-bottom: 1px solid #d8d8d8;
    padding: 20px 10px 5px;
    color: #333;
    margin: 0 25px 0;
  }
  .children-analysis {
    line-height: 30px;
    padding-left: 20px;
    margin: 15px 0;
    font-size: 13px;
    color: #666;
  }
  .answers-item {
    width: 100%;
    span {
      width: 100%;
      display: inline;
      word-wrap: break-word;
      word-break: normal;
    }
    .answers-tag {
      background: #cdd2f6;
      color: #fff;
      padding: 3px 10px;
      border-radius: 3px;
      &:not(:last-child) {
        margin-right: 10px;
      }
    }
  }
  .el-tag {
    margin-right: 6px;
  }
  .children-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
  }
  .btn {
    padding: 5px 10px;
  }
}

.content-right {
  width: 240px;
  background: #fff;
  position: fixed;
  top: 70px;
  right: 20px;
  bottom: 60px;
  z-index: 100;
  .time-title {
    background-color: rgb(30, 159, 255);
    width: 100%;
    height: 40px;
    line-height: 40px;
    text-align: center;
    padding-left: 10px;
    font-size: 14px;
    color: #fff;
  }
  .time-residue {
    font-size: 12px;
    font-weight: bold;
    line-height: 50px;
    text-align: center;
  }
  .route-title {
    background: #0094e5;
    color: #fff;
    line-height: 14px;
    line-height: 40px;
    padding: 0 10px;
  }
}

/deep/ .el-collapse-item__header {
  background: #f1f1f1;
  color: #000;
  padding: 0 10px;
  height: 40px;
  line-height: 40px;
}

.route-href {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  width: 100%;
  padding: 10px 15px 0;
  .href-item {
    width: 20%;
    height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    span {
      display: inline-block;
      width: 30px;
      height: 30px;
      line-height: 30px;
      text-align: center;
      font-size: 14px;
      border: 1px solid #d8d8d8;
    }
  }
  .href-item-active {
    span {
      border: 1px solid #0094e5;
      color: #0094e5;
    }
  }
}

.data-null {
  padding-top: 30px;
  .data-img {
    width: 64px;
    height: 64px;
  }
  .data-tip {
    margin: 0 auto 20px;
  }
}

.pagination {
  font-weight: 400;
  text-align: center;
}

.el-radio,
.el-checkbox {
  margin-right: 10px;
}

.box-card-no-border {
  border: none;
}

/deep/ .el-card__body {
  padding: 5px;
}

.el-input /deep/.el-input-group__prepend {
  background-color: #fff;
  border: 0px;
  width: 38px;
}
/deep/ .el-form-item--mini.el-form-item,
.el-form-item--small.el-form-item {
  margin-bottom: 24px;
}

/deep/ .el-form-item__error {
  line-height: 20px;
}
</style>
