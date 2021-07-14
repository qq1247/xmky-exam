const path = require('path')

module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'assets',
  lintOnSave: true,
  runtimeCompiler: true,
  chainWebpack: (config) => {
    config.resolve.alias.set('@', path.resolve(__dirname, './src'))
  }
}
