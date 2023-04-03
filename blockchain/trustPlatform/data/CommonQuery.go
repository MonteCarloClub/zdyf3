package data

import (
	"encoding/json"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"log"
	"trustPlatform/constant"
        "trustPlatform/utils"
        "fmt"
)

func init() {
	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// 根据attr name查询属性
// ===================================================================================
func QueryAttr(attrName string, stub shim.ChaincodeStubInterface) (attr *Attr, err error) {
	log.Println("query attr by name: " + attrName)
	bytes, err := QueryAttrBytes(attrName, stub)
	if err != nil {
		return nil, err
	}
	if len(bytes) == 0 {
		return nil, nil
	}

	if err = json.Unmarshal(bytes, &attr); err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 根据attr name查询属性
// ===================================================================================
func QueryAttrBytes(attrName string, stub shim.ChaincodeStubInterface) (attr []byte, err error) {
	log.Println("query attr by name: " + attrName)
	attr, err = stub.GetState(constant.AttrPrefix + attrName)
	if err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 查找通道列表
// ===================================================================================
func QueryChannelListBytes(stub shim.ChaincodeStubInterface) (apply [][]byte, err error) {
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"%s\"", constant.Channel)
	queryString += "}}"

	log.Println(queryString)
	if apply, err = utils.GetBytesFromDB(stub, queryString); err != nil {
		return nil, err
	}
	return
}


