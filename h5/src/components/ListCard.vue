<template>
  <div class="exam-item">
    <div class="exam-content">
      <!-- title -->
      <div class="title ellipsis">{{ data.name || data.examName }}</div>
      <!-- 创建者 -->
      <div class="content-info ellipsis" v-if="parentClassify.includes(name)">
        <span>创建者：{{ data.createUserName || data.userName }}</span>
      </div>
      <!-- 权限人员展示 -->
      <template v-if="parentClassify.includes(name)">
        <div class="content-info ellipsis">
          <span>使用权限：{{ data.readUserNames.join(',') || '暂无' }}</span>
        </div>
        <div class="content-info ellipsis">
          <span>编辑权限：{{ data.writeUserNames.join(',') || '暂无' }}</span>
        </div>
      </template>
      <!-- 试卷列表 -->
      <template v-if="name == 'paperList'">
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >创建人：{{ data.createUserName }}</el-col
          >
          <el-col :span="12" class="info-right"
            >修改人：{{ data.updateUserName || '***' }}</el-col
          >
        </el-row>
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >及格：{{ data.passScore }}</el-col
          >
          <el-col :span="12" class="info-right"
            >满分：{{ data.totalScore }}</el-col
          >
        </el-row>
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >组卷方式：{{ ['人工组卷', '自动组卷'][data.genType] }}</el-col
          >
          <el-col :span="12" class="info-right"
            >展示方式：{{ ['', '整张', '章节', '单题'][data.showType] }}</el-col
          >
        </el-row>
        <el-row class="content-info">
          <el-col class="info-state">{{
            data.state == 1 ? '已发布' : '草稿'
          }}</el-col>
        </el-row>
      </template>
      <!-- 考试列表 -->
      <template v-if="name == 'examList'">
        <el-row class="content-info">
          <el-col>发布人：{{ data.updateUserName || '***' }}</el-col>
        </el-row>
        <!-- <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >成绩公开：{{ data.scoreState == 1 ? '是' : '否' }}</el-col
          >
          <el-col :span="12" class="info-right"
            >排名公开：{{ data.rankState == 1 ? '是' : '否' }}</el-col
          >
        </el-row> -->
        <el-row :gutter="20" class="content-info">
          <el-col :span="12" class="info-left"
            >考试人数：{{ data.userNum }}</el-col
          >
          <el-col :span="12" class="info-right"
            >阅卷人数：{{ data.markNum }}</el-col
          >
        </el-row>
        <el-row class="content-info">
          <el-col class="info-state">{{
            data.state == 1 ? '已发布' : '草稿'
          }}</el-col>
        </el-row>
      </template>
      <!-- 我的考试列表 -->
      <template v-if="name == 'myExamList'">
        <el-row class="content-info">
          <el-col>创建者：{{ data.updateUserName }}</el-col>
        </el-row>
        <el-row class="content-info">
          <el-col>{{ data.examStartTime }}</el-col>
        </el-row>
        <el-row class="content-info">
          <el-col
            >及格：{{ data.totalScore || 0 }}/{{ data.paperTotalScore }}</el-col
          >
        </el-row>
        <div class="tagGroup">
          <el-tag size="mini" :type="data.state == 3 ? '' : 'danger'">{{
            data.stateName
          }}</el-tag
          >&nbsp;&nbsp;
          <el-tag size="mini" :type="data.markState == 3 ? '' : 'danger'">{{
            data.markStateName
          }}</el-tag
          >&nbsp;&nbsp;
        </div>
      </template>
      <!-- 按钮操作 -->
      <div class="handler">
        <!-- 基础操作（编辑、删除） -->
        <template v-if="baseSetting.includes(name)">
          <span data-title="编辑" @click="edit(data)">
            <i class="common common-edit"></i>
          </span>
          <span data-title="删除" @click="del(data)">
            <i class="common common-delete"></i>
          </span>
        </template>
        <!-- 合并 -->
        <span v-if="name == 'question'" data-title="移动" @click="move(data)">
          <i class="common common-move"></i>
        </span>
        <!-- 发布 -->
        <span
          v-if="childrenClassify.includes(name)"
          data-title="发布"
          @click="publish(data)"
        >
          <i class="common common-publish"></i>
        </span>
        <!-- 权限 -->
        <span
          v-if="parentClassify.includes(name)"
          data-title="权限"
          @click="role(data)"
        >
          <i class="common common-role"></i>
        </span>
        <!-- 开放（试题分类） -->
        <span v-if="name == 'question'" data-title="开放" @click="open(data)">
          <i class="common common-share"></i>
        </span>
        <!-- 试卷 -->
        <template v-if="name == 'paperList'">
          <span data-title="复制" @click="copy(data)">
            <i class="common common-copy"></i>
          </span>
          <span data-title="统计" @click="statistics(data)">
            <i class="common common-statistics"></i>
          </span>
          <!-- <span
          data-title="归档"
          @click="archive(data)"
        >
          <i class="common common-archive"></i>
        </span> -->
          <span data-title="开始组卷" @click="composition(data)">
            <i class="common common-composition"></i>
          </span>
        </template>
        <!-- 考试 -->
        <template v-if="name == 'examList'">
          <span data-title="通知" @click="message(data)">
            <i class="common common-messages"></i>
          </span>
          <span data-title="阅卷设置" @click="read(data)">
            <i class="common common-readPaper"></i>
          </span>
          <span data-title="考试用户" @click="user(data)">
            <i class="common common-wode"></i>
          </span>
        </template>
        <!-- 我的考试 -->
        <template>
          <span
            v-if="name == 'myExamList' && data.btn == 'unStart'"
            data-title="未开始"
          >
            <i class="common common-wode"></i>
          </span>
          <span
            v-if="name == 'myExamList'"
            :data-title="data.btn == 'start' ? '开始考试' : '预览考试'"
            @click="startExam(data)"
          >
            <i class="common common-wode"></i>
          </span>
        </template>
        <span
          v-if="detailTitles[name]"
          :data-title="detailTitles[name]"
          @click="detail(data)"
        >
          <i class="common common-list-row"></i>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: {},
    },
    name: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      detailTitles: {
        question: '考题详情',
        paper: '试卷列表',
        exam: '考试列表',
      },
      baseSetting: ['question', 'paper', 'exam', 'paperList', 'examList'],
      parentClassify: ['question', 'paper', 'exam'],
      childrenClassify: ['paperList', 'examList'],
    }
  },
  methods: {
    // 是否有权限（只有创建者才有权限）
    isRole(data) {
      // 是否是创建者
      const isCreateUser =
        data.createUserId &&
        JSON.parse(this.$store.state.userInfo).userId != data.createUserId
      // 是否是修改者
      const isUpdateUser =
        data.updateUserId &&
        JSON.parse(this.$store.state.userInfo).userId != data.updateUserId
      // 是否已经发布
      const isPublish = data.state == 1
      // 是否是分类
      const isparentClassify = this.parentClassify.includes(this.name)
      // 是否是子分类
      const isChildrenClassify = this.childrenClassify.includes(this.name)

      if (isparentClassify && (isCreateUser || isUpdateUser)) {
        this.$tools.message('暂无此项权限！', 'warning')
        return true
      }

      if (isChildrenClassify && isPublish) {
        this.$tools.message('已发布不可修改！', 'warning')
        return true
      }
      return false
    },
    // 编辑
    edit(data) {
      if (this.isRole(data)) return
      this.$emit('edit', data)
    },
    // 删除
    del(data) {
      if (this.isRole(data)) return
      this.$emit('del', data)
    },
    // 权限
    role(data) {
      if (this.isRole(data)) return
      this.$emit('role', data)
    },
    // 开放
    open(data) {
      if (this.isRole(data)) return
      this.$emit('open', data)
    },
    // 列表|详情
    detail(data) {
      this.$emit('detail', data)
    },
    // 复制
    copy(data) {
      this.$emit('copy', data)
    },
    // 通知
    message(data) {
      this.$emit('message', data)
    },
    // 组卷
    composition(data) {
      this.$emit('composition', data)
    },
    // 统计
    statistics(data) {
      this.$emit('statistics', data)
    },
    // 归档
    archive(data) {
      this.$emit('archive', data)
    },
    // 阅卷设置
    read(data) {
      this.$emit('read', data)
    },
    // 考试用户
    user(data) {
      this.$emit('user', data)
    },
    // 发布考试
    publish(data) {
      this.$emit('publish', data)
    },
    // 开始考试
    startExam(data) {
      this.$emit('startExam', data)
    },
    // 预览考试
    viewExam(data) {
      this.$emit('viewExam', data)
    },
    // 移动
    move(data) {
      if (this.isRole(data)) return
      this.$emit('move', data)
    },
  },
}
</script>

