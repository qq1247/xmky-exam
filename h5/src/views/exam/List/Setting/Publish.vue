<!--
 * @Description: 发布
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 13:14:16
-->
<template>
  <div class="container">
    <el-alert
      show-icon
      effect="dark"
      type="success"
      title="发布以进行后续操作操作"
      description="只有发布状态才可以进行阅卷设置、在线人员、统计等的操作"
    ></el-alert>
    <div class="form-footer">
      <el-button @click="publish" type="primary"> 发布 </el-button>
    </div>
  </div>
</template>

<script>
import { examPublish, examGet } from 'api/exam'

export default {
  data() {
    return {
      id: null,
      state: false,
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await examGet({ id: this.id })
      this.$nextTick(() => {
        this.state = res.data.state
      })
    }
  },
  methods: {
    publish() {
      if (this.state == 1) {
        this.$message.warning('考试已发布!')
        return
      }
      this.$confirm(`确认发布考试吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await examPublish({ id: this.id })
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
