package main

import (
	"encoding/json"
	"github.com/Nik-U/pbc"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	DecentralizedABE "github.com/wjfn/DecentralizedABE2020/model"
	"log"
	"os"
	"strconv"
        "time"
)



// ===================================================================================
// 批量声明新属性
// ===================================================================================
func batchDeclareAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user batch declare new attr start")

	var requestStr = args[0]
	attrRequest := new(UserAnnounceAttrRequest)
	if err := json.Unmarshal([]byte(requestStr), attrRequest); err != nil {
		return shim.Error(err.Error())
	}

	uid := attrRequest.Uid
	attrName := attrRequest.AttrName
	apk := attrRequest.APK

	user, err := QueryUserByUid(uid, stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	if user == nil {
		log.Println("don't have user with uid " + uid)
		return shim.Error("don't have this user")
	}


	for i := 0;i<65536;i++{
		newAttrName := attrName+strconv.Itoa(i)
		newAttr := NewAttr(uid, newAttrName, apk)
		user.AttrSet = append(user.AttrSet, newAttr.AttrName)
		if err = SaveUserAttrOnly(newAttr, stub); err != nil {
			return shim.Error(err.Error())
		}
	}

	//更新用户相关内容
	if err = UpdateUserAttr(user,stub); err != nil {
		return shim.Error(err.Error())
	}

	log.Println("user batch declare new attr success")
	return shim.Success(nil)
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
	for i:=0;i<65535;i++ {
		cur:=baseAttr+strconv.Itoa(i)
		if i==65535{
			policy+=cur
		} else {
			policy=policy+cur+and
		}
		log.Println("cur attr: "+cur)
		attr, err := QueryAttrByName(cur, stub)
		if err != nil {
			return shim.Error(err.Error())
		}
		if attr==nil {
			log.Println("attr is nil")
			return shim.Error("attr is nil")
		}
		log.Println(attr.AttrName)
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


//批量加密和解密
func (d *DABECC) batch(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	uid:=args[0]
	baseAttr:=args[1]
	freq,err:=strconv.Atoi(args[2])
	con:=args[3]
	content:=args[4]

	and:=" AND "
	or:=" OR "
	policy:="("
	//apkMap:=make(map[string]*DecentralizedABE.APK)
	user := d.Dabe.UserSetup(uid)
	authorities:=make(map[string]DecentralizedABE.Authority)
	attrMap:=make(map[string]*pbc.Element)
	for i:=1;i<=freq;i++ {
		cur:=baseAttr+strconv.Itoa(i)
		//attr, err := user.GenerateNewAttr(cur, d.Dabe)
		//if err != nil {
		//	log.Println(err.Error())
		//	return shim.Error(err.Error())
		//}

		if i==freq{
			policy+=cur
		}else if con=="and"{
			policy=policy+cur+and
		}else{
			policy=policy+cur+or
		}

		//if attr==nil {
		//	log.Println("attr is nil")
		//	return shim.Error("attr is nil")
		//}

		y := d.Dabe.CurveParam.GetNewZn()
		gy := d.Dabe.G.NewFieldElement().PowZn(d.Dabe.G, y)
		sk := DecentralizedABE.ASK{y}
		pk := DecentralizedABE.APK{gy}
		user.ASKMap[cur] = &sk
		user.APKMap[cur] = &pk

		key, err := user.KeyGenByUser(uid, cur, d.Dabe)
		if err != nil {
			log.Println(err)
			return shim.Error(err.Error())
		}
		attrMap[cur]=key
		//apkMap[cur]=attr
		log.Println("cur attr: "+cur)
	}
	policy+=")"
	log.Println(policy)
	authority:=&Authority{PK:user.EGGAlpha,APKMap:user.APKMap}
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
	log.Println("encrypt success")

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
	start := time.Now()
	plainText, err :=d.Dabe.Decrypt(cipher, attrMap, uid)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	cost := time.Since(start)
	log.Println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
	log.Println("cost = [%s]",cost)

	//将明文保存到文件
	file2,err:=os.Create("plaintext")
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	defer file2.Close()
	_,err = file2.Write(plainText)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	log.Println("decrypt success, plain text: "+string(plainText))
	return shim.Success(plainText)
}
























