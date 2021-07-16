<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <el-form-item>
        <el-input
          placeholder="请输入名称"
          v-model="queryForm.name"
          class="query-input"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="query" icon="el-icon-search" type="primary"
          >查询</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 内容 -->
    <div class="content">
      <div class="exam-list">
        <div class="exam-item">
          <div class="exam-content exam-add" @click="paperForm.show = true">
            <i class="common common-plus"></i>
            <span>添加试卷</span>
          </div>
        </div>
        <ListCard
          v-for="(item, index) in paperList"
          :key="index"
          :data="item"
          name="paperList"
          @edit="edit"
          @del="del"
          @copy="copy"
          @publish="publish"
          @composition="composition"
          @statistics="statistics"
        ></ListCard>
      </div>
      <!-- 分页 -->
      <el-pagination
        background
        layout="prev, pager, next"
        prev-text="上一页"
        next-text="下一页"
        hide-on-single-page
        :total="total"
        :page-size="pageSize"
        :current-page="curPage"
        @current-change="pageChange"
      ></el-pagination>
    </div>

    <el-dialog
      :visible.sync="paperForm.show"
      :show-close="false"
      center
      width="50%"
      :close-on-click-modal="false"
      @close="resetData('paperForm')"
    >
      <el-form
        :model="paperForm"
        :rules="paperForm.rules"
        ref="paperForm"
        label-width="100px"
      >
        <el-tabs v-model="paperForm.tabActive">
          <el-tab-pane
            v-for="item in paperForm.paperTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
          ></el-tab-pane>
          <template v-if="paperForm.tabActive == '0'">
            <el-form-item>
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
            <el-form-item label="试卷名称" prop="name">
              <el-input
                placeholder="请输入试卷名称"
                v-model="paperForm.name"
              ></el-input>
            </el-form-item>
            <el-form-item label="及格分数" prop="passScore" required>
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
            <el-form-item label="考前阅读">
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
            </el-form-item>
          </template>
          <template v-if="paperForm.tabActive == '1'">
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
          <template v-if="paperForm.tabActive == '2'">
            <el-form-item label="防作弊">
              <el-checkbox-group v-model="paperForm.options">
                <el-checkbox
                  v-for="(item, index) in paperForm.paperAntiCheat"
                  :label="String(index)"
                  :key="index"
                  >{{ item }}</el-checkbox
                >
              </el-checkbox-group>
              <!-- <el-input
                v-model="paperForm.paperMiniNum"
                placeholder="最小化次数"
                v-if="paperForm.options.includes('最小化')"
              >
                <span slot="append">后交卷</span>
              </el-input>-->
            </el-form-item>
          </template>
          <!-- <div v-if="paperForm.tabActive == '3'">
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
          </div>-->
        </el-tabs>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <!-- <el-button
          v-if="!(paperForm.tabActive == '2')"
          @click="paperNext"
          type="primary"
          >下一步</el-button
        > -->
        <el-button @click="addOrEdit">
          {{ paperForm.edit ? '修改' : '添加' }}
        </el-button>
        <el-button @click="paperForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Editor from '@/components/Editor.vue'
