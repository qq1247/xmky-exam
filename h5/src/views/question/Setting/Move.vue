<template>
  <el-form
    ref="moveForm"
    :model="examForm"
    :rules="examForm.rules"
    label-width="100px"
  >
    <el-form-item label="合并到" prop="questionType">
      <CustomSelect
        :multiple="false"
        placeholder="请选择题库"
        :value="examForm.questionType"
        :total="examForm.total"
        @change="selectQuestionType"
        @input="searchQuestionType"
        @currentChange="getMoreQuestionType"
        @visibleChange="getQuestionType"
      >
        <el-option
          v-for="item in examForm.questionTypes"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </CustomSelect>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="questionMove">编辑</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { questionTypeMove, questionTypeListpage } from 'api/question'
import CustomSelect from 'components/CustomSelect.vue'

export default {
  components: {
    CustomSelect
  },
  data() {
    return {
      id: null,
      examForm: {
        total: 1,
        curPage: 1,
        questionType: null,
        questionTypes: [],
        rules: {
          questionType: [
            { required: true, message: '请选择题库', trigger: 'blur' }
          ]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    // 获取题库
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListpage({
        name,
        curPage,
        pageSize: 5
      })
      this.examForm.questionTypes = typeList.data.list
      this.examForm.total = typeList.data.total
    },
    // 根据name 查询题库
    searchQuestionType(name) {
      this.getQuestionType(1, name)
    },
    // 获取更多题库
    getMoreQuestionType(curPage, name) {
      this.getQuestionType(curPage, name)
    },
    // 选择题库
    selectQuestionType(e) {
      this.examForm.questionType = e
    },
    // 合并题库
    async questionMove() {
      if (!this.examForm.questionType) {
        this.$message.warning('请选择题库')
        return
      }

      const res = await questionTypeMove({
        sourceId: this.id,
        targetId: this.examForm.questionType
      })
      if (res?.code === 200) {
        this.$message.success('合并成功！')
        this.$router.back()
      } else {
        this.$message.error('合并失败！')
      }
    }
  }
}
</script>
