<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <div>
        <el-form-item>
          <el-input
            placeholder="请输入名称"
            v-model="queryForm.examName"
          ></el-input>
        </el-form-item>
      </div>
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
          <div
            class="exam-content exam-add"
            @click="
              examForm.show = true
              examForm.edit = false
            "
          >
            <i class="common common-plus"></i>
            <span>添加试题</span>
          </div>
        </div>
        <div v-for="(item, index) in examList" :key="index" class="exam-item">
          <div class="exam-content">
            <div class="title">{{ item.name }}</div>
            <!-- <div class="no-date">
              <span>{{ item.no }}</span>
              <span>{{ item.date }}</span>
            </div> -->
            <div class="no-date">
              <span>读取权限：张三</span>
            </div>
            <div class="no-date">
              <span>使用权限：张三、张三、张三</span>
            </div>
            <div class="handler">
              <span data-title="编辑" @click="edit">
                <i class="common common-edit"></i>
              </span>
              <span data-title="删除" @click="del">
                <i class="common common-delete"></i>
              </span>
              <span data-title="权限" @click="role">
                <i class="common common-role"></i>
              </span>
              <span data-title="开放" @click="role">
                <i class="common common-share"></i>
              </span>
              <span data-title="试题列表" @click="role">
                <i class="common common-list-row"></i>
              </span>
              <!-- <span>
                <i class="common common-more-row"></i>
                <div class="handler-more">
                  <div class="more-item" @click="goDetail">试题详情</div>
                </div>
              </span> -->
            </div>
          </div>
        </div>
      </div>
      <el-pagination background layout="prev, pager, next" :total="1000">
      </el-pagination>
    </div>
    <el-dialog
      :visible.sync="examForm.show"
      :show-close="false"
      center
      width="30%"
      title="试题分类"
    >
      <el-form
        :model="examForm"
        :rules="examForm.rules"
        ref="examForm"
        label-width="60px"
      >
        <el-form-item label="名称" prop="examName">
          <el-input
            placeholder="请输入试题名称"
            v-model="examForm.examName"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="examAdd" type="primary">{{
          examForm.edit ? "修改" : "添加"
        }}</el-button>
        <el-button @click="examForm.show = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      queryForm: {
        examName: ""
      },
      examForm: {
        show: false,
        edit: false,
        examName: "",
        examThumbnail: "",
        rules: {
          examName: [
            { required: true, message: "请输入试题名称", trigger: "blur" }
          ]
        }
      },
      examList: [
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        },
        {
          name: "测试考试",
          no: "2021-01-01",
          date: "2019-02-02"
        }
      ]
    }
  },
  methods: {
    // 查询
    query() {},
    handleAvatarSuccess(res, file) {
      this.examThumbnail = URL.createObjectURL(file.raw)
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg"
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 格式!")
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!")
      }
      return isJPG && isLt2M
    },
    // 添加试卷信息
    examAdd() {
      this.$refs["examForm"].validate(valid => {
        if (!valid) {
          console.log("error")
          return
        }
      })
    },
    // 试题详情
    goDetail() {
      this.$router.push("/examLibrary/edit")
    },
    edit() {
      this.examForm.edit = true
      this.examForm.show = true
    },
    del() {},
    role() {}
  }
}
</script>

<style lang="scss" scoped>
.search {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 10px 0;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin: 0 auto;
}

.exam-list {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
}

.exam-item {
  width: calc(100% / 3);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.exam-content {
  display: flex;
  flex-direction: column;
  width: 95%;
  padding: 30px 15px 20px;
  background: #fff;
  height: 200px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s ease;
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 16px -10px rgba(95, 101, 105, 0.15);
  }
  .title {
    font-size: 16px;
    margin: 0 0 10px;
    word-break: keep-all;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .no-date {
    font-size: 12px;
    color: #9199a1;
    margin-bottom: 10px;
    /* span:first-child {
      color: #f60;
      font-weight: 700;
      margin-right: 30px;
    } */
  }
  .handler {
    span {
      display: inline-block;
      margin-right: 10px;
      text-align: center;
      width: 35px;
      height: 35px;
      color: #8392a5;
      border-radius: 50%;
      border: 1px solid #eff3f7;
      margin-top: 25px;
      line-height: 35px;
      position: relative;
      transition: all 0.3s ease-in-out;
      .handler-more {
        background: #4a5766;
        width: 120px;
        color: #fff;
        border-radius: 3px;
        font-size: 12px;
        position: absolute;
        left: 60px;
        top: 50%;
        transform: translateY(-50%);
        opacity: 0;
        transition: all 0.3s ease-in-out;
        .more-item {
          padding: 12px 0;
          border-bottom: 1px solid #65707d;
          line-height: 1.45;
          &:last-child {
            border-bottom: none;
          }
          &:hover {
            background: #0095e5;
            color: #fff;
          }
        }
        &::before {
          content: "";
          display: block;
          position: absolute;
          z-index: 100;
          left: -10px;
          top: 50%;
          transform: translateY(-50%);
          border-width: 5px 10px 5px 0;
          border-style: solid;
          border-color: transparent #4a5766 transparent transparent;
        }
      }
      &:last-child:hover {
        .handler-more {
          left: 50px;
          opacity: 1;
        }
      }
      &::after {
        content: attr(data-title);
        display: block;
        position: absolute;
        z-index: 100;
        bottom: -45px;
        transform: translateX(-50%);
        left: 50%;
        width: 70px;
        height: 30px;
        line-height: 30px;
        background: #0095e5;
        color: #fff;
        border-radius: 3px;
        font-size: 13px;
        opacity: 0;
      }
      &::before {
        content: "";
        display: block;
        position: absolute;
        z-index: 100;
        bottom: -18px;
        left: 50%;
        transform: translateX(-50%);
        border-width: 0 5px 10px 5px;
        border-style: solid;
        border-color: transparent transparent #0095e5;
        opacity: 0;
      }
      &:hover {
        border: 1px solid #0095e5;
        background: #0095e5;
        color: #fff;
        &::after {
          opacity: 1;
        }
        &::before {
          opacity: 1;
        }
      }
    }
  }
}

.exam-add {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #9199a1;
  .common-plus {
    display: inline-block;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
    border-radius: 50%;
    border: 1px solid #9199a1;
    font-size: 45px;
    color: #9199a1;
    margin-bottom: 10px;
    transition: all 0.3s ease-in-out;
  }
  &:hover {
    color: #0095e5;
    .common-plus {
      border: 1px solid #0095e5;
      background: #0095e5;
      color: #fff;
    }
  }
}

.thumbnail-uploader {
  /deep/ .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    &:hover {
      border-color: #409eff;
    }
    .thumbnail-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      line-height: 178px;
      text-align: center;
    }
  }
}

.thumbnail {
  width: 178px;
  height: 178px;
  display: block;
}

/deep/ .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #1e9fff;
  color: #fff;
}

/deep/.el-pagination.is-background .btn-next,
/deep/.el-pagination.is-background .btn-prev,
/deep/.el-pagination.is-background .el-pager li {
  margin: 0 3px;
  min-width: 34px;
  border: 1px solid #d4dfd9;
  background-color: #fff;
}
</style>
