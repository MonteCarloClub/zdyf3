package data

import (
	"encoding/json"
	"fmt"
	"github.com/go-kratos/kratos/pkg/ecode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"log"
	"time"
	"trustPlatform/constant"
	"trustPlatform/utils"
)

func init() {

	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// 根据orgId查询非fail状态的请求，如果有的话检查是否经过了3天，是的话将其置为fail，返回空
// ===================================================================================
func QueryCreateOrgApply(orgId string, status ApplyStatus, stub shim.ChaincodeStubInterface) (apply *OrgApply, err error) {
	log.Println("query active apply: " + orgId)
	if status == Fail {
		return nil, ecode.Error(ecode.RequestErr, "can't query fail apply")
	}
	var queryString string
	if status == Pending {
		queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"%s\",\"orgId\":\"%s\",\"type\":0,\"status\":{\"$lt\":\"%d\"}}}",
			constant.OrgApply, orgId, Success)
	} else {
		queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"%s\",\"orgId\":\"%s\",\"type\":0,\"status\":%d}}",
			constant.OrgApply, orgId, status)
	}
	log.Println(queryString)
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
		if err = json.Unmarshal(kv.Value, &apply); err != nil {
			return nil, err
		}

		if err = utils.CheckTimeWithin(apply.CreateTime, 3, time.Hour*24); err != nil {
			apply.Status = Fail
			err = SaveOrgApply(apply, stub)
			return nil, err
		}
	}
	return
}

// ===================================================================================
// 根据org attr name查询非fail状态的请求，如果有的话检查是否经过了3天，是的话将其置为fail，返回空
// ===================================================================================
func QueryDeclareOrgAttrApply(attrName string, status ApplyStatus, stub shim.ChaincodeStubInterface) (apply *OrgApply, err error) {
	log.Println("query active declare org attr apply: " + attrName)
	if status == Fail {
		return nil, ecode.Error(ecode.RequestErr, "can't query fail apply")
	}
	var queryString string
	if status == Pending {
		queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"%s\",\"attrName\":\"%s\",\"type\":1,\"status\":{\"$lt\":\"%d\"}}}",
			constant.OrgApply, attrName, Success)
	} else {
		queryString = fmt.Sprintf("{\"selector\":{\"docType\":\"%s\",\"attrName\":\"%s\",\"type\":1,\"status\":%d}}",
			constant.OrgApply, attrName, status)
	}
	log.Println(queryString)
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
		if err = json.Unmarshal(kv.Value, &apply); err != nil {
			return nil, err
		}

		if err = utils.CheckTimeWithin(apply.CreateTime, 3, time.Hour*24); err != nil {
			apply.Status = Fail
			err = SaveOrgApply(apply, stub)
			return nil, err
		}
	}
	return
}

// ===================================================================================
// 根据oid查询org bytes
// ===================================================================================
func QueryOrgBytesByOid(orgId string, stub shim.ChaincodeStubInterface) (org []byte, err error) {
	log.Println("query org bytes by orgId: " + orgId)
	org, err = stub.GetState(constant.IdPrefix + orgId)
	if err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 根据oid查询org
// ===================================================================================
func QueryOrgByOid(orgId string, stub shim.ChaincodeStubInterface) (org *Org, err error) {
	log.Println("query org by orgId: " + orgId)
	bytes, err := QueryOrgBytesByOid(orgId, stub)
	if err != nil {
		return nil, err
	}
	org = new(Org)
	if err = json.Unmarshal(bytes, org); err != nil {
		return nil, err
	}
	return
}
