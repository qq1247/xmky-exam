<template>
  <div class="exam-publish-box">
    <!-- 考试信息 -->
    <div class="cross-line-box">
      <div class="left-cross-line"></div>
      <div class="cross-text">考试信息</div>
      <div class="right-cross-line"></div>
    </div>

    <div class="quick-info">
      <div class="info-name">考试名称：</div>
      <div class="info-content">{{quickInfo.examName}}</div>
    </div>

    <div class="quick-info quick-info-more">
      <div class="info-more-2-width">
        <div class="info-name">考试时间：</div>
        <div class="info-content">{{`${quickInfo.startTime} - ${quickInfo.endTime}`}}</div>
      </div>
      <div class="info-more-2-width">
        <div class="info-name">阅卷时间：</div>
        <div class="info-content">{{`${quickInfo.markStartTime} - ${quickInfo.markEndTime}`}}</div>
      </div>
    </div>

    <!-- 选项信息 -->
    <div class="cross-line-box">
      <div class="left-cross-line"></div>
      <div class="cross-text">选项信息</div>
      <div class="right-cross-line"></div>
    </div>
    
    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">分数：</div>
        <div class="info-content">{{quickInfo.passScore}}</div>
      </div>
      <div class="info-more-4-width">
        <div class="info-name">组卷方式：</div>
        <div class="info-content">
           {{ quickInfo.genType === 1 ? '人工组卷' : '随机组卷' }}
        </div>
      </div>
      <div class="info-more-4-width">
        <div class="info-name">匿名组卷：</div>
        <div class="info-content">{{quickInfo.anonState === 1 ? '是' : '否'}}</div>
      </div>
      <div class="info-more-4-width">
        <div class="info-name">展开方式：</div>
        <div class="info-content">
           {{ quickInfo.showType === 1 ? '整张' : '单题' }}
        </div>
      </div>
    </div>

    <div class="quick-info quick-info-more">
      <div class="info-more-4-width">
        <div class="info-name">防作弊：</div>
        <div class="info-content">{{quickInfo.showOptions.join()}}</div>
      </div>
      <div class="info-more-4-width">
        <div class="info-name">阅卷方式：</div>
        <div class="info-content">
          {{ quickInfo.markType === 1 ? '智能阅卷' : '人工阅卷' }}
        </div>
      </div>
      <div class="info-more-4-width">
        <div class="info-name">成绩公开：</div>
        <div class="info-content">{{quickInfo.scoreState === 1 ? '是' : '否'}}</div>
      </div>
      <div class="info-more-4-width">
        <div class="info-name">排名公开：</div>
        <div class="info-content">{{quickInfo.rankState === 1 ? '是' : '否'}}</div>
      </div>
    </div>
    
    <!-- 用户信息 -->
    <div class="cross-line-box">
      <div class="left-cross-line"></div>
      <div class="cross-text">用户信息</div>
      <div class="right-cross-line"></div>
    </div>

    <!-- <div class="quick-info">
      <div class="info-name">及格：</div>
      <div class="info-content">
        {{ ((quickInfo.passScore / 100) * quickInfo.totalScore).toFixed() }}
      </div>
    </div> -->


    <div
      v-for="(user, index) in quickInfo.userList"
      :key="index"
      class="quick-info"
    >
      <div class="info-name">考试 / 阅卷：</div>
      <div class="info-content">
        <span>{{ user.examUserList.join(',') }}</span><span v-if="user.markUserName">/ {{ user.markUserName }}</span>
      </div>
    </div>
    <!-- <div class="footer">
      <el-button type="primary" @click="$emit('prev', '4')">上一步</el-button>
      <el-button type="primary" @click="publish">发布</el-button>
    </div> -->
  </div>
</template>

<script>
import { getQuick } from '@/utils/storage'
import { examPublish } from 'api/exam'
export default {
  data() {
    return {
      quickInfo: {}
    }
  },
  created() {
    this.quickInfo = getQuick()
    const showOptions = []
    if (this.quickInfo.options.length > 0) {
      this.quickInfo.options.forEach(element => {
        showOptions.push(element == 1 ? '试题乱序':'选项乱序')
      });
    }
    this.quickInfo.showOptions = showOptions
  },
  methods: {
    async publish() {
      const res = await examPublish({ id: this.quickInfo.examId })
      if (res?.code === 200) {
        this.$message.success('考试发布成功！')
        this.$router.push('/')
      } else {
        this.$message.error('请重新发布考试！')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-publish-box {
  padding: 15px 150px;
  .cross-line-box {
    display: flex;
    align-items: center;
    padding: 20px 0;
    .left-cross-line {
      width: 30px;
      border-bottom: 1px solid #D8D8D8;
    }
    .cross-text {
      padding: 0 10px;
      font-size: 18px;
      font-weight: bold;
      color: #0D2332
    }
    .right-cross-line {
      flex: 1;
      border-bottom: 1px solid #D8D8D8;
    }
  }
  .quick-info-more {
    justify-content: space-between;
    .info-more-2-width {
      width: calc(100% / 2);
      display: flex;
    }
    .info-more-4-width {
      width: calc(100% / 4);
      display: flex;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  .quick-info {
    width: 100%;
    display: flex;
    line-height: 30px;
    .info-name {
      color: #999;
    }
    .info-content {
      flex: 1;
      padding: 0 10px;
    }
  }
}
</style>
