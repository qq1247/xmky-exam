<template>
  <el-form
    :model="paperForm"
    :rules="paperForm.rules"
    ref="paperForm"
    label-width="100px"
  >
    <el-form-item label="选择模式">
      <el-radio
        v-for="item in paperForm.paperTypeList"
        :key="item.value"
        v-model="paperForm.paperType"
        :label="item.value"
        @change="selectPaperType"
        >{{ item.name }}</el-radio
      >
    </el-form-item>
    <el-form-item
      label="选择试卷"
      prop="selectPaperId"
      v-if="!paperForm.paperType"
      key="paperType"
    >
      <CustomSelect
        ref="paperSelect"
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
        ></el-option>
      </CustomSelect>
    </el-form-item>
    <template v-if="paperForm.paperType">
      <el-form-item label="组卷方式">
        <div class="exam-type">
          <div
            :class="
              paperForm.genType == index
                ? 'type-item type-item-active'
                : 'type-item '
            "
            v-for="(item, index) in paperForm.genTypes"
            :key="item.content"
            @click="setPaperType(index)"
          >
            <i :class="['common', `${item.icon}`]"></i>
            <i
              class="common common-selected"
              v-if="paperForm.genType == index"
            ></i>
            {{ item.content }}
          </div>
        </div>
      </el-form-item>
      <el-form-item label="阅卷方式" prop="markType">
        <el-radio
          v-for="item in paperForm.markTypeList"
          :key="item.value"
          v-model="paperForm.markType"
          :label="item.value"
          >{{ item.name }}</el-radio
        >
      </el-form-item>
      <el-form-item label="试卷名称" prop="name">
        <el-input
          placeholder="请输入试卷名称"
          v-model="paperForm.name"
        ></el-input>
      </el-form-item>
      <el-form-item label="及格（%）" prop="passScore">
        <el-input
          type="number"
          min="1"
          max="100"
          placeholder="请输入及格分数占总分百分比"
          v-model="paperForm.passScore"
        >
          <span slot="append">%</span>
        </el-input>
      </el-form-item>
      <el-form-item label="展示方式">
        <el-radio
          v-for="item in paperForm.showTypes"
          :key="item.value"
          v-model="paperForm.showType"
          :label="item.value"
          >{{ item.name }}</el-radio
        >
      </el-form-item>
    </template>
    <div class="footer">
      <el-button @click="next" type="primary">下一步</el-button>
    </div>
  </el-form>
</template>

