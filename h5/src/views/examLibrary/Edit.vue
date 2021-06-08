<template>
  <div class="agreements">
    <div id="contents">
      <div id="one" class="one" ref="agree1" index="2" v-show="show1 !== '2'">
        <p class="title">上海银行个人信用报告查询授权书</p>
        <p class="no">上海银行股份有限公司：</p>
        <p>
          本人（下称授权人）授权在贵行（下称被授权人）办理下列业务或下列情况时：
        </p>
        <p>
          <b>1、</b
          >授权人办理授信业务（包括个人贷款、贷记卡、准贷记卡等，涵盖贷前、贷中、贷后各阶段，下同）
        </p>
        <p><b>2、</b>授权人办理担保业务</p>
        <p>
          <b>3、</b
          >法人或其他组织办理授信业务或担保业务，被授权人需要查询授权人作为法定代表人或出资人的信用状况
        </p>
        <p><b>4、</b>授权人申请征信异议处理</p>
        <p>
          被授权人可以通过中国人民银行金融信用信息基础数据库查询和使用本人的个人信用报告，并向中国人民银行金融信用信息基础数据库提供本人及配偶个人基本信息和信用情况。
        </p>
        <p>
          上述授权期限为授权人签署本授权书之日起，至授权人在被授权人（包括其作为法定代表人或出资人的法人或其他组织）所有授信、担保等相关业务到期且履行完毕全部义务之日止。被授权人超出授权查询的一切后果及法律责任由被授权人承担。
        </p>
        <p class="big">授权人已经完全知晓并理解上述授权条款。</p>
        <p>特此授权。</p>
        <p>授权人本人（签字）*：{{ personName }}</p>
        <p>身份证件号*：{{ personCertNo }}</p>
        <p class="tr">授权日期：{{ year }}年{{ month }}月 {{ day }}日</p>
      </div>
      <div id="two" class="one" ref="agree2" index="3" v-show="show2 !== '3'">
        <p class="title">上海银行个人外部信用信息查询授权书</p>
        <p class="title">（小企业授信专用）</p>
        <p>上海银行股份有限公司：</p>

        <p>
          本人（下称授权人）授权在贵行（下称被授权人）办理下列业务或下列情况时：
        </p>
        <p>
          <b>1、</b
          >授权人办理授信业务（包括个人贷款、贷记卡、准贷记卡等，涵盖贷前、贷中、贷后各阶段，下同）
        </p>
        <p><b>2、</b>授权人办理担保业务</p>
        <p>
          <b>3、</b
          >法人或其他组织办理授信业务或担保业务，被授权人需要查询授权人作为法定代表人或出资人的信用状况
        </p>
        <p>
          被授权人可以通过工商、公安、法院、中国人民银行批准设立的征信机构查询和使用授权人本人的身份、信用、涉税、社会保险、住房公积金、涉诉、执行、违法违规信息。
          上述授权期限为授权人签署本授权书之日起，至授权人在被授权人所有授信、担保等相关业务到期且履行完毕全部义务之日止。
          授权人已经完全知晓并理解上述授权条款。
        </p>
        <p>特此授权。</p>
        <p>授权人本人（签字）*：{{ personName }}</p>
        <p>身份证件号*：{{ personCertNo }}</p>
        <p class="tr">授权日期：{{ year }}年{{ month }}月 {{ day }}日</p>
      </div>
      <div id="three" class="one" ref="agree3" index="4" v-show="show3 !== '4'">
        <p class="title">企业授权委托书</p>
        <p class="no">上海银行股份有限公司：</p>
        <p>
          本人是【{{
            companyName
          }}】公司（“本企业”）法定代表人（如本企业类型为个体工商户，则为负责人），并在此确认有权以自身名义直接代表本企业签署本文件并确认本委托书下述所列事项；
        </p>
        <p>兹代表本企业向贵行确认如下委托授权事项：</p>
        <p>
          1、授权贵行向与贵行合作的中金金融认证中心有限公司（简称为“CFCA”）申请本企业数字证书、注册本企业签约用户；
        </p>
        <p>
          2、确认本人有权通过在贵行平台（包括但不限于微信小程序）上以点击签署及实名手机号动态密码验证方式以调用CFCA数字证书完成包括《上海银行企业信用报告查询授权书》《上海银行企业外部信用信息查询授权书》等电子文件的线上电子签署，线上电子签约的相应电子签名均为本企业认可的可靠电子签名；
        </p>
        <p>
          3、确认可通过本人实名手机号接收邀请码、动态密码及相关平台（包括但不限于微信小程序）业务通知及信息；
        </p>
        <p>
          4、除非经贵行另行同意终止，上述授权在本人及/或本企业在贵行有授信或业务存续期内持续有效。
        </p>
        <p>
          5、本人作为本企业法定代表人（或如本企业类型为个体工商户的，则为负责人），再次确认有权代表本企业签署和确认本文件，本人已取得所有所需授权（如有）；本人承诺后续（如贵行要求）向贵行另行以线下纸质方式补充出具经本企业加盖公章的如下文件：(a)本《企业授权委托书》；以及(b)根据本《企业授权委托书》本企业调用CFCA数字证书所签署全部或部分电子文件；如因授权不足或瑕疵或因违反前述承诺而发生争议的，本人同意承担由此产生的一切后果与责任。
        </p>
        <p>特此确认。</p>
        <p>授权人本人姓名：{{ authPerson }}</p>
        <p>身份证件类型：{{ idCardType }}</p>
        <p>身份证件号*：{{ personCertNo }}</p>
        <p>授权日期：{{ year }}年{{ month }}月 {{ day }}日</p>
      </div>
    </div>
    <button class="btn color-fff" :disabled="disabled">
      {{ disabled ? "我已阅读并同意(" + time + "s)" : "我已阅读并同意" }}
    </button>
    <ul>
      <li
        v-for="item in agreementList"
        :key="item.id"
        :class="{
          active: current === item.id ? true : false
        }"
      >
        <a
          href="javascript:;"
          :id="item.id"
          @click.prevent="goToAnchor(item)"
          >{{ item.abbreviationName }}</a
        >
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      type: "02",
      operationType: "1",
      authStatus: "S",
      serialNo: "",
      userId: "",
      modalShow: false,
      phoneNo: "",
      key: Math.random(),
      code: "",
      timer: null,
      time: 15,
      disabled: true,
      agreementList: [],

      personName: "",
      personCertNo: "",
      validaDate: "",
      year: "",
      month: "",
      day: "",
      active1: true,
      active2: false,
      active3: false,
      // title: "上海银行企业信用报告查询授权书",
      topHeight: 0,
      id: "",
      ids: [],
      flag1: true,
      flag2: true,
      companyName: "",
      idCardType: "",
      authPerson: "",
      datas: {},
      current: "2",
      show1: "",
      show2: "",
      show3: ""
    }
  },
  mounted() {
    this.agreementList = [
      { id: "2", abbreviationName: "a22" },
      { id: "3", abbreviationName: "a33" },
      { id: "4", abbreviationName: "a44" },
      { id: "5", abbreviationName: "55" }
    ]
    window.addEventListener("scroll", this.handleScroll)
  },
  methods: {
    goToAnchor(item) {
      this.current = item.id
      if (item.id === "2") {
        this.$refs.agree1.scrollIntoView({})
      } else if (item.id === "3") {
        this.$refs.agree2.scrollIntoView({})
      } else if (item.id === "4") {
        this.$refs.agree3.scrollIntoView({})
      }
    },
    handleScroll() {
      let scrollTop =
        document.documentElement.scrollTop || document.body.scrollTop
      let headerHeight = 0
      let scrollTopHeight = Math.ceil(scrollTop + headerHeight)

      let offsetTop1 = this.$refs.agree1.offsetTop
      let offsetTop2 = this.$refs.agree2.offsetTop
      let offsetTop3 = this.$refs.agree3.offsetTop
      console.log(scrollTopHeight, offsetTop1, offsetTop2, offsetTop3)

      if (scrollTopHeight >= offsetTop1 && scrollTopHeight < offsetTop2) {
        this.current = this.$refs.agree1.getAttribute("index")
        return
      }

      if (scrollTopHeight >= offsetTop2 && scrollTopHeight < offsetTop3) {
        this.current = this.$refs.agree2.getAttribute("index")
        if (this.flag1) {
          this.id = this.ids[1]
          this.flag1 = false
        }
        return
      }

      if (scrollTopHeight >= offsetTop3) {
        this.current = this.$refs.agree3.getAttribute("index")
        if (this.flag2) {
          this.id = this.ids[2]
          this.flag2 = false
        }
        return
      }
    }
  }
}
</script>

<style lang="scss">
.one {
  margin-bottom: 30px;
}
#one,
#two,
#three {
  p {
    font-size: 16;
    color: #333;
    line-height: 32px;
    text-indent: 2em;
  }
  p.no {
    text-indent: 0;
  }
  p.title {
    text-align: center;
    font-weight: bold;
    text-indent: 0;
  }
  p.big {
    font-weight: bold;
  }
}
.agreements {
  width: 100%;
  padding: 0 5%;
  .title {
    width: 60%;
    margin: auto;
  }
  .txt {
    margin-bottom: 20px;
    text-indent: 2em;
  }
}
ul {
  position: fixed;
  top: 200px;
  left: 5%;
  li {
    margin-bottom: 2px;
    width: 94px;
    height: 48px;
    line-height: 28px;
    text-align: center;
    a {
      display: block;
      width: 100%;
      height: 100%;
      background: RGBA(69, 132, 213, 0.6);
      color: #fff;
      font-size: 15;
    }
  }
  li.active {
    a {
      background: RGBA(69, 132, 213, 0.95);
      filter: Alpha(opacity=95);
    }
  }
}
button {
  margin: 300px auto 20px;
}
</style>
