package main

import (
	"encoding/json"
	DecentralizedABE "github.com/MonteCarloClub/dabe/model"
	"github.com/Nik-U/pbc"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"log"
	"strconv"
	"strings"
)

// ===================================================================================
// user模块入口函数
// ===================================================================================
func (d *DABECC) UserInvoke(stub shim.ChaincodeStubInterface) pb.Response {
	log.Println("DABECC User Invoke")
	function, args := stub.GetFunctionAndParameters()
	log.Println(function)
	if function == "/user/create" {
		return d.create(stub, args)
	} else if function == "/user/declareAttr" {
		return d.declareAttr(stub, args)
	} else if function == "/user/share" {
		return d.share(stub, args)
	} else if function == "/user/assembleShare" {
		return d.assembleShare(stub, args)
	} else if function == "/user/approveAttr" {
		return d.approveAttr(stub, args)
	} else if function == "/user/assembleSecret" {
		return d.assembleSecret(stub, args)
	} else if function == "/user/new" {
		return create(stub, args)
	} else if function == "/user/batchCreate" {
		return d.batchCreate(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"/user/create\" \"/user/declareAttr\" " +
		"\"/user/share\" \"/user/assembleShare\" \"/user/approveAttr\"")
}

func (d *DABECC) assembleSecret(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user assemble secret")
	userNames := strings.Split(args[0], "-")
	secrets := strings.Split(args[1], "-")
	tStr := args[2]
	nStr := args[3]
	t, err := strconv.Atoi(tStr)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	n, err := strconv.Atoi(nStr)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	org, err := d.Dabe.OrgSetup(n, t, "nonsense", userNames)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	needUsers := make([]string, 0)
	needSecrets := make([]*pbc.Element, 0)
	for i := 0; i < n; i++ {
		if secrets[i] == "null" {
			continue
		}
		needUsers = append(needUsers, userNames[i])
		element, b := d.Dabe.CurveParam.Get0FromG1().SetString(secrets[i], 10)
		if !b {
			return shim.Error("convert secret error for " + userNames[i])
		}
		needSecrets = append(needSecrets, element)
	}
	secret, err := org.AssembleKeyPart(needUsers, needSecrets, d.Dabe)
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}
	return shim.Success([]byte(secret.String()))
}

func (d *DABECC) approveAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user approve attr")
	userBytes := args[0]
	toUid := args[1]
	attrName := args[2]

	user := new(DecentralizedABE.User)
	if err := DecentralizedABE.Deserialize2Struct([]byte(userBytes), user); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	var key *pbc.Element
	var err error
	if strings.HasPrefix(attrName, user.Name) {
		key, err = user.KeyGenByUser(toUid, attrName, d.Dabe)
	} else {
		split := strings.Split(attrName, ":")
		if len(split) != 2 {
			return shim.Error("attrName wrong")
		}
		key, err = user.KeyGenByOrg(toUid, attrName, d.Dabe, split[0])
	}
	if err != nil {
		log.Println(err)
		return shim.Error(err.Error())
	}

	return shim.Success([]byte(key.String()))
}

func (d *DABECC) assembleShare(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user assemble share")
	userBytes := args[0]
	orgName := args[1]
	attrName := args[2]
	nStr := args[3]
	n, err := strconv.Atoi(nStr)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	user := new(DecentralizedABE.User)
	if err := DecentralizedABE.Deserialize2Struct([]byte(userBytes), user); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	var sharesForUser []*pbc.Element
	aid := 0
	if attrName != "" {
		aid = 1
		sharesForUser = user.OSKMap[orgName].ASKMap[attrName].OthersShare
	} else {
		sharesForUser = user.OSKMap[orgName].OthersShare
	}

	if _, err := user.AssembleShare(sharesForUser, d.Dabe, n, aid, orgName, attrName); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	bytes, err := DecentralizedABE.Serialize2Bytes(user)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	return shim.Success(bytes)
}

func (d *DABECC) share(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user create share")
	log.Println(len(args))
	userBytes := args[0]
	orgName := args[1]
	userNames := strings.Split(args[2], ",")
	tStr := args[3]
	nStr := args[4]
	attrName := ""
	if len(args) == 6 {
		attrName = args[5]
	}
	t, err := strconv.Atoi(tStr)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	n, err := strconv.Atoi(nStr)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	user := new(DecentralizedABE.User)
	if err := DecentralizedABE.Deserialize2Struct([]byte(userBytes), user); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	org, err := d.Dabe.OrgSetup(n, t, orgName, userNames)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	if attrName == "" {
		if _, err := user.GenerateOrgShare(n, t, org.UserName2GID, orgName, d.Dabe); err != nil {
			log.Println(err.Error())
			return shim.Error(err.Error())
		}
	} else {
		if _, err := user.GenerateOrgAttrShare(n, t, org, d.Dabe, attrName); err != nil {
			log.Println(err.Error())
			return shim.Error(err.Error())
		}
	}

	bytes, err := DecentralizedABE.Serialize2Bytes(user)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	return shim.Success(bytes)
}

