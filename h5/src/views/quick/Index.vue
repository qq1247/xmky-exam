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
      <PaperSetting v-if="activeIndex == 0" @switchTab="switchTab"></PaperSetting>
      <QuestionBatchInput v-if="activeIndex == 1 && createdType == 1" :isBack="false" ref="QuestionBatchInput"/>
      <ExamSetting v-if="activeIndex == 2" style="height: calc(100vh - 220px);" ref="ExamSetting"/>
      <MarkSetting v-if="activeIndex == 3" style="height: calc(100vh - 220px);" ref="MarkSetting"/>
      <ExamPublish v-if="activeIndex == 4" style="height: calc(100vh - 220px);" ref="ExamPublish"/>
    </div>

    <PaperComposition v-if="activeIndex == 1 && createdType != 1" ref="PaperComposition">
    </PaperComposition>
    <div v-if="activeIndex !=0 && createdType != 1" class="paper-footer" :style="{marginTop: activeIndex == 1 ? '-47px' : '-67px'}">
      <el-button type="primary" @click="back" size="small">上一步</el-button>
      <el-button type="primary" @click="next" size="small">{{activeIndex == 4 ? '完成' : '下一步'}}</el-button>
    </div>
    <div class="exam-bottom" v-if="activeIndex == 0">
      <el-form ref="paperForm" :model="paperForm" :rules="paperForm.rules" label-width="140px" label-position="left">
        <el-form-item label="选择一张已有试卷" prop="selectPaperId">
          <CustomSelect
            ref="paperSelect"
            style="width: 100%"
            placeholder="请选择试卷"
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
import PaperComposition from './PaperComposition.vue'
import QuestionBatchInput from '@/components/EditQuestion/QuestionBatchInput.vue'
import ExamSetting from './ExamSetting.vue'
import ExamPublish from './ExamPublish.vue'
import MarkSetting from './MarkSetting.vue'
import CustomSelect from 'components/CustomSelect.vue'
import { questionTypeAdd } from 'api/question'
import dayjs from 'dayjs'
import {
  paperListPage,
  paperTypeAdd,
  paperTypeListPage,
  paperGet,
  paperAdd,
  paperEdit
} from 'api/paper'

export default {
  components: {
    CustomSelect,
    PaperSetting,
    PaperComposition,
    QuestionBatchInput,
    ExamSetting,
    ExamPublish,
    MarkSetting
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
      createdType: ''
    }
  },
  computed: {
    ...mapGetters(['name'])
  },
  created() {
    removeQuick()
  },
  destroyed() {
    removeQuick()
  },
  mounted() {
    this.paperForm.name = dayjs().format('YYYY-MM-DD')
    this.getPaperList()
  },
  methods: {
     // 切换标签
    switchTab(index, createdType) {
      this.createdType = createdType
      if (createdType == 0) {
        // 创建空白试卷
        this.createEmptyExam(index)
      } else if (createdType == 2) {
        // 随机试卷
        this.$message.warning('暂未开发！')
      } else {
        // 导入试题
        this.activeIndex = index
      }
      
    },
    // 创建空白试卷
    async createEmptyExam(index) {
      const params = {
        paperTypeId: this.paperForm.paperTypeId || 13,
        genType: this.paperForm.genType + 1,
        name: this.paperForm.name,
        passScore: this.paperForm.passScore,
        showType: this.paperForm.showType,
        markType: this.paperForm.markType
      }
      const res =
        getQuick()?.paperType === 1 && getQuick()?.id
          ? await paperEdit({ ...params, id: getQuick()?.id })
          : await paperAdd(params)
      if (res?.code == 200) {
        this.activeIndex = index
        this.paperForm.paperId = res.data || getQuick()?.id
        await this.setQuickInfo(this.paperForm.paperType)
      }
    },

    // 获取试卷列表
    async getPaperList(curPage = 1, name = '') {
      const paperList = await paperListPage({
        name,
        state: 1,
        curPage,
        pageSize: 5
      })
      this.paperForm.paperList = paperList.data.list
      this.paperForm.total = paperList.data.total
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
    }
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
