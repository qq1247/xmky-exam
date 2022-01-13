<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-11-12 10:58:56
 * @LastEditors: Che
 * @LastEditTime: 2022-01-13 14:52:39
-->
<template>
  <div class="container param-container">
    <!-- PassWord -->
    <PassWord
      :pwdValue="pwdValue"
      :pwdType="pwdType"
      @init="() => init()"
    ></PassWord>
    <!-- Logo -->
    <Logo :logo="orgLogo" :name="orgName" @resetLogo="resetLogo"></Logo>
    <!-- File -->
    <File :dir="fileUploadDir"></File>
    <!-- DataBase -->
    <DataBase :dir="dbBakDir"></DataBase>
    <!-- Email -->
    <Email :emailParams="emailParams"></Email>
  </div>
</template>

<script>
import Logo from '../Param/Logo.vue'
import File from '../Param/File.vue'
import Email from '../Param/Email.vue'
import DataBase from '../Param/DataBase.vue'
import PassWord from '../Param/PassWord.vue'
import { parmGet } from 'api/base'
import { getSetting, setSetting } from '@/utils/storage'
export default {
  components: {
    Logo,
    File,
    Email,
    DataBase,
    PassWord,
  },
  data() {
    return {
      orgLogo: null,
      orgName: '',
      dbBakDir: '',
      fileUploadDir: '',
      pwdType: 1,
      pwdValue: '',
      emailParams: {},
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      const { data } = await parmGet()
      this.orgLogo = data.orgLogo
      this.orgName = data.orgName
      this.dbBakDir = data.dbBakDir
      this.fileUploadDir = data.fileUploadDir
      this.pwdType = data.pwdType
      this.pwdValue = data.pwdValue
      this.emailParams = {
        emailPwd: data.emailPwd,
        emailHost: data.emailHost,
        emailProtocol: data.emailProtocol,
        emailEncode: data.emailEncode,
        emailUserName: data.emailUserName,
      }
    },
    async resetLogo() {
      await this.init()
      this.$store.dispatch('setting/changeSetting', {
        key: 'orgName',
        value: this.orgName,
      })
      let loginInfo = getSetting()
      loginInfo.orgName = this.orgName
      setSetting(loginInfo)
    },
  },
}
</script>

<style lang="scss">
.param-container {
  display: flex;
  align-items: flex-start;
  .param-option {
    width: 1024px;
    margin: 0 auto 20px;
    padding: 0 30px;
    background: #fff;
    border-radius: 10px;
    border: 1px solid #ececec;
  }
  .param-title {
    width: 100%;
    height: 50px;
    line-height: 50px;
    font-weight: 700;
    border-bottom: 1px solid #ececec;
    margin-bottom: 20px;
  }
}
</style>
