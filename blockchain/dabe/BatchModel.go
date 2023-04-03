package main

type BaseRequest struct {
	Uid       string `json:"uid"`
	Timestamp string `json:"timestamp"`
	Sign      string `json:"sign"`
}

// 用户声明新属性
type UserAnnounceAttrRequest struct {
	BaseRequest
	AttrName string `json:"attrName"`
	APK      string `json:"apk"`
}






