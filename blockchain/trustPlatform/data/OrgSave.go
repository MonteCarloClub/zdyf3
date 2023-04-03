package data

import (
	"encoding/json"
	"github.com/go-kratos/kratos/pkg/ecode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"log"
	"trustPlatform/constant"
)

func init() {
	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// 保存组织申请请求
// ===================================================================================
func SaveOrgApply(apply *OrgApply, stub shim.ChaincodeStubInterface) (err error) {
	log.Println("save org apply with oid: " + apply.OrgId)
	bytes, err := json.Marshal(apply)
	if err != nil {
		return ecode.Errorf(ecode.ServerErr, "marshal apply error")
	}

	if err = stub.PutState(constant.OrgApplyPrefix+apply.OrgId+":"+apply.FromUid, bytes); err != nil {
		return err
	}
	log.Println("save org apply with orgId: " + apply.OrgId + " success")
	return
}



// ===================================================================================
// 保存组织属性申请请求
// ===================================================================================
func SaveOrgAttrApply(apply *OrgApply, stub shim.ChaincodeStubInterface) (err error) {
	log.Println("save org attr apply with oid: " + apply.OrgId)
	bytes, err := json.Marshal(apply)
	if err != nil {
		return ecode.Errorf(ecode.ServerErr, "marshal apply error")
	}

	if err = stub.PutState(constant.OrgAttrApplyPrefix+apply.OrgId+":"+apply.FromUid, bytes); err != nil {
		return err
	}
	log.Println("save org attr apply with orgId: " + apply.OrgId + " success")
	return
}



// ===================================================================================
// 保存组织
// ===================================================================================
func SaveOrg(org *Org, stub shim.ChaincodeStubInterface) (err error) {
	log.Println("save org with oid: " + org.OrgId)
	bytes, err := json.Marshal(org)
	if err != nil {
		return ecode.Errorf(ecode.ServerErr, "marshal org error")
	}

	if err = stub.PutState(constant.IdPrefix+org.OrgId, bytes); err != nil {
		return err
	}
	log.Println("save org with orgId: " + org.OrgId + " success")
	return
}

// ===================================================================================
// 保存组织属性，同时更新组织相关内容
// ===================================================================================
func SaveOrgAttr(org *Org, attr *Attr, stub shim.ChaincodeStubInterface) (err error) {
	log.Printf("save org attr %s with uid %s\n", attr.AttrName, attr.Id)
	// 更新org
	org.AttrSet = append(org.AttrSet, attr.AttrName)
	if err := SaveOrg(org, stub); err != nil {
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
	log.Printf("save org attr %s with orgId %s success\n", attr.AttrName, attr.Id)
	return
}
