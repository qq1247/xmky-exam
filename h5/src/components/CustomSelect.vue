<template>
  <el-select
    :style="{ width: isAuto ? 'auto' : '100%' }"
    :value="value"
    :popper-class="popperClass"
    :multiple="multiple"
    :collapse-tags="tags"
    :placeholder="placeholder"
    :clearable="clearable"
    :remote="remote"
    :popper-append-to-body="false"
    :reserve-keyword="reserveKeyword"
    :filterable="filterable"
    :loading="loading"
    :remote-method="remoteMethod"
    @focus="focus"
    @change="change"
  >
    <slot></slot>
    <el-pagination
      v-if="showPage"
      class="custom-pager"
      @current-change="currentChange"
      :current-page="currentPage"
      :page-size="pageSize"
      layout="prev, pager, next"
      :total="total"
      :page-count="5"
    ></el-pagination>
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
      default: false,
    },
    tags: {
      type: Boolean,
      default: false,
    },
    popperClass: {
      type: String,
      default: '',
    },
    placeholder: {
      type: String,
      default: '请选择...',
    },
    clearable: {
      type: Boolean,
      default: false,
    },
    reserveKeyword: {
      type: Boolean,
      default: false,
    },
    filterable: {
      type: Boolean,
      default: false,
    },
    remoteMethod: {
      type: Function,
    },
    remote: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    currentPage: {
      type: Number,
      default: 1,
    },
    pageSize: {
      type: Number,
      default: 10,
    },
    total: {
      type: Number,
      default: 0,
    },
    showPage: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  components: {},
  mounted() {},
  methods: {
    focus() {
      this.$emit('focus')
    },
    currentChange(e) {
      this.$emit('currentChange', e)
    },
    change(e) {
      this.$emit('change', e)
    },
  },
}
</script>

<style lang="scss" scoped>
/deep/.el-select-dropdown__list {
  padding-bottom: 35px;
}
.custom-pager {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  line-height: 30px;
  background: #fff;
}
</style>
