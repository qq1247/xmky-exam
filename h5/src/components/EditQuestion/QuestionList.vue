<!--
 * @Description: 试题列表
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-19 14:23:55
 * @LastEditors: Che
 * @LastEditTime: 2022-01-12 10:54:00
-->
<template>
  <div>
    <!-- top -->
    <div class="top">
      <div class="top-title">试题列表</div>
      <div class="top-handler">
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
    <template v-if="list.questionList.length > 0">
      <!-- 试题卡片 -->
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
        <!-- 编辑模式 -->
        <div class="center-card-bottom" v-if="!preview">
          <div class="card-bottom-left">
            <el-tag class="center-tag-danger" size="mini" type="danger">{{
              question.type | typeName
            }}</el-tag>

            <el-tag class="center-tag-purple" effect="plain" size="mini">{{
              question.difficulty | difficultyName
            }}</el-tag>

            <el-tag effect="plain" size="mini" type="warning">{{
              ['', '智能', '非智能'][question.ai]
            }}</el-tag>

            <el-tag effect="plain" size="mini" type="danger"
              >{{ question.score }}分</el-tag
            >

            <el-tag effect="plain" size="mini">{{
              question.createUserName
            }}</el-tag>

            <el-tag
              :type="question.state == 1 ? 'info' : 'danger'"
              effect="plain"
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
        <!-- 预览模式 -->
        <div class="" v-else>
          <!-- 单选 -->
          <template v-if="question.type === 1">
            <el-radio-group class="question-option" v-if="question.options">
              <el-radio
                disabled
                :key="index"
                :label="String.fromCharCode(65 + index)"
                class="option-item"
                v-for="(option, index) in question.options"
              >
                <div
                  class="flex-items-center"
                  v-html="`${String.fromCharCode(65 + index)}、${option}`"
                ></div>
              </el-radio>
            </el-radio-group>
          </template>

          <!-- 多选 -->
          <template v-if="question.type === 2">
            <el-checkbox-group
              class="question-option"
              v-if="question.options"
              v-model="checkBoxOption"
            >
              <el-checkbox
                disabled
                :key="index"
                class="option-item"
                :label="String.fromCharCode(65 + index)"
                v-for="(option, index) in question.options"
              >
                <div
                  class="flex-items-center"
                  v-html="`${String.fromCharCode(65 + index)}、${option}`"
                ></div>
              </el-checkbox>
            </el-checkbox-group>
          </template>

          <!-- 判断 -->
          <template v-if="question.type === 4">
            <el-radio-group class="question-option" v-if="question.options">
              <el-radio
                disabled
                :key="index"
                :label="option"
                class="option-item"
                v-for="(option, index) in ['对', '错']"
                >{{ option }}</el-radio
              >
            </el-radio-group>
          </template>

          <!-- 问答 -->
          <template v-if="question.type === 5">
            <el-input
              disabled
              :rows="2"
              class="question-text"
              placeholder="请输入内容"
              type="textarea"
            ></el-input>
          </template>
        </div>
      </el-card>
    </template>
    <el-empty v-else description="暂无试题">
      <img slot="image" src="../../assets/img/data-null.png" alt="" />
    </el-empty>
  </div>
</template>

<script>
import { getOneDict } from '@/utils/getDict'
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
    return {
      preview: false,
      checkBoxOption: [],
    }
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
  methods: {
    setType(e) {
      this.preview = e
    },
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
.top {
  background: #fff;
  width: calc(100% - 20px);
  height: 40px;
  color: #333;
  position: absolute;
  top: 0;
  left: 10px;
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
  border: 1px solid #eee;
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
  &:nth-of-type(2) {
    margin-top: 50px;
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

.question-option {
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
.question-text {
  margin: 4px 1%;
  width: 98%;
}

/deep/.el-textarea.is-disabled .el-textarea__inner,
/deep/.el-input.is-disabled .el-input__inner {
  background-color: #fff;
  border-color: #0094e5;
  color: #000;
  cursor: default;
}
/deep/.el-checkbox__input.is-disabled.is-checked + span.el-checkbox__label,
/deep/.el-radio__input.is-disabled.is-checked + span.el-radio__label {
  color: #0094e5;
  cursor: default;
}
/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner,
/deep/.el-radio__input.is-disabled.is-checked .el-radio__inner {
  background-color: #0094e5;
  border-color: #0094e5;
}
/deep/.el-checkbox__input.is-disabled + span.el-checkbox__label,
/deep/.el-radio__input.is-disabled + span.el-radio__label {
  color: #000;
  cursor: default;
}
/deep/.el-checkbox__input.is-disabled .el-checkbox__inner,
/deep/.el-radio__input.is-disabled .el-radio__inner {
  background-color: #fff;
  cursor: default;
}
/deep/.el-checkbox__input.is-disabled.is-checked .el-checkbox__inner::after {
  border-color: #fff;
}
</style>
