## 说明

服务器：10.176.40.46（仅限校园网内，或者挂 VPN）

+ 用户名：`zdyf`，密码：`zdyf123!`
+ 用户名：`root`，密码：`39YwW8xh`

### 一键式部署环境

```bash
cd /home/zdyf
sudo ./setup.sh
```

会自动开启几个后端

```
#!/bin/bash
echo "1.Setup Fabric"
cd /home/zdyf/fn/go/src/github.com/hyperledger/fabric/scripts/fabric-samples/chaincode-docker-devmode
docker-compose -f docker-compose-couch.yaml up -d

trustplatform="CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=plat:0 ./trustPlatform"
dabe="CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=dabe:0 ./dabe"


echo "2.Run Fabric"
gnome-terminal -x docker exec -it chaincode bash -c "cd data_share/current/trustPlatform && $trustplatform"

gnome-terminal -x docker exec -it chaincode bash -c "cd data_share/current/dabe && $dabe"



echo "3.Instantiate Fabric"
gnome-terminal -x docker exec -it cli bash -c "peer chaincode install -p chaincodedev/chaincode/data_share/current/dabe -n dabe -v 0;peer chaincode instantiate -n dabe -v 0 -c '{"Args":[]}' -C myc"
echo "正在等待链码实例化完成，等待5秒"
sleep 5

echo "智能合约dabe部署成功"
gnome-terminal -x docker exec -it cli bash -c "peer chaincode install -p chaincodedev/chaincode/data_share/current/trustPlatform -n plat -v 0;peer chaincode instantiate -n plat -v 0 -c '{"Args":["true"]}' -C myc"

echo "正在等待链码实例化完成，等待5秒"
sleep 5
echo "智能合约plat部署成功"
```



### 具体部署环境说明

#### 1、启动Fabric环境

```bash
cd /home/zdyf/fn/go/src/github.com/hyperledger/fabric/scripts/fabric-samples/chaincode-docker-devmode
sudo docker-compose -f docker-compose-couch.yaml up -d
```

执行 `docker ps` 可以看到一个 ccenv 容器、一个 cli 节点、一个 peer 节点、一个 orderer 排序节点和一个 couchdb 数据库。

若没有全部启动，可以通过

`sudo docker-compose -f docker-compose-couch.yaml down`

停止程序后重新启动

 

#### 2、运行合约程序

```bash
sudo docker exec -it chaincode bash
cd data_share/current/trustPlatform
CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=plat:0 ./trustPlatform
```

新建 Terminal

```bash
sudo docker exec -it chaincode bash
cd data_share/current/dabe
CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=dabe:0 ./dabe
```

 

#### 3、安装并实例化合约

新建 Terminal

``` bash
sudo docker exec -it cli bash
peer chaincode install -p chaincodedev/chaincode/data_share/current/dabe -n dabe -v 0
peer chaincode instantiate -n dabe -v 0 -c '{"Args":[]}' -C myc
peer chaincode install -p chaincodedev/chaincode/data_share/current/trustPlatform -n plat -v 0
peer chaincode instantiate -n plat -v 0 -c '{"Args":["true"]}' -C myc

```

注：如果想在关闭终端之后仍保持程序运行，可以在第2、3步创建的终端中使用 <kbd>Ctrl</kbd> + <kbd>P</kbd> + <kbd>Q</kbd> 命令退出容器

**注：不要在服务器上直接改动区块链智能合约的相关代码，否则后果自负。服务器上底层区块链一直都在运行，可以直接调用。**



# 指标测试

**针对指标：**可管理数据源属性>2^16级维度

 

**步骤：**

新建terminal

```
sudo docker exec -it cli bash

peer chaincode invoke -n dabe -c '{"Args":["/common/batch","someone","someone:a","5","or","this is a test message"]}' -C myc
```

 

**说明：**该操作会自动进行加解密操作（自动创建用户、生成属性、加解密）

Args[0]：用户名

Args[1]：基本属性名

Args[2]：访问控制连接符（and 或 or）

Args[3]：要加密的消息

