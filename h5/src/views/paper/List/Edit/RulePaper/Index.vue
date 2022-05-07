<template>
  <div class="content">
    <div class="paper-handler">
      <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
        <Nav @tab="(e) => (tabIndex = e)" />
        <QuestionDrag v-if="tabIndex === '1'" ref="questionDrag" />
        <QuestionAdd v-if="tabIndex === '2'" />
        <QuestionTemplate v-if="tabIndex === '3'" />
        <QuestionRouter
          v-if="tabIndex === '4'"
          ref="questionRouter"
        />
      </el-scrollbar>
    </div>

    <el-scrollbar wrap-style="overflow-x:hidden;" class="paper-content">
      <Header ref="paperHeader" :paper-name="paperName" />
      <Composition
        v-if="!preview"
        ref="paperComposition"
        :paper-state="paperState"
      />
      <Preview v-if="preview" ref="paperPreview" />
    </el-scrollbar>
  </div>
</template>
<script>
import Nav from './Handler/Nav.vue'
import Header from './Content/Header.vue'
import Preview from './Content/Preview.vue'
import Composition from './Content/Composition.vue'
import QuestionAdd from './Handler/QuestionAdd.vue'
import QuestionDrag from './Handler/QuestionDrag.vue'
import QuestionTemplate from './Handler/QuestionTemplate.vue'
import QuestionRouter from './Handler/QuestionRouter.vue'
import { paperGet } from 'api/paper'
import { getQuick } from '@/utils/storage'
export default {
  components: {
    Nav,
    Header,
    Preview,
    Composition,
    QuestionAdd,
    QuestionDrag,
    QuestionRouter,
    QuestionTemplate
  },
  data() {
    return {
      tabIndex: '1',
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
.container {
  margin-top: -20px;
}
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 120px);
  margin: 0 auto;
}
.paper-handler {
  width: 500px;
  background: #fff;
  position: relative;
  border-radius: 8px;
}

.paper-content {
  background: #fff;
  width: calc(100% - 490px);
  margin-left: 10px;
  border-radius: 8px;
}
</style>
