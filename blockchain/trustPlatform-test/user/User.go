package user

import (
	"encoding/json"
	"github.com/go-kratos/kratos/pkg/ecode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"log"
	"strconv"
	"strings"
	"time"
	"trustPlatform/constant"
	"trustPlatform/data"
	"trustPlatform/request"
	"trustPlatform/utils"
)

func init() {

	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// user合约初始化函数
// ===================================================================================
func Init(stub shim.ChaincodeStubInterface) {
	log.Println("User init")
}

// ===================================================================================
// user模块入口函数
// ===================================================================================
func Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("TrustPlatformCC User Invoke")
	function, args := stub.GetFunctionAndParameters()
	if err := utils.CheckInputNumber(1, args); err != nil {
		return shim.Error(err.Error())
	}
	if strings.HasPrefix(function, "/user/create") {
		return create(stub, args)
	} else if strings.HasPrefix(function, "/user/declareAttr") {
		return declareAttr(stub, args)
	} else if strings.HasPrefix(function, "/user/getUser") {
		return getUser(stub, args)
	} else if strings.HasPrefix(function, "/user/applyAttr") {
		return applyAttr(stub, args)
	} else if strings.HasPrefix(function, "/user/getAttrApply") {
		return getAttrApply(stub, args)
	} else if strings.HasPrefix(function, "/user/approveAttrApply") {
		return approveAttrApply(stub, args)
	} else if strings.HasPrefix(function, "/user/batchDeclareAttr") {
		return batchDeclareAttr(stub, args)
	} else if strings.HasPrefix(function, "/user/batchApplyAttr") {
		return batchApplyAttr(stub, args)
	} else if strings.HasPrefix(function, "/user/batchApproveAttrApply") {
		return batchApproveAttrApply(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"/user/create\" \"/user/declareAttr\"" +
		" \"/user/getUser\" \"/user/applyAttr\" \"/user/getAttrApply\" \"/user/approveAttrApply\"")
}

// ===================================================================================
// 审批属性申请
// ===================================================================================
func approveAttrApply(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("approve attr apply")
	// 反序列化请求，验签
	var requestStr = args[0]
	log.Println(requestStr)
	approveRequest := new(request.ApproveAttrApplyRequest)
	if err := json.Unmarshal([]byte(requestStr), approveRequest); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	if err := preCheckRequest(requestStr, approveRequest.Uid, approveRequest.Sign, stub); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	fromUid := approveRequest.FromUid
	toUid := approveRequest.Uid
	toOrgId := approveRequest.ToOrgId
	attrName := approveRequest.AttrName
	remark := approveRequest.Remark
	secret := approveRequest.Secret
	agree := approveRequest.Agree

	if agree && secret == "" {
		log.Println("request params error")
		return shim.Error("request params error")
	}
	apply, err := data.QueryUserApplyByAllConditions(fromUid, toUid, toOrgId, attrName, stub)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	if apply.Status != constant.Pending {
		log.Println("apply status error")
		return shim.Error("apply status error")
	}

	if apply.ApplyType == constant.ToUser {
		if agree {
			apply.Status = constant.Success
		} else {
			apply.Status = constant.Fail
		}
	} else {
		if _, ok := apply.ApprovalMap[toUid]; !ok {
			log.Println("user not permitted")
			return shim.Error("user not permitted")
		}
		agreeNum := 0
		disagreeNum := 0
		for _, approval := range apply.ApprovalMap {
			if approval != nil {
				if approval.Agree {
					agreeNum++
				} else {
					disagreeNum++
				}
			}
		}
		if agree {
			agreeNum++
		} else {
			disagreeNum++
		}
		if agreeNum >= apply.T {
			apply.Status = constant.Success
		} else if disagreeNum > apply.N-apply.T {
			apply.Status = constant.Fail
		}
	}
	apply.ApprovalMap[toUid] = new(data.ApplyApproval)
	apply.ApprovalMap[toUid].Agree = agree
	apply.ApprovalMap[toUid].ApproveRemark = remark
	apply.ApprovalMap[toUid].Secret = secret

	if err := data.SaveUserAttrApply(apply, stub); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// ===================================================================================
// 查询给自己（uid）的申请
// ===================================================================================
func getAttrApply(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("get attr apply")
	var requestStr = args[0]

	applyRequest := new(request.GetAttrApplyRequest)
	if err := json.Unmarshal([]byte(requestStr), applyRequest); err != nil {
		return shim.Error(err.Error())
	}
	if applyRequest.FromUid == "" && applyRequest.ToOrgId == "" && applyRequest.ToUid == "" {
		return shim.Error("invalid request")
	}

	bytesArray, err := data.QueryUserAttrApplyBytes(applyRequest.FromUid, applyRequest.ToUid, applyRequest.ToOrgId, applyRequest.Status, stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	buffer := utils.GetListResponse(bytesArray)

	log.Println(string(buffer.Bytes()))
	return shim.Success(buffer.Bytes())
}

// ===================================================================================
// 申请属性
// ===================================================================================
func applyAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("apply attr")
	// 反序列化请求，验签
	var requestStr = args[0]
	log.Println(requestStr)
	applyRequest := new(request.ApplyAttrRequest)
	if err := json.Unmarshal([]byte(requestStr), applyRequest); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	if err := preCheckRequest(requestStr, applyRequest.Uid, applyRequest.Sign, stub); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	fromUid := applyRequest.Uid
	toUid := applyRequest.ToUid
	toOrgId := applyRequest.ToOrgId
	isPublic := applyRequest.IsPublic
	attrName := applyRequest.AttrName
	remark := applyRequest.Remark

	var toId string
	if toUid == "" {
		toId = toOrgId
	} else {
		toId = toUid
	}
	if toId == "" {
		log.Println("no to id")
		return shim.Error("no to id")
	}
	if err := utils.CheckAttr(attrName, toId); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	apply, err := data.NewUserApply(fromUid, toUid, toOrgId, attrName, remark, isPublic, stub)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	if err := data.SaveUserAttrApply(apply, stub); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// ===================================================================================
// 根据uid或公钥查询用户
// ===================================================================================
func getUser(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("get user")
	var requestStr = args[0]
	getUserRequest := new(request.GetUserRequest)
	if err := json.Unmarshal([]byte(requestStr), getUserRequest); err != nil {
		return shim.Error(err.Error())
	}
	if getUserRequest.Uid != "" {
		user, err := data.QueryUserJsonByUid(getUserRequest.Uid, stub)
		if err != nil {
			return shim.Error(err.Error())
		}
		return shim.Success(user)
	} else if getUserRequest.PublicKey != "" {
		user, err := data.QueryUserJsonByPublicKey(getUserRequest.PublicKey, stub)
		if err != nil {
			return shim.Error(err.Error())
		}
		return shim.Success(user)
	}
	return shim.Error("request error")
}

// ===================================================================================
// 创建一个新用户
// ===================================================================================
func create(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("create new user start")

	var requestStr = args[0]
	initRequest := new(request.UserInitRequest)
	if err := json.Unmarshal([]byte(requestStr), initRequest); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	pj, err := utils.GetRequestParamJson([]byte(requestStr))
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	if err = utils.VerifySign(string(pj), initRequest.PublicKey, initRequest.Sign); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	uid := initRequest.Uid
	publicKey := initRequest.PublicKey
	upk := initRequest.UPK
        utype:= initRequest.UserType

	if err := utils.CheckId(uid); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	if utils.ExistId(uid, stub) {
		log.Printf("uid %s already exists\n", uid)
		return shim.Error("uid already exists")
	}

	user, err := data.QueryUserByPublicKey(publicKey, stub)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	if user != nil {
		log.Printf("publicKey %s already exists\n", publicKey)
		return shim.Error("publicKey already exists")
	}

	user = data.NewUser(uid, publicKey, upk,utype)
	if err = data.SaveUser(user, stub); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

// ===================================================================================
// 用户声明新属性
// ===================================================================================
func declareAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user declare new attr start")

	var requestStr = args[0]
	attrRequest := new(request.UserAnnounceAttrRequest)
	if err := json.Unmarshal([]byte(requestStr), attrRequest); err != nil {
		return shim.Error(err.Error())
	}
	pj, err := utils.GetRequestParamJson([]byte(requestStr))
	if err != nil {
		return shim.Error(err.Error())
	}

	uid := attrRequest.Uid
	attrName := attrRequest.AttrName
	timestamp := attrRequest.Timestamp
	apk := attrRequest.APK
	sign := attrRequest.Sign

	if err := utils.CheckAttr(attrName, uid); err != nil {
		return shim.Error(err.Error())
	}

	if err := utils.CheckTimeWithin(timestamp, 5, time.Minute); err != nil {
		return shim.Error(err.Error())
	}

	user, err := data.QueryUserByUid(uid, stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	if user == nil {
		log.Println("don't have user with uid " + uid)
		return shim.Error("don't have this user")
	}

	if err = utils.VerifySign(string(pj), user.PublicKey, sign); err != nil {
		return shim.Error(err.Error())
	}

	for _, attr := range user.AttrSet {
		if attr == attrName {
			log.Printf("user %s already has attr %s\n", uid, attrName)
			return shim.Error("user already has attr")
		}
	}

	newAttr := data.NewAttr(uid, attrName, apk)
	if err = data.SaveUserAttr(user, newAttr, stub); err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
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


// ===================================================================================
// 批量声明新属性
// ===================================================================================
func batchDeclareAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user batch declare new attr start")

	var requestStr = args[0]
	attrRequest := new(request.UserAnnounceAttrRequest)
	if err := json.Unmarshal([]byte(requestStr), attrRequest); err != nil {
		return shim.Error(err.Error())
	}

	uid := attrRequest.Uid
	attrName := attrRequest.AttrName
	apk := attrRequest.APK

	user, err := data.QueryUserByUid(uid, stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	if user == nil {
		log.Println("don't have user with uid " + uid)
		return shim.Error("don't have this user")
	}


	for i := 0;i<65536;i++{
		newAttrName := attrName+strconv.Itoa(i)
		newAttr := data.NewAttr(uid, newAttrName, apk)
		user.AttrSet = append(user.AttrSet, newAttr.AttrName)
		if err = data.SaveUserAttrOnly(newAttr, stub); err != nil {
			return shim.Error(err.Error())
		}
	}

        //更新属性总数
	totalCntByte, err := stub.GetState(constant.TotalAttrCount)
	if err != nil {
		return shim.Error(err.Error())
	}
	totalCnt:=1
	if len(totalCntByte) != 0 {
		str := string(totalCntByte)
		totalCnt,err = strconv.Atoi(str)
		if err != nil{
			return shim.Error(err.Error())
		}
		totalCnt+=65536
	}
	str := strconv.Itoa(totalCnt)
	if err = stub.PutState(constant.TotalAttrCount,[]byte(str)); err != nil{
		return shim.Error(err.Error())
	}


	//更新用户相关内容
	if err = data.UpdateUserAttr(user,stub); err != nil {
		return shim.Error(err.Error())
	}

	log.Println("user batch declare new attr success")
	return shim.Success(nil)
}

// ===================================================================================
// 批量申请属性
// ===================================================================================
func batchApplyAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("bathc apply attr start")
	// 反序列化请求，验签
	var requestStr = args[0]
	//log.Println(requestStr)
	applyRequest := new(request.ApplyAttrRequest)
	if err := json.Unmarshal([]byte(requestStr), applyRequest); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	fromUid := applyRequest.Uid
	toUid := applyRequest.ToUid
	toOrgId := applyRequest.ToOrgId
	isPublic := applyRequest.IsPublic
	attrName := applyRequest.AttrName
	remark := applyRequest.Remark

	for i:=0;i<65536;i++ {
		newAttrName:=attrName+strconv.Itoa(i)
		apply, err := data.NewUserApply(fromUid, toUid, toOrgId, newAttrName, remark, isPublic, stub)
		if err != nil {
			log.Println(err)
			return shim.Error(err.Error())
		}
		if err := data.SaveUserAttrApply(apply, stub); err != nil {
			log.Println(err)
			return shim.Error(err.Error())
		}
	}
	log.Println("bathc apply attr success")
	return shim.Success(nil)
}

// ===================================================================================
// 批量审批属性申请
// ===================================================================================
func batchApproveAttrApply(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("batch approve attr apply start")
	// 反序列化请求，验签
	var requestStr = args[0]
	//log.Println(requestStr)
	approveRequest := new(request.ApproveAttrApplyRequest)
	if err := json.Unmarshal([]byte(requestStr), approveRequest); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	fromUid := approveRequest.FromUid
	toUid := approveRequest.Uid
	toOrgId := approveRequest.ToOrgId
	attrName := approveRequest.AttrName
	remark := approveRequest.Remark
	secret := approveRequest.Secret
	agree := approveRequest.Agree

	for i:=0;i<65536;i++ {
		newAttrName:=attrName+strconv.Itoa(i)
		apply, err := data.QueryUserApplyByAllConditions(fromUid, toUid, toOrgId, newAttrName, stub)
		if err != nil {
			log.Println(err)
			return shim.Error(err.Error())
		}
		apply.Status = constant.Success

		apply.ApprovalMap[toUid] = new(data.ApplyApproval)
		apply.ApprovalMap[toUid].Agree = agree
		apply.ApprovalMap[toUid].ApproveRemark = remark
		apply.ApprovalMap[toUid].Secret = secret

		if err := data.SaveUserAttrApply(apply, stub); err != nil {
			log.Println(err)
			return shim.Error(err.Error())
		}
	}
	log.Println("batch approve attr apply success")
	return shim.Success(nil)
}


