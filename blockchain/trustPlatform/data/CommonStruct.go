package data

import "trustPlatform/constant"

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
	return &Attr{ObjectType: constant.Attr, Id: uid, AttrName: attrName, APK: APK}
}

type ChannelInfo struct {
	// couchDB使用的type
	ObjectType string `json:"docType"`
	// 通道名
	ChannelName string `json:"channelName"`
}

func NewChannel(channelName string) *ChannelInfo {
	return &ChannelInfo{ObjectType: constant.Channel, ChannelName:channelName}
}

