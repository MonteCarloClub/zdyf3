# Abe-trustPlatform

这里是区块链部分

## 底层区块链智能合约部署说明

服务器：10.176.40.46(仅限校园网内)

用户名：zdyf   密码：zdyf123！ 用户名：root  密码：39YwW8xh

### 一键式部署环境

cd /home/zdyf   sudo ./setup.sh 会自动开启几个后端。

### 具体部署环境说明

#### 1、启动Fabric环境

cd /home/zdyf/fn/go/src/github.com/hyperledger/fabric/scripts/fabric-samples/chaincode-docker-devmode

sudo docker-compose -f docker-compose-couch.yaml up -d

 

执行docker ps可以看到一个ccenv容器、一个cli节点、一个peer节点、一个orderer排序节点和一个couchdb数据库。

​                               

若没有全部启动，可以通过sudo docker-compose -f docker-compose-couch.yaml down停止程序后重新启动。

 

#### 2、运行合约程序

sudo docker exec -it chaincode bash

cd 2020-Winter-vacation-homework/trustPlatform

CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=plat:0 ./trustPlatform

 

新建terminal

sudo docker exec -it chaincode bash

cd 2020-Winter-vacation-homework/dabe

CORE_PEER_ADDRESS=peer:7052 CORE_CHAINCODE_ID_NAME=dabe:0 ./dabe

 

#### 3、安装并实例化合约

新建terminal

sudo docker exec -it cli bash

peer chaincode install -p chaincodedev/chaincode/2020-Winter-vacation-homework/dabe -n dabe -v 0

peer chaincode instantiate -n dabe -v 0 -c '{"Args":[]}' -C myc

peer chaincode install -p chaincodedev/chaincode/2020-Winter-vacation-homework/trustPlatform -n plat -v 0

peer chaincode instantiate -n plat -v 0 -c '{"Args":["true"]}' -C myc

注：如果想在关闭终端之后仍保持程序运行，可以在第2、3步创建的终端中使用Ctrl+P+Q 命令退出容器



#### 4、运行java后端

新建terminal

cd /home/zdyf/fn/datashare-v8/

sudo nohup java -jar atp-0.0.1-SNAPSHOT.jar 2>&1 &

 

#### 5.运行前端

新建terminal

cd /home/zdyf/datashare/

sudo npm run dev



访问前端 http://10.176.40.46/#/ 可查看目前数据共享与交换平台。

注：可从仓库中，下载前后端代码，访问本地 localhost:8080/abe 进入数据共享交换平台。**不要在服务器上直接改动区块链智能合约的相关代码，否则后果自负。服务器上底层区块链一直都在运行，可以直接调用。**

