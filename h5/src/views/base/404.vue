<template>
  <div class="container">
    <img src="../../assets/img/404.png" />
    <router-link to="/" class="go-home">返回首页</router-link>
  </div>
</template>

<style lang="scss" scoped>
.container {
  justify-content: center;
  align-items: center;
  padding: 120px 0 70px;
  img {
    width: 350px;
    height: 250px;
  }
  .go-home {
    width: 120px;
    height: 40px;
    line-height: 40px;
    background: #1e9fff;
    box-sizing: border-box;
    text-align: center;
    font-size: 14px;
    color: #fff;
    text-decoration: none;
    border-radius: 3px;
    margin-top: 60px;
  }
}
</style>
