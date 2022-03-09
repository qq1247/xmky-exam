<template>
  <div class="paper-router">
    <div class="total-score">总分：{{ totalScore }}</div>
    <template v-if="paperQuestion.length > 0">
      <div
        class="router-content"
        v-for="(item, index) in paperQuestion"
        :key="index"
      >
        <div class="router-title" v-if="item.questionList">
          第{{ $tools.intToChinese(index + 1) }}章（共{{
            item.questionList.length
          }}题，合计{{ computeChapterScore(item.questionList) }}分）
        </div>
        <div class="router-link" v-if="item.questionList">
          <a
            :class="[
              'router-index',
              routerIndex === child.id ? 'router-active' : '',
            ]"
            @click="toHref(child.id)"
            v-for="(child, index) in item.questionList"
            :key="child.id"
            >{{ index + 1 }}</a
          >
        </div>
      </div>
    </template>
    <el-empty v-else description="暂无试题导航"> </el-empty>
  </div>
</template>

<script>
import { getQuick } from '@/utils/storage'
import { paperQuestionList } from 'api/paper'
export default {
  components: {},
  props: {},
  data() {
    return {
      routerIndex: '',
      paperQuestion: [],
    }
  },
  computed: {
    totalScore() {
      if (this.paperQuestion.length == 0) {
        return 0
      }

      const questionList = this.paperQuestion.reduce((acc, cur) => {
        acc.push(...cur.questionList)
        return acc
      }, [])

      const totalScore = questionList.reduce((acc, cur) => {
        return acc + cur.score
      }, 0)

      return totalScore
    },
  },
  created() {
    this.query()
  },
  methods: {
    // 查询试卷信息
    async query() {
      try {
        const res = await paperQuestionList({
          id: this.$route.params.id || getQuick().id,
        })
        res.data.map((item) => {
          item.chapter.show = true
        })
        this.paperQuestion = [...res.data]
      } catch (error) {
        this.$message.error(error.msg)
      }
    },
    // 计算章节分数
    computeChapterScore(data) {
      const num = data.reduce((acc, cur) => {
        return acc + cur.score
      }, 0)
      return num
    },
    // 定位
    toHref(id) {
      this.routerIndex = id
      document
        .querySelector(`#p-${id}`)
        .scrollIntoView({ block: 'end', inline: 'nearest' })
    },
  },
}
</script>

<style lang="scss" scoped>
.paper-router {
  background: #fff;
  position: relative;
  padding-top: 80px;
  .total-score {
    background: #0094e5;
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 40px;
    left: 0;
    z-index: 100;
  }
}

.router-content {
  margin: 0 20px;
  .router-title {
    line-height: 40px;
    font-size: 13px;
  }

  .router-index {
    position: relative;
    width: 28px;
    height: 28px;
    color: #c3c3c3;
    line-height: 28px;
    background: #f4f4f4;
    border: 1px solid #e5e5e5;
    text-align: center;
    display: inline-block;
    margin-bottom: 10px;
    margin-right: 10px;
    border-radius: 3px;
    text-decoration: none;
    cursor: pointer;
    user-select: none;
    &:nth-child(12n) {
      margin-right: 0;
    }
  }

  .router-active,
  a:hover {
    background: #c2e4ff;
    border: 1px solid #7fc2f6;
    color: #83c3f7;
  }
}
</style>
