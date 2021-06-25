<template>
  <div class="container">
    <div class="content">
      <div class="left">
        <el-tree
          :data="tree.treeList"
          @node-click="selTreeNode"
          accordion
          default-expand-all
          highlight-current
          node-key="id"
          ref="tree"
        ></el-tree>
      </div>
      <div class="right">
        <div class="search">
          <el-form :inline="true" :model="queryForm" class="form-inline" ref="queryForm">
            <el-form-item label prop="name">
              <el-input placeholder="请输入名称" v-model="queryForm.name"></el-input>
            </el-form-item>
            <el-form-item style="width: 200px">
              <el-button @click="query" icon="el-icon-search" type="primary">查询</el-button>
              <el-button @click="reset">重置</el-button>
            </el-form-item>
            <el-form-item>
              <el-button @click="toAdd" icon="el-icon-search" type="primary">添加</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="table">
          <el-table :data="listpage.list" style="width: 100%">
            <el-table-column label="名称">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column label="登录名称">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.loginName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="手机号">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.phone }}</span>
              </template>
            </el-table-column>
            <el-table-column label="角色">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.roles }}</span>
              </template>
            </el-table-column>
            <el-table-column label="组织机构名称">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.orgName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150px">
              <template slot-scope="scope">
                <el-button @click="toEdit(scope.row.id)" size="mini">修改</el-button>
                <el-button @click="del(scope.row.id)" size="mini" type="danger">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <el-dialog :visible.sync="editForm.show" title="用户">
      <el-form :model="editForm" :rules="editForm.rules" ref="editForm">
        <el-form-item label="组织机构名称" label-width="120px">
          <el-input disabled placeholder v-model="editForm.orgName"></el-input>
        </el-form-item>
        <el-form-item label="登录名称" label-width="120px" prop="loginName">
          <el-input placeholder="请输入登录名称" v-model="editForm.loginName"></el-input>
        </el-form-item>
        <el-form-item label="名称" label-width="120px" prop="name">
          <el-input placeholder="请输入名称" v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号" label-width="120px" prop="phone">
          <el-input placeholder="请输入手机号" v-model="editForm.phone"></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="doAdd" type="primary" v-if="editForm.id == null">添加</el-button>
        <el-button @click="doEdit" type="primary" v-if="editForm.id != null">修改</el-button>
        <el-button @click="editForm.show = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      listpage: {
        // 列表数据
        total: 0, // 总条数
        curPage: 1, // 当前第几页
        pageSize: 10, // 每页多少条
        list: [] // 列表数据
      },
      queryForm: {
        // 查询表单
        name: null,
        orgId: null
      },
      editForm: {
        // 修改表单
        id: null, // 主键
        name: null, // 名称
        loginName: null, // 登录名称
        phone: null, // 手机号
        orgId: null, // 组织机构ID
        orgName: null, // 组织机构名称
        roles: null, // 角色
        show: false, // 是否显示页面
        rules: {
          // 校验
          loginName: [
            { required: true, message: '请输入登录名称', trigger: 'change' }
          ],
          name: [{ required: true, message: "请输入名称", trigger: "change" }]
        }
      },
      tree: {
        curNode: null, // 当前选中节点
        treeList: [] // 树结构的列表
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    // 查询
    async query() {
      let {
        data: { list, total }
      } = await this.$https.userListpage({
        orgId: this.queryForm.orgId,
        name: this.queryForm.name,
        curPage: this.listpage.curPage,
        pageSize: this.listpage.pageSize
      })
      this.listpage.total = total
      this.listpage.list = list
    },
    // 重置
    async reset() {
      this.$refs["queryForm"].resetFields()
      this.listpage.curPage = 1
      this.query()
    },
    // 初始化树
    async initTree() {
      const { code, msg, data } = await this.$https.orgTreeList()
      if (code != 200) {
        alert(msg)
        return;
      }

      const list = data
      const treeList = []
      const treeMap = {}
      const idFiled = "ID";
      const textFiled = "NAME";
      const checkedFiled = "CHECKED";
      const parentField = "PARENT_ID";
      const iconClsFiled = "ICON";
      const openClsFiled = "OPEN";

      for (let i = 0; i < list.length; i++) {
        list[i]["id"] = list[i][idFiled]
        list[i]["label"] = list[i][textFiled]
        if (list[i][checkedFiled]) {
          list[i]["checked"] = true
        }
        if (list[i][openClsFiled]) {
          list[i]["open"] = true
        }
        if (list[i][iconClsFiled]) {
          list[i]["iconCls"] = list[i][iconClsFiled]
        }
        treeMap[list[i]["id"]] = list[i]
      }

      for (let i = 0; i < list.length; i++) {
        if (
          treeMap[list[i][parentField]] &&
          list[i]["id"] != list[i][parentField]
        ) {
          if (!treeMap[list[i][parentField]]["children"]) {
            treeMap[list[i][parentField]]["children"] = []
          }
          treeMap[list[i][parentField]]["children"].push(list[i])
        } else {
          treeList.push(list[i])
        }
      }

      this.tree.treeList = treeList

      this.$nextTick(() => {
        if (!this.tree.curNode) {
          const node = this.$refs.tree.getNode(1) // 如果是第一次初始化，选择跟节点
          this.$refs.tree.setCurrentNode(node)
          this.tree.curNode = node
          this.queryForm.orgId = ''; // 查询表单orgId设置为根节点   node.id
        } else {
          this.$refs.tree.setCurrentKey(this.tree.curNode.id) // 否则选中刷新前节点
          this.queryForm.orgId = this.tree.curNode.id // 查询表单orgId设置为当前选中节点
        }
      })
    },
    // 初始化
    async init() {
      await this.initTree()
      await this.query()
    },
    // 选择树节点
    async selTreeNode(data) {
      this.tree.curNode = data
      this.queryForm.orgId = data.id
      this.query()
    },
    // 添加组织机构
    toAdd() {
      this.editForm.show = true
      this.editForm.orgId = this.tree.curNode.id
      this.editForm.orgName = this.tree.curNode.label
    },
    // 添加组织机构
    doAdd() {
      this.$refs["editForm"].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await this.$https.userAdd({
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
          phone: this.editForm.phone
        })

        if (res.code != 200) {
          alert(res.msg)
          return;
        }

        this.$alert(res.data.initPwd, "初始化密码", {
          confirmButtonText: "确定",
        });

        this.editForm.show = false
        this.initTree()
        this.query()
      });
    },
    // 修改
    async toEdit(id) {
      const res = await this.$https.userGet({ id: id })
      if (res.code != 200) {
        alert(res.msg)
        return;
      }

      this.editForm.show = true
      this.editForm.id = res.data.id
      this.editForm.name = res.data.name
      this.editForm.loginName = res.data.loginName
      this.editForm.orgId = res.data.orgId
      this.editForm.orgName = res.data.orgName
      this.editForm.phone = res.data.phone
    },
    // 修改
    doEdit() {
      this.$refs["editForm"].validate(async (valid) => {
        if (!valid) {
          return false
        }

        const res = await this.$https.userEdit({
          id: this.editForm.id,
          name: this.editForm.name,
          loginName: this.editForm.loginName,
          orgId: this.editForm.orgId,
          phone: this.editForm.phone
        })

        if (res.code != 200) {
          alert(res.msg)
          return;
        }

        this.$alert(res.data.initPwd, '初始化密码', {
          confirmButtonText: "确定",
        });

        this.editForm.show = false
        this.initTree() // 初始化树
        this.query()
      });
    },
    // 删除
    async del(id) {
      this.$confirm("确定要删除？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(async () => {
        const res = await this.$https.userDel({ id })
        if (res.code != 200) {
          this.$message({
            type: "error",
            message: res.msg
          })
        }

        this.query()
      });
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  display: flex;
  align-items: center;
  padding-top: 120px;
  .left {
    float: left;
    width: 300px;
    height: 100%;
  }
  .right {
    margin-left: 300px;
    .content {
      width: 1200px;
      .search {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
      }
    }
  }
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
</style>
