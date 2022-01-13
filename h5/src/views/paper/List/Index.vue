<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-22 09:46:18
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 13:25:41
-->
<template>
  <div class="container">
    <template v-if="hashChildren">
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.name"
            class="query-input"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="search" icon="el-icon-search" type="primary"
            >查询</el-button
          >
        </el-form-item>
      </el-form>
      <!-- 内容 -->
      <div class="content">
        <div class="exam-list">
          <AddCard add-title="添加试卷" @addCard="add"></AddCard>
          <ListCard
            v-for="(item, index) in paperList"
            :key="index"
            :data="item"
            name="paperList"
            @del="del"
            @edit="edit"
            @copy="copy"
            @publish="publish"
            @composition="composition"
          ></ListCard>
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
        ></el-pagination>
      </div>
    </template>
    <router-view></router-view>
  </div>
</template>

<script>
import { paperListPage } from 'api/paper'
import ListCard from 'components/ListCard/ListCard.vue'
import AddCard from 'components/ListCard/AddCard.vue'

export default {
  components: {
    AddCard,
    ListCard,
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      curPage: 1,
      queryForm: {
        name: '',
        paperTypeId: 0,
      },
      paperList: [],
    }
  },
  computed: {
    hashChildren() {
      return this.$route.matched.length > 2 ? false : true
    },
  },
  mounted() {
    this.queryForm.paperTypeId = this.$route.params.id
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const paperList = await paperListPage({
        paperTypeId: this.queryForm.paperTypeId,
        curPage: this.curPage,
        pageSize: this.pageSize,
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
        tab: '1',
      })
    },
    // 编辑分类
    edit({ id }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        tab: '1',
      })
    },
    // 考试发布
    publish({ id }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        tab: '3',
      })
    },
    // 复制分类
    copy({ id }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        tab: '2',
      })
    },
    // 删除分类
    del({ id }) {
      this.$tools.switchTab('PaperListSetting', {
        id,
        paperTypeId: this.$route.params.id,
        tab: '4',
      })
    },
    // 组合试卷
    composition({ id }) {
      this.$tools.switchTab('PaperListEdit', {
        id,
        paperTypeId: this.$route.params.id,
      })
    },
    // 切换分页
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    },
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/list-card.scss';
</style>