import ListCard from '@/components/ListCard.vue'
export default {
  components: {
    Editor,
    ListCard,
  },
  data() {
    const validatePercentage = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入及格分占总百分比'))
      } else if (value > 100 || value < 1) {
        return callback(new Error(`请输入1-100范围的值`))
      } else {
        return callback()
      }
    }
    return {
      pageSize: 5,
      total: 1,
      curPage: 1,
      queryForm: {
        name: '',
        paperTypeId: 0,
      },
      paperForm: {
        show: false,
        edit: false,
        id: 0,
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
            name: '章节',
            value: '2',
          },
          {
            name: '单题',
            value: '3',
          },
        ],
        paperAntiCheat: [
          '试题乱序',
          '选项乱序',
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
            remark: '',
          },
        ],
        tabActive: '0',
        paperTabs: [
          {
            title: '基础信息',
            name: '0',
          },
          /* {
            title: '组卷方式',
            name: '1',
          },
          {
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
            content: '人工组卷',
          },
          {
            icon: 'common-exchange',
            content: '随机组卷',
          },
        ],
        rules: {
          name: [
            { required: true, message: '请填写试卷名称', trigger: 'blur' },
          ],
          passScore: [
            {
              validator: validatePercentage,
            },
          ],
        },
      },
      paperList: [],
    }
  },
  mounted() {
    const { id } = this.$route.query
    this.queryForm.paperTypeId = id
    this.query()
  },
  methods: {
    // 查询
    async query() {
      const paperList = await this.$https.paperListPage({
        paperTypeId: this.queryForm.paperTypeId,
        curPage: this.curPage,
        pageSize: this.pageSize,
      })
      this.paperList = paperList.data.list
      this.total = paperList.data.total
    },
    // 添加试卷信息
    addOrEdit() {
      if (!this.paperForm.name) {
        this.$tools.message('试卷名称不能为空', 'error')
        return
      }
      this.$refs['paperForm'].validate(async (valid) => {
        if (!valid) {
          return
        }

        const params = {
          paperTypeId: this.queryForm.paperTypeId,
          genType: this.paperForm.genType,
          name: this.paperForm.name,
          passScore: this.paperForm.passScore,
          readRemark: this.paperForm.readRemark,
          readNum: this.paperForm.readNum,
          showType: Number(this.paperForm.showType),
          options: this.paperForm.options.join(','),
          // paperRemark: [],
        }

        const res = this.paperForm.edit
          ? await this.$https.paperEdit({ ...params, id: this.paperForm.id })
          : await this.$https.paperAdd(params)

        if (res?.code == 200) {
          this.paperForm.show = false
          this.$tools.message(
            !this.paperForm.edit ? '添加成功！' : '修改成功！'
          )
          if (this.paperForm.edit) {
            this.pageChange()
          } else {
            this.pageChange(1)
          }
        } else {
          this.$tools.message(
            !this.paperForm.edit ? '添加失败！' : '修改失败！',
            'error'
          )
        }
      })
    },
    // 编辑分类
    edit(item) {
      this.paperForm.edit = true
      this.paperForm.id = item.id
      this.paperForm.genType = item.genType
      this.paperForm.name = item.name
      this.paperForm.passScore = item.passScore
      this.paperForm.readRemark = item.readRemark
      this.paperForm.readNum = item.readNum
      this.paperForm.showType = String(item.showType)
      this.paperForm.options = item.options == '' ? [] : item.options.split(',')
      this.paperForm.paperRemarkShow = !!item.paperRemark
      this.paperForm.paperRemark = !item.paperRemark
        ? [
            {
              score: '',
              remark: '',
            },
          ]
        : item.paperRemark
      this.paperForm.show = true
    },
    // 删除分类
    del({ id, name }) {
      this.$confirm(`确认删除【${name}】吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await this.$https.paperDel({ id }).catch((err) => {})
          if (res?.code == 200) {
            this.total -= 1
            if (this.total <= this.pageSize) {
              this.pageChange(1)
              return
            }
            this.$tools.message('删除成功！')
            this.total % this.pageSize == 0 && this.total != this.pageSize
              ? ((this.curPage -= 1), this.pageChange(this.curPage))
              : this.pageChange(this.curPage)
          } else {
            this.$tools.message(res.msg || '删除失败！', 'error')
          }
        })
        .catch(() => {})
    },
    // 复制分类
    async copy({ id }) {
      try {
        const res = await this.$https.paperCopy({
          id,
        })
        res?.code == 200
          ? (this.$tools.message('复制成功！'), this.pageChange())
          : this.$tools.message('复制失败！', 'error')
      } catch (error) {}
    },
    // 生成试卷
    composition({ id, name }) {
      this.$router.push({
        path: '/examPaper/edit',
        query: { id, name },
      })
    },
    // 统计分类
    statistics() {
      this.$tools.message('暂未开放！', 'warning')
    },
    // 考试发布
    async publish({ id, state }) {
      if (state == 1) {
        this.$tools.message('试卷已发布!', 'warning')
        return
      }
      this.$confirm(`确认发布试卷吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          const res = await this.$https.paperPublish({ id }).catch((err) => {})
          res?.code == 200
            ? (this.$tools.message('考试发布成功！'), this.pageChange())
            : this.$tools.message('请重新发布考试！', 'error')
        })
        .catch(() => {})
    },
    // 切换分页
    pageChange(val) {
      val && (this.curPage = val)
      this.query()
    },
    // tab切换
    paperNext() {
      this.$refs['paperForm'].validate(async (valid) => {
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
      if (index == 1) {
        this.paperForm.genType = 0
        this.$tools.message('暂未开放！', 'warning')
        return
      }
      this.paperForm.genType = index
    },
    // 添加评语
    remarkAdd() {
      this.paperForm.paperRemark.push({
        score: '',
        remark: '',
      })
    },
    // 删除评语
    remarkDel() {
      this.paperForm.paperRemark.pop()
    },
    // 清空还原数据
    resetData(name) {
      this.$tools.resetData(this, name)
    },
  },
}
</script>

<style lang="scss" scoped>
@import '../../assets/style/list-card.scss';

/deep/ .el-dialog__header {
  padding: 0;
}

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
