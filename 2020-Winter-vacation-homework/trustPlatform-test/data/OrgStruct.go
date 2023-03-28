package data

import (
	"strconv"
	"time"
	"trustPlatform/constant"
)

type Org struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 组织名
	OrgId string `json:"orgId"`
	// 声明属性集
	UidSet []string `json:"uidSet"`
	// 声明属性集
	AttrSet []string `json:"attrSet"`
	// 阈值
	T int `json:"t"`
	// 成员总数
	N int `json:"n"`
	// 组织ABE公钥
	OPK string `json:"opk"`
}

func NewOrg(orgId string, t, n int, uidSet []string, opk string) *Org {
	return &Org{
		ObjectType: constant.Org,
		OrgId:      orgId,
		UidSet:     uidSet,
		AttrSet:    make([]string, 0),
		T:          t,
		N:          n,
		OPK:        opk,
	}
}

type ApplyStatus int

const (
	PendingApprove ApplyStatus = iota
	PendingShare
	Success
	Fail
	Pending
)

type OrgApply struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 组织名
	OrgId string `json:"orgId"`
	// 指定成员
	UidMap map[string]bool `json:"uidMap"`
	// 阈值
	T int `json:"t"`
	// 成员总数
	N int `json:"n"`
	// 发起人UID
	FromUid string `json:"fromUid"`
	// 状态, active success fail
	Status ApplyStatus `json:"status"`
	// 创建时间
	CreateTime string `json:"createTime"`
	// 秘密 map[from]map[to]share
	ShareMap map[string]map[string]string `json:"shareMap"`
	// 部分opk map[from]opk_i
	OpkMap map[string]string `json:"opkMap"`
	// 类别
	Type constant.OrgApplyType `json:"type"`
	// 属性名称，申请组织时可以为空
	AttrName string `json:"attrName"`
}

func NewOrgApply(orgId, fromUid string, uidList []string, t, n int, applyType constant.OrgApplyType, attrName string) *OrgApply {
	uidMap := make(map[string]bool, n)
	shareMap := make(map[string]map[string]string, n)
	for _, uid := range uidList {
		uidMap[uid] = false
		shareMap[uid] = make(map[string]string, n-1)
	}
	uidMap[fromUid] = true
	return &OrgApply{
		ObjectType: constant.OrgApply,
		OrgId:      orgId,
		UidMap:     uidMap,
		T:          t,
		N:          n,
		FromUid:    fromUid,
		Status:     PendingApprove,
		CreateTime: strconv.FormatInt(time.Now().Unix(), 10),
		ShareMap:   shareMap,
		OpkMap:     make(map[string]string),
		Type:       applyType,
		AttrName:   attrName,
	}
}

type OrgShare struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 组织名
	OrgId string `json:"orgId"`
	// 秘密参数
	Secret string `json:"secret"`
	// 发起人
	FromUid string `json:"fromUid"`
	// 接收人
	ToUid string `json:"toUid"`
}
