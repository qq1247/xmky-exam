<!--
 * @Description: 前端简介
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-05-24 15:46:00
 * @LastEditors: Che
 * @LastEditTime: 2021-09-30 17:37:57
-->
# 在线考试

<p align="center">
  <a href="https://github.com/vuejs/vue">
    <img src="https://img.shields.io/badge/vue-2.6.10-brightgreen.svg" alt="vue">
  </a>
  <a href="https://gitee.com/zhanghucheng/exam">
    <img src="https://img.shields.io/badge/online--exam-3.1.0-brightgreen.svg" alt="online-exam">
  </a>
  <a href="https://gitee.com/zhanghucheng/exam/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/mashape/apistatus.svg" alt="license">
  </a>
  <a href="https://gitee.com/zhanghucheng/exam/releases">
    <img src="https://img.shields.io/badge/release-3.1.0-blue.svg" alt="release">
  </a>
</p>

## 简介

[online-exam](https://gitee.com/zhanghucheng/exam) 是一个在线考试前端解决方案，它基于 [vue](https://github.com/vuejs/vue) 和 [element-ui](https://github.com/ElemeFE/element)实现。它使用了最新的前端技术栈，内置了动态路由，权限验证，提炼了典型的业务模型，提供了丰富的功能组件，它可以帮助你快速搭建企业级在线考试产品原型。相信不管你的需求是什么，本项目都能帮助到你。

- [在线预览](http://47.92.221.134:8080/)

## 前序准备

你需要在本地安装 [node](http://nodejs.org/) 和 [git](https://git-scm.com/)。本项目技术栈基于 [ES2015+](http://es6.ruanyifeng.com/)、[vue](https://cn.vuejs.org/index.html)、[vuex](https://vuex.vuejs.org/zh-cn/)、[vue-router](https://router.vuejs.org/zh-cn/) 、[vue-cli](https://github.com/vuejs/vue-cli) 、[axios](https://github.com/axios/axios) 和 [element-ui](https://github.com/ElemeFE/element)。

<p align="center">
  <img width="900" src="https://images.gitee.com/uploads/images/2021/0806/144840_bb72dd0d_393390.png">
</p>

## 功能

```
- 登录

- 权限验证
  - 页面权限
  - 权限配置

- 多环境发布
  - dev
  - prod

- 功能
  - 编辑多类型试题
  - 组合多类型试卷
  - 组织考试
  - 实时考试
  - 自动阅卷
```

## 开发

```bash
# 克隆项目
git clone https://gitee.com/zhanghucheng/exam.git

# 进入项目目录
cd exam/h5

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 启动服务
npm run serve:dev
```

浏览器访问 http://localhost:9000

## 发布

```bash
# 构建测试环境
npm run build:test

# 构建生产环境
npm run build:prod
```

## 其它

```bash
# 代码格式检查
npm run lint

# 代码格式检查并自动修复
npm run lint -- --fix
```

## Online Demo

[在线 Demo](http://47.92.221.134:8080/)

## Changelog

Detailed changes for each release are documented in the [release notes](https://gitee.com/zhanghucheng/exam/blob/master/h5/CHANGELOG.md).


## Browsers support

Modern browsers and Internet Explorer 10+.

| [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/edge/edge_48x48.png" alt="IE / Edge" width="24px" height="24px" />](https://godban.github.io/browsers-support-badges/)</br>IE / Edge | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/firefox/firefox_48x48.png" alt="Firefox" width="24px" height="24px" />](https://godban.github.io/browsers-support-badges/)</br>Firefox | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/chrome/chrome_48x48.png" alt="Chrome" width="24px" height="24px" />](https://godban.github.io/browsers-support-badges/)</br>Chrome | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/safari/safari_48x48.png" alt="Safari" width="24px" height="24px" />](https://godban.github.io/browsers-support-badges/)</br>Safari |
| ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| IE10, IE11, Edge                                                                                                                                                                                                 | last 2 versions                                                                                                                                                                                                    | last 2 versions                                                                                                                                                                                                | last 2 versions                                                                                                                                                                                                |
## 交流群

<a href="https://jq.qq.com/?_wv=1027&k=GXh1hHSy">
    <img src="https://img.shields.io/badge/qq-811189776-blue" alt="811189776">
  </a>

## License

[MIT](https://gitee.com/zhanghucheng/exam/blob/master/LICENSE)

Copyright (c) 2018-present ZhangHuCheng