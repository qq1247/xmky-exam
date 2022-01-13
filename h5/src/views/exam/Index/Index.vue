<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-22 09:46:18
 * @LastEditors: Che
 * @LastEditTime: 2022-01-12 09:45:03
-->
<template>
  <div class="container">
    <template v-if="hashChildren">
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="form-inline search">
        <div>
          <el-form-item>
            <el-input
              placeholder="请输入名称"
              v-model="queryForm.queryName"
              class="query-input"
            ></el-input>
          </el-form-item>
        </div>
        <el-form-item>
          <el-button @click="search" icon="el-icon-search" type="primary"
            >查询</el-button
          >
        </el-form-item>
      </el-form>
      <!-- 内容 -->
      <div class="content">
        <div class="exam-list">
          <AddCard add-title="添加考试分类" @addCard="add"></AddCard>
          <IndexCard
            v-for="(item, index) in typeList"
            :key="index"
            :data="item"
            name="exam"
            @edit="edit"
            @del="del"
            @detail="goDetail"
          ></IndexCard>
        </div>
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
    <router-view v-else></router-view>
  </div>
</template>

<script>
import { examTypeListPage } from 'api/exam'
import AddCard from 'components/ListCard/AddCard.vue'
import IndexCard from 'components/ListCard/IndexCard.vue'

export default {
  components: {
    AddCard,
    IndexCard,
  },
  data() {
    return {
      pageSize: 5,
      total: 1,
      curPage: 1,
      queryForm: {
        queryName: '',
      },
      typeList: [],
    }
  },
  computed: {
    hashChildren() {
      return this.$route.matched.length > 2 ? false : true
    },
  },
  mounted() {
    this.query()
  },
  methods: {
    // 查询分类信息
    async query() {
      const typeList = await examTypeListPage({
        name: this.queryForm.queryName,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      this.typeList = typeList.data.list
      this.total = typeList.data.total
    },
    // 条件查询
    search() {
      this.curPage = 1
      this.query()
    },
    // 添加试题分类
    add() {
      this.$tools.switchTab('ExamIndexSetting', {
        id: 0,
        tab: '1',
      })
    },
    // 编辑分类
    edit({ id }) {
      this.$tools.switchTab('ExamIndexSetting', {
        id,
        tab: '1',
      })
    },
    // 删除分类
    del({ id }) {
      this.$tools.switchTab('ExamIndexSetting', {
        id,
        tab: '2',
      })
    },
    // 考试子分类
    goDetail({ id }) {
      this.$tools.switchTab('ExamList', {
        id,
      })
    },
    // 分页切换
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
