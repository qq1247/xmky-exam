<template>
  <div class="center-preview">
    <template v-if="paperQuestion.length">
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
                <el-radio-group class="children-option" v-model="child.answers">
                  <el-radio
                    class="option-item"
                    :key="index"
                    :label="String.fromCharCode(65 + index)"
                    v-for="(option, index) in child.options"
                  >
                    <div
                      class="flex-items-center"
                      v-html="`${String.fromCharCode(65 + index)}、${option}`"
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
                      v-html="`${String.fromCharCode(65 + index)}、${option}`"
                    ></div>
                  </el-checkbox>
                </el-checkbox-group>
              </template>

              <!-- 判断 -->
              <template v-if="child.type === 4">
                <el-radio-group class="children-option" v-model="child.answer">
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
    </template>
    <el-empty v-else description="暂无试卷"> </el-empty>
  </div>
</template>

<script>
import { paperQuestionList } from 'api/paper'
import { getQuick } from '@/utils/storage'
export default {
  data() {
    return { paperQuestion: [], paperName: '' }
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().id
    this.query()
  },
  methods: {
    // 查询试卷信息
    async query() {
      try {
        const res = await paperQuestionList({
          id: this.paperId,
        })
        res.data.map((item) => {
          item.chapter.show = true
        })
        this.paperQuestion = [...res.data]
      } catch (error) {
        this.$message.error(error.msg)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
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

.paper-handler {
  width: 500px;
  background: #fff;
  position: relative;
}

.paper-content {
  background: #fff;
  width: calc(100% - 500px);
  overflow: scroll;
  margin: 0 10px;
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
    padding: 10px;
    .chapter-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .chapter-name {
        flex: 1;
        margin-right: 20px;
        /deep/ .el-input__inner {
          border: 1px solid #fff;
          padding: 0 10px;
        }
        /deep/ .el-input__inner:hover,
        /deep/ .el-input__inner:focus {
          border: 1px solid #c0c4cc;
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
      line-height: 30px;
      border: none;
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
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 5px;
  margin-top: 10px;
  cursor: pointer;
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
</style>
