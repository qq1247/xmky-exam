<template>
  <div class="exam-item">
    <div class="exam-content">
      <div class="title">{{ data.name }}</div>
      <template v-if="['question', 'paper', 'exam'].includes(name)">
        <div class="content-info">
          <span>读取权限：{{ data.readUserNames || "暂无" }}</span>
        </div>
        <div class="content-info">
          <span>使用权限：{{ data.writeUserNames || "暂无" }}</span>
        </div>
      </template>
      <template v-if="name == 'paperList'">
        <div class="content-info ellipsis">
          <span>发布人：{{ data.userName }}</span>
        </div>
        <div class="content-info">
          <span class="space">及格：{{ data.passScore }}</span>
          <span>满分：{{ data.totalScore }}</span>
        </div>
        <div class="content-info">
          <span class="space"
            >组卷方式：{{ ["人工组卷", "自动组卷"][data.genType] }}</span
          >
          <span>展示方式：{{}}</span>
        </div>
      </template>
      <template v-if="name == 'examList'">
        <div class="content-info ellipsis">
          <span>发布人：张三</span>
        </div>
        <div class="content-info">
          <span class="space">成绩公开：是</span>
          <span>排名公开：是</span>
        </div>
        <div class="content-info">
          <span class="space">考试人数：50</span>
          <span>阅卷人数：5</span>
        </div>
        <div class="content-info">
          <span style="color: #ff9900">已发布</span>
        </div>
      </template>
      <div class="handler">
        <span data-title="编辑" @click="edit(data)">
          <i class="common common-edit"></i>
        </span>
        <span data-title="删除" @click="del(data)">
          <i class="common common-delete"></i>
        </span>
        <span v-if="name == 'question'" data-title="权限" @click="role(data)">
          <i class="common common-role"></i>
        </span>
        <span v-if="name == 'question'" data-title="开放" @click="open(data)">
          <i class="common common-share"></i>
        </span>
        <span v-if="name == 'paperList'" data-title="复制" @click="copy(data)">
          <i class="common common-copy"></i>
        </span>
        <span
          v-if="name == 'examList'"
          data-title="通知"
          @click="message(data)"
        >
          <i class="common common-message"></i>
        </span>
        <span
          v-if="['paperList', 'examList'].includes(name)"
          data-title="统计"
          @click="statistics(data)"
        >
          <i class="common common-statistics"></i>
        </span>
        <span
          v-if="['paperList', 'examList'].includes(name)"
          data-title="归档"
          @click="archive(data)"
        >
          <i class="common common-archive"></i>
        </span>
        <span
          v-if="name == 'paperList'"
          data-title="生成试卷"
          @click="composition(data)"
        >
          <i class="common common-composition"></i>
        </span>
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
      default: {}
    },
    name: {
      type: String,
      default: "",
    }
  },
  data() {
    return {
      detailTitles: {
        question: "考题详情",
        paper: "试卷列表",
        exam: "考试列表",
      }
    }
  },
  methods: {
    // 编辑
    edit(data) {
      this.$emit("edit", data)
    },
    // 删除
    del(data) {
      this.$emit("del", data)
    },
    // 权限
    role(data) {
      this.$emit("role", data)
    },
    // 开放
    open(data) {
      this.$emit("open", data)
    },
    // 列表|详情
    detail(data) {
      this.$emit("detail", data)
    },
    // 复制
    copy(data) {
      this.$emit("copy", data)
    },
    // 通知
    message(data) {
      this.$emit("message", data)
    },
    // 组卷
    composition(data) {
      this.$emit("composition", data)
    },
    // 统计
    statistics(data) {
      this.$emit("statistics", data)
    },
    // 归档
    archive(data) {
      this.$emit("archive", data)
    },
  }
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
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 16px -10px rgba(95, 101, 105, 0.15);
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
    font-size: 12px;
    color: #9199a1;
    margin-top: 10px;
    width: 100%;
    text-align: center;
    padding: 0 20px;
    .space {
      margin-right: 20px;
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
          content: "";
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
        content: "";
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
