<template>
  <div class="content">
    <div class="paper-handler">
      <el-scrollbar wrap-style="overflow-x:hidden;" style="height: 100%">
        <Nav @tab="(e) => (tabIndex = e)"></Nav>
        <QuestionDrag v-if="tabIndex === '1'" ref="questionDrag"></QuestionDrag>
        <QuestionAdd v-if="tabIndex === '2'"></QuestionAdd>
        <QuestionRouter
          v-if="tabIndex === '3'"
          ref="questionRouter"
        ></QuestionRouter>
      </el-scrollbar>
    </div>

    <div class="paper-content">
      <Header ref="paperHeader"></Header>
      <Composition
        v-if="!preview"
        :paperState="paperState"
        ref="paperComposition"
      ></Composition>
      <Preview v-if="preview" ref="paperPreview"></Preview>
    </div>
  </div>
</template>
<script>
import QuestionRouter from '@/views/paper/List/Edit/RulePaper/Handler/QuestionRouter.vue'
import QuestionDrag from '@/views/paper/List/Edit/RulePaper/Handler/QuestionDrag.vue'
import QuestionAdd from '@/views/paper/List/Edit/RulePaper/Handler/QuestionAdd.vue'
import Nav from '@/views/paper/List/Edit/RulePaper/Handler/Nav.vue'
import Header from '@/views/paper/List/Edit/RulePaper/Content/Header.vue'
import Preview from '@/views/paper/List/Edit/RulePaper/Content/Preview.vue'
import Composition from '@/views/paper/List/Edit/RulePaper/Content/Composition.vue'
import { paperTotalScoreUpdate, paperGet } from 'api/paper'
import { getQuick } from '@/utils/storage'
export default {
  components: {
    Nav,
    QuestionRouter,
    QuestionAdd,
    QuestionDrag,
    Composition,
    Header,
    Preview,
  },
  data() {
    return {
      tabIndex: '1',
      preview: false,
      paperId: 0,
      paperState: 2,
      paperTypeId: 0,
      markType: 1,
      paperName: '',
    }
  },
  async created() {
    this.paperId = this.$route.params.id || getQuick().paperId
    if (Number(this.paperId)) {
      const res = await paperGet({ id: this.paperId })
      this.paperName = res.data.name
      this.paperState = res.data.state
      this.markType = res.data.markType
    }
  },
  async activated() {
    this.paperId = this.$route.params.id || getQuick().paperId
    if (Number(this.paperId)) {
      const res = await paperGet({ id: this.paperId })
      this.paperName = res.data.name
      this.paperState = res.data.state
      this.markType = res.data.markType
    }
  },
  beforeDestroy() {
    if (this.paperState === '1' || !Number(this.id)) return false
    paperTotalScoreUpdate({
      id: this.paperId,
    })
  },
}
</script>

<style lang="scss" scoped>
.container {
  margin-top: -20px;
}
.content {
  display: flex;
  width: 100%;
  height: calc(100vh - 50px);
  margin: 0 auto;
}
.paper-handler {
  width: 500px;
  background: #fff;
  position: relative;
}

.paper-content {
  background: #fff;
  width: calc(100% - 500px);
  overflow: scroll;
  margin: 0 10px;
}

/deep/ .top {
  position: absolute;
  width: calc(100% - 720px);
}
</style>
