<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-10 10:14:03
 * @LastEditors: Che
 * @LastEditTime: 2021-09-13 17:49:09
-->
<template>
  <div class="detail-more">
    <!-- 类型，难度，其他选项，智能，分数 -->
    <div class="detail-tags">
      <el-tag effect="plain" type="info">{{ typeItem(this.data.type) }}</el-tag>
      <el-tag effect="plain" type="danger">{{
        difficultyItem(this.data.difficulty)
      }}</el-tag>
      <el-tag effect="plain" type="warning">{{
        ['', '智能', '非智能'][data.ai]
      }}</el-tag>
      <el-tag effect="plain" type="success">{{ data.score }}分</el-tag>
      <template v-if="data.scoreOptions">
        <el-tag
          effect="plain"
          type="info"
          v-for="item in data.scoreOptions.split(',')"
          :key="item"
          >{{ otherItem(item) }}</el-tag
        >
      </template>
    </div>
    <!-- 答案 -->
    <el-row :gutter="10">
      <template v-if="[1, 2, 4].includes(data.type)">
        <el-col :span="4"> 【答案】</el-col>
        <el-col :span="20">
          <div v-html="`${data.answers[0].answer}`"></div>
        </el-col>
      </template>
      <template v-if="data.type === 3">
        <el-col :span="4">【答案】</el-col>
        <el-col :span="20">
          <div
            v-for="(answer, index) in data.answers"
            :key="answer.id"
            class="answers-item"
          >
            <span>{{ `填空${index + 1}、` }}</span>
            <span
              class="answers-tag"
              v-for="(ans, index) in answer.answer"
              :key="index"
              >{{ ans }}</span
            >
          </div>
        </el-col>
      </template>
      <template v-if="data.type === 5">
        <el-col :span="4"> 【答案】</el-col>
        <el-col :span="20">
          <template v-if="data.ai === 1">
            <div
              v-for="(answer, index) in data.answers"
              :key="answer.id"
              class="answers-item"
            >
              <span>{{ `关键词${index + 1}、` }}</span>
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
      <el-col :span="4"> 【解析】</el-col>
      <el-col :span="20">
        <div v-html="`${data.analysis}`"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: () => {},
    },
    type: {
      type: Array,
      default: [],
    },
    difficulty: {
      type: Array,
      default: [],
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
    }
  },
  computed: {
    typeItem() {
      return function (type) {
        return this.getValue(this.type, type)
      }
    },
    difficultyItem() {
      return function (type) {
        return this.getValue(this.difficulty, type)
      }
    },
    otherItem() {
      return function (type) {
        return this.getValue(this.otherOptions, type)
      }
    },
  },
  methods: {
    getValue(arr, type) {
      const [value] = arr.filter(
        (item) => Number(item.dictKey) === Number(type)
      )
      return value.dictValue
    },
  },
}
</script>

<style lang="scss" scoped>
.detail-more {
  margin-top: 40px;
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
