package main

import (
	"bytes"
	"crypto/ecdsa"
	"crypto/rand"
	"crypto/x509"
	"encoding/base32"
	"encoding/hex"
	"encoding/json"
	"log"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"io"
	"math/big"
	"strconv"
	"strings"
)

type AuthChaincode struct {
	Type      string `json:"type"`
	UserName  string `json:"user_name"`
	PublicKey string `json:"public_key"`
	From      string `json:"from"`
	To        string `json:"to"`
	Message   string `json:"message"`
}

var IncorrectNumberError = "Incorrect number of arguments. Expecting "
var NamePrefix = "auth:userName:"
var MsgPrefix = "auth:message:"

// ===================================================================================
// Main
// ===================================================================================
func main() {
	err := shim.Start(new(AuthChaincode))
	if err != nil {
		log.Printf("Error starting Simple chaincode: %s", err)
	}
}

func (t *AuthChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("Auth Init")
	return shim.Success([]byte("Init success"))
}

func (t *AuthChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("Auth Invoke")
	function, args := stub.GetFunctionAndParameters()
	if function == "register" {
		return t.register(stub, args)
	} else if function == "queryAllUsers" {
		return t.queryAllUsers(stub, args)
	} else if function == "queryUsersWithPagination" {
		return t.queryUsersWithPagination(stub, args)
	} else if function == "queryUserByUserName" {
		return t.queryUserByUserName(stub, args)
	} else if function == "queryToMessagesWithPagination" {
		return t.queryToMessagesWithPagination(stub, args)
	} else if function == "queryFromMessagesWithPagination" {
		return t.queryFromMessagesWithPagination(stub, args)
	} else if function == "sendMessage" {
		return t.sendMessage(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"invoke\" \"delete\" \"query\"")
}

/**
通过公钥和用户名注册，用户名唯一
传入参数为：用户名，公钥，私钥对用户名的签名:r,s
*/
func (t *AuthChaincode) register(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth register")
	if len(args) != 4 {
		return shim.Error(IncorrectNumberError + "4")
	}
	//检查是否已存在
	fullName := NamePrefix + args[0]
	authBytes, err := stub.GetState(fullName)
	if err != nil {
		return shim.Error("get state error" + err.Error())
	}
	if authBytes != nil {
		return shim.Error("User already exists")
	}

	//验签
	verifyResult, errString := verify(args[1], []byte(args[0]), args[2], args[3])
	if !verifyResult {
		return shim.Error(errString)
	}

	//放入state
	namePublicKey := AuthChaincode{NamePrefix, args[0], args[1], "", "", ""}
	namePublicKeyJson, err := json.Marshal(namePublicKey)
	if err != nil {
		return shim.Error("marshal namePublicKey error: " + err.Error())
	}
	err = stub.PutState(fullName, []byte(namePublicKeyJson))
	if err != nil {
		return shim.Error("put state error: " + err.Error())
	}

	responseStr := args[0] + "success in register by " + args[1]
	log.Println(responseStr)
	return shim.Success([]byte(responseStr))
}

/**
查询所有用户
*/
func (t *AuthChaincode) queryAllUsers(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth queryAllUsers")
	if len(args) != 0 {
		return shim.Error(IncorrectNumberError + "0")
	}
	queryString := log.Sprintf("{\"selector\":{\"type\":\"%s\"}}", NamePrefix)
	iteratorInterface, err := stub.GetQueryResult(queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer iteratorInterface.Close()

	buffer, err := constructQueryResponseFromIterator(iteratorInterface)
	if err != nil {
		return shim.Error(err.Error())
	}

	log.Printf("- queryAllUsers queryResult:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

/**
查询用户，分页
入参：pagesize bootmark
*/
func (t *AuthChaincode) queryUsersWithPagination(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth queryUsersWithPagination")
	if len(args) != 2 {
		return shim.Error(IncorrectNumberError + "2")
	}
	queryString := log.Sprintf("{\"selector\":{\"type\":\"%s\"}}", NamePrefix)

	bufferWithPaginationInfo, err := getResultWithPagination(args[0], args[1], queryString, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	log.Printf("- queryUsersWithPagination queryResult:\n%s\n", bufferWithPaginationInfo.String())
	return shim.Success(bufferWithPaginationInfo.Bytes())
}

/**
精确查询用户
*/
func (t *AuthChaincode) queryUserByUserName(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth queryUserByUserName")
	if len(args) != 1 {
		return shim.Error(IncorrectNumberError + "1")
	}
	fullName := NamePrefix + args[0]
	authBytes, err := stub.GetState(fullName)
	if err != nil {
		return shim.Error("get state error" + err.Error())
	}
	if authBytes == nil {
		return shim.Error("User doesn't exists")
	}
	return shim.Success(authBytes)
}

/**
查看发送给自己的消息，分页
入参：自己用户名，pageSize，bookmark
*/
func (t *AuthChaincode) queryToMessagesWithPagination(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth queryMessagesWithPagination")
	if len(args) != 3 {
		return shim.Error(IncorrectNumberError + "3")
	}
	queryString := log.Sprintf("{\"selector\":{\"type\":\"%s\",\"to\":\"%s\"}}", MsgPrefix, args[0])
	bufferWithPaginationInfo, err := getResultWithPagination(args[1], args[2], queryString, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	log.Printf("- queryToMessagesWithPagination queryResult:\n%s\n", bufferWithPaginationInfo.String())
	return shim.Success(bufferWithPaginationInfo.Bytes())
}

/**
查看自己发出的的消息，分页
入参：自己用户名，pageSize，bookmark
*/
func (t *AuthChaincode) queryFromMessagesWithPagination(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth queryMessagesWithPagination")
	if len(args) != 3 {
		return shim.Error(IncorrectNumberError + "3")
	}
	queryString := log.Sprintf("{\"selector\":{\"type\":\"%s\",\"from\":\"%s\"}}", MsgPrefix, args[0])
	bufferWithPaginationInfo, err := getResultWithPagination(args[1], args[2], queryString, stub)
	if err != nil {
		return shim.Error(err.Error())
	}

	log.Printf("- queryFromMessagesWithPagination queryResult:\n%s\n", bufferWithPaginationInfo.String())
	return shim.Success(bufferWithPaginationInfo.Bytes())
}

/**
发送消息给其他人
入参为：发送给的用户名，消息，自己的用户名，验签消息：r,s
TODO 签名不要只签消息，会被他们使用。从ecdsa换成rsa，要不没法加密
*/
func (t *AuthChaincode) sendMessage(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("Auth sendMessage")
	if len(args) != 5 {
		return shim.Error(IncorrectNumberError + "5")
	}
	fromUserName := args[2]
	toUserName := args[0]
	encryptedMsg := args[1]
	rString := args[3]
	sString := args[4]

	//检查接收方是否存在
	fullNameTo := NamePrefix + toUserName
	publicKeyBytesTo, err := stub.GetState(fullNameTo)
	if err != nil {
		return shim.Error("get state exists" + err.Error())
	}
	if publicKeyBytesTo == nil {
		return shim.Error("User to doesn't exist")
	}

	//检查发送方是否存在
	fullNameFrom := NamePrefix + fromUserName
	authFromBytes, err := stub.GetState(fullNameFrom)
	if err != nil {
		return shim.Error("get state error " + err.Error())
	}
	if authFromBytes == nil {
		return shim.Error("User to doesn't exist")
	}
	var authFrom AuthChaincode
	err = json.Unmarshal(authFromBytes, &authFrom)
	if err != nil {
		return shim.Error("unmarshal error " + err.Error())
	}

	//验签
	verifyResult, errString := verify(authFrom.PublicKey, []byte(encryptedMsg), rString, sString)
	if !verifyResult {
		return shim.Error(errString)
	}

	//存入state
	authMessage := AuthChaincode{MsgPrefix, "", "", fromUserName, toUserName, encryptedMsg}
	marshal, err := json.Marshal(authMessage)
	if err != nil {
		return shim.Error("marshal json error: " + authMessage.From + " " + authMessage.To)
	}

	err = stub.PutState(uniqueName(), marshal)
	if err != nil {
		return shim.Error("put state error " + err.Error())
	}

	return shim.Success([]byte(""))
}

func getResultWithPagination(pageSizeStr, bookmark, query string, stub shim.ChaincodeStubInterface) (result *bytes.Buffer, err error) {
	result = nil
	pageSize, err := strconv.ParseInt(pageSizeStr, 10, 32)
	if err != nil {
		return
	}

	resultsIterator, responseMetadata, err := stub.GetQueryResultWithPagination(query, int32(pageSize), bookmark)
	if err != nil {
		return
	}
	buffer, err := constructQueryResponseFromIterator(resultsIterator)
	if err != nil {
		return
	}
	result = addPaginationMetadataToQueryResults(buffer, responseMetadata)
	return
}

// 验签
func verify(publicKeyString string, plaintext []byte, rString, sString string) (bool, string) {
	decodeByteArray, err := hex.DecodeString(publicKeyString)
	if err != nil {
		return false, "DecodeString error: " + err.Error()
	}
	pkixPublicKey, err := x509.ParsePKIXPublicKey(decodeByteArray)
	if err != nil {
		return false, "parse publicKey error: " + err.Error()
	}
	publicKey := pkixPublicKey.(*ecdsa.PublicKey)
	var r, s big.Int
	r.SetString(rString, 16)
	s.SetString(sString, 16)

	verify := ecdsa.Verify(publicKey, plaintext, &r, &s)
	if !verify {
		return false, "verify userName error"
	}
	return true, ""
}

// ===========================================================================================
// constructQueryResponseFromIterator constructs a JSON array containing query results from
// a given result iterator
// ===========================================================================================
func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) (*bytes.Buffer, error) {
	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	return &buffer, nil
}

// ===========================================================================================
// addPaginationMetadataToQueryResults adds QueryResponseMetadata, which contains pagination
// info, to the constructed query results
// ===========================================================================================
func addPaginationMetadataToQueryResults(buffer *bytes.Buffer, responseMetadata *pb.QueryResponseMetadata) *bytes.Buffer {

	buffer.WriteString("[{\"ResponseMetadata\":{\"RecordsCount\":")
	buffer.WriteString("\"")
	buffer.WriteString(log.Sprintf("%v", responseMetadata.FetchedRecordsCount))
	buffer.WriteString("\"")
	buffer.WriteString(", \"Bookmark\":")
	buffer.WriteString("\"")
	buffer.WriteString(responseMetadata.Bookmark)
	buffer.WriteString("\"}}]")

	return buffer
}

func uniqueName() string {
	name := base32.StdEncoding.WithPadding(base32.NoPadding).EncodeToString(generateBytesUUID())
	return strings.ToLower(name)
}

// GenerateBytesUUID returns a UUID based on RFC 4122 returning the generated bytes
func generateBytesUUID() []byte {
	uuid := make([]byte, 16)
	_, err := io.ReadFull(rand.Reader, uuid)
	if err != nil {
		panic(log.Sprintf("Error generating UUID: %s", err))
	}

	// variant bits; see section 4.1.1
	uuid[8] = uuid[8]&^0xc0 | 0x80

	// version 4 (pseudo-random); see section 4.1.3
	uuid[6] = uuid[6]&^0xf0 | 0x40

	return uuid
}
