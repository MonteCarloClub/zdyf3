package main

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"crypto/x509"
	"encoding/hex"
	"log"
	"math/big"
	"testing"
)

var (
	a_pri2 = "3077020101042059705a953bb82f70758cf8914df84044e555b69723bbe969c26aba2e9ef11a2ca00a06082a8648ce3d030107a144034200040ae52ae29402727b22a4f777417e5b3061feadb8c26db457b8642102901d35eb6e8432cf15e5b29daaec1bc4031eb2a7fda84c8d9497b7c68fe406af9e697c4e"
	a_pub2 = "3059301306072a8648ce3d020106082a8648ce3d030107034200040ae52ae29402727b22a4f777417e5b3061feadb8c26db457b8642102901d35eb6e8432cf15e5b29daaec1bc4031eb2a7fda84c8d9497b7c68fe406af9e697c4e"

	b_pri2 = "307702010104205f9b6758121a3cf9d32e42c27ce2e20ae5872b963c07f0ea0066882254479d65a00a06082a8648ce3d030107a14403420004f8d0bae2e00dec4c38f9c759f957af7b0fdafc9bbf15ebe125ace99fffd31677343dc2a276abafbd2f457ad2ecd3630b2c8eda63acdf94971ed1096543cb6476"
	b_pub2 = "3059301306072a8648ce3d020106082a8648ce3d03010703420004f8d0bae2e00dec4c38f9c759f957af7b0fdafc9bbf15ebe125ace99fffd31677343dc2a276abafbd2f457ad2ecd3630b2c8eda63acdf94971ed1096543cb6476"
)

func TestSign(t *testing.T) {
	priBytes, _ := hex.DecodeString(a_pri2)
	pubBytes, _ := hex.DecodeString(a_pub2)
	pri, _ := testUnmarshal(priBytes, pubBytes)
	r1, s1 := testSign(pri, []byte("测试魏延发送给thor"))
	log.Printf("r:%x\ns:%x\n", r1[:], s1[:])
}

func TestVerify(t *testing.T) {
	r := "3c22489aa1842816605a21fdaa7098d1ebc23ff5f9c4f41bda92ae67bd43a6e6"
	s := "f86510ff61e631ad5e71471b27bbdd66af8f421b3015091a5bcabf326ebc5989"
	priBytes, _ := hex.DecodeString(a_pri2)
	pubBytes, _ := hex.DecodeString(a_pub2)
	_, pub := testUnmarshal(priBytes, pubBytes)
	log.Println(testVerify(pub, []byte("测试魏延发送给thor"), r, s))
}

func TestGenerate(t *testing.T) {
	privateKey, e := ecdsa.GenerateKey(elliptic.P256(), rand.Reader)
	if e != nil {
		panic(e)
	}
	publicKey, e := x509.MarshalPKIXPublicKey(&privateKey.PublicKey)
	if e != nil {
		panic(e)
	}
	ecPrivateKey, e := x509.MarshalECPrivateKey(privateKey)
	if e != nil {
		panic(e)
	}
	log.Printf("%x\n", ecPrivateKey)
	log.Printf("%x\n", publicKey)
}

func testUnmarshal(private, public []byte) (ecdsa.PrivateKey, ecdsa.PublicKey) {
	pub, err := x509.ParsePKIXPublicKey([]byte(public))
	if err != nil {
		panic(err)
	}
	key, err := x509.ParseECPrivateKey([]byte(private))
	if err != nil {
		panic(err)
	}
	log.Println(key.X)
	log.Println(pub.(*ecdsa.PublicKey).X)
	log.Println(key.Y)
	log.Println(pub.(*ecdsa.PublicKey).Y)
	return *key, *pub.(*ecdsa.PublicKey)
}

func testSign(privateKey ecdsa.PrivateKey, msg []byte) (r, s []byte) {
	r2, s2, err := ecdsa.Sign(rand.Reader, &privateKey, msg)
	if err != nil {
		panic(err)
	}
	r = r2.Bytes()
	s = s2.Bytes()
	return
}

func testVerify(publicKey ecdsa.PublicKey, msg []byte, r, s string) bool {
	var r2, s2 big.Int
	r2.SetString(r, 16)
	s2.SetString(s, 16)
	b := ecdsa.Verify(&publicKey, msg, &r2, &s2)
	return b
}
