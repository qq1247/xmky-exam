<template>
  <el-form
    ref="editForm"
    :model="editForm"
    :rules="editForm.rules"
    label-width="100px"
  >
    <el-form-item label="所属机构" prop="orgId">
      <CustomSelect
        ref="orgSelect"
        :multiple="false"
        placeholder="请选择机构"
        :value="editForm.orgId"
        :total="editForm.orgListpage.total"
        @input="searchOrg"
        @change="selectOrg"
        @currentChange="getMoreOrg"
        @visibleChange="getOrgList"
      >
        <el-option
          v-for="item in editForm.orgListpage.list"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </CustomSelect>
    </el-form-item>
    <el-form-item label="登录账号" prop="loginName">
      <el-input
        v-model.trim="editForm.loginName"
        placeholder="请输入登录账号"
      />
    </el-form-item>
    <el-form-item label="姓名" prop="name">
      <el-input v-model.trim="editForm.name" placeholder="请输入姓名" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model.trim="editForm.email" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item label="头像" prop="avatar">
      <el-upload
        class="avatar-uploader"
        name="files"
        :headers="headers"
        :show-file-list="false"
        action="/api/file/upload"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
      >
        <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar">
        <i v-else class="el-icon-plus avatar-uploader-icon" />
      </el-upload>
    </el-form-item>
    <el-form-item>
      <el-button v-if="!id" type="primary" @click="add">添加</el-button>
      <el-button v-if="id" type="primary" @click="edit">修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { userAdd, userEdit, userGet } from 'api/user'
import { orgListPage } from 'api/base'
import CustomSelect from 'components/CustomSelect.vue'
export default {
  components: {
    CustomSelect
  },
  data() {
    const validateEmail = (rule, value, callback) => {
      if (
        value &&
        !/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(
          value
        )
      ) {
        return callback(new Error(`邮箱格式有误`))
      }
      return callback()
    }
    return {
      id: null,
      headers: {
        Authorization: this.$store.getters.token
      },
      editForm: {
        // 修改表单
        name: null, // 名称
        loginName: null, // 登录账号
        orgId: null, // 机构ID
        orgName: null, // 机构名称
        show: false, // 是否显示页面
        email: '',
        avatar: '',
        orgListpage: {
          total: 0,
          curPage: 1,
          pageSize: 5,
          list: []
        },
        rules: {
          // 校验
          loginName: [
            { required: true, message: '请输入登录账号', trigger: 'blur' }
          ],
          name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
          email: [{ required: false, validator: validateEmail }]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await userGet({ id: this.id })
      if (res.code !== 200) {
        this.$message.error(res.msg)
        return
      }

      await this.getOrgList()
      this.$nextTick(() => {
        this.editForm.name = res.data.name
        this.editForm.loginName = res.data.loginName
        this.editForm.orgId = res.data.orgId
        this.editForm.orgName = res.data.orgName
        this.editForm.roles = res.data.roles[0]
        this.$refs['orgSelect'].$refs['elSelect'].cachedOptions.push({
          currentLabel: res.data.orgName,
          currentValue: res.data.orgId,
          label: res.data.orgName,
          value: res.data.orgId
        })
        this.editForm.email = res.data.email || ''
        if (res?.data?.headFileId) {
          this.editForm.avatar = `/api/file/download?id=${Number(
            res.data.headFileId
          )}`
        }
      })
    }
  },
  methods: {
    // 添加机构
    add() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const res = await userAdd({
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
          phone: this.editForm.phone,
          headFileId:
            this.$tools.getQueryParam(this.editForm.avatar, 'id') || null,
          email: this.editForm.email
        })

        if (res.code !== 200) {
          this.$message.error(res.msg)
          return
        }
        this.$router.back()
        this.$alert(res.data.initPwd, '当前密码', {
          confirmButtonText: '确定'
        })
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async(valid) => {
        if (!valid) {
          return false
        }

        const res = await userEdit({
          id: this.id,
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
          headFileId:
            this.$tools.getQueryParam(this.editForm.avatar, 'id') || null,
          email: this.editForm.email
        })

        if (res.code !== 200) {
          this.$message.error(res.msg)
          return
        }

        this.$router.back()
      })
    },
    // 获取机构列表
    async getOrgList(curPage = 1, name = '') {
      const orgList = await orgListPage({
        name,
        curPage,
        pageSize: this.editForm.orgListpage.pageSize
      })
      this.editForm.orgListpage.list = orgList.data.list
      this.editForm.orgListpage.total = orgList.data.total
    },
    // 获取更多用户
    getMoreOrg(curPage, name) {
      this.getOrgList(curPage, name)
    },
    // 根据name查询机构
    searchOrg(name) {
      this.getOrgList(1, name)
    },
    // 选择考试用户
    selectOrg(e) {
      this.editForm.orgId = e
    },
    handleAvatarSuccess(res, file) {
      this.editForm.avatar = `/api/file/download?id=${Number(res.data.fileIds)}`
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt1M = file.size / 1024 / 1024 < 1

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt1M) {
        this.$message.error('上传头像图片大小不能超过 1MB!')
      }
      return isJPG && isLt1M
    }
  }
}
</script>

<style lang="scss" scoped>
.avatar-uploader {
  /deep/.el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    &:hover {
      border-color: #409eff;
    }
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      line-height: 178px;
      text-align: center;
    }
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
  }
}
.form-footer {
  padding-left: 120px;
}
</style>
