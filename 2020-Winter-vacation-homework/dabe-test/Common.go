package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	DecentralizedABE "github.com/wjfn/DecentralizedABE2020/model"
	"log"
        "github.com/Nik-U/pbc"
	"os"
	"strconv"
)

// ===================================================================================
// common模块入口函数
// ===================================================================================
func (d *DABECC) CommonInvoke(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("DABECC Common Invoke")
	function, args := stub.GetFunctionAndParameters()

	if function == "/common/encrypt" {
		return d.encrypt(stub, args)
	} else if function == "/common/decrypt" {
		return d.decrypt(stub, args)
	} else if function == "/common/batchEncryptAndDecrypt" {
		return d.batchEncryptAndDecrypt(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"/common/encrypt\" \"/common/decrypt\"")
}

func (d *DABECC) encrypt(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	requestBytes := args[0]
	request := new(EncryptRequest)
	log.Println(requestBytes)
	if err := DecentralizedABE.Deserialize2Struct([]byte(requestBytes), request); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	authorities := make(map[string]DecentralizedABE.Authority)
	for key, value := range request.AuthorityMap {
		authorities[key] = value
	}
	cipher, err := d.Dabe.Encrypt(request.PlainContent, request.Policy, authorities)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	bytes, err := DecentralizedABE.Serialize2Bytes(cipher)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	return shim.Success(bytes)
}

func (d *DABECC) decrypt(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	requestBytes := args[0]
	request := new(DecryptRequest)
	if err := DecentralizedABE.Deserialize2Struct([]byte(requestBytes), request); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	cipher := new(DecentralizedABE.Cipher)
	if err := DecentralizedABE.Deserialize2Struct([]byte(request.Cipher), cipher); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	log.Printf("%+v\n", request.AttrMap)
	log.Printf("%+v\n", cipher)

	plainText, err := d.Dabe.Decrypt(cipher, request.AttrMap, request.Uid)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	return shim.Success(plainText)
}

//批量加密和解密
func (d *DABECC) batchEncryptAndDecrypt(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	uid:=args[0]
	decryptor:=args[1]
	baseAttr:=args[2]
	content:=args[3]

	and:=" AND "
	policy:="("
	apkMap:=make(map[string]*DecentralizedABE.APK)
	user, _:= QueryUserByUid(uid, stub)
	authorities:=make(map[string]DecentralizedABE.Authority)
	attrMap:=make(map[string]*pbc.Element)
	for i:=0;i<65536;i++ {
		cur:=baseAttr+strconv.Itoa(i)
		policy+=cur+and
		attr, err := QueryAttrByName(cur, stub)
		if err != nil {
			return shim.Error(err.Error())
		}
		gy,_:=d.Dabe.G.NewFieldElement().SetString(attr.APK,10)
		attrMap[cur]=gy
		apk := &DecentralizedABE.APK{Gy:gy}
		apkMap[cur]=apk
	}
	policy+=")"
	pk,_:=d.Dabe.CurveParam.Pairing.NewUncheckedElement(2).SetString(user.UPK,10)
	authority:=&Authority{PK:pk,APKMap:apkMap}
	authorities[uid]=authority

	cipher, err := d.Dabe.Encrypt(content, policy, authorities)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	bytes, err := DecentralizedABE.Serialize2Bytes(cipher)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	//将密文保存到文件
	file,err:=os.Create("cipher")
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	defer file.Close()
	_,err = file.Write(bytes)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	//解密
	plainText, err :=d.Dabe.Decrypt(cipher, attrMap, decryptor)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success(plainText)
}