<style lang="scss" scoped>
.exam-data {
  width: calc(100% / 3);
  display: flex;
  justify-content: center;
  align-items: stretch;
  margin-bottom: 20px;
}

.exam-content {
  display: flex;
  flex-direction: column;
  width: 95%;
  padding: 30px 15px 20px;
  background: #fff;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 16px -10px rgba(95, 101, 105, 0.15);
  }
  .tagGroup {
    position: absolute;
    right: 10px;
    top: 10px;
  }
  .title {
    font-size: 16px;
    margin: 0 0 10px;
    word-break: keep-all;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .content-info {
    font-size: 13px;
    color: #9199a1;
    margin-top: 10px;
    .info-left {
      text-align: right;
    }
    .info-right {
      text-align: left;
    }
    .info-state {
      color: #ff9900;
    }
  }
  .handler {
    span {
      display: inline-block;
      margin-right: 10px;
      text-align: center;
      width: 35px;
      height: 35px;
      color: #8392a5;
      border-radius: 50%;
      border: 1px solid #eff3f7;
      margin-top: 25px;
      line-height: 35px;
      position: relative;
      transition: all 0.3s ease-in-out;
      .handler-more {
        background: #4a5766;
        width: 120px;
        color: #fff;
        border-radius: 3px;
        font-size: 12px;
        position: absolute;
        left: 60px;
        top: 50%;
        transform: translateY(-50%);
        opacity: 0;
        transition: all 0.3s ease-in-out;
        .more-data {
          padding: 12px 0;
          border-bottom: 1px solid #65707d;
          line-height: 1.45;
          &:last-child {
            border-bottom: none;
          }
          &:hover {
            background: #0095e5;
            color: #fff;
          }
        }
        &::before {
          content: '';
          display: block;
          position: absolute;
          z-index: 100;
          left: -10px;
          top: 50%;
          transform: translateY(-50%);
          border-width: 5px 10px 5px 0;
          border-style: solid;
          border-color: transparent #4a5766 transparent transparent;
        }
      }
      &:last-child:hover {
        .handler-more {
          left: 50px;
          opacity: 1;
        }
      }
      &::after {
        content: attr(data-title);
        display: block;
        position: absolute;
        z-index: 100;
        bottom: -45px;
        transform: translateX(-50%);
        left: 50%;
        width: 70px;
        height: 30px;
        line-height: 30px;
        background: #0095e5;
        color: #fff;
        border-radius: 3px;
        font-size: 13px;
        opacity: 0;
      }
      &::before {
        content: '';
        display: block;
        position: absolute;
        z-index: 100;
        bottom: -18px;
        left: 50%;
        transform: translateX(-50%);
        border-width: 0 5px 10px 5px;
        border-style: solid;
        border-color: transparent transparent #0095e5;
        opacity: 0;
      }
      &:hover {
        border: 1px solid #0095e5;
        background: #0095e5;
        color: #fff;
        &::after {
          opacity: 1;
        }
        &::before {
          opacity: 1;
        }
      }
    }
  }
}
</style>
