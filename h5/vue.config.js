/*
 * @Description:
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-07-27 17:31:01
 * @LastEditors: Che
 * @LastEditTime: 2022-01-14 19:23:27
 */
const os = require('os')
const path = require('path')
const fs = require('fs')
const stat = fs.stat
const CompressionWebpackPlugin = require('compression-webpack-plugin')

// resolve absolute paths
const resolve = (dir) => path.join(__dirname, dir)

//copy dir
const copy = (src, dst) => {
  fs.readdir(src, function (err, paths) {
    if (err) {
      throw err
    }
    paths.forEach(function (path) {
      let _src = `${src}/${path}`
      let _dst = `${dst}/${path}`
      let readable
      let writable
      stat(_src, function (err, st) {
        if (err) {
          throw err
        }

        if (st.isFile()) {
          readable = fs.createReadStream(_src) //创建读取流
          writable = fs.createWriteStream(_dst) //创建写入流
          readable.pipe(writable)
        } else if (st.isDirectory()) {
          exists(_src, _dst, copy)
        }
      })
    })
  })
}

// isDir
const exists = (src, dst, callback) => {
  fs.exists(dst, function (exists) {
    if (exists) {
      callback(src, dst)
    } else {
      fs.mkdir(dst, function () {
        callback(src, dst)
      })
    }
  })
}

/* exists(
  './public/tinymce/plugins/uploadImg',
  './node_modules/tinymce/plugins/uploadImg',
  copy
) */

exists(
  resolve('public/tinymce/plugins/uploadImg'),
  resolve('node_modules/tinymce/plugins/uploadImg'),
  copy
)

// Obtain the local IP address
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
  lintOnSave: process.env.NODE_ENV === 'development',
  productionSourceMap: false,
  runtimeCompiler: true,
  configureWebpack: {
    // provide the app's title in webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    name: '在线考试',
    resolve: {
      alias: {
        '@': resolve('src'),
        assets: resolve('src/assets'),
        api: resolve('src/api'),
        views: resolve('src/views'),
        components: resolve('src/components'),
      },
    },
    plugins: [
      new CompressionWebpackPlugin({
        filename: '[path][base].gz',
        algorithm: 'gzip',
        // test: /\.js$|\.html$|\.json$|\.css/,
        test: /\.js$|\.json$|\.css/,
        threshold: 102400, // compression size
        minRatio: 0.8, // compression ratio
        deleteOriginalAssets: false, // Deleting source Files
      }),
    ],
  },
  chainWebpack(config) {
    // it can improve the speed of the first screen, it is recommended to turn on preload
    config.plugin('preload').tap(() => [
      {
        rel: 'preload',
        // to ignore runtime.js
        // https://github.com/vuejs/vue-cli/blob/dev/packages/@vue/cli-service/lib/config/app.js#L171
        fileBlacklist: [/\.map$/, /hot-update\.js$/, /runtime\..*\.js$/],
        include: 'initial',
      },
    ])

    // when there are many pages, it will cause too many meaningless requests
    config.plugins.delete('prefetch')

    config.when(process.env.NODE_ENV !== 'development', (config) => {
      config
        .plugin('ScriptExtHtmlWebpackPlugin')
        .after('html')
        .use('script-ext-html-webpack-plugin', [
          {
            // `runtime` must same as runtimeChunk name. default is `runtime`
            inline: /runtime\..*\.js$/,
          },
        ])
        .end()

      config.optimization.splitChunks({
        chunks: 'all',
        cacheGroups: {
          libs: {
            name: 'chunk-libs',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial', // only package third parties that are initially dependent
          },
          elementUI: {
            name: 'chunk-elementUI', // split elementUI into a single package
            priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
            test: /[\\/]node_modules[\\/]_?element-ui(.*)/, // in order to adapt to cnpm
          },
          commons: {
            name: 'chunk-commons',
            test: resolve('src/components'), // can customize your rules
            minChunks: 3, //  minimum common number
            priority: 5,
            reuseExistingChunk: true,
          },
        },
      })
      // https:// webpack.js.org/configuration/optimization/#optimizationruntimechunk
      config.optimization.runtimeChunk('single')
    })
  },
  devServer: {
    open: true,
    host: getIPAddress(),
    port: 9000,
    https: false,
    hotOnly: true,
    proxy: {
      '/api': {
        target: process.env.VUE_APP_BASE_URL, // Address of the proxy, it can replace Axios default URL
        changeOrigin: true, // Interface cross-domain, Need to open
        // ws: true, // proxy websocket
        // pathRewrite
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
}
