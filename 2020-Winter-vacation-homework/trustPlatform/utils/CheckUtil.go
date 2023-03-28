package utils

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"log"
//	"regexp"
	//"strconv"
//	"strings"
	"time"
	"trustPlatform/constant"
)

const IncorrectNumberError = "Incorrect number of input arguments. Expecting "
const InvalidIDError = "id: %s is invalid\n"

//todo 默认为true
var SignSwitch = true

func init() {
	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// 校验sign
// ===================================================================================
func VerifySign(paramJson, publicKey, sign string) (err error) {
	if !SignSwitch {
		log.Println("debug on: no need to verify")
		return nil
	}
	if _, err = VerifyOnBase64(paramJson, sign, publicKey); err != nil {
		return err
	}
	return
}

// ===================================================================================
// 检测传入参数个数
// ===================================================================================
func CheckInputNumber(num int, args []string) (err error) {
	if len(args) == num {
		return
	}
	log.Println(IncorrectNumberError + string(num))
	err = fmt.Errorf(IncorrectNumberError + string(num))
	return
}

// ===================================================================================
// 检测id名称是否合法
// ===================================================================================
func CheckId(id string) (err error) {
//	if ok, _ := regexp.MatchString("^[a-zA-Z0-9]{6,18}$", id); !ok {
//		log.Printf(InvalidIDError, id)
//		err = fmt.Errorf(InvalidIDError, id)
//		return err
//	}
	return
}

// ===================================================================================
// 检测属性名称是否合法
// ===================================================================================
func CheckAttr(attr string, id string) (err error) {
//	split := strings.Split(attr, ":")
//	if len(split) != 2 || split[0] != id {
//		log.Printf("attr %s is not match id %s", attr, id)
//		return fmt.Errorf("attr %s is not match id %s", attr, id)
//	}
//	if ok, _ := regexp.MatchString("^[a-zA-Z0-9]{1,32}$", split[1]); !ok {
//		log.Printf(InvalidIDError, attr)
//		err = fmt.Errorf(InvalidIDError, attr)
//		return err
//	}
	return
}

// ===================================================================================
// 检测时间戳与此时的间隔是否不超过一定值
// ===================================================================================
func CheckTimeWithin(timestamp string, num int64, duration time.Duration) (err error) {
	//ts, err := strconv.ParseInt(timestamp, 10, 64)
	//if err != nil {
	//	return err
	//}
	//now := time.Now().Unix()
	//if now < ts || now > ts+num*int64(duration) {
	//	log.Printf("now is %d, but get %d", now, ts)
	//	return fmt.Errorf("time early or late")
	//}
	return
}

// ===================================================================================
// 判断id是否被注册
// ===================================================================================
func ExistId(id string, stub shim.ChaincodeStubInterface) (b bool) {
	log.Printf("check id is existed, id: %s\n", id)
	bytes, err := stub.GetState(constant.IdPrefix + id)
	if err != nil || len(bytes) == 0 {
		return false
	}
	return true
}
