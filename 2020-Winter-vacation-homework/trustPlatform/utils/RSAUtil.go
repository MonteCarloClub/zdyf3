package utils

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/hex"
	"log"
)

func init() {

	log.SetFlags(log.Ltime | log.Lshortfile)
}

// ===================================================================================
// 生成公私钥对
// ===================================================================================
func Generate() (privateKey *rsa.PrivateKey, publicKey *rsa.PublicKey, err error) {
	if privateKey, err = rsa.GenerateKey(rand.Reader, 2048); err != nil {
		return nil, nil, err
	}
	return
}

func GenerateOnBase64() (privateKey string, publicKey string, err error) {
	priKey, pubKey, err := Generate()
	if err != nil {
		return "", "", err
	}
	privateKey = base64.StdEncoding.EncodeToString(x509.MarshalPKCS1PrivateKey(priKey))
	publicKey = base64.StdEncoding.EncodeToString(x509.MarshalPKCS1PublicKey(pubKey))
	return
}

func GenerateOnHex() (privateKey string, publicKey string, err error) {
	priKey, pubKey, err := Generate()
	if err != nil {
		return "", "", err
	}
	privateKey = hex.EncodeToString(x509.MarshalPKCS1PrivateKey(priKey))
	publicKey = hex.EncodeToString(x509.MarshalPKCS1PublicKey(pubKey))
	return
}

// ===================================================================================
// 转化base64的私钥
// ===================================================================================
func ParsePrikeyOnBase64(privateKey string) (priKey *rsa.PrivateKey, err error) {
	priKeyBytes, err := base64.StdEncoding.DecodeString(privateKey)
	if err != nil {
		return nil, err
	}
	priKey, err = x509.ParsePKCS1PrivateKey(priKeyBytes)
	if err != nil {
		return nil, err
	}
	return
}

// ===================================================================================
// 转化base64的公钥
// ===================================================================================
func ParsePubkeyOnBase64(publicKey string) (pubKey *rsa.PublicKey, err error) {
	pubKeyBytes, err := base64.StdEncoding.DecodeString(publicKey)
	if err != nil {
		log.Println(err)
		return nil, err
	}
	pubKey2, err := x509.ParsePKIXPublicKey(pubKeyBytes)
	if err != nil {
		log.Println(err)
		return nil, err
	}
	return pubKey2.(*rsa.PublicKey), nil
}

// ===================================================================================
// 传入base64的私钥进行签名
// ===================================================================================
func SignOnBase64(data, privateKey string) (signData string, err error) {
	priKey, err := ParsePrikeyOnBase64(privateKey)
	if err != nil {
		return "", err
	}

	signData, err = Sign(data, priKey)
	if err != nil {
		return "", nil
	}
	return
}

// ===================================================================================
// 签名，传出的签名信息是hex编码的
// ===================================================================================
func Sign(data string, priKey *rsa.PrivateKey) (signData string, err error) {
	h := sha256.Sum256([]byte(data))
	signature, err := rsa.SignPKCS1v15(rand.Reader, priKey, crypto.SHA256, h[:])
	if err != nil {
		log.Printf("Error from signing: %s\n", err)
		return "", err
	}
	return hex.EncodeToString(signature), nil
}

// ===================================================================================
// 传入base64的公钥进行验签
// ===================================================================================
func VerifyOnBase64(origin, sign, publicKey string) (b bool, err error) {
	pubKey, err := ParsePubkeyOnBase64(publicKey)
	if err != nil {
		log.Println(err)
		return false, err
	}

	b, err = Verify(origin, sign, pubKey)
	if err != nil {
		log.Println(err)
		return false, err
	}
	return
}

// ===================================================================================
// 验签
// ===================================================================================
func Verify(origin, sign string, pubKey *rsa.PublicKey) (b bool, err error) {
	hash := sha256.Sum256([]byte(origin))
	signBytes, err := hex.DecodeString(sign)
	if err != nil {
		log.Println(err)
		return false, err
	}

	if err = rsa.VerifyPKCS1v15(pubKey, crypto.SHA256, hash[:], signBytes); err != nil {
		log.Println(err)
		return false, err
	}
	return true, nil
}
