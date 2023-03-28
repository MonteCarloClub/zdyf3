package utils

import (
	"bytes"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
        "strconv"
)

func GetBytesFromDB(stub shim.ChaincodeStubInterface, queryString string) ([][]byte, error) {
	iterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer iterator.Close()

	result := make([][]byte, 0)
	//对迭代器进行遍历操作
	for iterator.HasNext() {
		//通过迭代器的Next()方法获取下一个对象的Key与Value值(*queryResult.KV)
		kv, err := iterator.Next()
		if err != nil {
			return nil, err
		}

		result = append(result, kv.Value)
	}

	return result, nil
}

func GetBytesFromDB2(stub shim.ChaincodeStubInterface, queryString string, pageSize int, bookmark string) ([]byte, error) {
	resultsIterator, responseMetadata, err := stub.GetQueryResultWithPagination(queryString, int32(pageSize), bookmark)
	if err != nil {
		return nil, err
	}
	buffer, err := constructQueryResponseFromIterator(resultsIterator)
	if err != nil {
		return nil, err
	}

	buffer.WriteString(",")
	result := addPaginationMetadataToQueryResults(buffer, responseMetadata)
	result.WriteString("}")
	return result.Bytes(), nil
}

// ===========================================================================================
// constructQueryResponseFromIterator constructs a JSON array containing query results from
// a given result iterator
// ===========================================================================================
func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) (*bytes.Buffer, error) {
	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("{\"result\":[")

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
		buffer.WriteString("{\"key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"record\":")
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

	buffer.WriteString("\"responseMetadata\":{\"recordsCount\":")
	buffer.WriteString("\"")
	buffer.WriteString(fmt.Sprintf("%v", responseMetadata.FetchedRecordsCount))
	buffer.WriteString("\"")
	buffer.WriteString(", \"bookmark\":")
	buffer.WriteString("\"")
	buffer.WriteString(responseMetadata.Bookmark)
	buffer.WriteString("\"}")

	return buffer
}


func UpdateTotalCount(stub shim.ChaincodeStubInterface,prefix string )(err error){
	totalCntByte, err := stub.GetState(prefix)
	if err != nil {
		return err
	}
	totalCnt:=1
	if len(totalCntByte) != 0 {
		str := string(totalCntByte)
		totalCnt,err = strconv.Atoi(str)
		if err != nil{
			return err
		}
		totalCnt++
	}
	str := strconv.Itoa(totalCnt)
	if err = stub.PutState(prefix,[]byte(str)); err != nil{
		return err
	}
	return nil
}



