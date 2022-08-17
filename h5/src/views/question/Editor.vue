<template>
  <div class="content">
    <QuestionTxtImport
      ref="QuestionTxtImport"
      :question-type-id="questionTypeId"
    />
  </div>
</template>
<script>
import QuestionTxtImport from '@/components/EditQuestion/QuestionTxtImport.vue'
export default {
  components: {
    QuestionTxtImport
  },
  data() {
    return {
      questionTypeId: null // 题库id
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
