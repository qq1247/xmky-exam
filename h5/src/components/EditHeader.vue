<template>
  <div class="edit-header">
    <div class="header-left">
      <el-link
        class="back"
        :underline="false"
        @click="goBack"
        icon="el-icon-arrow-left"
        >返回</el-link
      >
      <template v-if="isQuestion">
        <el-tooltip
          content="单选"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-radio"></i>
        </el-tooltip>
        <el-tooltip
          content="多选"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-check-box"></i>
        </el-tooltip>
        <el-tooltip
          content="填空"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-cloze"></i>
        </el-tooltip>
        <el-tooltip
          content="判断"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-judge"></i>
        </el-tooltip>
        <el-tooltip
          content="问答"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-ask"></i>
        </el-tooltip>
        <el-tooltip
          content="试题模板"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-down-load"></i>
        </el-tooltip>
        <el-tooltip
          content="试题导入"
          effect="dark"
          placement="bottom"
          class="handler"
        >
          <i @click="loginOut" class="common common-up-load"></i>
        </el-tooltip>
      </template>
    </div>
    <div class="header-center ellipsis">{{ title }}</div>
    <div class="header-right">
      <span class="preview" @click="preview"
        ><i class="el-icon-view"></i>预览</span
      >
      <span class="line">|</span>
      <span class="user-info"
        ><i class="el-icon-user"></i>{{ JSON.parse(userInfo).userName }}</span
      >
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  props: {
    title: {
      type: String,
      default: '',
    },
    isQuestion: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  components: {},
  mounted() {},
  computed: mapState(['userInfo']),
  methods: {
    goBack() {
      this.$router.back()
    },
    preview() {
      this.$emit('preview')
    },
  },
}
</script>

<style lang="scss" scoped>
.edit-header {
  width: 100%;
  height: 50px;
  background: #2c2c2c;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  .header-left {
    .back {
      color: #fff;
    }
    .handler {
      width: 40px;
      height: 50px;
      line-height: 50px;
      text-align: center;
      cursor: pointer;
      &:first-of-type {
        margin-left: 20px;
      }
      &:hover {
        background: #0095e5;
      }
    }
  }
  .header-center {
    text-align: center;
    flex: 1;
  }
  .header-right {
    .preview,
    .user-info {
      cursor: pointer;
      i {
        margin-right: 8px;
      }
    }
    .line {
      margin: 0 15px;
      color: #999;
    }
  }
}
</style>
