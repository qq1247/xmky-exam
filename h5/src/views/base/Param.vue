<!--
 * @Description: 设置logo,name
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-09-08 13:33:12
 * @LastEditors: Che
 * @LastEditTime: 2021-11-09 15:10:27
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
          size="1"
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
import { parmLogo, parmGet } from 'api/base'
import { getOrg, setOrg } from '@/utils/storage'
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
      const {
        code,
        data: { orgLogo, orgName },
      } = await parmGet()
      if (code === 200) {
        this.paramForm.orgLogo.push({
          url: `${process.env.VUE_APP_BASE_URL}file/download?id=${Number(
            orgLogo
          )}`,
        })
        this.paramForm.orgName = orgName
        this.$store.dispatch('setting/changeSetting', {
          key: 'orgName',
          value: orgName,
        })
        let loginInfo = getOrg()
        loginInfo.orgName = orgName
        setOrg(loginInfo)
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

        const { code, msg } = await parmLogo(params)

        if (code === 200) {
          this.paramForm.orgLogo = []
          await this.init()
          let link = document.querySelector("link[rel*='icon']")
          link.href = `${process.env.VUE_APP_BASE_URL}login/logo?icon=true`
          this.$router.go()
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
