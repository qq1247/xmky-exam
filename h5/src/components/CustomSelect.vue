<template>
  <el-select
    :style="{ width: isAuto ? 'auto' : '100%' }"
    :value="value"
    :multiple="multiple"
    :placeholder="placeholder"
    @change="change"
    @visible-change="visibleChange"
  >
    <el-input
      v-model="selectInput"
      class="select-input"
      placeholder="请输入候选词"
      prefix-icon="el-icon-search"
      @input="input"
    ></el-input>
    <slot></slot>
    <div class="select-pager">
      <button
        :class="['page-pre', currentPage == 1 ? 'disabled' : '']"
        @click="currentChange(0)"
      >
        上一页
      </button>
      <div class="page-content">{{ currentPage }} / {{ totalPage }}</div>
      <button
        :class="['page-next', currentPage == totalPage ? 'disabled' : '']"
        @click="currentChange(1)"
      >
        下一页
      </button>
    </div>
  </el-select>
</template>

<script>
export default {
  props: {
    isAuto: {
      type: Boolean,
      default: false,
    },
    value: {
      type: Boolean | String | Number,
      default: '',
    },
    multiple: {
      type: Boolean,
      default: true,
    },
    placeholder: {
      type: String,
      default: '请选择...',
    },
    total: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      pageSize: 5,
      totalPage: 1,
      currentPage: 1,
      selectInput: '',
    }
  },
  watch: {
    total: {
      deep: true,
      immediate: true,
      handler(newValue) {
        this.totalPage =
          newValue % this.pageSize === 0
            ? newValue / this.pageSize
            : Math.ceil(newValue / this.pageSize)
      },
    },
  },
  created() {},
  methods: {
    input(e) {
      this.currentPage = 1
      this.$emit('input', e)
    },
    currentChange(e) {
      const preStatus = e === 0 && this.currentPage === 1
      const nextStatus = e === 1 && this.currentPage === this.totalPage
      if (preStatus || nextStatus) {
        return false
      }
      const currentPage =
        e === 0 ? (this.currentPage -= 1) : (this.currentPage += 1)
      this.$emit('currentChange', currentPage, this.selectInput)
    },
    change(e) {
      this.$emit('change', e)
    },
    visibleChange(e) {
      if (e) {
        this.$emit('visibleChange')
        return false
      }
      this.selectInput = ''
      this.currentPage = 1
    },
  },
}
</script>

<style lang="scss" scoped>
/deep/.el-select-dropdown__list {
  padding-bottom: 35px;
}
.select-input {
  padding: 0 10px;
  /deep/.el-input__inner {
    border: none;
    border-bottom: 1px solid #dcdfe6;
  }
}
.select-pager {
  width: 100%;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px 0;
  .page-pre,
  .page-next {
    width: 30%;
    line-height: 30px;
    text-align: center;
    cursor: pointer;
    font-size: 13px;
    border: 1px solid #dcdfe6;
    background: #fff;
    &:hover {
      border: 1px solid #0094e5;
      color: #0094e5;
    }
  }
  .page-pre {
    border-top-left-radius: 5px;
    border-bottom-left-radius: 5px;
  }
  .page-next {
    border-top-right-radius: 5px;
    border-bottom-right-radius: 5px;
  }
  .disabled {
    cursor: not-allowed;
    &:hover {
      border: 1px solid #dcdfe6;
      color: #999;
    }
  }
  .page-content {
    flex: 1;
    line-height: 32px;
    text-align: center;
    border-top: 1px solid #dcdfe6;
    border-bottom: 1px solid #dcdfe6;
  }
}
</style>
