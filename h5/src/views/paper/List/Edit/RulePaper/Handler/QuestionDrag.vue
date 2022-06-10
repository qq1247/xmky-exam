<template>
  <div class="handler-content">
    <el-form :model="queryForm" size="small">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-select
              v-model="queryForm.type"
              clearable
              placeholder="请选择类型"
            >
              <el-option
                v-for="dict in queryForm.typeDictList"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <el-select
              v-model="queryForm.difficulty"
              clearable
              placeholder="请选择难度"
            >
              <el-option
                v-for="dict in queryForm.difficultyDictList"
                :key="parseInt(dict.dictKey)"
                :label="dict.dictValue"
                :value="parseInt(dict.dictKey)"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-input
              v-model="queryForm.questionTypeName"
              placeholder="请查询试题分类"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <el-input v-model="queryForm.title" placeholder="请输入题干" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-input v-model="queryForm.score" placeholder="请输入分值" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <el-input v-model="queryForm.id" placeholder="请输入编号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-button
              type="primary"
              @click="randomQueryQuestion()"
            >随机查询</el-button>
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
        v-model="paperList"
        tag="div"
        class="drag-content"
        :sort="false"
        :group="{ name: 'paper', put: false }"
        chosen-class="drag-active"
        animation="300"
        @end="sourceEnd"
      >
        <transition-group>
          <div
            v-for="item in paperList"
            :id="item.id"
            :key="item.id"
            class="drag-item"
          >
            <div class="item-title">
              <span>{{ item.id }}、</span>
              <div class="render-title" v-html="`${item.title}`" />
            </div>
            <el-tag effect="dark" size="mini" type="primary">
              {{ item.type | typeName }}
            </el-tag>
            <el-tag effect="plain" size="mini" type="danger">
              {{ item.difficulty | difficultyName }}
            </el-tag>
            <el-tag
              effect="plain"
              size="mini"
              type="warning"
            >{{ item.score }}分</el-tag>
            <el-tag effect="plain" size="mini" type="info">
              {{ item.createUserName }}
            </el-tag>
          </div>
        </transition-group>
      </Draggable>
      <el-empty v-if="!paperList.length" description="暂无此类型题目" />
    </div>
    <el-pagination
      background
      layout="prev, pager, next"
      hide-on-single-page
      :total="total"
      :page-size="pageSize"
      :current-page="curPage"
      @current-change="pageChange"
    />
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
    Draggable
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
    }
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
        typeDictList: [] // 类型列表
      }
    }
  },
  async created() {
    this.queryForm.typeDictList = getOneDict('QUESTION_TYPE')
    this.queryForm.difficultyDictList = getOneDict('QUESTION_DIFFICULTY')
    const res = await paperGet({
      id: this.$route.params.id || getQuick().id
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
        pageSize: this.pageSize
      })
      if (res?.code === 200) {
        this.total = res.data.total
        this.paperList = res.data.list
        this.paperList.map((item) => {
          item.analysisShow = false
        })
      } else {
        this.$message.error('请刷新重新获取试题！')
      }
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
        pageSize: this.pageSize
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
        questionIds
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
    }
  }
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
    .render-title {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
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
