<!--
 * @Description: 删除
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-21 10:11:03
 * @LastEditors: Che
 * @LastEditTime: 2022-01-05 09:47:46
-->
<template>
  <el-button @click="del" type="danger">删除</el-button>
</template>

<script>
import { paperDel } from 'api/paper'

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
      if (!Number(this.id)) return false
      this.$confirm(`确认删除吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await paperDel({ id: this.id })
          if (res?.code == 200) {
            this.$message('删除成功！')
            this.$router.back()
          } else {
            this.$message(res.msg || '删除失败！', 'error')
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
  },
}
</script>
