<template>
  <div class="container">
    <!-- 搜索 -->
    <el-form :inline="true" :model="queryForm" class="form-inline search">
      <el-form-item>
        <el-input
          placeholder="请输入名称"
          v-model="queryForm.examName"
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
          <div class="exam-content exam-add">
            <i class="common common-exam-add"></i>
            <span>添加试卷</span>
          </div>
        </div>
        <div v-for="(item, index) in examList" :key="index" class="exam-item">
          <div class="exam-content">
            <div class="title">{{ item.name }}</div>
            <div class="no-date">
              <span>{{ item.no }}</span>
              <span>{{ item.date }}</span>
            </div>
            <div class="handler">
              <span data-title="编辑">
                <i class="common common-edit"></i>
              </span>
              <span data-title="详情">
                <i class="common common-dingdan"></i>
              </span>
              <span data-title="删除">
                <i class="common common-delete"></i>
              </span>
              <span data-title="权限">
                <i class="common common-role"></i>
              </span>
              <span data-title="更多">
                <i class="common common-more-row"></i>
              </span>
            </div>
          </div>
        </div>
      </div>
      <el-pagination background layout="prev, pager, next" :total="1000">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      queryForm: {
        examName: ""
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
  components: {},
  mounted() {},
  methods: {
    query() {}
  }
}
</script>

<style lang="scss" scoped>
.container {
  background: #f7f7f7;
}
.search {
  height: 80px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  padding: 0 20px;
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
  margin-bottom: 30px;
}

.exam-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 95%;
  padding: 20px 50px;
  border: 1px solid transparent;
  background: #fff;
  height: 200px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  &:hover {
    border-radius: 5px;
    border: 1px solid #1e9fff;
    box-shadow: 0 6px 10px 0 rgba(95, 101, 105, 0.15);
  }
  .title {
    font-size: 14px;
    color: #000;
    font-weight: bold;
    margin-top: 20px;
  }
  .no-date {
    font-size: 12px;
    color: #999;
    margin-top: 10px;
    span:first-child {
      margin-right: 30px;
    }
  }
  .handler {
    span {
      display: inline-block;
      width: 40px;
      height: 40px;
      line-height: 40px;
      border-radius: 50%;
      border: 1px solid #d8d8d8;
      text-align: center;
      margin: 20px 10px 0 0;
      position: relative;
      &:hover {
        border: 1px solid #1e9fff;
        background: #1e9fff;
        color: #fff;
        &::after {
          content: attr(data-title);
          display: block;
          position: absolute;
          z-index: 100;
          top: -38px;
          left: 0;
          width: 70px;
          height: 30px;
          line-height: 30px;
          background: #1e9fff;
          color: #fff;
          border-radius: 5px;
          font-size: 13px;
        }
        &::before {
          content: "";
          display: block;
          position: absolute;
          z-index: 100;
          top: -8px;
          left: 15px;
          border-width: 5px;
          border-style: solid;
          border-color: #1e9fff transparent transparent transparent;
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
  color: #c8c8c8;
  .common-exam-add {
    display: inline-block;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
    border-radius: 50%;
    border: 1px solid #c8c8c8;
    font-size: 45px;
    color: #c8c8c8;
    margin-bottom: 10px;
    transition: all 0.3s ease-in-out;
  }
  &:hover {
    color: #1e9fff;
    .common-exam-add {
      border: 1px solid #1e9fff;
      background: #1e9fff;
      color: #fff;
    }
  }
}
</style>
