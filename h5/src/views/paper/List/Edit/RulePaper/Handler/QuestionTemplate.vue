<template>
  <div class="handler-content">
    <el-form
      ref="questionForm"
      size="small"
      :model="questionForm"
      :rules="questionForm.rules"
      label-width="80px"
    >
      <el-form-item label="题库" prop="questionTypeId">
        <CustomSelect
          :multiple="false"
          placeholder="请选择题库"
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
          />
        </CustomSelect>
      </el-form-item>
    </el-form>
    <QuestionTemplate
      :back="false"
      :question-type-id="questionForm.questionTypeId"
      @questionExport="questionExport"
    />
  </div>
</template>

<script>
import CustomSelect from 'components/CustomSelect.vue'
import QuestionTemplate from '@/components/Question/QuestionTemplate.vue'
import htmlDocx from 'html-docx-js/dist/html-docx'
import saveAs from 'file-saver'
import { questionTypeListpage } from 'api/question'
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
            { required: true, message: '请选择题库', trigger: 'blur' },
          ],
        },
      },
    }
  },
  methods: {
    // 获取题库
    async getQuestionType(curPage = 1, name = '') {
      const typeList = await questionTypeListpage({
        name,
        curPage,
        pageSize: 5,
      })
      this.questionForm.questionTypes = typeList.data.list
      this.questionForm.total = typeList.data.total
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
    selectQuestionType(questionTypeId) {
      this.$set(this.questionForm, 'questionTypeId', questionTypeId)
    },
    // 组合导出的docx-html
    async compositionHtml() {
      const paperName = this.$parent.$parent.paperName
      const paperDetail =
        this.$parent.$parent.$refs['paperComposition'].paperQuestion

      // 试卷标题
      let stringHtml = `<p style="text-align: center;font-size: 22px;font-weight: 600;">${paperName}</p>`

      for (let i = 0; i < paperDetail.length; i++) {
        // 试卷章节、章节描述
        stringHtml += `
          <p
            style="
              width: 100%;
              background-color: #fafcff;
              font-size: 16px;
              font-weight: 600;
              color: #0c2e41;
              padding: 16px;
              border-top: 1px solid #f0f0f0;
              border-bottom: 1px solid #f0f0f0;
            "
          >
            ${paperDetail[i].chapter.name}
          </p>
          <p style="font-size: 14px; color: #557587; padding: 8px 16px 8px 32px">
            ${paperDetail[i].chapter.description}
          </p>
        `

        for (let j = 0; j < paperDetail[i].questionList.length; j++) {
          const title = paperDetail[i].questionList[j].title.replace(
            />/,
            `style="word-wrap: break-word;font-weight: 600;color: #0c2e41;" >${
              j + 1
            }、`
          )
          stringHtml += title
          if (paperDetail[i].questionList[j].type === 4) {
            const options = ['对', '错']
            for (let index = 0; index < options.length; index++) {
              const option = `<p style="color: #557587;line-height:1;">&nbsp;&nbsp;${String.fromCharCode(
                65 + index
              )}、${options[index]}</p>`
              stringHtml += option
            }
          } else {
            for (
              let index = 0;
              index < paperDetail[i].questionList[j].options.length;
              index++
            ) {
              const option = paperDetail[i].questionList[j].options[
                index
              ].option.replace(
                />/,
                `style="color: #557587;line-height:1;" >&nbsp;&nbsp;${String.fromCharCode(
                  65 + index
                )}、`
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
      const parser = new DOMParser()
      const doc = parser.parseFromString(stringHtml, 'text/html')
      const regularImages = doc.querySelectorAll('img')
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
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
        const img = new Image()
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
      const stringHtml = await this.compositionHtml()
      const docxHtml = await this.convertImagesToBase64(stringHtml)
      const converted = htmlDocx.asBlob(docxHtml, { orientation: 'portrait' })
      saveAs(converted, `${this.$parent.$parent.paperName}.docx`)
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
