<template>
  <el-form ref="paperForm" :model="paperForm" label-width="100px">
    <el-form-item label="试题乱序">
      <el-switch
        v-model="paperForm.optionOrder"
        :active-value="2"
        :inactive-value="1"
      />
    </el-form-item>
    <el-form-item label="选项乱序">
      <el-switch
        v-model="paperForm.questionOrder"
        :active-value="2"
        :inactive-value="1"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="order"> 开启 </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { paperSxe, paperGet } from 'api/paper'

export default {
  data() {
    return {
      paperForm: {
        id: null,
        optionOrder: 1,
        questionOrder: 1,
      },
    }
  },
  async mounted() {
    this.paperForm.id = this.$route.params.id
    const res = await paperGet({ id: this.paperForm.id })
    this.paperForm.optionOrder = res.data.options.includes('1') ? 2 : 1
    this.paperForm.questionOrder = res.data.options.includes('2') ? 2 : 1
  },
  methods: {
    async order() {
      const options = []
      if (this.paperForm.optionOrder === 2) {
        options.push(1)
      } else {
        const index = options.findIndex((option) => option === 1)
        index !== -1 && options.splice(index, 1)
      }

      if (this.paperForm.questionOrder === 2) {
        options.push(2)
      } else {
        const index = options.findIndex((option) => option === 2)
        index !== -1 && options.splice(index, 1)
      }

      const res = await paperSxe({
        id: this.paperForm.id,
        options,
      })

      res?.code === 200
        ? (this.$message.success('开启乱序成功！'), this.$router.back())
        : this.$message.error('开启乱序失败！')
    },
  },
}
</script>