func (d *DABECC) declareAttr(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("user declareAttr")
	userBytes := args[0]
	attrName := args[1]
	user := new(DecentralizedABE.User)
	if err := DecentralizedABE.Deserialize2Struct([]byte(userBytes), user); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	if _, err := user.GenerateNewAttr(attrName, d.Dabe); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	bytes, err := DecentralizedABE.Serialize2Bytes(user)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	return shim.Success(bytes)
}

func (d *DABECC) create(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("create user")
	name := args[0]
	user := d.Dabe.UserSetup(name)
	bytes, err := DecentralizedABE.Serialize2Bytes(user)
	if err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}
	return shim.Success(bytes)
}

//批量注册用户
func (d *DABECC) batchCreate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("batch create user")
	name := args[0]
	cnt, _ := strconv.Atoi(args[1])
	for i := 1; i <= cnt; i++ {
		newName := name + strconv.Itoa(i)
		user := d.Dabe.UserSetup(newName)
		bytes, err := DecentralizedABE.Serialize2Bytes(user)
		if err != nil {
			log.Println(err.Error())
			return shim.Error(err.Error())
		}
		if err = stub.PutState("ID:"+newName, bytes); err != nil {
			return shim.Error(err.Error())
		}
		log.Println("user: " + newName)
	}
	return shim.Success(nil)
}

// ===================================================================================
// 根据uid查询用户
// ===================================================================================
func QueryUserByUid(uid string, stub shim.ChaincodeStubInterface) (user *User, err error) {
	log.Println("query user by uid: " + uid)
	bytes, err := stub.GetState("ID:" + uid)
	if err != nil {
		return nil, err
	}
	if len(bytes) == 0 {
		return nil, nil
	}

	if err = json.Unmarshal(bytes, &user); err != nil {
		return nil, err
	}
	return
}

//根据属性名查属性信息
func QueryAttrByName(attrName string, stub shim.ChaincodeStubInterface) (attr *Attr, err error) {
	bytes, err := stub.GetState("ATTR:" + attrName)
	if err != nil {
		return nil, err
	}
	if len(bytes) == 0 {
		return nil, nil
	}

	if err = json.Unmarshal(bytes, &attr); err != nil {
		return nil, err
	}
	return
}

type User struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 用户名
	Uid string `json:"uid"`
	// 用户公钥
	PublicKey string `json:"publicKey"`
	// 声明属性集
	AttrSet []string `json:"attrSet"`
	// 用户属性集
	HoldAttrSet []string `json:"holdAttrSet"`
	// 用户ABE公钥
	UPK string `json:"upk"`
}

type Attr struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 用户名
	Id string `json:"id"`
	// 属性名
	AttrName string `json:"attrName"`
	// 属性公钥
	APK string `json:"apk"`
}

func NewAttr(uid string, attrName string, APK string) *Attr {
	return &Attr{ObjectType: "attr", Id: uid, AttrName: attrName, APK: APK}
}

// ===================================================================================
// 保存用户属性
// ===================================================================================
func SaveUserAttrOnly(attr *Attr, stub shim.ChaincodeStubInterface) (err error) {
	// 插入attr
	attrBytes, err := json.Marshal(attr)
	if err != nil {
		return err
	}

	if err = stub.PutState("ATTR:"+attr.AttrName, attrBytes); err != nil {
		return err
	}
	log.Printf("save user attr %s with uid %s success\n", attr.AttrName, attr.Id)
	return
}

// ===================================================================================
// 更新用户相关内容
// ===================================================================================
func UpdateUserAttr(user *User, stub shim.ChaincodeStubInterface) (err error) {
	// 更新user
	userBytes, err := json.Marshal(user)
	if err != nil {
		return err
	}

	if err = stub.PutState("ID:"+user.Uid, userBytes); err != nil {
		return err
	}
	return
}

// ===================================================================================
// 创建一个新用户
// ===================================================================================
func create(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	log.Println("create new user start")

	var requestStr = args[0]
	initRequest := new(UserInitRequest)
	if err := json.Unmarshal([]byte(requestStr), initRequest); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	uid := initRequest.Uid
	publicKey := initRequest.PublicKey
	upk := initRequest.UPK

	user := NewUser(uid, publicKey, upk)
	if err := SaveUser(user, stub); err != nil {
		log.Println(err.Error())
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

// 用户初始化
type UserInitRequest struct {
	BaseRequest
	PublicKey string `json:"publicKey"`
	UPK       string `json:"upk"`
}

func NewUser(uid string, publicKey string, upk string) *User {
	return &User{ObjectType: "user", Uid: uid, PublicKey: publicKey, UPK: upk}
}

// ===================================================================================
// 保存用户
// ===================================================================================
func SaveUser(user *User, stub shim.ChaincodeStubInterface) (err error) {
	log.Println("save user with uid: " + user.Uid)
	bytes, err := json.Marshal(user)

	if err = stub.PutState("ID:"+user.Uid, bytes); err != nil {
		return err
	}
	log.Println("save user with uid: " + user.Uid + " success")
	return
}
