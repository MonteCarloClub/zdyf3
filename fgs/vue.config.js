module.exports = (options = {}) => ({
  publicPath: './',
  outputDir: '../backend/src/main/resources/static/fgs',
  devServer: {
    host: 'localhost',
    port: 8077,
    disableHostCheck: true,   // That solved it
    proxy: {
      '/api/': {
        // target: 'http://10.176.40.46:8080',
        target: 'http://10.131.131.3:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/abe'
        }
      }
    },
  },
})
