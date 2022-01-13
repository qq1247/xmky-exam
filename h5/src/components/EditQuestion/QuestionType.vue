<!--
 * @Description: 添加试题
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-10-19 14:22:34
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 10:49:10
-->
<template>
  <div>
    <div id="question_driver_types">
      <div class="top">添加题型</div>
      <div
        :class="[
          'type-btn',
          questionType === btn.type ? 'type-btn type-btn-active' : 'type-btn',
        ]"
        :key="btn.type"
        @click="updateType(btn.type)"
        v-for="btn in typeButtons"
      >
        <i :class="btn.icon"></i>
        {{ btn.name }}
        <i class="common common-subscript sub-script"></i>
      </div>
    </div>
    <div class="divider"></div>
    <div
      id="question_driver_template"
      class="handler-btn"
      :key="handler.type"
      @click="otherHandler(handler.type)"
      v-for="handler in handlerButtons"
    >
      <i :class="handler.icon"></i>
      {{ handler.name }}
    </div>
  </div>
</template>

<script>
import { questionPublish, questionDel } from 'api/question'
export default {
  props: {
    questionType: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      typeButtons: [
        // 左侧按钮组1
        {
          type: 1,
          name: '单选题',
          icon: 'common common-radio',
        },
        {
          type: 2,
          name: '多选题',
          icon: 'common common-checkbox',
        },
        {
          type: 3,
          name: '填空题',
          icon: 'common common-cloze',
        },
        {
          type: 4,
          name: '判断题',
          icon: 'common common-judge',
        },
        {
          type: 5,
          name: '问答题',
          icon: 'common common-ask',
        },
      ],
      handlerButtons: [
        // 左侧按钮组2
        {
          type: 1,
          name: '导入导出',
          icon: 'common common-template-down',
        },
        {
          type: 2,
          name: '一键发布',
          icon: 'common common-publish',
        },
        {
          type: 3,
          name: '一键清空',
          icon: 'common common-delete',
        },
      ],
    }
  },
  mounted() {},
  methods: {
    // 更新类型
    updateType(value) {
      this.$emit('updateType', value)
    },
    // 操作函数
    otherHandler(type) {
      const parentData = this.$parent.$parent.$data
      switch (type) {
        case 1:
          this.$emit('showTemplate', true)
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
      this.$confirm('请选择发布方式？', '发布方式', {
        distinguishCancelAndClose: true,
        confirmButtonText: '全部发布',
        cancelButtonText: '单页发布',
      })
        .then(async () => {
          await questionPublish({
            questionTypeId: parentData.queryForm.questionTypeId,
          })
          this.$message.success('全部发布成功！')
          this.$emit('showTemplate', false)
        })
        .catch(async (action) => {
          if (action === 'cancel') {
            const ids = parentData.list.questionList.reduce((acc, cur) => {
              cur.state === 2 && acc.push(cur.id)
              return acc
            }, [])
            if (!ids.length) return false
            await questionPublish({
              ids,
            })
            this.$message.success('单页发布成功！')
            this.$emit('showTemplate', false)
          }
        })
    },
    // 清空操作
    del(parentData) {
      this.$confirm('请选择清空方式？', '清空方式', {
        distinguishCancelAndClose: true,
        confirmButtonText: '全部清空',
        cancelButtonText: '单页清空',
      })
        .then(async () => {
          await questionDel({
            questionTypeId: parentData.queryForm.questionTypeId,
          })
          this.$message.success('全部清空成功！')
          this.$emit('showTemplate', false)
        })
        .catch(async (action) => {
          if (action === 'cancel') {
            const ids = parentData.list.questionList.reduce((acc, cur) => {
              acc.push(cur.id)
              return acc
            }, [])
            if (!ids.length) return false
            await questionDel({
              ids,
            })
            this.$message.success('单页清空成功！')
            this.$emit('showTemplate', false)
          }
        })
    },
  },
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
  border-bottom: 1px solid #eee;
  &::before {
    content: '';
    display: inline-block;
    position: relative;
    top: 2px;
    left: -10px;
    width: 2px;
    height: 14px;
    background: #0095e5;
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
  &:nth-of-type(2) {
    margin-top: 50px;
  }
  &:hover {
    border: 1px solid #0095e5;
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
      color: #0095e5;
    }
    &:last-child {
      font-size: 22px;
      position: absolute;
      bottom: -11px;
      right: -1px;
      color: #0095e5;
      display: none;
    }
  }
}
.type-btn-active {
  border: 1px solid #0095e5;
  color: #0095e5;
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
    background: #0095e5;
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
