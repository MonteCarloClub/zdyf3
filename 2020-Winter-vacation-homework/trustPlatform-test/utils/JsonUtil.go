package utils

import (
	"bytes"
	"encoding/json"
	"log"
	"sort"
)

// ===================================================================================
// 获取除去sign外的json string
// ===================================================================================
func GetRequestParamJson(request []byte, ) (paramJson []byte, err error) {
	resultMap, err := getMapFromJson(request)
	if err != nil {
		log.Println(err)
		return nil, err
	}
	//1.定义一个slice
	keys := make([]string, 0, len(resultMap))
	sortMap := make(map[string]interface{})
	//2.遍历map获取key-->s1中
	for key := range resultMap {
		keys = append(keys, key)
	}
	//3.给s1进行排序
	sort.Strings(keys)
	//4.遍历resultMap
	for _, k := range keys { // 先下标，再数值
		sortMap[k] = resultMap[k]
	}

	if paramJson, err = json.Marshal(sortMap); err != nil {
		log.Println(err)
		return nil, err
	}

	return
}

func getMapFromJson(jsonBody []byte) (result map[string]interface{}, err error) {
	//result = make(map[string]string)
	if err = json.Unmarshal(jsonBody, &result); err != nil {
		return nil, err
	}
	delete(result, "sign")
	return
}

func GetListResponse(bytesArray [][]byte) bytes.Buffer {
	var buffer bytes.Buffer
	var isSplit bool
	buffer.WriteString("[")
	for _, byteArray := range bytesArray {
		if isSplit {
			buffer.WriteString(",")
		}
		buffer.Write(byteArray)
		isSplit = true
	}
	buffer.WriteString("]")
	return buffer
}
