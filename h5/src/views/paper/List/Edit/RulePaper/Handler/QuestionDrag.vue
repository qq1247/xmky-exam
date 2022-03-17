<template>
  <div class="handler-content">
    <el-form :model="queryForm" size="small">
      <el-row :gutter="20">
        <el-col :span="12">
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
        </el-col>
        <el-col :span="12">
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
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-input
              placeholder="请查询试题分类"
              v-model="queryForm.questionTypeName"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <el-input
              placeholder="请输入题干"
              v-model="queryForm.title"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-input
              placeholder="请输入分值"
              v-model="queryForm.score"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <el-input
              placeholder="请输入编号"
              v-model="queryForm.id"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
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
      layout="prev, pager, next"
      hide-on-single-page
      :total="total"
      :page-size="pageSize"
      :current-page="curPage"
      @current-change="pageChange"
    ></el-pagination>
  </div>
</template>

<script>
import { getQuick } from '@/utils/storage'
import { getOneDict } from '@/utils/getDict'
import { paperQuestionAdd, paperGet } from 'api/paper'
import { questionListPage } from 'api/question'
import Draggable from 'vuedraggable'
export default {
  components: {
    Draggable,
  },
  data() {
    return {
      total: 1,
      curPage: 1,
      pageSize: 5,
      markType: 1,
      paperList: [],
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
    }
  },
  filters: {
    typeName(data) {
      return getOneDict('QUESTION_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
    difficultyName(data) {
      return getOneDict('QUESTION_DIFFICULTY').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    },
  },
  async created() {
    this.queryForm.typeDictList = getOneDict('QUESTION_TYPE')
    this.queryForm.difficultyDictList = getOneDict('QUESTION_DIFFICULTY')
    const res = await paperGet({
      id: this.$route.params.id || getQuick().id,
    })
    this.markType = res.data.markType
    this.queryQuestion()
  },
  methods: {
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
        exPaperId: this.$route.params.id || getQuick().id,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      res?.code === 200
        ? ((this.paperList = res.data.list), (this.total = res.data.total))
        : this.$message.error('请刷新重新获取试题！')
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
        exPaperId: this.$route.params.id || getQuick().id,
        state: 1,
        rand: 'rand',
        curPage: 1,
        pageSize: this.pageSize,
      })
      res?.code === 200
        ? (this.paperList = res.data.list)
        : this.$message.error('请刷新重新获取试题！')
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
    // 分页变化
    pageChange(val = 1) {
      this.curPage = val
      this.queryQuestion(val)
    },
  },
}
</script>

<style lang="scss" scoped>
.handler-content {
  padding: 50px 10px 10px;
}

.el-select,
.el-form-item,
.el-button {
  width: 100%;
}

.drags {
  .drag-content {
    width: 100%;
    border: 1px solid #d8d8d8;
    border-radius: 5px;
  }
  .drag-item {
    border-bottom: 1px solid #e8e8e8;
    padding: 15px;
    font-size: 14px;
    cursor: move;
    &:last-child {
      border-bottom: none;
    }
  }
  .item-title {
    font-size: 14px;
    text-align: left;
    line-height: 30px;
    display: flex;
    padding-left: 7px;
    p {
      margin: 0;
      padding: 0;
    }
  }
  .el-tag {
    margin-right: 15px;
    margin-top: 5px;
    &:nth-of-type(1) {
      margin-left: 50px;
    }
  }
}
/deep/ .el-pagination {
  padding: 20px 0 0;
}
</style>
