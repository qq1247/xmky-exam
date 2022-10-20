<template>
  <el-form
    ref="editForm"
    :model="examInfo"
    :rules="editForm.rules"
    label-width="100px"
    class="mark-setting-box"
  >
    <el-row v-for="(examUser, index) in examInfo.examUsers" :key="index">
      <el-col :span="12">
        <!-- 
          prop写法：源码片段
          function getPropByPath(obj, path, strict) {// obj=model="examInfo"  path=prop="`examUsers.${index}.examUserIds`"
            var tempObj = obj;
            path = path.replace(/\[(\w+)\]/g, '.$1');
            path = path.replace(/^\./, '');

            var keyArr = path.split('.');
            var i = 0;
            for (var len = keyArr.length; i < len - 1; ++i) {
              if (!tempObj && !strict) break;
              var key = keyArr[i];
              if (key in tempObj) {
                tempObj = tempObj[key];// 按.分割，一层一层找有没有属性
              } else {
                if (strict) {
                  throw new Error('please transfer a valid prop path to form item!');// 报错提示
                }
                break;
              }
            }
            return {
              o: tempObj,
              k: keyArr[i],
              v: tempObj ? tempObj[keyArr[i]] : null
            };
          };
        -->
        <el-form-item
          label="考试用户"
          :prop="`examUsers.${index}.examUserIds`"
          :rules="editForm.rules.examUserIds"
        >
          <CustomSelect
            :value="examUser.examUserIds"
            url="user/listpage"
            :params="{}"
            optionLabel="name"
            optionValue="id"
            searchParmName="name"
            :multiple="true"
            @change="_examUserUpdate(index, $event)"
          >
            <template #customText="{ item }">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{
                item.orgName
              }}</span>
            </template>
          </CustomSelect>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          v-if="markQuestions.length > 0"
          label="阅卷用户"
          :prop="`examUsers.${index}.markUserId`"
          :rules="editForm.rules.markUserId"
        >
          <CustomSelect
            :value="examUser.markUserId"
            url="user/listpage"
            :params="{ type: 2 }"
            optionLabel="name"
            optionValue="id"
            searchParmName="name"
            @change="_markUserUpdate(index, $event)"
          >
            <template #customText="{ item }">
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{
                item.orgName
              }}</span>
            </template>
          </CustomSelect>
        </el-form-item>
      </el-col>
    </el-row>
    <div v-if="markQuestions.length > 0" class="remark-buttons">
      <!-- 有主观题，可添加多个阅卷用户 -->
      <el-form-item>
        <el-button
          type="primary"
          size="mini"
          icon="el-icon-plus"
          @click="userGroupAdd"
          plain
          >添加</el-button
        >
        <el-button
          v-if="this.examInfo.examUsers.length > 1"
          size="mini"
          icon="el-icon-minus"
          @click="userGroupDel"
          type="danger"
          plain
          >删除</el-button
        >
      </el-form-item>
    </div>
  </el-form>
</template>

<script>
import CustomSelect from 'components/CustomSelect.vue'
import { mapState } from 'vuex'
import { mapGetters } from 'vuex'
import { mapMutations } from 'vuex'
export default {
  components: {
    CustomSelect,
  },
  data() {
    return {
      editForm: {
        rules: {
          // 校验规则
          examUserIds: [{
            trigger: 'change',
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请选择用户'))
              }
              if (!value instanceof Array) {
                return callback(new Error('请选择用户'))
              }
              if (value.length === 0) {
                return callback(new Error('请选择用户'))
              }
              return callback()
            },},
          ],
          markUserId: [{
            trigger: 'change',
            validator: (rule, value, callback) => {
              if (!value) {
                return callback(new Error('请选择用户'))
              }
              return callback()
            },
          },],
        },
      },
    }
  },
  mounted: function () {
    if (!this.examInfo.examUsers.length) {
      this.userGroupAdd()
    }
  },
  computed: {
    ...mapState('exam', ['examInfo']),
    ...mapGetters('exam', ['totalScore', 'markQuestions']),
  },
  methods: {
    ...mapMutations('exam', [
      'userGroupAdd',
      'userGroupDel',
      'examUsersUpdate',
      'markUserUpdate',
    ]),
    /** 
     * 考试用户修改
     * 同一个用户在多行只能有一个
     * 如果当前选中行的值，在其他行存在，则其他行删除值
     */
    async _examUserUpdate(index, value) {
      this.examUsersUpdate({ index, value })
      let cur = this.examInfo.examUsers[index].examUserIds
      this.examInfo.examUsers.map((examUser, index1) => {
        if (index !== index1) {
          let other = [...examUser.examUserIds]
          cur.map(examUserId => {
            let index2 = other.indexOf(examUserId)
            if (index2 !== -1) {
              other.splice(index2, 1)
              this.examUsersUpdate({ index: index1, value: other })
            }
          })
        }
      })
    },
    // 选择阅卷用户
    _markUserUpdate(index, value) {
      if (!value) {// bug：点击清空按钮时，回调该方法，value=""，导致页面显示ture
        value = null
      }
      this.markUserUpdate({ index, value })
      let cur = this.examInfo.examUsers[index].markUserId
      this.examInfo.examUsers.map((examUser, index1) => {
        if (index !== index1) {
          let other = examUser.markUserId
          if (cur == other) {
            this.markUserUpdate({ index: index1, value: null })
          }
        }
      })
    },
    // 下一步
    next() {
      this.$refs['editForm'].validate((valid) => {
        if (!valid) {
          return
        }
        this.$parent.activeIndex++
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.mark-setting-box {
  padding: 15px 150px;
}
</style>
