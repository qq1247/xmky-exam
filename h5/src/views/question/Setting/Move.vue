<template>
  <el-form
    ref="editForm"
    :model="editForm"
    :rules="editForm.rules"
    label-width="100px"
  >
    <el-form-item label="合并到" prop="questionType">
      <CustomSelect
        :multiple="false"
        placeholder="请选择题库"
        :value="editForm.questionType"
        :total="editForm.total"
        @change="selectQuestionType"
        @input="searchQuestionType"
        @currentChange="getMoreQuestionType"
        @visibleChange="getQuestionType"
      >
        <el-option
          v-for="item in editForm.questionTypes"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </CustomSelect>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="questionMove">确定</el-button>
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
      id: null, // 当前题库ID
      editForm: {
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
      let self = this
      this.editForm.questionTypes = typeList.data.list.filter(item => {// 不显示自己
        return item.id !== self.id;
      });
      this.editForm.total = typeList.data.total
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
      this.editForm.questionType = e
    },
    // 合并题库
    questionMove() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        questionTypeMove({
          sourceId: this.id,
          targetId: this.editForm.questionType
        }).then(() => {
          this.$message.success('合并成功！')
        })
      })

    }
  }
}
</script>
