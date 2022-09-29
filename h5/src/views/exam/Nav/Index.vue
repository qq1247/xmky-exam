<template>
  <div class="container">
    <div :class="['exam-top', activeIndex == 1 && createdType == 1 ? 'exam-top-height': '']">
      <div class="exam-nav-box">
        <div
          v-for="(item, index) in navList"
          :key="index"
          :class="['exam-nav-item', activeIndex == index ? 'exam-nav-active' : 'exam-nav-not']"
          @click="activeIndex == index ? switchTab(index) : ''"
        >
          {{ item }}
        </div>
      </div>
      <PaperSetting v-if="activeIndex === 0" @switchTab="switchTab"></PaperSetting>
      <QuestionTxtImport v-if="activeIndex === 1 && createdType === 1">
        <template #leftOpt>
          <el-button plain @click="preStep()" size="mini" >返回</el-button>
        </template>
        <template #rightOpt="{ errNum, questionList }" >
          <el-button type="primary" size="mini" @click="txtImport(errNum, questionList)">导入</el-button>
        </template>
      </QuestionTxtImport>
      <ExamSetting v-if="activeIndex === 2" style="height: calc(100vh - 220px);" ref="ExamSetting"/>
      <MarkSetting v-if="activeIndex === 3" style="height: calc(100vh - 220px);" ref="MarkSetting"/>
      <ExamPublish v-if="activeIndex === 4" style="height: calc(100vh - 220px);" ref="ExamPublish"/>
    </div>

    <Paper v-if="activeIndex == 1 && createdType === 0"  @toEditor="toEditor" ref="Paper"></Paper>

    <div v-if="activeIndex !=0 && createdType === 0" class="paper-footer" :style="{marginTop: activeIndex == 1 ? '-47px' : '-67px'}">
      <el-button type="primary" @click="back" size="small">上一步</el-button>
      <el-button type="primary" @click="next" size="small">{{activeIndex == 4 ? '完成' : '下一步'}}</el-button>
    </div>
    
    <div class="exam-bottom" v-if="activeIndex == 0">
      <el-form ref="paperForm" :model="paperForm" :rules="paperForm.rules" label-width="140px" label-position="left">
        <el-form-item label="从考试中选择" prop="selectPaperId">
          <CustomSelect
            ref="paperSelect"
            style="width: 100%"
            placeholder="请选择考试"
            :multiple="false"
            :value="paperForm.selectPaperId"
            :total="paperForm.total"
            @input="searchPaper"
            @change="selectPaper"
            @currentChange="getMorePaper"
            @visibleChange="getPaperList"
          >
            <el-option
              v-for="item in paperForm.paperList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </CustomSelect>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { removeQuick, getQuick, setQuick } from '@/utils/storage'
import PaperSetting from './PaperSetting.vue'
import Paper from './Paper.vue'
import QuestionTxtImport from '@/components/Question/QuestionTxtImport.vue'
import ExamSetting from './ExamSetting.vue'
// import ExamPublish from './ExamPublish.vue'
// import MarkSetting from './MarkSetting.vue'
import CustomSelect from 'components/CustomSelect.vue'
// import { questionTypeAdd } from 'api/question'
import dayjs from 'dayjs'
import { mapMutations } from 'vuex'
// import {
//   paperListpage,
//   paperTypeAdd,
//   paperTypeListpage,
//   paperGet,
//   paperAdd,
//   paperEdit
// } from 'api/paper'

