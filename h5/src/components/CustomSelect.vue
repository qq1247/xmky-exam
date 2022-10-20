<template>
  <el-select
    ref="select"
    v-model="myValue"
    :multiple="multiple"
    @visible-change="
      (visible) => {
        if (visible) {
          this.query()
        }
      }
    "
    @change="value => this.$emit('change', value)"
    @clear="clear"
    clearable
    style="width: 100%"
    size="mini"
  >
    <!-- <div class="toolbar">
      <div class="toolbar-tag">
        <i class="xm-iconfont xm-icon-quanxuan"></i><span>全选</span>
      </div>
      <div class="toolbar-tag">
        <i class="xm-iconfont xm-icon-qingkong"></i><span>清空</span>
      </div>
    </div> -->
    <el-input
      v-model="searchText"
      class="search-box"
      placeholder="请输入检索内容"
      prefix-icon="el-icon-search"
      @input="query"
    />
    <el-option
      v-for="item in list"
      :key="item[optionValue]"
      :label="item[optionLabel]"
      :value="item[optionValue]"
    >
      <slot name="customText" :item="item"></slot>
    </el-option>
    <el-pagination
      :page-size="pageSize"
      :current-page="curPage"
      :total="total"
      @current-change="
        (val) => {
          this.curPage = val
          this.query()
        }
      "
      layout="prev, pager, next"
      background
    >
    </el-pagination>

    <!-- <div slot="empty">
      <div class="select-func">
        <slot name="func" />
      </div>
      <el-input
        v-model="selectInput"
        class="select-input"
        placeholder="请输入候选词"
        prefix-icon="el-icon-search"
        @input="input"
      />
      <div class="dropdown-empty">无数据</div>
    </div> -->
  </el-select>
</template>

<script>
import request from '../api/request'
export default {
  props: {
    value: {
      type: [Boolean, String, Number, Array],
      default: '',
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    optionLabel: {
      type: [String, Number],
      default: '',
    },
    optionValue: {
      type: [String, Number],
      default: '',
    },
    url: {
      type: [String],
      default: '',
    },
    params: {
      type: [Object],
      default: function () {
        return {}
      },
    },
    searchParmName: {
      type: [String],
      default: '',
    },
  },
  data() {
    return {
      curPage: 1,
      pageSize: 3,
      searchText: '',
      total: 0,
      list: [],
      myValue: '',
    }
  },
  mounted: function () {
    let _params = {
      curPage: 1,
      pageSize: 100,
      ...this.params,
    }
    if (this.value instanceof Array) {
      _params.ids = this.value.join(',')
    } else {
      _params.ids = this.value
    }

    let _this = this
    let _value = _this.value
    request(this.url, _params).then((res) => {
      res.data.list.forEach((item) => {// 第一次显示，把id显示成名称
        _this.$refs.select.cachedOptions.push({
          currentLabel: item[_this.optionLabel],
          currentValue: item[_this.optionValue],
          label: item[_this.optionLabel],
          value: item[_this.optionValue],
        })
      })
      _this.myValue = _value
    })
  },
  watch: {
    value(val) {
      this.myValue = val
    },
  },
  methods: {
    async clear() {
      console.log(this.myValue)
    },
    query() {
      let _params = {
        curPage: this.curPage,
        pageSize: this.pageSize,
        searchParmName: this.searchParmName,
        ...this.params,
      }
      _params[this.searchParmName] = this.searchText

      request(this.url, _params).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.search-box {
  /deep/ .el-input__inner {
    border-width: 0px 0px 1px 0px;
    border-radius: 0px;
  }
  /deep/ .el-input__inner:focus {
    border-color: #c0c4cc;
  }
}
</style>
