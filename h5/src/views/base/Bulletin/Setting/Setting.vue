<!--
 * @Description: 设置
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-12-16 16:01:13
 * @LastTinymceEditors: Che
 * @LastEditTime: 2022-01-21 17:04:02
-->
<template>
  <div class="container">
    <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
      <el-form-item label="标题" label-width="120px" prop="title">
        <el-input placeholder="请输入标题" v-model="editForm.title"></el-input>
      </el-form-item>
      <el-form-item label="内容" label-width="120px" prop="content">
        <TinymceEditor
          :value="editForm.content"
          @editorListener="editorListener"
          id="content"
        ></TinymceEditor>
      </el-form-item>
      <el-form-item label="阅读人员" label-width="120px" prop="examUser">
        <CustomSelect
          ref="readSelect"
          placeholder="请选择用户"
          :value="editForm.examUser"
          :total="editForm.total"
          @input="searchUser"
          @change="selectExamUser"
          @currentChange="getMoreUser"
          @visibleChange="getUserList"
        >
          <el-option
            v-for="item in editForm.examUsers"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </CustomSelect>
      </el-form-item>
      <el-form-item label="展示时间" label-width="120px" prop="showTime">
        <el-date-picker
          v-model="editForm.showTime"
          type="datetimerange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="展示状态" label-width="120px" prop="showType">
        <el-radio
          v-for="item in editForm.showTypeList"
          :key="item.value"
          v-model="editForm.showType"
          :label="item.value"
          >{{ item.name }}</el-radio
        >
      </el-form-item>
    </el-form>
    <div class="form-footer">
      <el-button @click="add" type="primary" v-if="!id">添加</el-button>
      <el-button @click="edit" type="primary" v-if="id">修改</el-button>
    </div>
  </div>
</template>

<script>
import { bulletinAdd, bulletinEdit, bulletinGet } from 'api/base'
import { userListPage } from 'api/user'
import Upload from 'components/Upload'
import TinymceEditor from 'components/TinymceEditor/Index.vue'
import CustomSelect from 'components/CustomSelect.vue'

import * as dayjs from 'dayjs'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
dayjs.extend(isSameOrBefore)
export default {
  components: {
    TinymceEditor,
    Upload,
    CustomSelect,
  },
  data() {
    const validateShowTime = (rule, value, callback) => {
      if (value.length == 0) {
        return callback(new Error('请选择展示时间'))
      }
      if (dayjs(value[1]).isSameOrBefore(dayjs())) {
        return callback(new Error(`请选择当前时间后的时间段`))
      }
      return callback()
    }
    return {
      id: null,
      // 修改表单
      editForm: {
        title: '', // 标题
        imgFileId: [], // 图片
        content: '', // 内容
        showTime: [], //展示时间
        showType: 1, // 置顶状态
        showTypeList: [
          {
            name: '默认',
            value: 1,
          },
          {
            name: '置顶',
            value: 2,
          },
        ],
        show: false, // 是否显示页面
        examUsers: [],
        examUser: [],
        pageSize: 5, // 每页多少条
        total: 0,
        // 校验
        rules: {
          title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
          showTime: [{ required: true, validator: validateShowTime }],
        },
      },
      headers: {
        Authorization: this.$store.getters.token,
      },
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    if (Number(this.id)) {
      const res = await bulletinGet({ id: this.id })
      if (res.code != 200) {
        this.$message.error(res.msg)
        return
      }

      this.editForm.imgFileId = []
      this.editForm.examUser = []
      this.editForm.content = res.data?.content || ''
      this.$nextTick(() => {
        this.editForm.title = res.data.title
        this.editForm.showType = res.data.showType
        this.editForm.showTime = [
          `${res.data.startTime}`,
          `${res.data.endTime}`,
        ]
        if (res.data.readUserNames !== '') {
          const { roleIds: readIds, roleNames: readNames } =
            this.compositionRoles(res.data.readUserIds, res.data.readUserNames)
          this.editForm.examUser.push(...readIds)
          this.$refs['readSelect'].$refs['elSelect'].cachedOptions.push(
            ...readNames
          )
        }
        if (res.data.imgFileId !== '') {
          this.editForm.imgFileId.push({
            url: `/api/file/download?id=${Number(res.data.imgFileId)}`,
          })
        }
      })
    }
  },
  methods: {
    // 编辑器监听事件
    editorListener(id, value) {
      this.editForm[id] = value
    },
    // 获取用户
    async getUserList(curPage = 1, name = '') {
      const examUsers = await userListPage({
        name,
        curPage,
        pageSize: this.editForm.pageSize,
      })

      this.editForm.examUsers = examUsers.data.list
      this.editForm.total = examUsers.data.total
    },
    // 获取更多用户
    getMoreUser(curPage, name) {
      this.getUserList(curPage, name)
    },
    // 根据name 查询人员
    searchUser(name) {
      this.getUserList(1, name)
    },
    // 选择阅卷考生
    selectExamUser(e) {
      this.editForm.examUser = e
    },
    // 添加机构
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await bulletinAdd({
          title: this.editForm.title,
          content: this.editForm.content,
          readUserIds: this.editForm.examUser,
          showType: this.editForm.showType,
          startTime: this.editForm.showTime[0],
          endTime: this.editForm.showTime[1],
          imgFileId:
            this.editForm.showType === 3 && this.editForm.imgFileId.length > 0
              ? this.editForm.imgFileId[0].response.data.fileIds
              : null,
        })

        if (res.code != 200) {
          this.$message.error(res.msg)
          return
        }

        this.$router.back()
      })
    },
    // 修改
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }
        const res = await bulletinEdit({
          id: this.id,
          title: this.editForm.title,
          imgFileId:
            this.editForm.showType === 3 && this.editForm.imgFileId.length > 0
              ? this.editForm.imgFileId[0]?.response
                ? this.editForm.imgFileId[0].response.data.fileIds
                : this.$tools.getQueryParam(
                    this.editForm.imgFileId[0].url,
                    'id'
                  )
              : null,
          content: this.editForm.content,
          readUserIds: this.editForm.examUser,
          showType: this.editForm.showType,
          startTime: this.editForm.showTime[0],
          endTime: this.editForm.showTime[1],
        })

        if (res.code != 200) {
          this.$message.error(res.msg)
          return
        }

        this.$router.back()
      })
    },
    // 组合回显数据
    compositionRoles(userIds, userNames) {
      const ids = userIds
        .filter((item) => item !== '')
        .map((item) => Number(item))
      const names = userNames
      const roles = ids.reduce(
        (acc, cur, index) => {
          acc['roleIds'].push(cur)
          acc['roleNames'].push({
            currentLabel: names[index],
            currentValue: cur,
            label: names[index],
            value: cur,
          })
          return acc
        },
        { roleIds: [], roleNames: [] }
      )
      return roles
    },
  },
}
</script>

<style lang="scss" scoped>
.form-footer {
  padding-left: 60px;
}
</style>
