package test

import (
	"crypto"
	"crypto/rsa"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"testing"
	"trustPlatform/request"
	"trustPlatform/utils"
)

func TestValidRequest(t *testing.T) {
	var rs = []byte("{\"uid\":\"xxx\",\"publicKey\":\"123\",\"timestamp\":\"15666841563\",\"sign\":\"456\"}")
	r := new(request.UserInitRequest)
	if err := json.Unmarshal(rs, r); err != nil {
		panic(err)
	}
	pj, err := utils.GetRequestParamJson(rs)
	if err != nil {
		panic(err)
	}
	_ = utils.VerifySign(string(pj), "123", "13")
}

func TestCheckId(t *testing.T) {
	err := utils.CheckId("czncznczn")
	if err != nil {
		panic(err)
	}
}

func TestVerifyBase64(t *testing.T)  {
	pubKey, err := utils.ParsePubkeyOnBase64("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqBqOqZkdzBl0Z0WoMi9xa3R66ScOfRy+Hj3huTFslkpLgDaTpZ7iPx9ysyIqRnT036Lloh0nkitPe8nX3azHwqE0ebmhPGf9OxVKIDOW5meYj3lxYWAPP2xL4wLRB5c23pjYhe5W4E20/6BrwUp9zgDwytfQ9iKlhjP8+ThctJ9BdXP5kORGrnh1CajdjMvhyuKB+Cnh5VJkMSYhykOJw/0DEv97+nbujqpPRWpOGU7jvl9PBAF5GabrGloTUR9jIOTvO2aChCW/Y+9wvvGyNLB0+tS0lmzFvuxHuM9trQOk5bD36JwJ/lyhUBnRTtAFDPNhoR4ppymKq3CMgCTsywIDAQAB")
	if err != nil {
		panic(err)
	}
	signBytes, err := hex.DecodeString("46a39954a88f2856caad9e9c9628a006f9cf73c5c514cd752a1ccd052bc364446bb1845c2abfbc6ebcfdc34d9193d22bed69627cad44e038fa7656f3c82b10d8c5952a958f27106b41772eb333f283a05ded03f4809e4ba3eddbbb16117d33d1b583428df8dc805cddb7102475443e0481435dc340f54885a91ffabce06cf174983280619287035898f1ac43e988041d590ff988bb958a3950bc6e74bad531051aade0e033fead6ed8f0dc2595078ad285c8750d813c20994610c272890266e596b84280e290cd3ccacf4ef3c13fc7a8404f5cf825ec673fdad6dd8cb2c078d2e1a435e7e8e054faf3d3e3063d8bc8ffdb425f749c9848d73bbf620908f9c667")
	hash, _ := hex.DecodeString("1d5d97b82f64718c00425e0288e7ad43182a374a93d0e6ed046ea9f616ef5a66")
	if err = rsa.VerifyPKCS1v15(pubKey, crypto.SHA256, hash[:], signBytes); err != nil {
		fmt.Println(err)
	}
}
