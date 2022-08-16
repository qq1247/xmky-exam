<template>
  <div>
    <div class="top">添加题型</div>
    <div
      v-for="btn in typeButtons"
      :key="btn.type"
      :class="['type-btn', questionType === btn.type ? 'type-btn-active' : '']"
      @click="updateType(btn.type)"
    >
      <img :src="btn.icon" alt="">
      {{ btn.name }}
      <i class="common common-subscript sub-script" />
    </div>
    <div class="divider" />
    <div
      v-for="handler in handlerButtons"
      :key="`handler${handler.type}`"
      class="handler-btn"
      @click="otherHandler(handler.type)"
    >
      <i :class="handler.icon" />
      {{ handler.name }}
    </div>
  </div>
</template>

<script>
import { questionPublish, questionDel } from 'api/question'
export default {
  data() {
    return {
      questionType: 1,
      typeButtons: [
        // 左侧按钮组1
        {
          type: 1,
          name: '单选题',
          icon: require('../../assets/img/question/question-radio.png')
        },
        {
          type: 2,
          name: '多选题',
          icon: require('../../assets/img/question/question-check.png')
        },
        {
          type: 3,
          name: '填空题',
          icon: require('../../assets/img/question/question-cloze.png')
        },
        {
          type: 4,
          name: '判断题',
          icon: require('../../assets/img/question/question-judge.png')
        },
        {
          type: 5,
          name: '问答题',
          icon: require('../../assets/img/question/question-ask.png')
        }
      ],
      handlerButtons: [
        // 左侧按钮组2
        {
          type: 1,
          name: '文本导入',
          icon: 'common common-template-down'
        },
        {
          type: 2,
          name: '一键发布',
          icon: 'common common-publish'
        },
        {
          type: 3,
          name: '一键删除',
          icon: 'common common-delete'
        }
      ]
    }
  },
  mounted() {},
  methods: {
    // 更新类型
    updateType(value) {
      this.questionType = value
      this.$emit('updateType', value)
    },
    // 操作函数
    otherHandler(type) {
      this.questionType = null
      const parentData = this.$parent.$parent.$data
      switch (type) {
        case 1:
          this.$router.push({ name: 'QuestionBatchInput' })
          // this.$emit('showTemplate', true)
          break
        case 2:
          if (!['', '2'].includes(parentData.queryForm.state)) {
            this.$message.warning('请选择草稿状态再发布！')
            return
          }
          this.publish(parentData)
          break
        case 3:
          this.del(parentData)
          break
        default:
          break
      }
    },
    // 发布操作
    publish(parentData) {
      this.$confirm('确定要发布？', '提示', {
        distinguishCancelAndClose: true,
        confirmButtonText: '全部发布',
        cancelButtonText: '当页发布'
      })
        .then(async() => {
          await questionPublish({
            questionTypeId: parentData.questionTypeId
          })
          this.$message.success('全部发布成功！')
          this.$emit('showTemplate', false)
        })
        .catch(async(action) => {
          if (action === 'cancel') {
            const ids = parentData.list.questionList.reduce((acc, cur) => {
              cur.state === 2 && acc.push(cur.id)
              return acc
            }, [])
            if (!ids.length) return false
            await questionPublish({
              ids
            })
            this.$message.success('当页发布成功！')
            this.$emit('showTemplate', false)
          }
        })
    },
    // 删除操作
    del(parentData) {
      this.$confirm(
        '确定要删除？<br/>不影响关联的试卷等，可以正常显示和使用',
        '提示',
        {
          distinguishCancelAndClose: true,
          confirmButtonText: '全部删除',
          cancelButtonText: '当页删除',
          dangerouslyUseHTMLString: true
        }
      )
        .then(async() => {
          await questionDel({
            questionTypeId: parentData.questionTypeId
          })
          this.$message.success('删除成功！')
          this.$emit('showTemplate', false)
        })
        .catch(async(action) => {
          if (action === 'cancel') {
            const ids = parentData.list.questionList.reduce((acc, cur) => {
              acc.push(cur.id)
              return acc
            }, [])
            if (!ids.length) return false
            await questionDel({
              ids
            })
            this.$message.success('删除成功！')
            this.$emit('showTemplate', false)
          }
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.top {
  background: #fff;
  width: 100%;
  height: 40px;
  line-height: 40px;
  color: #333;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 100;
  font-weight: 600;
  padding-left: 30px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 2px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0094e5;
  }
}
.type-btn {
  width: 110px;
  height: 40px;
  margin: 7px auto;
  line-height: 40px;
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
  user-select: none;
  border-radius: 3px;
  border: 1px solid #eeeeff;
  img {
    margin-right: 10px;
    margin-left: 13px;
  }
  &:nth-of-type(2) {
    margin-top: 50px;
  }
  &:hover {
    border: 1px solid #0094e5;
  }
  i {
    font-size: 16px;
    &:first-child {
      display: block;
      width: 22px;
      height: 22px;
      line-height: 22px;
      text-align: center;
      margin: 0 10px;
      color: #0094e5;
    }
    &:last-child {
      font-size: 22px;
      position: absolute;
      bottom: -11px;
      right: -1px;
      color: #0094e5;
      display: none;
    }
  }
}
.type-btn-active {
  border: 1px solid #0094e5;
  color: #0094e5;
  i:last-child {
    display: block;
  }
}
.divider {
  border-bottom: 1px solid #eee;
  margin: 16px auto;
}
.handler-btn {
  width: 110px;
  height: 40px;
  background: #eee;
  margin: 7px auto;
  text-align: center;
  line-height: 40px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  user-select: none;
  border-radius: 3px;
  &:hover {
    background: #0094e5;
    color: #ffff;
  }
  i {
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    font-size: 16px;
    display: block;
    margin-right: 10px;
  }
}
</style>
