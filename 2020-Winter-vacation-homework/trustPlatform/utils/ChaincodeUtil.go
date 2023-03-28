package utils

import (
	"github.com/go-kratos/kratos/pkg/ecode"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"log"
)

func InvokeOtherChaincode(ccName string, stub shim.ChaincodeStubInterface, args []string) ([]byte, error){
	log.Printf("invoke chaincode %s in channel %s\n", ccName, stub.GetChannelID())
	response := stub.InvokeChaincode(ccName, toChaincodeArgs2(args), stub.GetChannelID())
	if response.Status != shim.OK {
		log.Println("invoke with error:", response.Message)
		return nil, ecode.Error(ecode.ServerErr, response.Message)
	}
	log.Printf("invoke chaincode %s in channel %s success\n", ccName, stub.GetChannelID())
	return response.Payload, nil
}

func toChaincodeArgs2(args []string) [][]byte {
	result := make([][]byte, len(args))
	for i, arg := range args {
		result[i] = []byte(arg)
	}
	return result
}