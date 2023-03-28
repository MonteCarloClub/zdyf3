package request

type GenerateOPKRequest struct {
	UserNames  []string
	PartPkList []string
	N          int
	T          int
}

type GenerateAPKRequest struct {
	UserNames  []string
	PartPkList []string
	N          int
	T          int
	AttrName   string
}
