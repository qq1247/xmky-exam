<template>
  <div class="content-left">
    <div class="user-info">
      <el-avatar
        :size="64"
        v-if="$store.getters.userAvatar"
        :src="`/api/file/download?id=${Number($store.getters.userAvatar)}`"
        ><i class="common common-wo"></i
      ></el-avatar>
      <div class="user-name">
        {{ $store.getters.name || '***' }}&nbsp;/&nbsp;{{
          $store.getters.orgName || '***'
        }}
      </div>
    </div>
    <div class="exam-head">答题卡</div>
    <el-scrollbar wrap-style="overflow-x:hidden;">
      <div class="router-content" style="margin-top: 10px">
        <div class="router-link">
          <a
            :class="[
              'router-index',
              routerIndex === item ? 'router-active' : '',
            ]"
            v-for="(item, index) in questionIds"
            :key="item.id"
            @click="$emit('toHref', item)"
            >{{ index + 1 }}</a
          >
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
export default {
  props: {
    questionIds: {
      type: Array,
      default: () => [],
    },
    routerIndex: {
      type: [String, Number],
      default: '',
    },
  },
  data() {
    return {}
  },
}
</script>

<style lang="scss" scoped>
@import 'assets/style/exam.scss';
.question-router {
  border-top: 10px solid #f7f8f9;
  font-size: 14px;
  position: relative;
}

.el-scrollbar {
  border-radius: 0 0 8px 8px;
}
</style>
