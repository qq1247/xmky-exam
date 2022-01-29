<template>
  <div class="container">
    <div class="content">
      <div class="content-left">
        <el-scrollbar
          wrap-style="overflow-x:hidden;"
          style="height: 100%"
          ref="leftScroll"
        >
          <div class="left-top">添加试题</div>
          <el-form
            :model="queryForm"
            label-width="70px"
            class="paper-form"
            :inline="true"
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
              <el-select
                clearable
                placeholder="请选择类型"
                v-model="queryForm.type"
              >
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
                clearable
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
            <el-row>
              <el-col :span="12">
                <el-form-item>
                  <el-button type="primary" @click="randomQueryQuestion()"
                    >随机查询</el-button
                  >
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item>
                  <el-button type="primary" @click="search">查询</el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div class="drags">
            <Draggable
              tag="div"
              class="drag-content"
              v-model="paperList"
              :sort="false"
              :group="{ name: 'paper', put: false }"
              chosenClass="drag-active"
              animation="300"
              @end="sourceEnd"
              :disabled="paperQuestion.length == 0 ? true : false"
            >
              <transition-group>
                <div
                  class="drag-item"
                  v-for="item in paperList"
                  :key="item.id"
                  :id="item.id"
                >
                  <div class="item-title">
                    <span>{{ item.id }}、</span>
                    <div v-html="`${item.title}`"></div>
                  </div>
                  <el-tag effect="dark" size="mini" type="primary">
                    {{ item.type | typeName }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="danger">
                    {{ item.difficulty | difficultyName }}
                  </el-tag>
                  <el-tag effect="plain" size="mini" type="warning"
                    >{{ item.score }}分</el-tag
                  >
                  <el-tag effect="plain" size="mini" type="info">
                    {{ item.createUserName }}
                  </el-tag>
                </div>
              </transition-group>
            </Draggable>
            <el-empty v-if="!paperList.length" description="暂无此类型题目">
            </el-empty>
          </div>
          <el-pagination
            background
            small
            layout="prev, pager, next"
            hide-on-single-page
            :total="total"
            :page-size="pageSize"
            :current-page="curPage"
            @current-change="pageChange"
          ></el-pagination>
        </el-scrollbar>
      </div>

      <div class="content-center">
        <div class="top">
          <div class="top-title">{{ paperName }}</div>
          <div class="top-handler">
            <!-- 编辑、预览模式 -->
            <div class="type">
              <div
                class="type-item common common-edit"
                :class="!preview ? 'active' : ''"
                @click="setType(false)"
                title="编辑模式"
              ></div>
              <div
                class="type-item common common-preview"
                :class="preview ? 'active' : ''"
                @click="setType(true)"
                title="预览模式"
              ></div>
            </div>
          </div>
        </div>

        <div class="center-drag" v-if="!preview">
          <Draggable
            tag="div"
            v-model="paperQuestion"
            group="paperParent"
            chosenClass="drag-question-active"
            animation="300"
            v-if="paperQuestion.length > 0"
            @update="chapterMove"
          >
            <div
              class="drag-item drag-content drag-parent"
              v-for="(item, index) in paperQuestion"
              :key="index"
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
                        editorListener(
                          'chapterName',
                          e.target.value,
                          item.chapter
                        )
                    "
                  ></el-input>
                  <!-- <div class="item-title">{{ item.chapter.name }}</div> -->
                  <div class="chapter-handler">
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
                      @click="batchSetting(item.chapter, index)"
                      class="btn"
                      icon="common common-setting"
                      round
                      size="mini"
                      >批量设置</el-button
                    >
                    <el-button
                      @click="chapterFold(index)"
                      class="btn"
                      icon="common common-fold"
                      round
                      size="mini"
                      >折叠</el-button
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

              <Draggable
                tag="div"
                v-if="item.chapter.show"
                :class="[
                  'drag-content',
                  item.questionList.length != 0 ? 'drag-children' : '',
                ]"
                v-model="item.questionList"
                group="paper"
                chosenClass="drag-question-active"
                animation="300"
                :data-id="item.chapter.id"
                @end="questionMove"
              >
                <template v-if="item.questionList.length > 0">
                  <div
                    class="children-content"
                    v-for="(child, index) in item.questionList"
                    :key="child.id"
                    :id="`p-${child.id}`"
                  >
                    <div class="item-title">
                      <span>{{ index + 1 }}、</span>
                      <div v-html="`${child.title}`"></div>
                    </div>
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
                        <template v-if="[1, 4].includes(child.type)">
                          <el-col :span="2.5"> 【答案】： </el-col>
                          <el-col :span="21">
                            <div
                              v-if="child.answers && child.answers.length > 0"
                              v-html="`${child.answers[0].answer}`"
                            ></div>
                          </el-col>
                        </template>
                        <template v-if="child.type === 2">
                          <el-col :span="2.5"> 【答案】： </el-col>
                          <el-col :span="21">
                            <div
                              v-if="child.answers && child.answers.length > 0"
                            >
                              <span
                                v-for="answer in child.answers"
                                :key="answer.id"
                                >{{ answer.answer.join('，') }}</span
                              >
                            </div>
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
                              <span>{{
                                `填空${$tools.intToChinese(index + 1)}、`
                              }}</span>
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
                                <span>{{
                                  `关键词${$tools.intToChinese(index + 1)}、`
                                }}</span>
                                <span
                                  class="answers-tag"
                                  v-for="(ans, index) in answer.answer"
                                  :key="index"
                                  >{{ ans }}</span
                                >
                              </div>
                            </template>
                            <div
                              v-if="
                                child.ai === 2 &&
                                child.answers &&
                                child.answers.length > 0
                              "
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
                          {{ child.type | typeName }}
                        </el-tag>
                        <el-tag effect="plain" size="mini" type="danger">
                          {{ child.difficulty | difficultyName }}
                        </el-tag>
                        <el-tag effect="plain" size="mini" type="warning"
                          >{{ child.score }}分</el-tag
                        >
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
                          @click="del(child.id)"
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
                <el-empty v-else description="拖拽题目到此处">
                  <template slot="image">
                    <i class="common common-drag" style="font-size: 35px"></i>
                  </template>
                </el-empty>
              </Draggable>
            </div>
          </Draggable>
          <div class="add-chapter" @click="paperChapterAdd">
            <i class="common common-click"></i>
            <span>点击添加章节</span>
          </div>
          <!-- <el-empty v-else description="暂无试卷"> </el-empty> -->
        </div>

        <div class="center-preview" v-if="preview">
          <div v-for="(item, index) in paperQuestion" :key="index">
            <div class="chapter">
              <div class="chapter-name">{{ item.chapter.name }}</div>
              <div class="chapter-name" v-html="item.chapter.description"></div>
            </div>

            <div
              :class="[
                'drag-content',
                item.questionList.length != 0 ? 'drag-children' : '',
              ]"
            >
              <template v-if="item.questionList.length > 0">
                <div
                  class="children-content"
                  v-for="(child, index) in item.questionList"
                  :key="child.id"
                  :id="`p-${child.id}`"
                >
                  <div
                    class="item-title"
                    :style="{
                      marginBottom: child.type == 3 ? '10px' : '0',
                      borderBottom: '1px solid #f3f3f3',
                    }"
                  >
                    <span>{{ index + 1 }}、</span>
                    <div v-html="`${child.title}`"></div>
                  </div>

                  <!-- 单选 -->
                  <template v-if="child.type === 1">
                    <el-radio-group
                      class="children-option"
                      v-model="child.answers"
                    >
                      <el-radio
                        class="option-item"
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

                  <!-- 判断 -->
                  <template v-if="child.type === 4">
                    <el-radio-group
                      class="children-option"
                      v-model="child.answer"
                    >
                      <el-radio
                        class="option-item"
                        :key="index"
                        :label="option"
                        v-for="(option, index) in ['对', '错']"
                        >{{ option }}</el-radio
                      >
                    </el-radio-group>
                  </template>
                </div>
              </template>
              <el-empty v-else description="暂无试题"> </el-empty>
            </div>
          </div>
        </div>
      </div>

      <div class="content-right">
        <div class="total-score">总分：{{ totalScore }}</div>
        <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
          <template v-if="paperQuestion.length > 0">
            <div
              class="router-content"
              v-for="(item, index) in paperQuestion"
              :key="index"
            >
              <div class="router-title" v-if="item.questionList">
                第{{ $tools.intToChinese(index + 1) }}章（共{{
                  item.questionList.length
                }}题，合计{{ computeChapterScore(item.questionList) }}分）
              </div>
              <div class="router-link" v-if="item.questionList">
                <a
                  :class="[
                    'router-index',
                    routerIndex === child.id ? 'router-active' : '',
                  ]"
                  @click="toHref(child.id)"
                  v-for="(child, index) in item.questionList"
                  :key="child.id"
                  >{{ index + 1 }}</a
                >
              </div>
            </div>
          </template>
          <el-empty v-else description="暂无试题导航"> </el-empty>
        </el-scrollbar>
      </div>
    </div>

    <el-dialog
      :visible.sync="settingForm.show"
      :show-close="false"
      width="40%"
      title="分值选项设置"
      :close-on-click-modal="false"
      @close="resetData('settingForm')"
    >
      <el-form
        :model="settingForm"
        :rules="settingForm.rules"
        ref="settingForm"
        label-width="70px"
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
              :label="
                settingForm.type === 3
                  ? `填空${$tools.intToChinese(index + 1)}`
                  : `关键词${$tools.intToChinese(index + 1)}`
              "
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
          <div class="setting-checkbox" v-if="settingForm.type === 2">
            漏选得<el-input
              v-if="settingForm.ai === 1"
              v-model="settingForm.multipScore"
            >
            </el-input
            >分
            <!-- <el-form-item
              prop="multipScore"
              class="ai-score"
              :show-message="settingForm.ai === 1 ? true : false"
            >
            </el-form-item> -->
          </div>
          <el-form-item v-if="settingForm.type === 3">
            <el-checkbox-group v-model="settingForm.scoreOptions">
              <el-tooltip
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
          <el-form-item v-if="settingForm.type === 5">
            <el-checkbox-group v-model="settingForm.scoreOptions">
              <el-tooltip
                class="item"
                content="大小写不敏感"
                effect="dark"
                placement="top"
              >
                <el-checkbox :label="3">大小写不敏感</el-checkbox>
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

    <el-dialog
      :visible.sync="batchForm.show"
      :show-close="false"
      width="40%"
      title="批量设置"
      :close-on-click-modal="false"
      @close="resetData('batchForm')"
    >
      <el-form
        :model="batchForm"
        :rules="batchForm.rules"
        ref="batchForm"
        label-width="80px"
      >
        <el-form-item label="每题得分" prop="score">
          <el-input-number
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
            v-model.number="batchForm.score"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="选项设置">
          <el-checkbox-group v-model="batchForm.scoreOptions">
            <el-tooltip
              class="item"
              content="默认题目分数的一半"
              effect="dark"
              placement="top"
            >
              <el-checkbox :label="1">漏选得分</el-checkbox>
            </el-tooltip>
            <el-tooltip
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
        <el-form-item
          label="漏选得分"
          prop="multipScore"
          v-if="batchForm.scoreOptions.includes(1)"
        >
          <el-input-number
            :max="100"
            :min="1"
            :step="1"
            controls-position="right"
            v-model.number="batchForm.multipScore"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="setBatchScore" type="primary">设置</el-button>
        <el-button @click="batchForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { getOneDict } from '@/utils/getDict'
