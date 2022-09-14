<template>
  <div class="container">
    <template v-if="hasChildren">
      <!-- 题库查询 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <div>
          <el-form-item>
            <el-input
              v-model="queryForm.name"
              placeholder="请输入题库名称"
              class="query-input"
            />
          </el-form-item>
        </div>
        <el-form-item>
          <el-button icon="el-icon-search" type="primary" @click="query(1)">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 题库列表 -->
      <div class="content">
        <div class="exam-list">
          <AddCard add-title="添加题库" @addCard="add" />
          <div
            v-for="(questionType, index) in queryList.list"
            :key="`questionType${index}`"
            class="exam-item"
          >
            <div class="exam-content">
              <div class="title ellipsis">
                {{ questionType.name }}
              </div>
              <div class="content-info ellipsis"></div>
              <div class="content-info ellipsis">
                <span>试题数量：{{ questionType.questionNum }}</span>
              </div>
              <div class="handler">
                <span data-title="修改" @click="edit(questionType.id)">
                  <i class="common common-edit" />
                </span>
                <span data-title="删除" @click="del(questionType.id)">
                  <i class="common common-delete" />
                </span>
                <span data-title="合并" @click="move(questionType.id)">
                  <i class="common common-move" />
                </span>
                <span data-title="文本导入" @click="txtImport(questionType.id)">
                  <i class="common common-template-down" />
                </span>
                <span data-title="试题列表" @click="questionList(questionType.id)">
                  <i class="common common-list-row" />
                </span>
                <span class="last-span">
                  <i class="common common-more-row" />
                  <div class="handler-more">
                    <div class="more-item" @click="statistics(questionType.id)">
                      <i class="common common-statistics" />试题统计
                    </div>
                  </div>
                </span>
              </div>
            </div>
          </div>
        </div>
        <!-- 分页 -->
        <el-pagination
          background
          layout="prev, pager, next"
          prev-text="上一页"
          next-text="下一页"
          hide-on-single-page
          :total="queryList.total"
          :page-size="queryList.pageSize"
          :current-page="queryList.curPage"
          @current-change="query()"
        />
      </div>
    </template>
    <router-view v-else />
  </div>
</template>

<script>
import { questionTypeListpage, questionTypeAdd } from 'api/question'
import AddCard from 'components/ListCard/AddCard.vue'
export default {
  components: {
    AddCard,
  },
  data() {
    return {
      queryForm: {
        name: null, // 题库名称
      },
      queryList: {
        curPage: 1, // 当前第几页
        pageSize: 5, // 每页多少条
        total: 0, // 总条数
        list: [], // 数据列表
      },
    }
  },
  computed: {
    hasChildren() {
      return !(this.$route.matched.length > 2)
    },
  },
  mounted() {
    this.query()
  },
  methods: {
    // 题库查询
    async query(curPage) {
      // 题库查询
      if (curPage) {
        this.queryList.curPage = curPage
      }

      let {data: { list, total }} = await questionTypeListpage({
        ...this.queryForm,
        curPage: this.queryList.curPage,
        pageSize: this.queryList.pageSize,
      })

      this.queryList.total = total
      this.queryList.list = list

        // 如果是第一次访问，自动添加一个默认题库
      if (!list.length) {
        let res = await questionTypeAdd({
          name: '我的题库',
        })

        this.query()
      }
    },
    // 题库添加
    add() {
      this.$tools.switchTab('QuestionIndexSetting', {
        questionTypeId: 0,
        tab: '1',
      })
    },
    // 题库修改
    edit(id) {
      this.$tools.switchTab('QuestionIndexSetting', {
        questionTypeId: id,
        tab: '1',
      })
    },
    // 试题列表
    questionList(id) {
      this.$tools.switchTab('QuestionIndexList', {
        questionTypeId: id,
      })
    },
    // 合并试题
    move(id) {
      this.$tools.switchTab('QuestionIndexSetting', {
        questionTypeId: id,
        tab: '2',
      })
    },
    // 题库导入
    txtImport(id) {
      this.$tools.switchTab('QuestionIndexTxtImport', {
        questionTypeId: id,
      })
    },
    // 题库删除
    del(id) {
      this.$tools.switchTab('QuestionIndexSetting', {
        questionTypeId: id,
        tab: '3',
      })
    },
    // 题库统计
    statistics(id) {
      this.$tools.switchTab('QuestionIndexStatistics', {
        questionTypeId: id
      })
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
