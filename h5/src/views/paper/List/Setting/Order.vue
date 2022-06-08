<template>
  <el-form
    ref="paperForm"
    :model="paperForm"
    :rules="rules"
    label-width="100px"
  >
    <el-form-item label="乱序选项" prop="orderOption">
      <el-checkbox-group v-model="paperForm.orderOption">
        <el-checkbox
          v-for="(option, indexOption) in paperForm.orderOptions"
          :key="indexOption"
          :label="String(option.no)"
        >{{ option.option }}
        </el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="order"> 开启 </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { paperSxe } from 'api/paper'

export default {
  data() {
    return {
      paperForm: {
        id: null,
        orderOption: [],
        orderOptions: [
          {
            no: 1,
            option: '章节乱序'
          },
          {
            no: 2,
            option: '试题乱序'
          }
        ]
      },
      rules: {
        orderOption: [
          {
            required: true,
            type: 'array',
            message: '请选择乱序方式',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  async mounted() {
    this.paperForm.id = this.$route.params.id
  },
  methods: {
    order() {
      this.$refs['paperForm'].validate(async(valid) => {
        if (!valid) {
          return
        }
        const res = await paperSxe({
          id: this.paperForm.id,
          options: this.paperForm.orderOption
        })
        res?.code === 200
          ? (this.$message.success('开启乱序成功！'), this.$router.back())
          : this.$message.error('开启乱序失败！')
      })
    }
  }
}
</script>
