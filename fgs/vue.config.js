module.exports = (options = {}) => ({
  publicPath: './',
  outputDir: '../backend/src/main/resources/fgs',
  devServer: {
    host: 'localhost',
    port: 8087,
    disableHostCheck: true,   // That solved it
    proxy: {
      '/api/': {
        target: 'http://10.176.40.46:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    },
  },
})