import {
  paperQuestionList,
  paperChapterAdd,
  paperChapterEdit,
  paperChapterDel,
  paperQuestionClear,
  paperQuestionDel,
  paperScoreUpdate,
  paperQuestionAdd,
  paperChapterMove,
  paperQuestionMove,
  paperTotalScoreUpdate,
  paperUpdateBatchScore,
} from 'api/paper'
import { questionListPage } from 'api/question'
import TinymceEditor from 'components/TinymceEditor/Index.vue'
import PageShow from 'components/PaperContent/PageShow.vue'
import Draggable from 'vuedraggable'
import { paperGet } from 'api/paper'
export default {
  components: {
    Draggable,
    PageShow,
    TinymceEditor,
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
      preview: false,
      routerIndex: '',
      paperId: 0,
      paperState: 2,
      paperTypeId: 0,
      markType: 1,
      paperName: '',
      pageSize: 5,
      curPage: 1,
      total: 0,
      chapterForm: {
        id: 0,
        name: '章节名称',
        description: '章节描述',
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
        questionId: null,
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
      batchForm: {
        show: false,
        id: null,
        score: '',
        scoreOptions: [],
        multipScore: '',
        rules: {
          score: [
            { required: true, message: '请设置每题得分', trigger: 'change' },
          ],
        },
      },
    }
  },
  computed: {
    totalScore() {
      if (this.paperQuestion.length == 0) {
        return 0
      }

      const questionList = this.paperQuestion.reduce((acc, cur) => {
        acc.push(...cur.questionList)
        return acc
      }, [])

      const totalScore = questionList.reduce((acc, cur) => {
        return acc + cur.score
      }, 0)

      return totalScore
    },
  },
  filters: {
    typeName(data) {
      return getOneDict('QUESTION_TYPE').find((item) => item.no === data)
        .dictValue
    },
    difficultyName(data) {
      return getOneDict('QUESTION_DIFFICULTY').find((item) => item.no === data)
        .dictValue
    },
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
    setType(e) {
      this.preview = e
    },
    // 初始化
    async init() {
      await this.query()
      await this.queryQuestion()
      this.queryForm.typeDictList = getOneDict('QUESTION_TYPE')
      this.queryForm.difficultyDictList = getOneDict('QUESTION_DIFFICULTY')
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
        this.$message.error(error.msg)
      }
    },
    // 查询试题
    async queryQuestion() {
      const res = await questionListPage({
        id: this.queryForm.id,
        ai: this.markType === 1 ? 1 : '',
        state: 1,
        type: this.queryForm.type,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.score,
        scoreEnd: this.queryForm.score,
        exPaperId: this.paperId,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      res?.code === 200
        ? ((this.paperList = res.data.list), (this.total = res.data.total))
        : this.$message.error('请刷新重新获取试题！')
    },
    // 计算章节分数
    computeChapterScore(data) {
      const num = data.reduce((acc, cur) => {
        return acc + cur.score
      }, 0)
      return num
    },
    // 条件查询
    search() {
      this.curPage = 1
      this.queryQuestion()
    },
    // 随机查询试题
    async randomQueryQuestion() {
      const res = await questionListPage({
        id: this.queryForm.id,
        ai: this.markType === 1 ? 1 : '',
        type: this.queryForm.type,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        difficulty: this.queryForm.difficulty,
        scoreStart: this.queryForm.score,
        scoreEnd: this.queryForm.score,
        exPaperId: this.paperId,
        state: 1,
        rand: 'rand',
        curPage: 1,
        pageSize: this.pageSize,
      })
      res?.code === 200
        ? (this.paperList = res.data.list)
        : this.$message.error('请刷新重新获取试题！')
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
    setChapterName(e) {},
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
      // this.refreshData(res, '编辑章节')
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
    // 章节折叠
    chapterFold(index) {
      this.paperQuestion[index].chapter.show =
        !this.paperQuestion[index].chapter.show
    },
    // 展示批量设置
    batchSetting({ id }) {
      this.batchForm.id = id
      this.batchForm.show = true
    },
    // 清空试卷试题
    async chapterClear({ id }, index) {
      if (this.paperQuestion[index].questionList.length == 0) {
        this.$message.warning('试题已清空，请重新添加试题！')
        return
      }
      this.$confirm(`确认清空章节下的所有试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperQuestionClear({ chapterId: id })
          this.refreshData(res, '清空试题')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 删除试题
    del(questionId) {
      this.$confirm(`确认删除该试题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperQuestionDel({ id: this.paperId, questionId })
          this.refreshData(res, '删除试题')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 更新页面数据
    refreshData(res, title) {
      res?.code === 200
        ? (this.$message(`${title}成功！`), this.query(), this.queryQuestion())
        : this.$message.error(`${title}失败！`)
    },
    // 设置分数
    setting(data) {
      this.settingForm.questionId = data.id
      this.settingForm.type = data.type
      this.settingForm.ai = data.ai
      this.settingForm.score = data.score
      this.settingForm.scoreOptions = data.scoreOptions ? data.scoreOptions : []
      this.settingForm.answers = data.answers
      this.settingForm.multipScore =
        data.type === 2 && data.ai === 1 && data.scoreOptions
          ? data.answers[0].score
          : ''
      this.settingForm.show = true
    },
    // 设置分数
    setScore() {
      let paperQuestionAnswerScore = []
      if (this.settingForm.ai === 1) {
        if (this.settingForm.type === 2) {
          paperQuestionAnswerScore = this.settingForm.answers.reduce((acc) => {
            acc.push(this.settingForm.multipScore)
            return acc
          }, [])
        } else {
          paperQuestionAnswerScore = this.settingForm.answers.reduce(
            (acc, cur) => {
              acc.push(Number(cur.score))
              return acc
            },
            []
          )
        }
      }

      this.$refs['settingForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        let params = {
          id: this.paperId,
          questionId: this.settingForm.questionId,
          score: this.settingForm.score,
          subScores:
            this.settingForm.type === 2
              ? this.settingForm.scoreOptions.length === 0
                ? []
                : paperQuestionAnswerScore
              : paperQuestionAnswerScore,
        }

        if (
          [2, 3, 5].includes(this.settingForm.type) &&
          this.settingForm.ai === 1
        ) {
          params = { ...params, scoreOptions: this.settingForm.scoreOptions }
        }

        const updateScore = await paperScoreUpdate(params)

        if (updateScore?.code === 200) {
          this.$message.success('编辑成功！')
          this.settingForm.show = false
          this.query()
        }
      })
    },
    // 批量设置分数
    async setBatchScore() {
      const res = await paperUpdateBatchScore({
        chapterId: this.batchForm.id,
        score: this.batchForm.score,
        subScores: this.batchForm.scoreOptions.includes(1)
          ? this.batchForm.multipScore
          : null,
        scoreOptions: this.batchForm.scoreOptions,
      })
      if (res?.code === 200) {
        this.$message.success('编辑成功！')
        this.batchForm.show = false
        this.query()
      }
    },
    // 拖拽原题结束
    async sourceEnd({ to, item }) {
      const chapterId = to.dataset.id
      const questionIds = Number(item.id)
      const res = await paperQuestionAdd({
        chapterId,
        questionIds,
      })
      if (res?.code !== 200) return false
      if (!this.paperList.length) {
        this.search()
      }
    },
    // 章节移动
    async chapterMove({ newIndex, oldIndex }) {
      const sourceId = this.paperQuestion[newIndex].chapter.id
      const targetId = this.paperQuestion[oldIndex].chapter.id
      const res = await paperChapterMove({
        sourceId,
        targetId,
      })

      if (res?.code === 200) {
        this.query()
      }
    },
    // 试题移动
    async questionMove({ to, from, newIndex, oldIndex }) {
      const toChapterId = to.dataset.id
      const sourceQuestion = this.filterQuestion(toChapterId)
      const sourceId = sourceQuestion[newIndex].id
      const targetId = sourceQuestion[oldIndex].id

      const res = await paperQuestionMove({
        id: this.paperId,
        sourceId,
        targetId,
      })

      if (res?.code === 200) {
        this.query()
      }
    },
    // 筛选数据
    filterQuestion(chapterId) {
      const list = this.paperQuestion.filter(
        (item) => item.chapter.id == chapterId
      )
      return list[0].questionList
    },
    // 分页变化
    pageChange(val = 1) {
      this.curPage = val
      this.queryQuestion(val)
    },
    // 定位锚点
    toHref(id) {
      this.routerIndex = id
      document
        .querySelector(`#p-${id}`)
        .scrollIntoView({ block: 'end', inline: 'nearest' })
    },
    // 重置数据
    resetData(name) {
      this.$refs[name].resetFields()
    },
  },
  beforeDestroy() {
    if (this.paperState === '1' || !Number(this.id)) return false
    paperTotalScoreUpdate({
      id: this.paperId,
    })
  },
}
</script>

<style lang="scss" scoped>
.container {
  margin-top: -20px;
}
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 60px);
  margin: 0 auto;
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
  position: relative;
  .left-top {
    background: #fff;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding-left: 30px;
    border-bottom: 1px solid #eee;
    &::before {
      content: '';
      display: inline-block;
      position: relative;
      top: 2px;
      left: -10px;
      width: 2px;
      height: 16px;
      background: #0095e5;
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
    padding: 50px 0 0 10px;
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
      &:last-child {
        border-bottom: none;
      }
    }
    .el-tag {
      margin-left: 10px;
    }
  }
}

