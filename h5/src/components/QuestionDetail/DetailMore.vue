<template>
  <div v-if="Object.keys(data).length" class="detail-more">
    <!-- 类型，其他选项，智能，分数 -->
    <div class="detail-tags">
      <el-tag class="center-tag-danger" size="mini" type="danger">{{
        data.type | type
      }}</el-tag>

      <el-tag effect="plain" size="mini" type="warning">{{
        ['', '智能', '非智能'][data.markType]
      }}</el-tag>

      <el-tag
        effect="plain"
        size="mini"
        type="danger"
      >{{ data.score }}分</el-tag>

      <el-tag
        :type="data.state === 1 ? 'info' : 'danger'"
        effect="plain"
        size="mini"
      >{{ data.state === 1 ? '发布' : '草稿' }}</el-tag>

      <template v-if="data.markOptions">
        <el-tag
          v-for="item in data.markOptions"
          :key="item"
          size="small"
          type="info"
        >{{ getValue(item) }}</el-tag>
      </template>
    </div>
    <!-- 答案 -->
    <el-row :gutter="10">
      <template v-if="[1, 4].includes(data.type)">
        <el-col :span="1.5">【答案】</el-col>
        <el-col :span="20">
          <div v-html="`${data.answers[0].answer}`" />
        </el-col>
      </template>

      <template v-if="data.type === 2">
        <el-col :span="1.5">【答案】</el-col>
        <el-col :span="20">
          <template v-if="data.answers && data.answers.length > 0">
            <span v-for="answer in data.answers" :key="answer.id">{{
              answer.answer.join('，')
            }}</span>
          </template>
        </el-col>
      </template>

      <template v-if="data.type === 3">
        <el-col :span="1.5">【答案】</el-col>
        <el-col :span="20">
          <div
            v-for="(answer, index) in data.answers"
            :key="answer.id"
            class="answers-item"
          >
            <span>{{
              `填空${$tools.intToChinese(index + 1)}、${answer.answer.join(
                '，'
              )}`
            }}</span>
          </div>
        </el-col>
      </template>

      <template v-if="data.type === 5">
        <el-col :span="1.5"> 【答案】</el-col>
        <el-col :span="20">
          <template v-if="data.markType === 1">
            <div
              v-for="(answer, index) in data.answers"
              :key="answer.id"
              class="answers-item"
            >
              <span>{{ `关键词${$tools.intToChinese(index + 1)}、` }}</span>
              <span
                v-for="(ans, indexAnswer) in answer.answer"
                :key="indexAnswer"
                class="answers-tag"
              >{{ ans }}</span>
            </div>
          </template>
          <div v-if="data.markType === 2" v-html="`${data.answers[0].answer}`" />
        </el-col>
      </template>
    </el-row>
    <!-- 解析 -->
    <el-row :gutter="10">
      <el-col :span="1.5"> 【解析】</el-col>
      <el-col :span="20">
        <div v-html="`${data.analysis}`" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOneDict } from '@/utils/getDict'
export default {
  filters: {
    type(data) {
      return getOneDict('QUESTION_TYPE').find(
        (item) => Number(item.dictKey) === data
      ).dictValue
    }
  },
  props: {
    data: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      otherOptions: [
        {
          dictKey: '1',
          dictValue: '漏选得分'
        },
        {
          dictKey: '2',
          dictValue: '答案无顺序'
        },
        {
          dictKey: '3',
          dictValue: '大小写不敏感'
        }
      ],
      typeList: []
    }
  },
  created() {
    this.typeList = getOneDict('QUESTION_TYPE')
  },
  methods: {
    getValue(type) {
      const [value] = this.otherOptions.filter(
        (item) => Number(item.dictKey) === Number(type)
      )
      return value.dictValue
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-more {
  margin-top: 20px;
  padding-left: 30px;
  .detail-tags {
    display: flex;
    flex-wrap: wrap;
    .el-tag {
      margin-right: 15px;
      margin-bottom: 10px;
    }
  }
  .detail-answer {
    display: flex;
    margin-bottom: 10px;
  }
  .detail-analysis {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
  }
}

.el-col {
  line-height: 30px;
}
</style>
