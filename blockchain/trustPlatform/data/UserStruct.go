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
	PublicKey string `json:"publicKey"`
	// 声明属性集
	AttrSet []string `json:"attrSet"`
	// 用户属性集
	HoldAttrSet []string `json:"holdAttrSet"`
	// 用户ABE公钥
	UPK string `json:"upk"`
}

func NewUser(uid string, publicKey string, upk string) *User {
	return &User{ObjectType: constant.User, Uid: uid, PublicKey: publicKey, UPK: upk}
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
}

func NewSharedMessage(uid, content string, tags []string) *SharedMessage {
	return &SharedMessage{
		ObjectType: constant.SharedMessage,
		Uid:        uid,
		Tags:       tags,
		Content:    content,
	}
}
