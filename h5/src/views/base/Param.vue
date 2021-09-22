<!--
 * @Description: 设置logo,name
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-08 13:33:12
 * @LastEditors: Che
 * @LastEditTime: 2021-09-14 18:21:33
-->
<template>
  <div class="container">
    <el-form :model="paramForm" ref="paramForm">
      <el-form-item label="单位名称" label-width="300px" prop="orgName">
        <el-input
          placeholder="请输入单位名称"
          v-model="paramForm.orgName"
        ></el-input>
      </el-form-item>
      <el-form-item label="单位商标" label-width="300px" prop="orgLogo">
        <Upload
          ref="logoUpload"
          type="image"
          :files="paramForm.orgLogo"
          @success="logoSucess"
          @remove="logoRemove"
        />
      </el-form-item>
      <el-form-item label label-width="300px">
        <el-button @click="setting" type="primary">设置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import Upload from 'components/Upload'
import { parmGet, parmEditLogo } from 'api/base'
import { getInfo, setInfo } from '@/utils/storage'
export default {
  components: {
    Upload,
  },
  data() {
    return {
      paramForm: {
        orgLogo: [],
        orgName: '',
        id: null,
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 初始化
    async init() {
      const res = await parmGet()
      if (res.code === 200 && res.data) {
        res.data.orgLogo &&
          this.paramForm.orgLogo.push({
            url: `${process.env.VUE_APP_BASE_URL}file/download?id=${Number(
              res.data.orgLogo
            )}`,
          })
        this.paramForm.orgName = res.data.orgName
        this.paramForm.id = res.data.id
        this.$store.dispatch('setting/changeSetting', {
          key: 'orgLogo',
          value: res.data.orgLogo,
        })
        this.$store.dispatch('setting/changeSetting', {
          key: 'orgName',
          value: res.data.orgName,
        })

        let loginInfo = getInfo()
        loginInfo.orgLogo = res.data.orgLogo
        loginInfo.orgName = res.data.orgName
        setInfo(loginInfo)
      }
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

        const { code, msg } = await parmEditLogo(
          this.paramForm.id ? { id: this.paramForm.id, ...params } : params
        )

        if (code === 200) {
          this.paramForm.orgLogo = []
          this.init()
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
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: flex-start;
}
</style>
