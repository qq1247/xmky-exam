const path = require("path")

module.exports = {
  publicPath: "/",
  outputDir: "dist",
  assetsDir: "assets",
  lintOnSave: true,
  runtimeCompiler: true,
  chainWebpack: config => {
    config.resolve.alias.set("@", path.resolve(__dirname, "./src"))
  },
  configureWebpack: () => {},
  devServer: {
    open: true,
    host: "192.168.110.231", //192.168.110.231
    port: 8080,
    https: false,
    hotOnly: false,
    proxy: {
      "/api": {
        target: "http://192.168.110.231:8080/api/", //代理地址，这里设置的地址会代替axios中设置的baseURL
        changeOrigin: true, // 如果接口跨域，需要进行这个参数配置
        //ws: true, // proxy websockets
        //pathRewrite方法重写url
        pathRewrite: {
          "^/api": "/"
        }
      }
    }
  }
}
