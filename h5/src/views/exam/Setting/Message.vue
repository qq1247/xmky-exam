<template>
  <div>
    <el-switch
      v-model="state"
      :active-value="2"
      :inactive-value="1"
      active-text="阅卷用户"
      inactive-text="考试用户"
      @change="changeType"
    />
    <Editor
      id="emailExam"
      :value="content"
      class="email-content"
      placeholder="邮件主体"
      @editorListener="editorListener"
    />
    <div>
      <el-button type="primary" @click="message(false)">通知</el-button>
      <el-button type="primary" @click="message(true)">通知测试</el-button>
    </div>
  </div>
</template>

<script>
import { examEmail, examGet } from 'api/exam'
import Editor from 'components/Editor/Index.vue'
export default {
  components: {
    Editor
  },
  data() {
    return {
      id: null,
      state: 1,
      content: `<div>
        <br/>
        <p>【姓名】 您好：</p>
        <p style="text-indent: 1em;">考试：【考试名称】</p>
        <p style="text-indent: 1em;">时间：【考试开始时间】至【考试结束时间】</p>
        <p style="text-indent: 1em;">链接：<a href="${location.origin}" target="_blank">${location.origin}</a></p>
        <br />
        <p style="color: red">注意事项：</p>
        <p  style="text-indent: 1em;">请使用火狐或谷歌浏览器</p>
        <p  style="text-indent: 1em;">考中中途退出，继续进入答题即可</p>
        <br />
        <p>祝：考试顺利</p>
        <br/>
      </div>`,
      examContent: '',
      markContent: '',
      paperMarkType: 1
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    const res = await examGet({ id: this.id })
    this.paperMarkType = res.data.paperMarkType
    this.examContent = this.markContent = this.content
  },
  methods: {
    changeType(e) {
      if (this.paperMarkType === 1) {
        this.state === 1
        this.$message('本场考试为智能阅卷模式！')
        return
      }

      if (e === 1) {
        this.content = this.examContent
      } else {
        if (
          this.markContent.includes('时间：【考试开始时间】至【考试结束时间】')
        ) {
          this.markContent = this.markContent.replace(
            '时间：【考试开始时间】至【考试结束时间】',
            '时间：【阅卷开始时间】至【阅卷结束时间】'
          )
        }
        this.content = this.markContent
      }
    },
    editorListener(id, value) {
      this.content = value
      if (
        this.paperMarkType === 1 ||
        (this.paperMarkType === 2 && this.state === 1)
      ) {
        this.examContent = value
      }
      if (this.paperMarkType === 2 && this.state === 2) {
        this.markContent = value
      }
    },
    async message(flag) {
      const notifyType = flag ? 3 : this.state
      const res = await examEmail({
        id: this.id,
        notifyType,
        content: this.content
      })
      if (res?.code === 200) {
        this.$message.success(flag ? '测试通过！' : '邮件通知成功！')
        this.$router.back()
      } else {
        this.$message.error(res.msg || flag ? '测试失败！' : '邮件通知失败！')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
/deep/ .tinymce-box {
  margin: 10px 0 20px;
  .tinymce-content {
    min-height: 200px;
  }
}
</style>
