<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-10 10:14:03
 * @LastEditors: Che
 * @LastEditTime: 2022-01-18 10:03:23
-->
<template>
  <div class="detail-more" v-if="Object.keys(data).length">
    <!-- 类型，难度，其他选项，智能，分数 -->
    <div class="detail-tags">
      <el-tag class="center-tag-danger" size="mini" type="danger">{{
        data.type | typeName
      }}</el-tag>

      <el-tag class="center-tag-purple" effect="plain" size="mini">{{
        data.difficulty | difficultyName
      }}</el-tag>

      <el-tag effect="plain" size="mini" type="warning">{{
        ['', '智能', '非智能'][data.ai]
      }}</el-tag>

      <el-tag effect="plain" size="mini" type="danger"
        >{{ data.score }}分</el-tag
      >

      <el-tag effect="plain" size="mini">{{ data.createUserName }}</el-tag>

      <el-tag
        :type="data.state == 1 ? 'info' : 'danger'"
        effect="plain"
        size="mini"
        >{{ data.state == 1 ? '发布' : '草稿' }}</el-tag
      >

      <template v-if="data.scoreOptions">
        <el-tag
          size="small"
          type="info"
          v-for="item in data.scoreOptions"
          :key="item"
          >{{ getValue(item) }}</el-tag
        >
      </template>
    </div>
    <!-- 答案 -->
    <el-row :gutter="10">
      <template v-if="[1, 4].includes(data.type)">
        <el-col :span="1.5">【答案】</el-col>
        <el-col :span="20">
          <div v-html="`${data.answers[0].answer}`"></div>
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
          <template v-if="data.ai === 1">
            <div
              v-for="(answer, index) in data.answers"
              :key="answer.id"
              class="answers-item"
            >
              <span>{{ `关键词${$tools.intToChinese(index + 1)}、` }}</span>
              <span
                class="answers-tag"
                v-for="(ans, index) in answer.answer"
                :key="index"
                >{{ ans }}</span
              >
            </div>
          </template>
          <div v-if="data.ai === 2" v-html="`${data.answers[0].answer}`"></div>
        </el-col>
      </template>
    </el-row>
    <!-- 解析 -->
    <el-row :gutter="10">
      <el-col :span="1.5"> 【解析】</el-col>
      <el-col :span="20">
        <div v-html="`${data.analysis}`"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOneDict } from '@/utils/getDict'
export default {
  props: {
    data: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      otherOptions: [
        {
          dictKey: '1',
          dictValue: '漏选得分',
        },
        {
          dictKey: '2',
          dictValue: '答案无顺序',
        },
        {
          dictKey: '3',
          dictValue: '大小写不敏感',
        },
      ],
      typeList: [],
      difficultyList: [],
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
  created() {
    this.typeList = getOneDict('QUESTION_TYPE')
    this.difficultyList = getOneDict('QUESTION_DIFFICULTY')
  },
  methods: {
    getValue(type) {
      const [value] = this.otherOptions.filter(
        (item) => Number(item.dictKey) === Number(type)
      )
      return value.dictValue
    },
  },
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
