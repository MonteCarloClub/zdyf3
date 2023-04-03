package main

import (
	DecentralizedABE "github.com/MonteCarloClub/dabe/model"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"io/ioutil"
	"log"
	"os"
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
	} else if function == "/common/batchDeclareAttr" {
		return batchDeclareAttr(stub, args)
	} else if function == "/common/batch" {
		return d.batch(stub, args)
	} else if function == "/common/getCipher" {
		return d.getCipher(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"/common/encrypt\" \"/common/decrypt\"")
}

func (d *DABECC) encrypt(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	requestBytes := args[0]
	request := new(EncryptRequest)
	//log.Println(requestBytes)
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

	//将密文保存到本地文件
	basePath := "encrypt/" + request.UserName
	path := basePath + "/" + request.FileName
	if !isExist(basePath) {
		err := CreateMutiDir(basePath)
		if err != nil {
			log.Println(err)
			return shim.Error(err.Error())
		}
	}

	file, err := os.Create(path)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	defer file.Close()
	_, err = file.Write(bytes)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	//返回简化的密文
	cipherSimple := &CipherSimple{C0: cipher.C0, C1s: cipher.C1s, C2s: cipher.C2s, C3s: cipher.C3s, Policy: cipher.Policy}
	bytes, err = DecentralizedABE.Serialize2Bytes(cipherSimple)
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

	//从本地读取密文
	basePath := "encrypt/" + request.SharedUser
	path := basePath + "/" + request.FileName
	if !isExist(basePath) {
		log.Println("path not exist: " + basePath)
		return shim.Error("path not exist: " + basePath)
	}

	cipherBytes, err := ioutil.ReadFile(path)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	cipher := new(DecentralizedABE.Cipher)
	if err := DecentralizedABE.Deserialize2Struct(cipherBytes, cipher); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	log.Printf("%+v\n", request.AttrMap)
	//log.Printf("%+v\n", cipher)

	_, err = d.Dabe.Decrypt(cipher, request.AttrMap, request.Uid)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func (d *DABECC) getCipher(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	requestBytes := args[0]
	request := new(DecryptRequest)
	if err := DecentralizedABE.Deserialize2Struct([]byte(requestBytes), request); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	//从本地读取密文
	basePath := "encrypt/" + request.SharedUser
	path := basePath + "/" + request.FileName
	if !isExist(basePath) {
		log.Println("path not exist: " + basePath)
		return shim.Error("path not exist: " + basePath)
	}
	cipherBytes, err := ioutil.ReadFile(path)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success(cipherBytes)
}