.content-center {
  background: #fff;
  width: calc(100% - 560px);
  overflow: scroll;
  margin: 0 10px;
  .top {
    background: #fff;
    width: calc(100% - 610px);
    height: 40px;
    color: #333;
    position: fixed;
    z-index: 100;
    font-weight: 600;
    padding-left: 30px;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
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
    .top-handler {
      display: flex;
      align-items: center;
    }
  }
  .type {
    display: flex;
    justify-content: flex-end;
    background: #fff;
    border-radius: 4px;
    margin-left: 20px;
    box-shadow: -7px 0 13px -5px rgba(0, 0, 0, 0.2);
    .type-item {
      width: 40px;
      height: 40px;
      line-height: 40px;
      text-align: center;
      color: #555;
      cursor: pointer;
      &:first-child {
        border-top-left-radius: 4px;
        border-bottom-left-radius: 4px;
      }
      &:last-child {
        border-top-right-radius: 4px;
        border-bottom-right-radius: 4px;
      }
    }
    .active {
      background: #0095e5;
      color: #fff;
    }
  }
  .center-drag {
    width: 100%;
    padding: 10px;
    padding-top: 50px;
    .drag-item {
      cursor: move;
      margin-bottom: 10px;
      border-radius: 5px;
    }
  }
  .center-preview {
    width: 100%;
    padding: 50px 10px 10px;
    .chapter {
      line-height: 40px;
    }
    .drag-content {
      border: none;
    }
  }
  .chapter {
    display: flex;
    flex-direction: column;
    padding: 5px 0;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .chapter-name {
        flex: 1;
        margin-right: 20px;
        /deep/.el-input__inner {
          border: none;
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
      border: none;
    }
    .chapter-description {
      padding-left: 10px;
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
  .children-content {
    border-left: 1px solid #f3f3f3;
    border-right: 1px solid #f3f3f3;
    font-size: 14px;
    box-sizing: border-box;
    &:not(:last-child) {
      border-bottom: none;
    }
    .item-title {
      background: #e5f4fc;
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

.add-chapter {
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.03);
  height: 100px;
  cursor: pointer;
  margin-top: 10px;
  border-radius: 5px;
  i {
    font-size: 25px;
    margin-right: 10px;
  }
}

.item-title {
  font-size: 14px;
  text-align: left;
  line-height: 50px;
  display: flex;
  padding-left: 7px;
  p {
    margin: 0;
    padding: 0;
  }
}

.content-right {
  width: 240px;
  background: #fff;
  position: relative;
  padding-top: 40px;
  .total-score {
    background: #0094e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
  }
}

.router-content {
  margin: 0 20px;
  .router-title {
    line-height: 40px;
    font-size: 13px;
  }

  .router-index {
    position: relative;
    width: 28px;
    height: 28px;
    color: #c3c3c3;
    line-height: 28px;
    background: #f4f4f4;
    border: 1px solid #e5e5e5;
    text-align: center;
    display: inline-block;
    margin-bottom: 10px;
    margin-right: 10px;
    border-radius: 3px;
    text-decoration: none;
    cursor: pointer;
    user-select: none;
    &:nth-child(5n) {
      margin-right: 0;
    }
  }

  .router-active,
  a:hover {
    background: #c2e4ff;
    border: 1px solid #7fc2f6;
    color: #83c3f7;
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

.setting-checkbox {
  display: flex;
  align-items: center;
  margin-left: 80px;
  /deep/ .el-input {
    width: 80px;
    margin: 0 10px;
  }
}
</style>
