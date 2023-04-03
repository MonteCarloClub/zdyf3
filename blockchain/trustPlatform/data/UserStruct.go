package data

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"trustPlatform/constant"
)

type User struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 用户名
	Uid string `json:"uid"`
	// 用户公钥
	//PublicKey string `json:"publicKey"`
	// 声明属性集
	AttrSet []string `json:"attrSet"`
	// 用户属性集
	HoldAttrSet []string `json:"holdAttrSet"`
	// 用户ABE公钥
	UPK string `json:"upk"`
        //用户类型  user or org
	UserType string `json:"userType"`
        //用户所在通道
	Channel string `json:"channel"`
}

func NewUser(uid string, upk string,utype string,channel string) *User {
	return &User{ObjectType: constant.User, Uid: uid, UPK: upk,UserType:utype,Channel:channel}
}

type UserApply struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`

	FromUid   string             `json:"fromUid"`
	ToUid     string             `json:"toUid"`
	ToOrgId   string             `json:"toOrgId"`
	IsPublic  bool               `json:"isPublic"`
	AttrName  string             `json:"attrName"`
	Remark    string             `json:"remark"`
	T         int                `json:"t"`
	N         int                `json:"n"`
	ApplyType constant.ApplyType `json:"applyType"`

	// 状态
	Status constant.AttrApplyStatus `json:"status"`
	// 审批回复
	ApprovalMap map[string]*ApplyApproval `json:"approvalMap"`
}

type ApplyApproval struct {
	Agree         bool   `json:"agree"`
	Secret        string `json:"secret"`
	ApproveRemark string `json:"approveRemark"`
}

func NewUserApply(fromUid, toUid, toOrgId, attrName, remark string, isPublic bool, stub shim.ChaincodeStubInterface) (*UserApply, error) {
	applyType := constant.ToUser
	t := 1
	n := 1
	approvalMap := make(map[string]*ApplyApproval)
	if toUid == "" {
		applyType = constant.ToOrg
		org2, err := QueryOrgByOid(toOrgId, stub)
		if err != nil {
			return nil, err
		}
		t = org2.T
		n = org2.N
		for _, uid := range org2.UidSet {
			approvalMap[uid] = nil
		}
	} else {
		approvalMap[toUid] = nil
	}

	return &UserApply{
		ObjectType:  constant.AttrApply,
		FromUid:     fromUid,
		ToUid:       toUid,
		ToOrgId:     toOrgId,
		IsPublic:    isPublic,
		AttrName:    attrName,
		Remark:      remark,
		T:           t,
		N:           n,
		ApplyType:   applyType,
		Status:      constant.Pending,
		ApprovalMap: approvalMap,
	}, nil
}

type SharedMessage struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`

	Uid  string   `json:"uid"`
	Tags []string `json:"tags"`
	// 加密内容
	Content string `json:"content"`
        FileName string `json:"fileName"`
        TimeStamp string `json:"timeStamp"`
        Ip        string `json:"ip"`
	Location  string `json:"location"`
	Policy    string `json:"policy"`
}

func NewSharedMessage(uid, content string, fileName string, timeStamp string, tags []string, ip,location,policy string) *SharedMessage {
	return &SharedMessage{
		ObjectType: constant.SharedMessage,
		Uid:        uid,
		Tags:       tags,
		Content:    content,
                FileName:   fileName,
                TimeStamp:  timeStamp,
                Ip:         ip,
		Location:   location,
		Policy:     policy,
	}
}

type AttrHistory struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`

	Uid  string   `json:"uid"`

	// 授权用户
	FromUid string `json:"fromUid"`

	AttrName string `json:"attrName"`

	Operation string `json:"operation"`

	TimeStamp string `json:"timeStamp"`
}

func NewAttrHistory(uid, fromUid string, attrName string, operation string, timeStamp string) *AttrHistory {
	return &AttrHistory{
		ObjectType: constant.AttrHistory,
		Uid:        uid,
		FromUid:    fromUid,
		AttrName:   attrName,
		Operation: operation,
		TimeStamp:  timeStamp,
	}
}
