<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-18 15:44:29
 * @LastEditors: Che
 * @LastEditTime: 2021-09-22 12:52:57
-->
<template>
  <div>
    <div
      class="comment-item"
      v-for="(comment, index) in list"
      :key="comment.id"
    >
      <el-avatar
        shape="square"
        :size="40"
        icon="el-icon-user-solid"
        v-if="comment.avatar"
      ></el-avatar>
      <div class="item-content">
        <div class="content-info">
          <span class="info-name">{{ comment.name }}：</span
          >{{ comment.content }}
        </div>
        <div class="content-handler">
          <span>{{ comment.time }}</span
          ><span
            class="reply"
            v-if="comment.children"
            @click.stop="showReply(index)"
            >回复</span
          >
        </div>
        <ReplyText
          showTriangle
          v-if="comment.reply"
          @reply="childrenReply($event, comment.id)"
        ></ReplyText>
        <div
          class="content-more"
          v-if="comment.children && !comment.children.length"
          @click="getChildrenReply(comment.id)"
        >
          点击查看更多回复！
        </div>
        <div
          :class="comment.children ? 'children-content' : ''"
          v-if="comment.children && comment.children.length"
        >
          <comment-item :list="comment.children"></comment-item>
        </div>
      </div>
    </div>
    <div class="show-more" @click="showMore">查看更多&gt;</div>
  </div>
</template>

<script>
import ReplyText from './ReplyText.vue'
export default {
  name: 'CommentItem',
  components: {
    ReplyText,
  },
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {}
  },
  mounted() {},
  methods: {
    showReply(index) {
      const reply = this.list[index].reply
      this.$set(this.list[index], 'reply', !reply)
    },
    childrenReply(text, anonymity, id) {
      this.$emit('childrenReply', text, anonymity, id)
    },
    getChildrenReply(id) {
      this.$emit('getChildrenReply', id)
    },
    showMore() {
      this.$emit('showMore')
    },
  },
}
</script>

<style lang="scss" scoped>
.comment-item {
  display: flex;
  padding: 5px 10px 10px;
  .item-content {
    flex: 1;
    margin-left: 10px;
    font-size: 13px;
    .content-info {
      overflow: hidden;
      word-wrap: break-word;
      word-break: break-all;
      color: #666;
      line-height: 20px;
      .info-name {
        color: #eb7350;
      }
    }
    .content-handler {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 5px 10px 0 0;
      .reply {
        cursor: pointer;
        &:hover {
          font-weight: 600;
        }
      }
    }
    .content-more {
      background: #eaeaec;
      line-height: 30px;
      margin-top: 10px;
      cursor: pointer;
      padding: 0 15px;
      color: #eb7350;
      font-size: 13px;
    }
    .children-content {
      padding: 5px 10px;
      margin-top: 10px;
      background: #eaeaec;
    }
  }
}

.show-more {
  line-height: 40px;
  text-align: center;
  color: #eb7350;
  cursor: pointer;
}
</style>
