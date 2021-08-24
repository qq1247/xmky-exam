<template>
  <div class="container">
    <div class="content">
      <el-form :inline="true" :model="queryForm" ref="queryForm">
        <el-row>
          <el-col :span="17">
            <el-form-item label prop="name">
              <el-input
                @focus="queryForm.queryShow = true"
                placeholder="请输入名称"
                v-model="queryForm.name"
              ></el-input>
            </el-form-item>
            <el-button
              @click="query"
              class="query-search"
              icon="el-icon-search"
              type="primary"
              >查询</el-button
            >
          </el-col>
          <el-col :span="7"></el-col>
        </el-row>
        <div v-if="queryForm.queryShow"></div>
      </el-form>
      <div class="table">
        <el-table
          :data="listpage.list"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          default-expand-all
          row-key="id"
          style="width: 100%"
        >
          <el-table-column label="名称">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="上级机构">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.parentName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="排序">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.no }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-link
                :underline="false"
                @click="get(null, scope.row.id, scope.row.name)"
                icon="common el-icon-circle-plus-outline"
                >添加</el-link
              >
              <el-link
                :underline="false"
                @click="get(scope.row.id)"
                icon="common common-edit"
                v-if="scope.row.id != 1"
                >修改</el-link
              >
              <el-link
                :underline="false"
                @click="del(scope.row.id)"
                icon="common common-delete"
                v-if="scope.row.id != 1"
                >删除</el-link
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <el-dialog :visible.sync="editForm.show" title="机构">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="上级机构" label-width="120px">
          <el-input
            disabled
            placeholder
            v-model="editForm.parentName"
          ></el-input>
        </el-form-item>
        <el-form-item label="名称" label-width="120px" prop="name">
          <el-input placeholder="请输入名称" v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="排序" label-width="120px" prop="no">
          <el-input-number
            :max="100"
            :min="1"
            v-model.number="editForm.no"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="add" type="primary" v-if="editForm.id == null"
          >添加</el-button
        >
        <el-button @click="edit" type="primary" v-if="editForm.id != null"
          >修改</el-button
        >
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { orgListPage, orgAdd, orgEdit, orgGet, orgDel } from '@/api/base'
export default {
  data() {
    return {
      // 列表数据
      listpage: {
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 100, // 每页多少条
        list: [], // 列表数据
      },
      // 查询表单
      queryForm: {
        name: null,
        parentId: null,
      },
      // 修改表单
      editForm: {
        id: null, // 主键
        name: null, // 名称
        no: null, // 排序
        show: false, // 是否显示页面
        parentId: null, // 父ID
        parentName: null, // 父名称
        rules: {
          // 校验
          name: [{ required: true, message: '请输入名称', trigger: 'change' }],
          no: [{ required: true, message: '请输入排序', trigger: 'change' }],
        },
      },
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query() {
      const {
        data: { list, total },
      } = await orgListPage({
        parentId: this.queryForm.parentId,
        name: this.queryForm.name,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize,
      })

      const treeList = []
      const treeMap = {}
      const idFiled = 'id'
      const parentField = 'parentId'

      for (let i = 0; i < list.length; i++) {
        list[i]['id'] = list[i][idFiled]
        treeMap[list[i]['id']] = list[i]
      }

      for (let i = 0; i < list.length; i++) {
        if (
          treeMap[list[i][parentField]] &&
          list[i]['id'] != list[i][parentField]
        ) {
          if (!treeMap[list[i][parentField]]['children']) {
            treeMap[list[i][parentField]]['children'] = []
          }
          treeMap[list[i][parentField]]['children'].push(list[i])
        } else {
          treeList.push(list[i])
        }
      }

      this.listpage.list = treeList
    },
    // 重置
    async reset() {
      this.$refs['queryForm'].resetFields()
      this.listpage.curPage = 1
      this.query()
    },
    // 初始化
    async init() {
      await this.query()
    },
    // 添加机构
    add() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await orgAdd({
          name: this.editForm.name,
          parentId: this.editForm.parentId,
          no: this.editForm.no,
        })

        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 修改机构
    edit() {
      this.$refs['editForm'].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const { code, msg } = await orgEdit({
          id: this.editForm.id,
          name: this.editForm.name,
          no: this.editForm.no,
        })

        if (code != 200) {
          alert(msg)
          return
        }

        this.editForm.show = false
        this.query()
      })
    },
    // 获取机构
    async get(id, parentId, parentName) {
      this.$nextTick(() => {
        this.$refs['editForm'].resetFields()
      })

      if (!id) {
        this.editForm.show = true
        this.editForm.parentId = parentId
        this.editForm.parentName = parentName
        return
      }

      const res = await orgGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return
      }

      this.editForm.show = true
      this.editForm.id = res.data.id
      this.editForm.name = res.data.name
      this.editForm.parentId = res.data.parentId
      this.editForm.parentName = res.data.parentName
      this.editForm.no = res.data.no
    },
    // 删除
    async del(id) {
      this.$confirm('确定要删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await orgDel({ id })
        if (res.code != 200) {
          this.$message.error(res.msg)
        }

        this.query()
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  .content {
    width: 1170px;
  }
}
.query-search {
  width: 150px;
  height: 40px;
  border-radius: 5px;
}
.el-input {
  width: 300px;
  float: left;
}
.el-input-number {
  float: left;
}

.el-dialog__title {
  float: left;
}
/deep/ .el-table th {
  background-color: #d3dce6;
  color: #475669;
  text-align: center;
  height: 55px;
}
/deep/ .el-table td {
  color: #8392a6;
  font-size: 12px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}
/deep/ .common {
  padding-right: 10px;
  color: #0096e7;
  font-style: inherit;
  font-weight: bold;
}
.el-link {
  padding-right: 20px;
  color: #8392a6;
  font-size: 12px;
}
/deep/ .el-input__inner:focus {
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075),
    0 0 8px rgba(102, 175, 233, 0.6);
  border: 1px solid #f2f4f5;
}
.common-arrow-down {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

/deep/.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #0095e5;
  color: #fff;
}

/deep/.el-pagination.is-background .btn-next,
/deep/.el-pagination.is-background .btn-prev,
/deep/.el-pagination.is-background .el-pager li {
  margin: 0 3px;
  min-width: 35px;
  border: 1px solid #d4dfd9;
  background-color: #fff;
  padding: 0 10px;
}
</style>
