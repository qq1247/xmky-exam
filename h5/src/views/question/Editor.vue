<template>
  <div class="content">
    <QuestionBatchInput
      ref="QuestionBatchInput"
      :question-type-id="questionTypeId"
    />
  </div>
</template>
<script>
import QuestionBatchInput from '@/components/EditQuestion/QuestionBatchInput.vue'
export default {
  components: {
    QuestionBatchInput
  },
  data() {
    return {
      questionTypeId: null // 试题分类id
    }
  },
  created() {
    this.questionTypeId = Number(this.$route.params.id)
  },
  methods: {}
}
</script>
<style lang="scss" scoped>
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 200px);
  margin: 0 auto;
}
</style>
