module.exports = (options = {}) => ({
  publicPath: './',
  outputDir: '../backend/src/main/resources/static/ds',
  devServer: {
    host: 'localhost',
    port: 8066,
    disableHostCheck: true,   // That solved it
    proxy: {
      '/dev/': {
        target: 'http://10.176.40.46:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/dev': ''
        }
      },
      '/cert/': {
        target: 'http://10.176.40.46/dpki/',
        changeOrigin: true,
        pathRewrite: {
          '^/cert': ''
        }
      }
    },
  },
})
