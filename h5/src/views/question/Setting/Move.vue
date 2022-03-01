<!--
 * @Description: 移动题库
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:08:50
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 10:03:04
-->
<template>
  <el-form
    :model="examForm"
    :rules="examForm.rules"
    ref="moveForm"
    label-width="100px"
  >
    <el-form-item label="移动到" prop="questionType">
      <CustomSelect
        :multiple="false"
        placeholder="请选择试题分类"
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
        ></el-option>
      </CustomSelect>
    </el-form-item>
    <el-form-item>
      <el-button @click="questionMove" type="primary">编辑</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { questionTypeMove, questionTypeListPage } from 'api/question'
import CustomSelect from 'components/CustomSelect.vue'

export default {
  components: {
    CustomSelect,
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
            { required: true, message: '请选择试题分类', trigger: 'blur' },
          ],
        },
      },
    }
  },
  async mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    // 获取试题分类
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListPage({
        name,
        curPage,
        pageSize: 5,
      })
      this.examForm.questionTypes = typeList.data.list
      this.examForm.total = typeList.data.total
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
    selectQuestionType(e) {
      this.examForm.questionType = e
    },
    // 移动试题分类
    async questionMove() {
      if (!this.examForm.questionType) {
        this.$message.warning('请选择试题分类')
        return
      }

      const res = await questionTypeMove({
        sourceId: this.id,
        targetId: this.examForm.questionType,
      })
      if (res?.code == 200) {
        this.$message.success('移动成功！')
        this.$router.back()
      } else {
        this.$message.error('移动失败！')
      }
    },
  },
}
</script>
