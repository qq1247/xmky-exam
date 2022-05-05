<template>
  <div>
    <RulePaper ref="rulePaper"></RulePaper>
    <div class="footer">
      <el-button @click="$emit('prev', '1')" type="primary">上一步</el-button>
      <el-button @click="next" type="primary">下一步</el-button>
    </div>
  </div>
</template>
<script>
import RulePaper from '@/views/paper/List/Edit/RulePaper/Index.vue'
import { paperPublish, paperGet } from 'api/paper'
import { getQuick, setQuick } from '@/utils/storage'
export default {
  components: {
    RulePaper,
  },
  data() {
    return {}
  },
  methods: {
    // 试卷总分
    totalScore(paperQuestion) {
      if (paperQuestion.length == 0) {
        return 0
      }

      const questionList = paperQuestion.reduce((acc, cur) => {
        acc.push(...cur.questionList)
        return acc
      }, [])

      const totalScore = questionList.reduce((acc, cur) => {
        return acc + cur.score
      }, 0)

      return totalScore
    },
    // 组合试卷下一步
    next() {
      const paperQuestion =
        this.$refs.rulePaper.$refs.paperComposition.paperQuestion
      const next =
        paperQuestion.length &&
        paperQuestion.some((item) => item.questionList.length)
      if (next) {
        this.$confirm('提交组卷并发布，且不可修改！', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(async () => {
            const res = await paperPublish({ id: getQuick().id })
            if (res?.code === 200) {
              const paperInfo = await paperGet({ id: getQuick().id })
              console.log(paperInfo)
              const quickInfo = getQuick()
              quickInfo.markType = paperInfo.data.markType
              quickInfo.state = paperInfo.data.state
              quickInfo.totalScore = paperInfo.data.totalScore
              setQuick(quickInfo)
              setTimeout(() => {
                this.$emit('next', '3')
              }, 500)
            } else {
              this.$message.warning('请重新提交发布！')
            }
          })
          .catch(() => {})
      } else {
        this.$message.warning('试卷不能为空！')
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 110px);
  margin: 0 auto;
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
}

/deep/ .top {
  position: absolute;
  width: calc(100% - 1040px);
}

.footer {
  position: fixed;
  bottom: 0;
  width: calc(100% - 201px);
  right: 0;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(13px);
  display: flex;
  padding: 10px 10px 10px 30px;
  box-shadow: 1px -3px 13px -6px rgba(#000000, 0.15);
}
</style>
