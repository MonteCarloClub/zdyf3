import request from '@/utils/request'

/** 组织管理
 * 
 * 申请成立新组织
 * 同意加入新组织
 * 提交 part-pk（生成组织/组织属性）
 * 最终确认（生成新组织/组织新属性）
 * 查询新增组织/组织属性申请
 * 查询组织信息
 * 声明组织新属性
 * 审批组织新属性
**/

export const orgApi = {
    /**
     * 申请成立新组织
     * @param {*} _data 来自前端的参数，用于发送请求，字段不一定和请求字段一致，需要转换一下
     * @returns Promise
     */
    create: function (_data) {
        // fileName 申请用户名
        // t        阈值        Int
        // n        组织成员总数 Int
        // users    组织成员列表 List
        // orgName  组织名称
        const data = {
            fileName: _data.userName,
            t: _data.threshold,
            n: _data.usersNum,
            users: _data.users,
            orgName: _data.orgName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/apply/creation',
                method: 'post',
                data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success",  描述
                //     "data": null
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 同意加入新组织
     * @param {*} _data 参数
     * @returns Promise
     */
    join: function ({ userName, orgName }) {
        // fileName 用户名
        // orgName  组织名称
        const data = {
            fileName: userName,
            orgName: orgName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/apply/creation/approval',
                method: 'post',
                data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success",  描述
                //     "data":null
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 提交 part-pk 生成组织 
     * @param {*} _data 参数
     * @returns Promise
     */
    submitppk: function ({ orgName, userName }) {
        // type     类型      CREATION/ ATTRIBUTE
        // orgName  组织名称
        // fileName 用户名  
        // attrName 属性名    生成组织时为’’
        const data = {
            type: 'CREATION',
            orgName,
            fileName: userName,
            attrName: "",
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/part-pk',
                method: 'post',
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":null,  描述
                //     "data": {}
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 提交 part-pk 生成组织属性 
     * @param {*} _data 参数
     * @returns Promise
     */
    submitOrgAttppk: function ({ orgName, userName, attrName }) {
        // type     类型      CREATION/ ATTRIBUTE
        // orgName  组织名称
        // fileName 用户名 
        // attrName 属性名    生成组织时为’’
        const data = {
            type: 'ATTRIBUTE',
            orgName,
            fileName: userName,
            attrName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/part-pk',
                method: 'post',
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":null,  描述
                //     "data": {}
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 最终确认，生成新组织
     * @param {*} _data 参数
     * @returns Promise
     */
    completeOrg: function ({ orgName, userName }) {
        // orgName  组织名称
        // type     类型      CREATION 
        // userName 用户名    生成组织属性时为’’
        // attrName 属性名    生成组织时为’’
        const data = {
            type: "CREATION",
            orgName: orgName,
            userName: userName,
            attrName: "",
        }

        return this.complete(data)
    },

    /**
     * 最终确认，生成新组织新属性 
     * @param {*} _data 参数
     * @returns Promise
     */
    completeOrgAttr: function ({ orgName, userName, attrName }) {
        // type     类型      ATTRIBUTE
        // orgName  组织名称
        // userName 用户名
        // attrName 属性名
        return this.complete({
            type: "ATTRIBUTE", 
            orgName, 
            userName, 
            attrName
        })
    },

    /**
     * 最终确认（生成新组织/组织新属性）
     * @param {*} _data 参数
     * @returns Promise
     */
    complete: function ({ type, orgName, userName, attrName }) {
        // type     类型      CREATION/ ATTRIBUTE
        // orgName  组织名称
        // fileName 用户名    生成组织属性时为’’
        // attrName 属性名    生成组织时为’’
        const data = {
            type: type,
            orgName: orgName,
            fileName: userName,
            attrName: attrName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/complete-pk',
                method: 'post',
                params: data,
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":null,  描述
                //     "data":null
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 查询新增组织申请
     * @param {*} _data 参数
     * @returns Promise
     */
    tempOrgInfo: function ({ orgName }) {
        // type     类型      CREATION/ ATTRIBUTE
        // orgName  组织名称
        // attrName 属性名    生成组织时为’’
        const params = {
            type: "CREATION",
            orgName: orgName,
            attrName: "",
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/apply',
                method: 'get',
                params,
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success", 描述
                //     "data": {
                //          String orgId;
                //          Map<String, Boolean> uidMap;
                //          Map<String, Map<String, String>> shareMap;
                //          Map<String, String> opkMap;
                //          Integer t;
                //          Integer n;
                //          String fromUserName;
                //          OrgApplyStatusEnum status;
                //          String createTime;
                //          String attrName;
                //          OrgApplyTypeEnum type;
                //      }
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(error => {
                // 调用 Mock 的数据
                if (error) {
                    console.log(error);
                }
                else reject(error)
            })
        })
    },

    /**
     * 查询新增组织属性申请
     * @param {*} _data 参数
     * @returns Promise
     */
    tempOrgAttrInfo: function ({ orgName, attrName }) {
        // type     类型      ATTRIBUTE
        // orgName  组织名称
        // attrName 属性名    
        const params = {
            type: "ATTRIBUTE",
            orgName: orgName,
            attrName: attrName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/apply',
                method: 'get',
                params,
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success", 描述
                //     "data": {
                //          String orgId;
                //          Map<String, Boolean> uidMap;
                //          Map<String, Map<String, String>> shareMap;
                //          Map<String, String> opkMap;
                //          Integer t;
                //          Integer n;
                //          String fromUserName;
                //          OrgApplyStatusEnum status;
                //          String createTime;
                //          String attrName;
                //          OrgApplyTypeEnum type;
                //      }
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(error => {
                // 调用 Mock 的数据
                if (error) {
                    console.log(error);
                }
                else reject(error)
            })
        })
    },

    /**
     * 查询组织信息
     * @param {*} _data 参数
     * @returns Promise
     */
    detailInfo: function ({ orgName }) {
        // 以下参数放在 params 中
        // orgName  组织名称
        const data = {
            orgName
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/',
                method: 'get',
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success", 描述
                //     "data": {
                //          String orgId;
                //          List<String> uidSet;
                //          List<String> attrSet;
                //          Integer t;
                //          Integer n;
                //          String opk;
                //      }
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 声明组织新属性
     * @param {*} _data 参数
     * @returns Promise
     */
    declareAttr: function ({orgName, userName, attrName}) {
        // orgName  组织名称
        // fileName 用户名
        // attrName 属性名
        const data = {
            orgName,
            fileName: userName,
            attrName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/apply/attribute',
                method: 'post',
                data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success", 描述
                //     "data": object   返回数据
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 审批组织新属性
     * @param {*} data 参数
     * @returns Promise
     */
    approvalAttr: function ({userName, orgName, attrName}) {
        // fileName 用户名
        // orgName  组织名称
        // attrName 属性名
        const data = {
            fileName: userName,
            orgName,
            attrName,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/org/apply/attribute/approval',
                method: 'post',
                data,
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success", 描述
                //     "data": object   返回数据
                // }
                if (response.code === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },
}