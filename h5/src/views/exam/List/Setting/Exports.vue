<template>
  <el-form
    ref="examForm"
    :model="examForm"
    :rules="examForm.rules"
    label-width="100px"
  >
    <el-form-item label="考试用户" prop="examUser">
      <el-select
        v-model="examForm.examUser"
        clearable
        placeholder="请选择考试用户"
      >
        <el-option
          v-for="user in examForm.examUserList"
          :key="user.id"
          :label="user.name"
          :value="user.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="exportsPaper">导出</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { paperQuestionList } from 'api/paper'
import { examGet, examMarkUserList } from 'api/exam'
import htmlDocx from 'html-docx-js/dist/html-docx'
import saveAs from 'file-saver'

export default {
  data() {
    return {
      id: null,
      examForm: {
        paperId: 0,
        examName: '',
        examUser: null,
        examUserList: [],
        paperQuestion: [],
        rules: {
          examUser: [
            { required: true, message: '请选择考试用户', trigger: 'change' }
          ]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const {
        data: { paperId, name }
      } = await examGet({
        id: this.id
      })
      this.examForm.paperId = paperId
      this.examForm.examName = name

      const examMarkUser = await examMarkUserList({ id: this.id })

      if (examMarkUser.data.length > 0) {
        this.examForm.examUserList = examMarkUser.data.reduce((acc, cur) => {
          acc.push(...cur.examUserList)
          return acc
        }, [])
      }
    }
  },
  methods: {
    // 查询试卷信息
    async queryPaper() {
      const res = await paperQuestionList({
        id: this.examForm.paperId,
        examId: this.id,
        userId: this.examForm.examUser
      })
      this.examForm.paperQuestion = [...res.data]
    },
    // 组合导出的docx-html
    async compositionHtml() {
      const paperDetail = this.examForm.paperQuestion
      let stringHtml = `<p style="text-align: center;font-size: 20px;font-weight: 600;">${this.examForm.examName}</p>`

      for (let i = 0; i < paperDetail.length; i++) {
        stringHtml += `<br/><p>${paperDetail[i].chapter.name}</p><p>${paperDetail[i].chapter.description}</p><br/>`

        for (let j = 0; j < paperDetail[i].questionList.length; j++) {
          const title = paperDetail[i].questionList[j].title.replace(
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
              const option = `<p>&nbsp;&nbsp;<span>${String.fromCharCode(
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
              const option = paperDetail[i].questionList[j].options[
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
    // 异步获取图片
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
    async exportsPaper() {
      this.$refs['examForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }
        await this.queryPaper()
        const stringHtml = await this.compositionHtml()
        const docxHtml = await this.convertImagesToBase64(stringHtml)
        const converted = htmlDocx.asBlob(docxHtml, { orientation: 'portrait' })
        saveAs(converted, 'paper.docx')
      })
    }
  }
}
</script>