<script>
import {
  paperListPage,
  paperTypeAdd,
  paperTypeListPage,
  paperGet,
  paperAdd,
  paperEdit,
} from 'api/paper'
import { getQuick, setQuick } from '@/utils/storage'
import CustomSelect from 'components/CustomSelect.vue'
import dayjs from 'dayjs'
export default {
  components: { CustomSelect },
  data() {
    const validatePercentage = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入及格分占总百分比'))
      } else if (!/^\+?[1-9][0-9]*$/.test(value)) {
        return callback(new Error(`必须是整数数字`))
      } else if (value > 100 || value < 1) {
        return callback(new Error(`请输入1-100范围的值`))
      } else {
        return callback()
      }
    }
    return {
      paperForm: {
        paperType: 0,
        paperTypeId: null,
        paperId: null,
        paperList: [],
        paperTypeList: [
          {
            name: '选择试卷',
            value: 0,
          },
          {
            name: '创建试卷',
            value: 1,
          },
        ],
        selectPaperId: null,
        pageSize: 5,
        total: 1,
        name: '',
        passScore: '',
        readRemark: '',
        readNum: '',
        showType: '1',
        showTypes: [
          {
            name: '整张',
            value: '1',
          },
          {
            name: '单题',
            value: '3',
          },
        ],
        paperAntiCheat: ['试题乱序', '选项乱序'],
        options: [],
        paperMiniNum: '',
        paperRemarkShow: false,
        paperRemark: [
          {
            score: '',
            remark: '',
          },
        ],
        paperTabs: [
          {
            title: '基础信息',
            name: '0',
          },
          {
            title: '组卷方式',
            name: '1',
          },
        ],
        genType: 0,
        genTypes: [
          {
            icon: 'common-person',
            content: '人工组卷',
          },
          {
            icon: 'common-random',
            content: '随机组卷',
          },
        ],
        markType: 1,
        markTypeList: [
          {
            name: '智能阅卷',
            value: 1,
          },
          {
            name: '人工阅卷',
            value: 2,
          },
        ],
        paperTypeItem: {},
        rules: {
          selectPaperId: [
            { required: true, message: '请选择试卷', trigger: 'change' },
          ],
          name: [
            { required: true, message: '请填写试卷名称', trigger: 'blur' },
          ],
          passScore: [
            {
              required: true,
              trigger: 'blur',
              validator: validatePercentage,
            },
          ],
        },
      },
    }
  },
  async created() {
    const quickInfo = getQuick()
    if (!Object.keys(quickInfo).length || !quickInfo) return false

    // 创建试卷
    if (quickInfo.paperType) {
      this.paperForm.paperType = quickInfo.paperType
      this.$nextTick(() => {
        this.paperForm.genType = quickInfo.genType - 1
        this.paperForm.name = quickInfo.name
        this.paperForm.passScore = quickInfo.passScore
        this.paperForm.showType = String(quickInfo.showType)
        this.paperForm.markType = quickInfo.markType
      })
    }

    // 选择试卷
    if (!quickInfo.paperType) {
      this.paperForm.paperType = quickInfo.paperType
      await this.selectPaperType(quickInfo.paperType)
      this.$nextTick(() => {
        this.paperForm.selectPaperId = quickInfo.id
        this.$refs['paperSelect'] &&
          this.$refs['paperSelect'].$refs['elSelect'].cachedOptions.push({
            currentLabel: quickInfo.name,
            currentValue: quickInfo.id,
            label: quickInfo.name,
            value: quickInfo.id,
          })
      })
    }
  },
  methods: {
    // 选择模式
    async selectPaperType(paperType) {
      if (paperType) {
        this.paperForm.name = dayjs().format('YYYY-MM-DD')
        this.paperForm.passScore = 60
        const paperTypeList = await paperTypeListPage({
          name: '我的试卷',
          curPage: 1,
          pageSize: this.paperForm.pageSize,
        })
        if (!paperTypeList?.data?.list.length) {
          const res = await paperTypeAdd({
            name: '我的试卷',
          })
          res?.code === 200 && (this.paperForm.paperTypeId = res.data)
        } else {
          this.paperForm.paperTypeId = paperTypeList?.data?.list[0].id
        }
      }
    },
    // 组卷方式
    setPaperType(index) {
      if (index == 1) {
        this.paperForm.genType = 0
        this.$message('暂未开放！', 'warning')
        return
      }
      this.paperForm.genType = index
    },
    // 获取试卷列表
    async getPaperList(curPage = 1, name = '') {
      const paperList = await paperListPage({
        name,
        state: 1,
        curPage,
        pageSize: this.paperForm.pageSize,
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
      this.paperForm.selectPaperId = paperId
    },
    // 下一步
    next() {
      this.$refs['paperForm'].validate(async (valid) => {
        if (!valid) {
          return
        }
        if (this.paperForm.paperType) {
          const params = {
            paperTypeId: this.paperForm.paperTypeId,
            genType: this.paperForm.genType + 1,
            name: this.paperForm.name,
            passScore: this.paperForm.passScore,
            showType: Number(this.paperForm.showType),
            markType: this.paperForm.markType,
          }

          if (getQuick().paperType && getQuick()?.state === 1) {
            this.$emit('next', '3')
            return false
          }

          const res =
            getQuick()?.paperType === 1 && getQuick()?.id
              ? await paperEdit({ ...params, id: getQuick()?.id })
              : await paperAdd(params)
          this.paperForm.paperId = res.data || getQuick()?.id
          await this.setQuickInfo()
          this.$emit('next', '2')
        } else {
          this.paperForm.paperId = this.paperForm.selectPaperId
          await this.setQuickInfo()
          this.$emit('next', '3')
        }
      })
    },
    // 设置quickInfo
    async setQuickInfo() {
      const { data } = await paperGet({ id: this.paperForm.paperId })
      setQuick({
        ...data,
        paperType: this.paperForm.paperType,
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.exam-type {
  display: flex;
}

.type-item {
  width: 150px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  border: 1px solid #d8d8d8;
  font-size: 14px;
  margin-right: 10px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  i {
    font-size: 24px;
    margin-right: 10px;
  }
  .common-selected {
    position: absolute;
    right: 5px;
    bottom: 8px;
    font-size: 12px;
    margin-right: 0;
    line-height: 0;
    color: #fff;
    &::after {
      content: '';
      display: block;
      position: absolute;
      z-index: 10;
      right: -6px;
      bottom: -9px;
      border-bottom: 25px solid #1e9fff;
      border-left: 25px solid transparent;
    }
    &::before {
      position: absolute;
      z-index: 50;
      right: -3px;
      bottom: -3px;
    }
  }
}

.type-item-active {
  border: 1px solid #1e9fff;
  color: #1e9fff;
}

.footer {
  position: fixed;
  bottom: 0;
  width: calc(100% - 201px);
  right: 0;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(13px);
  display: flex;
  padding: 10px 10px 10px 30px;
  box-shadow: 1px -3px 13px -6px rgba(#000000, 0.15);
}
</style>