export default {
  components: {
    CustomSelect,
    PaperSetting,
    Paper,
    QuestionTxtImport,
    ExamSetting,
    // ExamPublish,
    // MarkSetting
  },
  data() {
    return {
      activeIndex: 0,
      navList: ['1.选择试卷', '2.添加试题', '3.配置规则', '4.添加用户', '5.发布考试'],
      paperForm: {
        paperType: 1,
        paperTypeId: null,
        paperList: [],
        selectPaperId: null,
        passScore: 60,
        showType: 1,
        pageSize: 5,
        total: 1,
        name: '',
        state: 2,
        genType: 0,
        markType: 1,
        rules: {
          selectPaperId: [
            { required: true, message: '请选择试卷', trigger: 'change' }
          ]
        }
      },
      createdType: 0
    }
  },
  computed: {
  },
  mounted() {
  },
  methods: {
    ...mapMutations('exam', [
      'addQuestion',
    ]),
    preStep() {
      this.activeIndex = 1
      this.createdType = 0
    },
     // 切换标签
    switchTab(index, createdType) {
      this.createdType = createdType
      this.activeIndex = index
    },

    // 获取试卷列表
    async getPaperList(curPage = 1, name = '') {
      // const paperList = await paperListpage({
      //   name,
      //   state: 1,
      //   curPage,
      //   pageSize: 5
      // })
      // this.paperForm.paperList = paperList.data.list
      // this.paperForm.total = paperList.data.total
    },
    // 获取更多试卷列表
    getMorePaper(curPage, name) {
      this.getPaperList(curPage, name)
    },
    // 根据name 查询试卷
    searchPaper(name) {
      this.getPaperList(1, name)
    },
    // 选择试卷
    selectPaper(paperId) {
      if (paperId) {
        this.activeIndex = 2
        this.paperForm.selectPaperId = paperId
        let quickInfo = this.paperForm.paperList.filter(item => {
          return item.id == paperId
        })
        setQuick(quickInfo[0])
      } else {
        this.paperForm.selectPaperId = null
      }  
    },
    //上一步 
    back() {
      if (this.activeIndex == 2 && this.paperForm.selectPaperId) {
        this.activeIndex = 0
        this.paperForm.selectPaperId = null
        removeQuick()
      } else {
        this.activeIndex--
      }
    },
    //下一步
    next() {
      if (this.activeIndex == 2) {
        this.$refs['ExamSetting'].next()
      }
      else if (this.activeIndex == 3) {
        this.$refs['MarkSetting'].next()
      }
      else if (this.activeIndex == 4) {
        this.$refs['ExamPublish'].publish()
      } else {
        this.activeIndex++
      }
    },
    // 设置quickInfo
    async setQuickInfo(paperType) {
      const { data } = await paperGet({ id: this.paperForm.paperId })
      setQuick({
        ...data,
        paperType: paperType
      })
    },
    toEditor() {
      this.activeIndex = 1
      this.createdType = 1
    },
    // 文本导入
    async txtImport(errNum, questionList) {
      if (errNum > 0) {
        this.$message.warning('试题错误格式' + errNum + '处，请处理')
        return
      }

      questionList.forEach(question => {
        this.addQuestion(question)
      });

      this.$message.info('导入成功')
      this.preStep();
    }, 
  }
}
</script>

<style lang="scss" scoped>
.exam-top-height {
  height: 99%;
}
.exam-top {
  width: 100%;
  // height: 260px;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  border-radius: 15px;
  background-color: #fff;
  .exam-nav-box {
    width: 100%;
    min-height: 40px;
    display: flex;
    align-items: center;
    background-color: #f0f9fe;
    border-top-left-radius: 15px;
    border-top-right-radius: 15px;
    .exam-nav-item {
      width: calc(100% / 5);
      min-height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      color: #687983;
      cursor: pointer;
      &:first-child {
        border-top-left-radius: 15px;
      }
      &:last-child {
        border-top-right-radius: 15px;
      }
    }
    .exam-nav-active {
      background: #0094e4;
      color: #DDF5FB;
    }
    .exam-nav-not {
      cursor: not-allowed;
    }
  }
  .footer {
    display: flex;
    margin: 15px;
    justify-content: flex-end;
  }
}

.paper-footer {
  display: flex;
  height: 32px;
  margin-right: 15px;
  margin-top: -47px;
  justify-content: flex-end;
  z-index: 9;
}
.exam-bottom {
  display: flex;
  padding: 15px;
  background-color: #fff;
  border-radius: 15px;
  /deep/ .el-form {
    width: 100%;
  }
  /deep/ .el-form-item {
    margin-bottom: 0;
  }
}
</style>
