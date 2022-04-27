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
      @questionExport="questionExport"
    ></QuestionTemplate>
  </div>
</template>

<script>
import CustomSelect from 'components/CustomSelect.vue'
import QuestionTemplate from '@/components/EditQuestion/QuestionTemplate.vue'
import htmlDocx from 'html-docx-js/dist/html-docx'
import saveAs from 'file-saver'
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
    // 组合导出的docx-html
    async compositionHtml() {
      let paperName = this.$parent.$parent.paperName
      let paperDetail =
        this.$parent.$parent.$refs['paperComposition'].paperQuestion
      let stringHtml = `<p style="text-align: center;font-size: 20px;font-weight: 600;">${paperName}</p>`

      for (let i = 0; i < paperDetail.length; i++) {
        stringHtml += `<br/><p>${paperDetail[i].chapter.name}</p><p>${paperDetail[i].chapter.description}</p><br/>`

        for (let j = 0; j < paperDetail[i].questionList.length; j++) {
          let title = paperDetail[i].questionList[j].title.replace(
            />/,
            `><span>${j + 1}、</span>`
          )
          stringHtml += title
          if (
            !paperDetail[i].questionList[j].options.length &&
            paperDetail[i].questionList[j].type !== 4
          ) {
            continue
          }

          if (paperDetail[i].questionList[j].type === 4) {
            const options = ['对', '错']
            for (let index = 0; index < options.length; index++) {
              let option = `<p>&nbsp;&nbsp;<span>${String.fromCharCode(
                65 + index
              )}、</span><span>${options[index]}</span></p>`
              stringHtml += option
            }
          } else {
            for (
              let index = 0;
              index < paperDetail[i].questionList[j].options.length;
              index++
            ) {
              let option = paperDetail[i].questionList[j].options[
                index
              ].replace(
                />/,
                `>&nbsp;&nbsp;<span>${String.fromCharCode(65 + index)}、</span>`
              )
              stringHtml += option
            }
          }
        }
      }

      return stringHtml
    },
    // 替换docx-html中的图片
    async convertImagesToBase64(stringHtml) {
      let parser = new DOMParser()
      let doc = parser.parseFromString(stringHtml, 'text/html')
      let regularImages = doc.querySelectorAll('img')
      let canvas = document.createElement('canvas')
      let ctx = canvas.getContext('2d')
      for (let index = 0; index < regularImages.length; index++) {
        const imgElement = regularImages[index]
        ctx.clearRect(0, 0, canvas.width, canvas.height)
        canvas.width = imgElement.width
        canvas.height = imgElement.height
        await this.loadImg(canvas, ctx, imgElement).then((dataURL) => {
          imgElement.setAttribute('src', dataURL)
        })
      }
      canvas.remove()
      return doc.body.innerHTML
    },
    loadImg(canvas, ctx, imgElement) {
      return new Promise((resolve, reject) => {
        let img = new Image()
        img.src = imgElement.src
        img.setAttribute('crossOrigin', 'Anonymous')
        img.onload = () => {
          ctx.drawImage(img, 0, 0, imgElement.width, imgElement.height)
          const dataURL = canvas.toDataURL()
          resolve(dataURL)
        }
        img.onerror = reject
      })
    },
    // 导出试题
    async questionExport() {
      let stringHtml = await this.compositionHtml()
      let docxHtml = await this.convertImagesToBase64(stringHtml)
      let converted = htmlDocx.asBlob(docxHtml, { orientation: 'portrait' })
      saveAs(converted, 'paper.docx')
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
