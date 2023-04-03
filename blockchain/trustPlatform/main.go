package main

import (
	"log"
	"trustPlatform/common"
	"trustPlatform/org"
	"trustPlatform/user"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"

	"strings"
	"trustPlatform/utils"
)

// 主模块
type TrustPlatformCC struct {
}

func init() {
	log.SetPrefix("[TrustPlatformCC]")
	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// Main
// ===================================================================================
func main() {
	err := shim.Start(new(TrustPlatformCC))
	if err != nil {
		log.Printf("Error starting Simple chaincode: %s", err)
	}
}

// ===================================================================================
// 初始化函数，如果有需要初始化的模块请在自己模块中定义init函数，在此调用，不要直接写在主模块中
// ===================================================================================
func (t *TrustPlatformCC) Init(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("TrustPlatformCC Init")
	// SIGN_SWITCH，实例化时初始化，测试时可以关掉sign，方便测试
	_, args := stub.GetFunctionAndParameters()
	log.Println(args)
	if len(args) == 1 && "false" == args[0] {
		utils.SignSwitch = false
	}
	// example
	user.Init(stub)
	return shim.Success([]byte("Init success"))
}

// ===================================================================================
// 调用合约入口，按模块划分，例如我们有用户、组织、数据分享三大模块（可以添加），按照路径层级传参
// 例如 用户声明新属性 /user/attribute/add args: userName, attributePublicKey
// user为用户模块，attribute为要操作的用户属性，add代表新建/声明，两个args按指定顺序传入
// ===================================================================================
func (t *TrustPlatformCC) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("\nTrustPlatformCC Invoke")
	function, _ := stub.GetFunctionAndParameters()
	if strings.HasPrefix(function, "/user") {
		return user.Invoke(stub)
	} else if strings.HasPrefix(function, "/org") {
		return org.Invoke(stub)
	} else if strings.HasPrefix(function, "/common") {
		return common.Invoke(stub)
	}

	return shim.Error("Invalid invoke function name. Expecting \"/user\" ")
}
