<template>
  <div class="content">
    <div class="paper-handler">
      <el-scrollbar wrap-style="overflow-x:hidden;">
        <div class="paper-title">
          <span>试题</span>
        </div>
        <QuestionDrag ref="questionDrag" />
      </el-scrollbar>
    </div>

    <el-scrollbar wrap-style="overflow-x:hidden;" class="paper-content">
      <!-- <Header ref="paperHeader" :paper-name="paperName" /> -->
      <Composition
        v-if="!preview"
        ref="paperComposition"
        :paper-state="paperState"
      />
      <Preview v-if="preview" ref="paperPreview" />
      <slot></slot>
    </el-scrollbar>
  </div>
</template>
<script>
// import Header from '@/views/paper/List/Edit/RulePaper/Content/Header.vue'
import Preview from '@/views/paper/List/Edit/RulePaper/Content/Preview.vue'
import Composition from '@/views/paper/List/Edit/RulePaper/Content/Composition.vue'
import QuestionDrag from '@/views/paper/List/Edit/RulePaper/Handler/QuestionDrag.vue'
import { paperGet } from 'api/paper'
import { getQuick } from '@/utils/storage'
export default {
  components: {
    // Header,
    Preview,
    Composition,
    QuestionDrag
  },
  data() {
    return {
      preview: false,
      paperId: 0,
      paperState: 2,
      paperTypeId: 0,
      markType: 1,
      paperName: ''
    }
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().id
    if (Number(this.paperId)) {
      const res = await paperGet({ id: this.paperId })
      this.paperName = res.data.name
      this.paperState = res.data.state
      this.markType = res.data.markType
    }
  }
}
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 240px);
  margin: 0 auto;
}
.paper-handler {
  width: 400px;
  background: #fff;
  position: relative;
  border-radius: 8px;
  .paper-title {
    line-height: 40px;
    background: #fff;
    width: 100%;
    height: 40px;
    font-size: 16px;
    color: #333;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 100;
    font-weight: 600;
    padding: 0 20px;
    border-radius: 8px 8px 0 0;
    border-bottom: 1px solid #eee;
    &::before{
      content: '';
      display: inline-block;
      position: relative;
      top: 5px;
      left: -10px;
      width: 2px;
      height: 20px;
      background: #0094e5;
    }
  }
}

.paper-content {
  background: #fff;
  width: calc(100% - 400px);
  margin-left: 10px;
  border-radius: 8px;
}
</style>