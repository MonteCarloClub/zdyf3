package test

import (
	"encoding/json"
	"log"
	"testing"
	"trustPlatform/request"
)

func TestMarshalRequest(t *testing.T) {
	r := request.UserInitRequest{
		BaseRequest: request.BaseRequest{
			Uid:       "cznczn",
			Timestamp: "123456789",
			Sign:      "123456789",
		},
		PublicKey:   "MIIBCgKCAQEAw+pDu9WIh87GY2xRsCD8BqFmn87ghdmhBsmscj0s8H8pcaYAdEhUM5TSnQozgVVMzCO11T6Wlsldr9znzWEEenLRex6kmw25IkHqL9p6UIq4pEurS62WUgBZ5p24gKBciN6sRdjQeK+DJdk08akF4GCnZ2Q6ZIcCbdJLkzYj0tcbwrjoJXOKzayWkBQnkF/0GvWXkCvHvVeGhqUUctE+gdeOzxINwHwA14mxiM6KmNq0eBPhGNY5iM1LO2d75R/UH4+rqu4euh7twwYexxFIRMERWiRKratdK30vSy9kzRFpwOgJ1YwlrsIQV+IcQsT3Jmg9wcV+OIQWjXCxbzO29QIDAQAB",
		UPK:         "test",
	}
	bytes, _ := json.Marshal(r)
	log.Println(bytes)
}
