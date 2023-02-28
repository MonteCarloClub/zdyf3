package data

import (
	"encoding/json"
	"fmt"
	"github.com/go-kratos/kratos/pkg/ecode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"log"
	"trustPlatform/constant"
	"trustPlatform/utils"
)

func init() {

	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// 根据uid查询用户
// ===================================================================================
func QueryUserByUid(uid string, stub shim.ChaincodeStubInterface) (user *User, err error) {
	log.Println("query user by uid: " + uid)
	bytes, err := stub.GetState(constant.IdPrefix + uid)
	if err != nil {
		return nil, err
	}
	if len(bytes) == 0 {
		return nil, nil
	}

	if err = json.Unmarshal(bytes, &user); err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 根据uid查询用户json
// ===================================================================================
func QueryUserJsonByUid(uid string, stub shim.ChaincodeStubInterface) (user []byte, err error) {
	log.Println("query user json by uid: " + uid)
	user, err = stub.GetState(constant.IdPrefix + uid)
	if err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 根据公钥查询用户
// ===================================================================================
func QueryUserByPublicKey(pk string, stub shim.ChaincodeStubInterface) (user *User, err error) {
	log.Println("query user by publicKey: " + pk)
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"%s\",\"publicKey\":\"%s\"}}", constant.User, pk)
	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	if resultsIterator.HasNext() {
		kv, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}

		if err = json.Unmarshal(kv.Value, &user); err != nil {
			return nil, err
		}
	}
	return
}

// ===================================================================================
// 根据公钥查询用户json
// ===================================================================================
func QueryUserJsonByPublicKey(pk string, stub shim.ChaincodeStubInterface) (user []byte, err error) {
	log.Println("query user by publicKey: " + pk)
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"%s\",\"publicKey\":\"%s\"}}", constant.User, pk)
	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	if resultsIterator.HasNext() {
		kv, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		return kv.Value, nil
	}
	return
}

func QueryUserApplyByAllConditions(fromUid, toUid, toOrgId, attrName string, stub shim.ChaincodeStubInterface) (apply *UserApply, err error) {
	var toId string
	if toOrgId != "" {
		toId = toOrgId
	} else {
		toId = toUid
	}
	log.Printf("query attr apply to %s from uid %s\n", toId, fromUid)
	bytes, err := stub.GetState(constant.AttrApplyPrefix + toId + ":" + fromUid + attrName)
	if err != nil {
		return nil, err
	}

	apply = new(UserApply)
	if err = json.Unmarshal(bytes, &apply); err != nil {
		return nil, nil
	}
	return
}

// ===================================================================================
// 查找属性申请
// ===================================================================================
func QueryUserAttrApplyBytes(fromUid, toUid, toOrgId string, status constant.AttrApplyStatus, stub shim.ChaincodeStubInterface) (apply [][]byte, err error) {
	log.Printf("query attr apply by from %s to %s: status = %d\n", fromUid, toUid, status)
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"%s\"", constant.AttrApply)
	if fromUid != "" {
		queryString += fmt.Sprintf(",\"fromUid\":\"%s\"", fromUid)
	}
	if toUid != "" {
		queryString += fmt.Sprintf(",\"toUid\":\"%s\"", toUid)
	}
	if toOrgId != "" {
		queryString += fmt.Sprintf(",\"toOrgId\":\"%s\"", toOrgId)
	}
	if status != constant.All {
		queryString += fmt.Sprintf(",\"status\":%d", status)
	}
	queryString += "}}"

	log.Println(queryString)
	if apply, err = utils.GetBytesFromDB(stub, queryString); err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 查找属性申请
// ===================================================================================
func QueryUserAttrApply(fromUid, toUid, toOrgId string, status constant.AttrApplyStatus, stub shim.ChaincodeStubInterface) (result []*UserApply, err error) {
	bytesArray, err := QueryUserAttrApplyBytes(fromUid, toUid, toOrgId, status, stub)
	if err != nil {
		return nil, err
	}
	result = make([]*UserApply, 0)
	for _, bytes := range bytesArray {
		temp := new(UserApply)
		err := json.Unmarshal(bytes, temp)
		if err != nil {
			return nil, err
		}
		result = append(result, temp)
	}
	return
}

// ===================================================================================
// 查找属性申请
// ===================================================================================
func GetSharedMessage(fromUid, tag string, pageSize int, bookmark string, stub shim.ChaincodeStubInterface) (result []byte, err error) {
	log.Printf("query shared message from %s with %s\n", fromUid, tag)
	if fromUid == "" && tag == "" {
		log.Println("cannot query all message")
		return nil, ecode.Error(ecode.RequestErr, "cannot query all message")
	}
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"%s\"", constant.SharedMessage)
	if fromUid != "" {
		queryString += fmt.Sprintf(",\"uid\":\"%s\"", fromUid)
	}
	if tag != "" {
		queryString += fmt.Sprintf(",\"tags\":{\"$elemMatch\":{\"$eq\":\"%s\"}}", tag)
	}
	queryString += "}}"

	log.Println(queryString)
	if result, err = utils.GetBytesFromDB2(stub, queryString, pageSize, bookmark); err != nil {
		return nil, err
	}
	return
}
