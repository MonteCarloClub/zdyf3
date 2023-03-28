package main

import (
	"github.com/Nik-U/pbc"
	DecentralizedABE "github.com/wjfn/DecentralizedABE2020/model"
)

type GenerateOPKRequest struct {
	UserNames  []string
	PartPkList []*pbc.Element `field:"2"`
	N          int
	T          int
}

type GenerateAPKRequest struct {
	UserNames  []string
	PartPkList []*pbc.Element `field:"0"`
	N          int
	T          int
	AttrName   string
}

type EncryptRequest struct {
	PlainContent string
	Policy       string
	AuthorityMap map[string]*Authority
}

type Authority struct {
	PK     *pbc.Element `field:"2"`
	APKMap map[string]*DecentralizedABE.APK
}

type DecryptRequest struct {
	Cipher  string
	AttrMap map[string]*pbc.Element `field:"0"`
	Uid     string
}

func (a *Authority) GetPK() *pbc.Element {
	return a.PK
}

func (a *Authority) GetAPKMap() map[string]*DecentralizedABE.APK {
	return a.APKMap
}
