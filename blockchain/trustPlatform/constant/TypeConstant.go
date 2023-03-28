package constant

type AttrApplyStatus int

const (
	Pending AttrApplyStatus = iota
	Success
	Fail
	Revoke
	All
)

type ApplyType int

const (
	ToUser ApplyType = iota
	ToOrg
)

type OrgApplyType int

const (
	CreateOrg OrgApplyType = iota
	DeclareAttr
)
