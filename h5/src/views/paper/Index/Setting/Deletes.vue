<!--
 * @Description: 删除
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-21 10:11:03
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 10:01:10
-->
<template>
  <div class="container">
    <el-alert
      show-icon
      type="error"
      effect="dark"
      title="删除后不可恢复，请慎重操作！"
      description="删除操作将删除所关联的所有信息，文件等..."
    ></el-alert>
    <div class="form-footer">
      <el-button @click="del" type="danger">删除</el-button>
    </div>
  </div>
</template>

<script>
import { paperTypeDel } from 'api/paper'

export default {
  data() {
    return {
      id: null,
    }
  },
  mounted() {
    this.id = this.$route.params.id
  },
  methods: {
    del() {
      this.$confirm(`确认删除吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperTypeDel({ id: this.id })
          if (res?.code == 200) {
            this.$message.success('删除成功！')
            this.$router.back()
          } else {
            this.$message.error(res.msg || '删除失败！')
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
