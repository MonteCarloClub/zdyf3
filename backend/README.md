# Abe-trustPlatform

服务器：10.176.40.46（仅限校园网内，或者挂 VPN）

+ 用户名：`zdyf`，密码：`zdyf123!`
+ 用户名：`root`，密码：`39YwW8xh`


## 运行

新建 Terminal

```bash
$ cd /home/zdyf/fn/datashare-v8/
$ sudo nohup java -jar atp-0.0.1-SNAPSHOT.jar 2>&1 &
```


注：可从仓库中，下载前后端代码，访问本地 localhost:8080/abe 进入大导航页，根据页面上的导航进入对应的系统界面


## 编译打包

[IDEA打包jar包_萧篱衣的博客-CSDN博客_ideajar包](https://blog.csdn.net/weixin_47272508/article/details/125974386)

## IPFS部署
后端提供将加密文件上传至IPFS功能，需要在运行环境中部署IPFS。

下载go-ipfs：
IPFS官网：https://dist.ipfs.io/

# 1.解压IPFS
tar -zxvf go-ipfs_v0.10.0_darwin-arm64.tar.gz

cd go-ipfs
 
# 2.设置ipfs命令到全局路径PATH中
 
# 3.初始化IPFS服务
ipfs init
 
# 4.解决跨域问题
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Methods '["PUT", "GET", "POST", "OPTIONS"]'
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Origin '["*"]'
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Credentials '["true"]'
ipfs config --json API.HTTPHeaders.Access-Control-Allow-Headers '["Authorization"]'
ipfs config --json API.HTTPHeaders.Access-Control-Expose-Headers '["Location"]'

# 5.启动ipfs服务
ipfs daemon

# 6.访问IPFS的web网址，看到网站内容说明IPFS服务搭建成功，可以在设置中对IPFS默认配置做进一步修改
http://127.0.0.1:5001/webui

参考https://blog.csdn.net/yorickjun/article/details/128493789



