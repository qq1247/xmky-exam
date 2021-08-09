const path = require('path')
const os = require('os')

// 获取本机电脑IP
const getIPAddress = () => {
  const interfaces = os.networkInterfaces()
  for (const devName in interfaces) {
    const iface = interfaces[devName]
    for (let i = 0; i < iface.length; i++) {
      const alias = iface[i]
      if (
        alias.family === 'IPv4' &&
        alias.address !== '127.0.0.1' &&
        !alias.internal
      ) {
        return alias.address
      }
    }
  }
}

module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'assets',
  lintOnSave: true,
  runtimeCompiler: true,
  pages: {
    index: {
      entry: 'src/main.js',
      template: 'public/index.html',
      filename: 'index.html',
      title: '在线考试',
      chunks: ['chunk-vendors', 'chunk-common', 'index'],
    },
  },
  chainWebpack: (config) => {
    config.resolve.alias.set('@', path.resolve(__dirname, './src'))
  },
  configureWebpack: () => {},
  devServer: {
    open: true,
    host: getIPAddress(),
    port: 9000,
    https: false,
    hotOnly: true,
    proxy: {
      '/api': {
        target: process.env.VUE_APP_BASE_URL, // 代理地址，这里设置的地址会代替axios中设置的baseURL
        changeOrigin: true, // 如果接口跨域，需要进行这个参数配置
        // ws: true, // proxy websocket
        // pathRewrite方法重写url
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
}
