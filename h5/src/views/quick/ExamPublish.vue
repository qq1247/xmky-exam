<template>
  <div>
    <div class="quick-info">
      <div class="info-name">考试名称：</div>
      <div class="info-content">{{ quickInfo.examName }}</div>
    </div>
    <div class="quick-info">
      <div class="info-name">试卷名称：</div>
      <div class="info-content">{{ quickInfo.paperName }}</div>
    </div>
    <div class="quick-info">
      <div class="info-name">组卷方式：</div>
      <div class="info-content">
        {{ quickInfo.genType === 1 ? '人工组卷' : '随机组卷' }}
      </div>
    </div>
    <div class="quick-info">
      <div class="info-name">阅卷方式：</div>
      <div class="info-content">
        {{ quickInfo.markType === 1 ? '智能阅卷' : '人工阅卷' }}
      </div>
    </div>
    <div class="quick-info">
      <div class="info-name">及格：</div>
      <div class="info-content">{{ quickInfo.passScore }}</div>
    </div>
    <div class="quick-info">
      <div class="info-name">展示方式：</div>
      <div class="info-content">
        {{ quickInfo.showType === 1 ? '整张' : '单体' }}
      </div>
    </div>
    <div
      class="quick-info"
      v-for="(user, index) in quickInfo.userList"
      :key="index"
    >
      <div class="info-name">考试 / 阅卷：</div>
      <div class="info-content">
        {{ user.examUserList.join(',') }} / {{ user.markUserName }}
      </div>
    </div>
    <div class="quick-info">
      <div class="info-name"></div>
      <div>
        <el-button @click="$emit('prev')" type="primary">上一步</el-button>
        <el-button @click="publish" type="primary">发布</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getQuick } from '@/utils/storage'
import { examPublish } from 'api/exam'
export default {
  data() {
    return {
      quickInfo: {},
    }
  },
  methods: {
    async publish() {
      const res = await examPublish({ id: this.quickInfo.examId })
      if (res?.code == 200) {
        this.$message.success('考试发布成功！')
        this.$router.push('/')
      } else {
        this.$message.error('请重新发布考试！')
      }
    },
  },
  activated() {
    this.quickInfo = getQuick()
  },
}
</script>

<style lang="scss" scoped>
.quick-info {
  display: flex;
  line-height: 30px;
  &:last-child {
    margin-top: 10px;
  }
}
.info-name {
  text-align: right;
  color: #999;
  width: 120px;
}
.info-content {
  padding-left: 10px;
}
</style>
