<template>
  <div class="handler-content">
    <el-form :model="queryForm" size="small">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-input
              v-model="queryForm.questionTypeName"
              placeholder="请选择题库"
            />
          </el-form-item>
        </el-col>
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
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item>
            <el-input v-model="queryForm.title" placeholder="请输入题干" />
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
    <el-card
      v-for="question in questionList"
      :key="question.id"
      shadow="hover"
      class="center-card"
      @click.native="questionAdd(question)"
    >
      <!-- :class="[
        'center-card',
        question.id === id ? 'center-card-active' : '',
      ]" -->
      <div class="center-card-top">
        <span>{{ question.id }}、</span>
        <div class="render-title" v-html="`${question.title.replace(/<[^>]+>/g, '')}`" />
      </div>
      <div class="center-card-bottom">
        <div class="card-bottom-left">
          <el-tag class="center-tag-danger" size="mini" type="danger">{{
            question.type | typeName
          }}</el-tag>

          <el-tag effect="plain" size="mini" type="success">{{
            ['', '客观题', '主观题'][question.markType]
          }}</el-tag>

          <el-tag
            effect="plain"
            size="mini"
            type="danger"
          >{{ question.score }}分</el-tag>

          <el-tag effect="plain" size="mini" type="info">{{
            question.createUserName
          }}</el-tag>

          <el-tag
            :type="question.state === 1 ? 'info' : 'primary'"
            effect="plain"
            size="mini"
          >{{['回收站', '发布', '草稿'][question.state]}}</el-tag>
        </div>
        <div class="card-bottom-right">
          <el-button
            class="btn"
            size="mini"
            type="primary"
            icon="el-icon-back"
          >导入
          </el-button>
        </div>
      </div>
    </el-card>
    <el-pagination
      background
      layout="prev, pager, next"
      hide-on-single-page
      :total="total"
      :pager-count="5"
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
import { questionListpage } from 'api/question'
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
  },
  data() {
    return {
      total: 1,
      curPage: 1,
      pageSize: 5,
      markType: 1,
      questionList: [],
      queryForm: {
        id: '', // 编号
        title: '', // 题干
        type: null, // 类型
        score: '', // 分数
        questionTypeName: '', // 题库name
        questionTypeId: 1, // 题库id
        typeDictList: [] // 类型列表
      }
    }
  },
  async created() {
    this.queryForm.typeDictList = getOneDict('QUESTION_TYPE')
    this.queryQuestion()
  },
  methods: {
    // 查询试题
    async queryQuestion() {
      const res = await questionListpage({
        id: this.queryForm.id,
        markType: this.markType === 1 ? 1 : '',
        state: 1,
        type: this.queryForm.type,
        title: this.queryForm.title,
        questionTypeName: this.queryForm.questionTypeName,
        scoreStart: this.queryForm.score,
        scoreEnd: this.queryForm.score,
        exPaperId: this.$route.params.id || getQuick().id,
        curPage: this.curPage,
        pageSize: this.pageSize
      })
      if (res?.code === 200) {
        this.total = res.data.total
        this.questionList = res.data.list
        this.questionList.map((item) => {
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
    // 拖拽原题结束
    // 分页变化
    pageChange(val = 1) {
      this.curPage = val
      this.queryQuestion(val)
    },
    // 添加试题
    questionAdd(question) {
      this.questionList.some((item, i)=>{
        if(item.id === question.id){
          this.questionList.splice(i, 1)
          return true
        }
      })
      this.$emit('questionAdd', question)
      this.$emit('toHref', question.id)
    }
  }
}
// 解决火狐浏览器拖拽弹出新窗口
document.body.ondrop = function (event) {
  // 阻止默认动作的发生，还需要阻止事件冒泡
  event.preventDefault();
  event.stopPropagation();
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
    overflow: hidden;
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
    display: inline-flex;

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
.center-card {
  cursor: pointer;
  margin: 0 10px 10px 10px;
  display: flex;
  flex-direction: column;

  /deep/ .el-card__body {
    padding: 10px 15px;
  }

  &:hover {
    border: 1px solid #0094e5;
    background: rgba(#0094e5, 0.06%);

    .card-bottom-right {
      display: block;
    }
  }

  // &:nth-of-type(2) {
  //   //margin-top: 50px;
  // }
}
.center-card-active {
  border: 1px solid #0094e5;
}
.center-card-active {
  border: 1px solid #0094e5;
}

.center-card-top {
  font-size: 14px;
  text-align: left;
  display: flex;

  .render-title {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

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
    padding: 3px 8px;

    img {
      margin-right: 4px;
      vertical-align: -1px;
    }
  }
  /deep/ .el-card__body {
    cursor: pointer;
  }
}
</style>
