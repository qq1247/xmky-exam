<!--
 * @Description: 试题列表
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-19 14:23:55
 * @LastEditors: Che
 * @LastEditTime: 2021-11-29 15:44:08
-->
<template>
  <div>
    <template v-if="list.questionList.length > 0">
      <el-card
        :class="['center-card', question.id == id ? 'center-card-active' : '']"
        shadow="hover"
        v-for="question in list.questionList"
        :key="question.id"
        @click.native="showDetails(question.id)"
      >
        <div class="center-card-top">
          <span>{{ question.id }}、</span>
          <div v-html="`${question.title}`"></div>
        </div>
        <div class="center-card-bottom">
          <div class="card-bottom-left">
            <el-tag class="center-tag-danger" size="mini" type="danger">{{
              question.typeName
            }}</el-tag>
            <el-tag class="center-tag-purple" effect="plain" size="mini">{{
              question.difficultyName
            }}</el-tag>
            <el-tag effect="plain" size="mini" type="danger"
              >{{ question.score }}分</el-tag
            >
            <el-tag effect="plain" size="mini">{{
              question.updateUserName
            }}</el-tag>
            <el-tag
              :type="question.state == 1 ? 'info' : 'danger'"
              effect="dark"
              size="mini"
              >{{ question.state == 1 ? '发布' : '草稿' }}</el-tag
            >
          </div>
          <div class="card-bottom-right">
            <el-button
              plain
              round
              class="btn"
              size="mini"
              type="primary"
              icon="el-icon-document-copy"
              @click.stop="copy(question.id)"
              >复制</el-button
            >
            <template v-if="question.state !== 0">
              <el-button
                plain
                round
                class="btn"
                size="mini"
                type="primary"
                icon="el-icon-delete"
                @click.stop="del(question.id)"
                >删除</el-button
              >
            </template>
            <template v-if="question.state === 2">
              <el-button
                plain
                round
                class="btn"
                size="mini"
                type="primary"
                icon="el-icon-document"
                @click.stop="questionEdit(question.id)"
                >编辑</el-button
              >
              <el-button
                plain
                round
                class="btn"
                size="mini"
                type="primary"
                icon="el-icon-share"
                @click.stop="publish(question.id, question.state)"
                >发布</el-button
              >
            </template>
          </div>
        </div>
      </el-card>
      <!-- 分页 -->
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="list.total"
        :page-size="list.pageSize"
        :current-page="list.curPage"
        @current-change="pageChange"
      ></el-pagination>
    </template>
    <el-empty v-else description="暂无试题">
      <img slot="image" src="../../assets/img/data-null.png" alt="" />
    </el-empty>
  </div>
</template>

<script>
export default {
  components: {},
  props: {
    list: {
      type: Object,
      default: () => {},
    },
    id: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {}
  },
  methods: {
    pageChange(curPage) {
      this.$emit('pageChange', curPage)
    },
    showDetails(id) {
      this.$emit('showDetails', id)
    },
    questionEdit(id) {
      this.$emit('questionEdit', id)
    },
    copy(id) {
      this.$emit('copy', id)
    },
    del(id) {
      this.$emit('del', id)
    },
    publish(id, state) {
      this.$emit('publish', id, state)
    },
  },
}
</script>

<style lang="scss" scoped>
.center-card {
  cursor: pointer;
  margin: 0 10px 10px 10px;
  padding: 0 5px;
  display: flex;
  flex-direction: column;
  &:hover {
    border: 1px solid #0095e5;
    .card-bottom-right {
      display: block;
    }
  }
}
.center-card-active {
  border: 1px solid #0095e5;
}
.center-card-top {
  font-size: 14px;
  text-align: left;
  display: flex;
  p {
    margin: 0;
    padding: 0;
  }
}
.center-card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 30px;
  margin: 10px 0 -5px;
  .el-tag {
    margin-right: 8px;
  }
  .center-tag-danger {
    background-color: #feeddc;
    border-color: #fddbb9;
    color: #fa8718;
    height: 20px;
    line-height: 18px;
  }
  .center-tag-purple {
    border-color: #daddf9;
    color: #838eea;
    height: 20px;
    line-height: 18px;
  }
  .card-bottom-right {
    display: none;
  }
  .btn {
    padding: 5px 10px;
  }
}

/deep/.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #0095e5;
  color: #fff;
}

/deep/.el-pagination.is-background .btn-next,
/deep/.el-pagination.is-background .btn-prev,
/deep/.el-pagination.is-background .el-pager li {
  margin: 0 3px;
  min-width: 35px;
  border: 1px solid #d4dfd9;
  background-color: #fff;
  padding: 0 10px;
}
</style>
