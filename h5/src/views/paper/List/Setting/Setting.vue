<template>
  <el-form
    ref="paperForm"
    :model="paperForm"
    :rules="paperForm.rules"
    label-width="100px"
  >
    <el-form-item label="组卷方式">
      <div class="exam-type">
        <div
          v-for="(item, index) in paperForm.genTypes"
          :key="item.content"
          :class="[
            'type-item',
            paperForm.genType === index ? 'type-item-active' : '',
            id ? 'type-item-disabled' : '',
          ]"
          @click="setPaperType(index)"
        >
          <i :class="['common', `${item.icon}`]" />
          <i v-if="paperForm.genType == index" class="common common-selected" />
          {{ item.content }}
        </div>
      </div>
    </el-form-item>
    <el-form-item label="阅卷方式" prop="markType">
      <el-radio
        v-for="item in paperForm.markTypeList"
        :key="item.value"
        v-model="paperForm.markType"
        :disabled="id ? true : false"
        :label="item.value"
        @change="selectMarkType"
      >{{ item.name }}</el-radio>
    </el-form-item>
    <el-form-item label="试卷名称" prop="name">
      <el-input v-model="paperForm.name" placeholder="请输入试卷名称" />
    </el-form-item>
    <el-form-item label="及格（%）" prop="passScore">
      <el-input
        v-model="paperForm.passScore"
        type="number"
        min="1"
        max="100"
        placeholder="请输入及格分数占总分百分比"
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
      >{{ item.name }}</el-radio>
    </el-form-item>
    <!-- <el-form-item label="考前阅读">
              <Editor
                :value="paperForm.readRemark"
                @editorListener="readRemark"
                id="readRemark"
              ></Editor>
            </el-form-item>
            <el-form-item label="阅读时长">
              <el-input
                type="number"
                placeholder="请输入阅读时长"
                v-model="paperForm.readNum"
              ></el-input>
            </el-form-item> -->
    <!-- <template v-if="paperForm.tabActive === '2'">
            <el-form-item label="防作弊">
              <el-checkbox-group v-model="paperForm.options">
                <el-checkbox
                  v-for="(item, index) in paperForm.paperAntiCheat"
                  :label="String(index)"
                  :key="index"
                  >{{ item }}</el-checkbox
                >
              </el-checkbox-group>
              <el-input
                v-model="paperForm.paperMiniNum"
                placeholder="最小化次数"
                v-if="paperForm.options.includes('最小化')"
              >
                <span slot="append">后交卷</span>
              </el-input>
            </el-form-item>
          </template>
          <div v-if="paperForm.tabActive === '3'">
            <el-form-item label="成绩评语">
              <el-checkbox v-model="paperForm.paperRemarkShow">开启</el-checkbox>
              <template v-if="paperForm.paperRemarkShow">
                <div v-for="item in paperForm.paperRemark" :key="item.id" class="exam-remark">
                  大于等于
                  <el-input
                    v-model="item.score"
                    type="number"
                    min="1"
                    max="100"
                    placeholder="百分比"
                    class="remark-percentage"
                  ></el-input>%，
                  <el-input v-model="item.remark" class="remark-content" placeholder="请输入评语"></el-input>
                </div>
                <div class="remark-buttons">
                  <el-button @click="remarkAdd" type="primary" size="mini" icon="el-icon-plus">添加评语</el-button>
                  <el-button
                    v-if="paperForm.paperRemark.length > 1"
                    @click="remarkDel"
                    size="mini"
                    icon="el-icon-minus"
                  >添加评语</el-button>
                </div>
              </template>
            </el-form-item>
          </div> -->
    <el-form-item>
      <el-button type="primary" @click="addOrEdit">
        {{ id ? '修改' : '添加' }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { paperEdit, paperAdd, paperGet } from 'api/paper'
// import Editor from 'components/Editor/Index.vue'
import dayjs from 'dayjs'

export default {
  components: {
    // Editor
  },
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
      id: null,
      paperTypeId: null,
      paperForm: {
        name: '',
        passScore: '',
        readRemark: '',
        readNum: '',
        state: 2,
        showType: '1',
        showTypes: [
          {
            name: '整张',
            value: '1'
          },
          {
            name: '单题',
            value: '3'
          }
        ],
        paperAntiCheat: [
          '试题乱序',
          '选项乱序'
          /* "禁用右键",
          "禁用复制",
          "最小化" */
        ],
        options: [],
        paperMiniNum: '',
        paperRemarkShow: false,
        paperRemark: [
          {
            score: '',
            remark: ''
          }
        ],
        tabActive: '0',
        paperTabs: [
          {
            title: '基础信息',
            name: '0'
          },
          {
            title: '组卷方式',
            name: '1'
          }
          /* {
            title: '防作弊',
            name: '2',
          },
          {
            title: "成绩评语",
            name: "3",
          } */
        ],
        genType: 0,
        genTypes: [
          {
            icon: 'common-person',
            content: '人工组卷'
          },
          {
            icon: 'common-random',
            content: '随机组卷'
          }
        ],
        markType: 1,
        markTypeList: [
          {
            name: '智能阅卷',
            value: 1
          },
          {
            name: '人工阅卷',
            value: 2
          }
        ],
        rules: {
          name: [
            { required: true, message: '请填写试卷名称', trigger: 'blur' }
          ],
          passScore: [
            {
              required: true,
              trigger: 'blur',
              validator: validatePercentage
            }
          ]
        }
      }
    }
  },
  async mounted() {
    this.id = this.$route.params.id
    this.paperTypeId = this.$route.params.paperTypeId
    if (Number(this.id)) {
      const res = await paperGet({ id: this.id })
      this.$nextTick(() => {
        this.paperForm.genType = res.data.genType - 1
        this.paperForm.name = res.data.name
        this.paperForm.passScore = res.data.passScore
        this.paperForm.showType = String(res.data.showType)
        this.paperForm.markType = res.data.markType
        this.paperForm.state = res.data.state
      })
    } else {
      this.paperForm.name = dayjs().format('YYYY-MM-DD')
      this.paperForm.passScore = 60
    }
  },
  methods: {
    // 添加试卷信息
    addOrEdit() {
      if (!this.paperForm.name) {
        this.$message.error('试卷名称不能为空')
        return
      }
      this.$refs['paperForm'].validate(async(valid) => {
        if (!valid) {
          return
        }

        const params = {
          paperTypeId: this.paperTypeId,
          genType: this.paperForm.genType + 1,
          name: this.paperForm.name,
          passScore: this.paperForm.passScore,
          showType: Number(this.paperForm.showType),
          markType: this.paperForm.markType
        }

        const res = this.id
          ? await paperEdit({ ...params, id: this.id })
          : await paperAdd(params)

        if (res?.code === 200) {
          this.$message.success(!this.id ? '添加成功！' : '修改成功！')
          this.$router.back()
        } else {
          this.$message.error(!this.id ? '添加失败！' : '修改失败！')
        }
      })
    },
    // tab切换
    paperNext() {
      this.$refs['paperForm'].validate(async(valid) => {
        if (!valid) {
          return
        }
        this.paperForm.tabActive = Number(this.paperForm.tabActive) + 1 + ''
      })
    },
    // 考试阅读富文本
    readRemark(id, value) {
      this.paperForm[id] = value
    },
    // 组卷方式
    setPaperType(index) {
      if (this.id) {
        return
      }
      this.paperForm.genType = index
      this.paperForm.markType = 1
    },
    // 选择阅卷类型
    selectMarkType(e) {
      if (e === 2 && this.paperForm.genType === 1) {
        this.$message.warning('暂不支持')
        this.paperForm.markType = 1
        return
      }
    },
    // 添加评语
    remarkAdd() {
      this.paperForm.paperRemark.push({
        score: '',
        remark: ''
      })
    },
    // 删除评语
    remarkDel() {
      this.paperForm.paperRemark.pop()
    }
  }
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

.type-item-disabled {
  border: 1px solid #c0c4cc;
  color: #c0c4cc;
  .common-selected {
    &::after {
      border-bottom: 25px solid #c0c4cc;
    }
  }
}

.exam-remark {
  display: flex;
  margin-top: 15px;
}

.remark-percentage {
  width: 120px;
  margin: 0 5px;
}

.remark-content {
  width: 230px;
}

.remark-buttons {
  margin: 15px 0;
}
</style>
