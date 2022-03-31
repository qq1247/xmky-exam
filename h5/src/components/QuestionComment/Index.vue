<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-09-18 15:44:29
 * @LastEditors: Che
 * @LastEditTime: 2021-09-29 17:01:17
-->
<template>
  <div>
    <div
      class="comment-item"
      v-for="(comment, index) in list"
      :key="comment.id"
    >
      <el-avatar
        :size="40"
        shape="square"
        :src="`/api/file/download?id=${Number(comment.avatar)}`"
        ><i class="common common-wo"></i
      ></el-avatar>
      <div class="item-content">
        <div class="content-info">
          <span class="info-name">{{ comment.name }}：</span
          >{{ comment.content }}
        </div>
        <div class="content-handler">
          <span>{{ comment.time }}</span
          ><span
            class="comment"
            v-if="comment.children && commentState === 2"
            @click.stop="showComment(index)"
            >回复</span
          >
        </div>
        <CommentText
          showTriangle
          v-if="comment.comment"
          @comment="childrenComment(arguments, comment.id)"
        ></CommentText>
        <div
          class="content-more"
          v-if="comment.children && !comment.children.length"
          @click="getChildrenComment(comment.id)"
        >
          点击查看更多回复！
        </div>
        <div
          :class="comment.children ? 'children-content' : ''"
          v-if="comment.children && comment.children.length"
        >
          <comment-item
            :list="comment.children"
            @showMore="showMore(comment.id, index)"
          ></comment-item>
        </div>
      </div>
    </div>
    <div class="show-more" @click="showMore">查看更多&gt;</div>
  </div>
</template>

<script>
import CommentText from './CommentText.vue'
export default {
  name: 'CommentItem',
  components: {
    CommentText,
  },
  props: {
    list: {
      type: Array,
      default: () => [],
    },
    commentState: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {}
  },
  mounted() {},
  methods: {
    showComment(index) {
      const comment = this.list[index].comment
      this.$set(this.list[index], 'comment', !comment)
    },
    childrenComment([text, anonymity], id) {
      this.$emit('childrenComment', text, anonymity, id)
    },
    getChildrenComment(id) {
      this.$emit('getChildrenComment', id)
    },
    showMore(id, index) {
      this.$emit('showMore', id, index)
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
      .comment {
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
