<!--
 * @Description: 开放题库
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:07:54
 * @LastEditors: Che
 * @LastEditTime: 2022-01-06 14:26:29
-->
<template>
  <div class="container">
    <el-alert
      show-icon
      type="success"
      effect="dark"
      title="发布后将不可进行组合试卷"
    ></el-alert>
    <div class="form-footer">
      <el-button @click="publish" type="primary"> 发布 </el-button>
    </div>
  </div>
</template>

<script>
import { paperPublish, paperGet } from 'api/paper'
export default {
  data() {
    return {
      id: null,
      state: 1,
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await paperGet({ id: this.id })
      this.$nextTick(() => {
        this.state = res.data.state
      })
    }
  },
  methods: {
    publish() {
      if (this.state == 1) {
        this.$message.warning('试卷已发布!')
        return
      }
      this.$confirm(`确认发布试卷吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperPublish({ id: this.id })
          if (res?.code == 200) {
            this.$message.success('考试发布成功！')
            this.$router.back()
          } else {
            this.$message.error('请重新发布考试！')
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  margin-top: 10px;
}
</style>
