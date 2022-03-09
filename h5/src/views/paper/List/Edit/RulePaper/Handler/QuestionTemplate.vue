<template>
  <div class="handler-content">
    <el-form
      size="small"
      ref="questionForm"
      :model="questionForm"
      :rules="questionForm.rules"
      label-width="80px"
    >
      <el-form-item label="试题分类" prop="questionTypeId">
        <CustomSelect
          :multiple="false"
          placeholder="请选择试题分类"
          :value="questionForm.questionTypeId"
          :total="questionForm.total"
          @change="selectQuestionType"
          @input="searchQuestionType"
          @currentChange="getMoreQuestionType"
          @visibleChange="getQuestionType"
        >
          <el-option
            v-for="item in questionForm.questionTypes"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </CustomSelect>
      </el-form-item>
    </el-form>
    <QuestionTemplate
      :back="false"
      :questionTypeId="questionForm.questionTypeId"
    ></QuestionTemplate>
  </div>
</template>

<script>
import CustomSelect from 'components/CustomSelect.vue'
import QuestionTemplate from '@/components/EditQuestion/QuestionTemplate.vue'
import { questionTypeListPage } from 'api/question'
export default {
  components: {
    CustomSelect,
    QuestionTemplate,
  },
  props: {},
  data() {
    return {
      questionForm: {
        questionTypeId: null,
        questionTypes: [],
        questionType: 1,
        rules: {
          questionTypeId: [
            { required: true, message: '请选择试题分类', trigger: 'blur' },
          ],
        },
      },
    }
  },
  methods: {
    // 获取试题分类
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListPage({
        name,
        curPage,
        pageSize: 5,
      })
      this.questionForm.questionTypes = typeList.data.list
      this.questionForm.total = typeList.data.total
    },
    // 根据name 查询试题分类
    searchQuestionType(name) {
      this.getQuestionType(1, name)
    },
    // 获取更多试题分类
    getMoreQuestionType(curPage, name) {
      this.getQuestionType(curPage, name)
    },
    // 选择试题分类
    selectQuestionType(questionTypeId) {
      this.$set(this.questionForm, 'questionTypeId', questionTypeId)
    },
  },
}
</script>

<style lang="scss" scoped>
.handler-content {
  padding: 50px 20px 10px;
}
.template-content {
  padding: 20px;
}
</style>
