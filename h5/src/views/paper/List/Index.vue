<template>
  <div class="container">
    <template v-if="hashChildren">
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <el-form-item>
          <el-input
            v-model="queryForm.name"
            placeholder="请输入名称"
            class="query-input"
          />
        </el-form-item>
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
          <AddCard add-title="添加试卷" @addCard="add" />
          <ListCard
            v-for="(item, index) in paperList"
            :key="index"
            :data="item"
            name="paperList"
            @del="del"
            @edit="edit"
            @copy="copy"
            @cheat="cheat"
            @publish="publish"
            @composition="composition"
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
    <router-view />
  </div>
</template>

<script>
import { paperListpage } from 'api/paper'
import ListCard from 'components/ListCard/ListCard.vue'
import AddCard from 'components/ListCard/AddCard.vue'

export default {
  components: {
    AddCard,
    ListCard
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      curPage: 1,
      queryForm: {
        name: '',
        paperTypeId: 0
      },
      paperList: []
    }
  },
  computed: {
    hashChildren() {
      return !(this.$route.matched.length > 2)
    }
  },
  mounted() {
    this.queryForm.paperTypeId = this.$route.params.id
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const paperList = await paperListpage({
        paperTypeId: this.queryForm.paperTypeId,
        name: this.queryForm.name,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      this.paperList = paperList.data.list
      this.total = paperList.data.total
    },
    // 条件查询
    search() {
      this.curPage = 1
      this.query()
    },
    // 添加分类
    add() {
      this.$tools.switchTab('PaperListSetting', {
        id: 0,
        paperTypeId: this.$route.params.id,
        genType: 0,
        tab: '1'
      })
    },
    // 编辑分类
    edit({ id, genType }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        genType,
        tab: '1'
      })
    },
    // 复制分类
    copy({ id, genType }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        genType,
        tab: '2'
      })
    },
    // 考试发布
    publish({ id, genType }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        genType,
        tab: '3'
      })
    },
    // 防作弊
    cheat({ id, genType }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        genType,
        tab: '4'
      })
    },
    // 删除分类
    del({ id, genType }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        genType,
        tab: '5'
      })
    },
    // 组合试卷
    composition({ id, genType }) {
      this.$tools.switchTab('PaperListEdit', {
        id,
        paperTypeId: this.$route.params.id,
        genType
      })
    },
    // 切换分页
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    }
  }
}
</script>

<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
