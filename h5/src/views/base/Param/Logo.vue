<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 11:00:07
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 14:52:46
-->
<template>
  <el-form :model="paramForm" label-width="100px" ref="paramForm">
    <el-form-item label="单位名称" prop="orgName">
      <el-input
        placeholder="请输入单位名称"
        v-model="paramForm.orgName"
      ></el-input>
    </el-form-item>
    <el-form-item label="单位商标" prop="orgLogo">
      <Upload
        ref="logoUpload"
        type="image"
        :files="paramForm.orgLogo"
        @success="logoSuccess"
        @remove="logoRemove"
        size="1"
      />
    </el-form-item>
    <el-form-item>
      <el-button @click="setting" type="primary">设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import Upload from 'components/Upload'
import { parmGet, parmLogo } from 'api/base'
import { getSetting, setSetting } from '@/utils/storage'
export default {
  components: { Upload },
  data() {
    return {
      paramForm: {
        orgLogo: [],
        orgName: '',
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      const { data } = await parmGet()
      this.paramForm.orgName = data.orgName
      this.paramForm.orgLogo.push({
        url: `/api/file/download?id=${Number(data.orgLogo)}`,
      })
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
          this.$store.dispatch('setting/changeSetting', {
            key: 'orgName',
            value: this.paramForm.orgName,
          })
          let loginInfo = getSetting()
          loginInfo.orgName = this.paramForm.orgName
          setSetting(loginInfo)
        } else {
          this.$message.error(msg)
        }
      })
    },
    // 上传logo成功
    logoSuccess(res, file, fileList) {
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
