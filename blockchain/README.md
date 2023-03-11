# 2020-Winter-vacation-homework
1 寒假任务: 搭建fabric多机通信，熟练使用sdk(语言待定)，写一个带智能合约的小demo（用户多点登录为基础，可扩展加入公私钥签名加密、用户其他涉及使用数据的功能），外部由ip调用。 完成日期2020-02-21。

2 基于ipv6的接口调用：调研纯ipv6的接口调用方式是否兼容fabric等。完成日期待定

3 密码模块：调研使用的密码 | 密码的开发。 完成日期待定

4 属性背书插件/平台：在大家具备一定基础上进行讨论。讨论日期待定

5 数据交换共享平台：在上述成果上讨论解决方案，并组装插件。完成日期待定

## 说明

服务器：10.176.40.46（仅限校园网内，或者挂 VPN）

+ 用户名：`zdyf`，密码：`zdyf123!`
+ 用户名：`root`，密码：`39YwW8xh`

### 一键式部署环境

```bash
$ cd /home/zdyf
$ sudo ./setup.sh
```

会自动开启几个后端



### 具体部署环境说明

#### 1、启动Fabric环境

```bash
$ cd /home/zdyf/fn/go/src/github.com/hyperledger/fabric/scripts/fabric-samples/chaincode-docker-devmode

$ sudo docker-compose -f docker-compose-couch.yaml up -d
```

执行 `docker ps` 可以看到一个 ccenv 容器、一个 cli 节点、一个 peer 节点、一个 orderer 排序节点和一个 couchdb 数据库。

若没有全部启动，可以通过

`sudo docker-compose -f docker-compose-couch.yaml down`

停止程序后重新启动

 

#### 2、运行合约程序

```bash
$ sudo docker exec -it chaincode bash
$ cd 2020-Winter-vacation-homework/trustPlatform
$ CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=plat:0 ./trustPlatform
```

新建 Terminal

```bash
$ sudo docker exec -it chaincode bash
$ cd 2020-Winter-vacation-homework/dabe
$ CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=dabe:0 ./dabe
```

 

#### 3、安装并实例化合约

新建 Terminal

``` bash
$ sudo docker exec -it cli bash
$ peer chaincode install -p chaincodedev/chaincode/2020-Winter-vacation-homework/dabe -n dabe -v 0
$ peer chaincode instantiate -n dabe -v 0 -c '{"Args":[]}' -C myc
$ peer chaincode install -p chaincodedev/chaincode/2020-Winter-vacation-homework/trustPlatform -n plat -v 0
$ peer chaincode instantiate -n plat -v 0 -c '{"Args":["true"]}' -C myc
```

注：如果想在关闭终端之后仍保持程序运行，可以在第2、3步创建的终端中使用 <kbd>Ctrl</kbd> + <kbd>P</kbd> + <kbd>Q</kbd> 命令退出容器



#### 4、运行java后端

新建 Terminal

```bash
$ cd /home/zdyf/fn/datashare-v8/
$ sudo nohup java -jar atp-0.0.1-SNAPSHOT.jar 2>&1 &
```



注：可从仓库中，下载前后端代码，访问本地 localhost:8080/abe 进入数据共享交换平台。**不要在服务器上直接改动区块链智能合约的相关代码，否则后果自负。服务器上底层区块链一直都在运行，可以直接调用。**

