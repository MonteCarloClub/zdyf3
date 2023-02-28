package data

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
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
// 保存用户
// ===================================================================================
func SaveUser(user *User, stub shim.ChaincodeStubInterface) (err error) {
	log.Println("save user with uid: " + user.Uid)
	bytes, err := json.Marshal(user)

	//TODO: test error
	if err != nil {
		return ecode.Errorf(ecode.AccessDenied, "test")
	}

	if err = stub.PutState(constant.IdPrefix+user.Uid, bytes); err != nil {
		return err
	}

        //更新用户/机构总数
	if user.UserType == "user"{
		if err=utils.UpdateTotalCount(stub,constant.TotalUserCount);err!=nil{
			return err
		}
	}else {
		if err=utils.UpdateTotalCount(stub,constant.TotalOrgCount);err!=nil{
			return err
		}
	}

	log.Println("save user with uid: " + user.Uid + " success")
	return
}

// ===================================================================================
// 保存用户属性，同时更新用户相关内容
// ===================================================================================
func SaveUserAttr(user *User, attr *Attr, stub shim.ChaincodeStubInterface) (err error) {
	log.Printf("save user attr %s with uid %s\n", attr.AttrName, attr.Id)
	// 更新user
	user.AttrSet = append(user.AttrSet, attr.AttrName)
	userBytes, err := json.Marshal(user)
	if err != nil {
		return err
	}

	if err = stub.PutState(constant.IdPrefix+user.Uid, userBytes); err != nil {
		return err
	}

	// 插入attr
	attrBytes, err := json.Marshal(attr)
	if err != nil {
		return err
	}

	if err = stub.PutState(constant.AttrPrefix+attr.AttrName, attrBytes); err != nil {
		return err
	}

        //更新属性总数
	if err = utils.UpdateTotalCount(stub,constant.TotalAttrCount);err!=nil{
		return err
	}
	//log.Printf("save user attr %s with uid %s success\n", attr.AttrName, attr.Id)
	return
}


// ===================================================================================
// 保存用户属性
// ===================================================================================
func SaveUserAttrOnly(attr *Attr, stub shim.ChaincodeStubInterface) (err error) {
	// 插入attr
	attrBytes, err := json.Marshal(attr)
	if err != nil {
		return err
	}

	if err = stub.PutState(constant.AttrPrefix+attr.AttrName, attrBytes); err != nil {
		return err
	}
	log.Printf("save user attr %s with uid %s success\n", attr.AttrName, attr.Id)
	return
}

// ===================================================================================
// 更新用户相关内容
// ===================================================================================
func UpdateUserAttr(user *User,stub shim.ChaincodeStubInterface) (err error) {
	// 更新user
	userBytes, err := json.Marshal(user)
	if err != nil {
		return err
	}

	if err = stub.PutState(constant.IdPrefix+user.Uid, userBytes); err != nil {
		return err
	}
	return
}


// ===================================================================================
// 保存用户对属性的申请
// ===================================================================================
func SaveUserAttrApply(apply *UserApply, stub shim.ChaincodeStubInterface) (err error) {
	var toId string
	if apply.ToUid != "" {
		toId = apply.ToUid
	} else {
		toId = apply.ToOrgId
	}
	//log.Printf("save attr apply to %s from uid %s\n", toId, apply.FromUid)

	applyBytes, err := json.Marshal(apply)
	if err != nil {
		return err
	}

	if err = stub.PutState(constant.AttrApplyPrefix+toId+":"+apply.FromUid+apply.AttrName, applyBytes); err != nil {
		return err
	}

	//log.Printf("save attr apply to %s from uid %s success\n", toId, apply.FromUid)
	return
}

// ===================================================================================
// 保存分享的信息
// ===================================================================================
func SaveSharedMessage(message *SharedMessage, stub shim.ChaincodeStubInterface) (err error) {
	log.Printf("save shared message from: %s with %s\n", message.Uid, message.Tags)

	messageBytes, err := json.Marshal(message)
	if err != nil {
		return err
	}

	padding := "0"
	var key string
	sth := []byte{1}
	for len(sth) != 0 {
		hash := sha256.Sum256([]byte(message.Content + padding))
		key = constant.SharedMessagePrefix + message.Uid + ":" + hex.EncodeToString(hash[:])
		log.Println("key: ", key)
		sth, _ = stub.GetState(key)
		padding += padding
	}

	if err = stub.PutState(key, messageBytes); err != nil {
		return err
	}

	log.Printf("save shared message from: %s with %s success\n", message.Uid, message.Tags)
	return
}
