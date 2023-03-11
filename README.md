# 重点研发计划-课题三【2019YFB2101703】

课题任务书和相关文档见：`/doc`

## 代码

本仓库由以下三部分源码组成

1. 区块链：`/blockchain`
2. 后端：`/backend`
3. 前端：
   1. 数据共享交换：`/ds`
   2. 细粒度权限管理系统：`/fgs`

## 静态资源

打包好的前端静态资源见本仓库的 **Releases**，可以从中下载**数据共享交换**和**细粒度权限管理系统**对应的前端资源（是一个压缩包）

静态资源有以下两种使用方式

**1. 与本地的后端调试**

请将两个压缩包解压后分别替换 `backend\src\main\resources\static`目录下的 `ds` 和 `fgs`，保证替换后的目录结构像下面这样

```
static
|   index.html
|
+---ds
|   |   index.html
|   +---css
|   +---js
|
+---fgs
    |   index.html
    +---css
    +---js
```

**2. 部署至服务器**

需要配合 Nginx 使用，配置文件在`/etc/nginx/sites-available/default`，参考配置如下

```nginx
server{
    location /datashare {
        alias /home/zdyf/datashare/;
        #指定主页
        index index.html;
        #自动跳转
        autoindex on;
    }

    location /fgs {
        alias /home/zdyf/fgs/;
        #指定主页
        index index.html;
        #自动跳转
        autoindex on;
    }
}
```

解释：

用户在浏览器访问 `<your-domain>/datashare` 时，请求打到服务器上交由 Nginx 处理，Nginx 根据配置的内容返回服务器上 `/home/zdyf/datashare/` 路径下的静态文件（`.html`、`.css`、`.js` 等）给浏览器，最后展示完整的前端页面。

所以**部署前端**实际上就是把本地打包好的（数据共享交换平台）静态资源上传至`/home/zdyf/datashare/`目录下

>  同理，细粒度权限管理系统的静态资源放在`/home/zdyf/fgs/`

最终服务器上的目录结构如下：

```
home/zdyf
|
+---datashare
|   |   index.html
|   +---css
|   +---js
|
+---fgs
    |   index.html
    +---css
    +---js
```

