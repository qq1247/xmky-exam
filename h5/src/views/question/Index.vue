<template>
  <div class="container">
    <template v-if="hashChildren">
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <div>
          <el-form-item>
            <el-input
              v-model="queryForm.queryName"
              placeholder="请输入名称"
              class="query-input"
            />
          </el-form-item>
        </div>
        <el-form-item>
          <el-button
            icon="el-icon-search"
            type="primary"
            @click="search"
          >查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 内容 -->
      <div class="content">
        <div class="exam-list">
          <AddCard add-title="添加题库" @addCard="add" />
          <IndexCard
            v-for="(item, index) in typeList"
            :key="index"
            :data="item"
            name="question"
            @del="del"
            @edit="edit"
            @role="role"
            @open="open"
            @move="move"
            @detail="goDetail"
            @statistics="statistics"
            @txtImport="txtImport"
          />
        </div>
        <!-- 分页 -->
        <el-pagination
          background
          layout="prev, pager, next"
          prev-text="上一页"
          next-text="下一页"
          hide-on-single-page
          :total="total"
          :page-size="pageSize"
          :current-page="curPage"
          @current-change="pageChange"
        />
      </div>
    </template>

    <router-view v-else />
  </div>
</template>

<script>
import { questionTypeListPage, questionTypeAdd } from 'api/question'
import AddCard from 'components/ListCard/AddCard.vue'
import IndexCard from '@/components/ListCard/IndexCard.vue'
export default {
  components: {
    AddCard,
    IndexCard
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      curPage: 1,
      queryForm: {
        queryName: ''
      },
      typeList: []
    }
  },
  computed: {
    hashChildren() {
      return !(this.$route.matched.length > 2)
    }
  },
  mounted() {
    this.query()
  },
  methods: {
    // 查询分类数据
    async query() {
      const typeList = await questionTypeListPage({
        name: this.queryForm.queryName,
        curPage: this.curPage,
        pageSize: this.pageSize
      })

      if (!typeList.data.list.length) {
        const res = await questionTypeAdd({
          name: '我的试题'
        })

        if (res?.code === 200) {
          this.query()
        }
      }

      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 搜索
    search() {
      this.curPage = 1
      this.query()
    },
    // 添加题库
    add() {
      this.$tools.switchTab('QuestionIndexSetting', {
        id: 0,
        tab: '1'
      })
    },
    // 编辑分类
    edit({ id }) {
      this.$tools.switchTab('QuestionIndexSetting', {
        id,
        tab: '1'
      })
    },
    // 试题详情
    goDetail({ id }) {
      this.$tools.switchTab('QuestionIndexEdit', {
        id
      })
    },
    // 权限人员信息
    role({ id }) {
      this.$tools.switchTab('QuestionIndexSetting', {
        id,
        tab: '2'
      })
    },
    // 合并试题
    move({ id }) {
      this.$tools.switchTab('QuestionIndexSetting', {
        id,
        tab: '3'
      })
    },
    // 试题开放
    open({ id }) {
      this.$tools.switchTab('QuestionIndexOpen', {
        id
      })
    },
    // 统计
    statistics({ id }) {
      this.$tools.switchTab('QuestionIndexStatistics', {
        id
      })
    },
    // 文本导入
    txtImport({ id }) {
      this.$tools.switchTab('QuestionIndexTxtImport', {
        id
      })
    },
    // 删除分类
    del({ id }) {
      this.$tools.switchTab('QuestionIndexSetting', {
        id,
        tab: '4'
      })
    },
    // 分页切换
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    }
  }
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';
</style>
