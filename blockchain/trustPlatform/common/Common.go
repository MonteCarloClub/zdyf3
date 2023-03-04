package common

import (
	"encoding/json"
	"github.com/go-kratos/kratos/pkg/ecode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"log"
	"strings"
	"trustPlatform/data"
	"trustPlatform/request"
	"trustPlatform/utils"
)

func init() {
	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// common初始化函数
// ===================================================================================
func Init(stub shim.ChaincodeStubInterface) {
	log.Println("Common init")
}

// ===================================================================================
// common模块入口函数
// ===================================================================================
func Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("TrustPlatformCC Common Invoke")
	function, args := stub.GetFunctionAndParameters()
	if err := utils.CheckInputNumber(1, args); err != nil {
		return shim.Error(err.Error())
	}
	if strings.HasPrefix(function, "/common/getAttr") {
		return getAttr(stub, args)
	} else if strings.HasPrefix(function, "/common/shareMessage") {
		return shareMessage(stub, args)
	} else if strings.HasPrefix(function, "/common/getMessage") {
		return getMessage(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"/common/getAttr\" ")
}

func getAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	attrName := args[0]
	attr, err := data.QueryAttrBytes(attrName, stub)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	log.Println(string(attr))
	return shim.Success(attr)
}

// ===================================================================================
// 分享信息
// ===================================================================================
func shareMessage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("enter shareMessage")
	// 反序列化请求，验签
	var requestStr = args[0]
	shareRequest := new(request.ShareMessageRequest)
	if err := json.Unmarshal([]byte(requestStr), shareRequest); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	if err := preCheckRequest(requestStr, shareRequest.Uid, shareRequest.Sign, stub); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	if len(shareRequest.Tags) > 10 {
		return shim.Error("too much tags")
	}

	message := data.NewSharedMessage(shareRequest.Uid, shareRequest.Content, shareRequest.Tags)
	if err := data.SaveSharedMessage(message, stub); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// ===================================================================================
// 获得信息
// ===================================================================================
func getMessage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("get message")
	// 反序列化请求
	var requestStr = args[0]
	log.Println(requestStr)
	getRequest := new(request.GetSharedMessageRequest)
	if err := json.Unmarshal([]byte(requestStr), getRequest); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	result, err := data.GetSharedMessage(getRequest.FromUid, getRequest.Tagup, getRequest.PageSize, getRequest.Bookmark, stub)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success(result)
}

// ===================================================================================
// 检查请求参数并验签
// ===================================================================================
func preCheckRequest(requestStr string, uid, sign string, stub shim.ChaincodeStubInterface) error {
	requestJson, err := utils.GetRequestParamJson([]byte(requestStr))
	if err != nil {
		log.Println(err)
		return err
	}
	requestUser, err := data.QueryUserByUid(uid, stub)
	if err != nil {
		log.Println(err)
		return err
	}
	if requestUser == nil {
		log.Println("don't have requestUser with uid " + uid)
		return ecode.Error(ecode.RequestErr, "don't have this requestUser")
	}
	if err = utils.VerifySign(string(requestJson), requestUser.PublicKey, sign); err != nil {
		log.Println(err)
		return err
	}
	return nil
}
