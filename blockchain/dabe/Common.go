package main

import (
	"github.com/MonteCarloClub/dabe/model"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"log"
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
	}

	return shim.Error("Invalid invoke function name. Expecting \"/common/encrypt\" \"/common/decrypt\"")
}

func (d *DABECC) encrypt(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	requestBytes := args[0]
	request := new(EncryptRequest)
	log.Println(requestBytes)
	if err := model.Deserialize2Struct([]byte(requestBytes), request); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	authorities := make(map[string]model.Authority)
	for key, value := range request.AuthorityMap {
		authorities[key] = value
	}
	cipher, err := d.Dabe.Encrypt(request.PlainContent, request.Policy, authorities)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	bytes, err := model.Serialize2Bytes(cipher)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	return shim.Success(bytes)
}

func (d *DABECC) decrypt(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	requestBytes := args[0]
	request := new(DecryptRequest)
	if err := model.Deserialize2Struct([]byte(requestBytes), request); err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	cipher := new(model.Cipher)
	if err := model.Deserialize2Struct([]byte(request.Cipher), cipher); err != nil {
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
