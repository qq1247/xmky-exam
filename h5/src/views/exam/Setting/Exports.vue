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
          :key="`${user.name}-${user.id}`"
          :label="user.name"
          :value="String(user.id)"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="exportsPaper">导出</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { myMarkPaper, myMarkAnswerList } from 'api/my'
import { examGet, examMarkUserList } from 'api/exam'
import htmlDocx from 'html-docx-js/dist/html-docx'
import saveAs from 'file-saver'

export default {
  data() {
    return {
      id: null,
      genType: 1,
      examForm: {
        paperId: 0,
        examName: '',
        examUser: '',
        examUserName: '',
        paperAnswer: [],
        examUserList: [],
        paperQuestion: [],
        markState: 1,
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
        data: { paperId, name, markState }
      } = await examGet({
        id: this.id
      })
      this.examForm.paperId = paperId
      this.examForm.examName = name
      this.examForm.markState = markState

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
    // 查询试卷
    async queryPaper() {
      const res = await paperGet({
        id: this.examForm.paperId
      })
      this.genType = res.data.genType
    },
    // 查询试卷信息
    async queryPaperInfo() {
      const paperQuestion = await myMarkPaper({
        examId: this.id,
        userId: this.examForm.examUser
      })
      const paperAnswer = await myMarkAnswerList({
        examId: this.id,
        userId: this.examForm.examUser
      })

      this.examForm.paperQuestion = [...paperQuestion.data]
      this.examForm.paperAnswer = [...paperAnswer.data]
    },
    // 组合导出的docx-html
    async compositionHtml() {
      await this.queryPaper()
      await this.queryPaperInfo()
      const paperDetail = this.examForm.paperQuestion
      // 用户信息
      // const userInfo = this.examForm.examUserList.find(
      //   (user) => (user.id = Number(this.examForm.examUser))
      // )
      this.getUserNameByUserId(this.examForm.examUser)
      // 总成绩
      const totalScore = this.examForm.paperAnswer.reduce(
        (acc, cur) => (acc += cur.score),
        0
      )
      // 试题名称
      let stringHtml = `<p style="text-align: center;font-size: 22px;font-weight: 600;">${this.examForm.examName}</p>`
      // 姓名、分数
      stringHtml += `<p style="color: #0094e5;">姓名：${this.examForm.examUserName}</p>`
      stringHtml += `<p style="color: #eb5b5b;">分数：${totalScore}</p>`
      for (let i = 0; i < paperDetail.length; i++) {
        // 章节和章节描述
        stringHtml += `
          <br/><p
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
          // 匹配用户自己作答的答案
          const {
            answers: selfAnswer,
            score: selfScore,
            questionScore
          } = this.examForm.paperAnswer.find(
            (answer) => answer.questionId === paperDetail[i].questionList[j].id
          )

          // 匹配正确答案
          const modelAnswer = paperDetail[i].questionList[j].answers[0].answer

          // 试题标题
          let title
          if (paperDetail[i].questionList[j].type === 3) {
            title = paperDetail[i].questionList[j].title.replace(
              />/,
              `style="word-wrap: break-word;font-weight: 600;color: #0c2e41;" >${
                j + 1
              }、`
            )

            const underlineList = title.match(/[_]{5,}/g)
            underlineList.map((underline, index) => {
              const titleStart = title.substring(0, title.indexOf(underline))
              const titleEnd = title.substring(
                title.indexOf(underline) + underline.length
              )

              const inputHtml = selfAnswer[index]
                ? `<span style="text-decoration: underline; padding: 0 10px;">&nbsp;&nbsp;${selfAnswer[index]}&nbsp;&nbsp;</span>&nbsp;`
                : `${underline}`
              title = `${titleStart}${inputHtml}${titleEnd}`
            })
          } else {
            title = paperDetail[i].questionList[j].title.replace(
              />/,
              `style="word-wrap: break-word;font-weight: 600;color: #0c2e41;" >${
                j + 1
              }、`
            )
          }

          if (paperDetail[i].questionList[j].type === 3) {
            title = title.replace(
              /<\/p>$/,
              `<span>（${selfScore}分）&nbsp;&nbsp;</span><span>（${
                selfScore === questionScore ? '√' : '×'
              }）</span></p>`
            )
          } else {
            title = title.replace(
              /<\/p>$/,
              `<span>（${selfScore}分）</span></p>`
            )
          }

          stringHtml += title

          // 单选、多选、判断的选项
          if (paperDetail[i].questionList[j].type === 4) {
            const options = ['对', '错']
            for (let index = 0; index < options.length; index++) {
              const check = selfAnswer.includes(options[index])
              const inputHtml = check
                ? `<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAMAAAAolt3jAAAAAXNSR0IArs4c6QAAADZQTFRFAAAAAJLnAJPjAJPmAJTlBpbmAJTlAJXmG6DpAJTlEJvmbMLwdsXwx+j5z+v68vr++Pz+////814pEwAAAAl0Uk5TABVTgs/x8vL9ACNG/wAAAFpJREFUCNddz0EOwCAIBECoUKyKuv//bK3pRfY2CYGFiIiTmEli2rm0+hhe9drKHTs9L7P+WlamVIHZSmkTqInEgfasNMCFbADlYwGGnbzjcFgVDoUaseTxwgsL8Ads4klplQAAAABJRU5ErkJggg=="/>`
                : `<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOBAMAAADtZjDiAAAAAXNSR0IArs4c6QAAACdQTFRFAAAAi4uLq6uruLi4ubm5v7+/vb29xsbG2tra8PDw8vLy+/v7////iJ7dHAAAAAh0Uk5TAAtDdr7q7P0ioTVoAAAARElEQVQI12NgYDRJcxZgYGBQ7dw9I4iBganqzJkzyxUYRNcA6VOBDBZnQKCZwRNMT2HIBtPb4DRMHKYOpg9mDsxcqD0ARIszq8RDun8AAAAASUVORK5CYII=" />`
              let option = `<p style="color: #557587;line-height:1;">&nbsp;&nbsp;${inputHtml}&nbsp;${String.fromCharCode(
                65 + index
              )}、${options[index]}</p>`

              const modelCheck = modelAnswer.includes(options[index])

              option = option.replace(
                /<\/p>$/,
                `&nbsp;&nbsp;<span>${modelCheck ? '√' : '×'}</span></p>`
              )

              stringHtml += option
            }
          } else {
            for (
              let index = 0;
              index < paperDetail[i].questionList[j].options.length;
              index++
            ) {
              const check = selfAnswer.includes(String.fromCharCode(65 + index))
              let inputHtml

              if (paperDetail[i].questionList[j].type === 1) {
                inputHtml = check
                  ? `<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAMAAAAolt3jAAAAAXNSR0IArs4c6QAAADZQTFRFAAAAAJLnAJPjAJPmAJTlBpbmAJTlAJXmG6DpAJTlEJvmbMLwdsXwx+j5z+v68vr++Pz+////814pEwAAAAl0Uk5TABVTgs/x8vL9ACNG/wAAAFpJREFUCNddz0EOwCAIBECoUKyKuv//bK3pRfY2CYGFiIiTmEli2rm0+hhe9drKHTs9L7P+WlamVIHZSmkTqInEgfasNMCFbADlYwGGnbzjcFgVDoUaseTxwgsL8Ads4klplQAAAABJRU5ErkJggg=="/>`
                  : `<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOBAMAAADtZjDiAAAAAXNSR0IArs4c6QAAACdQTFRFAAAAi4uLq6uruLi4ubm5v7+/vb29xsbG2tra8PDw8vLy+/v7////iJ7dHAAAAAh0Uk5TAAtDdr7q7P0ioTVoAAAARElEQVQI12NgYDRJcxZgYGBQ7dw9I4iBganqzJkzyxUYRNcA6VOBDBZnQKCZwRNMT2HIBtPb4DRMHKYOpg9mDsxcqD0ARIszq8RDun8AAAAASUVORK5CYII=" />`
              }

              if (paperDetail[i].questionList[j].type === 2) {
                inputHtml = check
                  ? `<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAMAAAAolt3jAAAAAXNSR0IArs4c6QAAAEVQTFRFAAAAAJ3rAJfnAJTnAJjkAJnrAJXmAJTlAJbmAJXmAJPlAJTmAJTmAJTlAJTlAJPlAJTlAJTmAJTlAJTmAJTlAJTlAJTlfh9LYgAAABZ0Uk5TAA0gKy8yPFhae4elrLPg4+fq9fz9/p5+sxYAAABQSURBVAjXZY85EoAwDANlc5orEAf//6kUZJwEtttCGgkAcYYAQNQyKgAlc5TAVsFF70b32fUw27rgOi1rf5ZsHIerrorx1/zqZwYk1SPbCw/NHgusTJbBIwAAAABJRU5ErkJggg=="/>`
                  : `<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOBAMAAADtZjDiAAAAAXNSR0IArs4c6QAAABhQTFRFAAAAZmZmd3d3sLCwwsLCxsbG+vr6////YxzpbQAAAAV0Uk5TAAUPZ/v+OmdKAAAALklEQVQI12NgEFJSUlJkYGB2DQ0NDTFgEEkvLy8vc2RQLQeBILJpmDkwc6H2AAAJISBHCX2J9QAAAABJRU5ErkJggg==" />`
              }

              let option = paperDetail[i].questionList[j].options[
                index
              ].option.replace(
                />/,
                `style="color: #557587;line-height:1;" >&nbsp;&nbsp;${inputHtml}&nbsp;${String.fromCharCode(
                  65 + index
                )}、`
              )

              const modelCheck = modelAnswer.includes(
                String.fromCharCode(65 + index)
              )

              option = option.replace(
                /<\/p>$/,
                `&nbsp;&nbsp;<span>${modelCheck ? '√' : '×'}</span></p>`
              )
              stringHtml += option
            }
          }

          // 问答答案
          if (paperDetail[i].questionList[j].type === 5) {
            if (selfAnswer.length) {
              const selfAnswers = selfAnswer[0].split('\n')
              selfAnswers.map((item) => {
                stringHtml += `<p style="color: #557587;">${item}</p>`
              })
            } else {
              stringHtml += `<p style="color: #557587;">&nbsp;&nbsp;${
                selfAnswer[0] || ''
              }</p>`
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
        if (imgElement.src.includes('data:image')) continue
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

        if (this.examForm.markState !== 3) {
          this.$message.warning('阅卷尚未结束，请等待！')
          return false
        }
        const stringHtml = await this.compositionHtml()
        const docxHtml = await this.convertImagesToBase64(stringHtml)
        const converted = htmlDocx.asBlob(docxHtml, { orientation: 'portrait' })
        this.getUserNameByUserId(this.examForm.examUser)
        saveAs(converted, `${this.examForm.examName}-${this.examForm.examUserName}.docx`)
      })
    },
    // 根据id获取用户名称
    getUserNameByUserId(id) {
      this.examForm.examUserList.forEach(item => {
        if (item.id === parseInt(id)) {
          this.examForm.examUserName = item.name
        }
      })
    }
  }
}
</script>
