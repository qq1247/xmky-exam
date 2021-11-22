<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 11:00:07
 * @LastEditors: Che
 * @LastEditTime: 2021-11-12 16:44:54
-->
<template>
  <div class="param-option">
    <div class="param-title">单位参数</div>
    <el-form :model="paramForm" :label-position="labelPosition" ref="paramForm">
      <el-form-item label="单位名称" label-width="100px" prop="orgName">
        <el-input
          placeholder="请输入单位名称"
          v-model="paramForm.orgName"
        ></el-input>
      </el-form-item>
      <el-form-item label="单位商标" label-width="100px" prop="orgLogo">
        <Upload
          ref="logoUpload"
          type="image"
          :files="paramForm.orgLogo"
          @success="logoSucess"
          @remove="logoRemove"
          size="1"
        />
      </el-form-item>
      <el-form-item label label-width="100px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import Upload from 'components/Upload'
import { parmLogo } from 'api/base'
export default {
  components: { Upload },
  props: {
    logo: {
      type: Number,
      default: null,
    },
    name: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      labelPosition: 'left',
      paramForm: {
        orgLogo: [],
        orgName: '',
      },
    }
  },
  watch: {
    logo: {
      immediate: true,
      handler(n, o) {
        this.setData()
      },
    },
  },
  update() {
    this.setData()
  },
  methods: {
    // 初始化
    async setData() {
      this.paramForm.orgLogo = []
      this.paramForm.orgLogo.push({
        url: `${process.env.VUE_APP_BASE_URL}file/download?id=${Number(
          this.logo
        )}`,
      })
      this.paramForm.orgName = this.name
    },
    // 设置
    setting() {
      this.$refs['paramForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const params = {
          orgLogo:
            this.paramForm.orgLogo.length > 0
              ? this.paramForm.orgLogo[0]?.response
                ? this.paramForm.orgLogo[0].response.data.fileIds
                : this.$tools.getQueryParam(this.paramForm.orgLogo[0].url, 'id')
              : null,
          orgName: this.paramForm.orgName,
        }

        const { code, msg } = await parmLogo(params)

        if (code === 200) {
          this.$message({
            message: '设置成功',
            duration: 1000,
          })
          this.$emit('resetLogo')
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 上传logo成功
    logoSucess(res, file, fileList) {
      this.paramForm.orgLogo = fileList
    },
    // 上传logo失败
    logoClear(ref) {
      if (this.paramForm.orgLogo.length > 0) {
        this.$refs[ref].clear()
        this.$set(this.paramForm, 'orgLogo', [])
      }
    },
    // 删除logo
    logoRemove(file, fileList) {
      this.paramForm.orgLogo = fileList
    },
  },
}
</script>
