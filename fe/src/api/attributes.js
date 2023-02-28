import request from '@/utils/request'
import { localAttrs } from "../mock/attributes"

export const attrApi = {

    /**
     * dabe 生成用户新属性
     * @param {*} _data 来自前端的参数，用于发送请求，字段不一定和请求字段一致，需要转换一下
     * @returns Promise
     */
    dabeGenerate: function (_data) {
        // fileName 用户名
        // attrName 
        const data = {
            fileName: _data.name,
            attrName: _data.attr,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/dabe/user/attr',
                method: 'post',
                data,
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success",  描述
                //     "data": {
                //         "appliedAttrMap":{ },
                //         "privacyAttrMap":{},
                //         "APKMap":{
                //             "someone:friend":{"GY":"[369150..., 601530...]"}
                //         },
                //         "ASKMap":{
                //             "someone:friend":{ "Y":"791100..."}
                //         },
                //         "EGGAlpha":"[206605..., 320061...]",
                //         "Alpha":"907358...",
                //         "GAlpha":"[569334..., 105875...]",
                //         "Name":"someone",
                //         "OPKMap":{},
                //         "OSKMap":{ },
                //         "Password":"202cb962ac59075b964b07152d234b70",
                //         "UserType":"org",
                //         "Channel":"myc"
                //     }
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
                    resolve(localAttrs.dabe(_data))
                }
                else reject(error)
            })
        })
    },

    /**
     * plat 生成用户新属性
     * @param {*} _data 参数
     * @returns Promise
     */
    platGenerate: function (_data) {
        // fileName 用户名
        // attrName 属性名
        const data = {
            fileName: _data.name,
            attrName: _data.attr,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr',
                method: 'post',
                data,
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success",  描述
                //     "data": {}
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
                    resolve(localAttrs.same(_data))
                }
                else reject(error)
            })
        })
    },

    /**
     * 申请用户属性
     * @param {*} _data 参数
     * @returns Promise
     */
    apply: function ({ name, attr, to, org, remark }) {
        const _data = { name, attr, to, org, remark }
        // fileName    用户名
        // toUserName  属性所属用户名称
        // toOrgName   属性所属组织名称
        // remark      备注
        // attrName    属性名
        const data = {
            fileName: _data.name,
            attrName: _data.attr,
            toUserName: _data.to,
            toOrgName: _data.org,
            remark: _data.remark
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr/apply',
                method: 'post',
                data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":null,  描述
                //     "data": {}
                // }
                // console.log("[api]", response);
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
     * 查询用户属性申请
     * @param {*} _data 参数
     * @returns Promise
     */
    applications: function ({ to, role, user, status }) {
        const _data = { to, role, user, status }
        // 以下参数放在 headers 中
        // toId     被申请用户/组织名
        // type     申请类型（用户属性/组织属性） （Int）0 用户；1 组织
        // userName 申请人
        // status   申请状态 ALL / PENDING / SUCCESS / FAIL
        const params = {
            toId: _data.to,
            type: _data.role,
            userName: _data.user,
            status: _data.status
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr/apply',
                method: 'get',
                params,
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":null,  描述
                //     "data":[
                //         {
                //             "fromUid":"someone2",
                //             "toUid":"someone",
                //             "toOrgId":"",
                //             "isPublic":true,
                //             "attrName":"someone:family",
                //             "remark":"",
                //             "n":1,
                //             "t":1,
                //             "applyType":"TO_USER",
                //             "status":"PENDING",
                //             "approvalMap":{
                //                 "someone":null
                //             }
                //         },
                //         {
                //             "fromUid":"someone2",
                //             "toUid":"someone",
                //             "toOrgId":"",
                //             "isPublic":true,
                //             "attrName":"someone:friend",
                //             "remark":"",
                //             "n":1,
                //             "t":1,
                //             "applyType":"TO_USER",
                //             "status":"PENDING",
                //             "approvalMap":{
                //                 "someone":null
                //             }
                //         }
                //     ]
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
     * 审批用户的属性申请
     * @param {*} _data 参数
     * @returns Promise
     */
    approval: function ({ user, to, attr, remark, agree }) {
        const _data = { user, to, attr, remark, agree }
        // fileName    用户名
        // toUserName  被申请人/组织
        // attrName    属性名
        // remark      备注
        // agree       是否同意
        const data = {
            fileName: _data.user,
            toUserName: _data.to,
            attrName: _data.attr,
            remark: _data.remark,
            agree: _data.agree
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr/approval',
                method: 'post',
                data,
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
     * 同步属性
     * @param {*} _data 参数
     * @returns Promise
     */
    sync: function (user) {
        // fileName    用户名
        const data = {
            fileName: user,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr/sync',
                method: 'post',
                data,
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":null,  描述
                //     "data": {
                //         "appliedAttrMap":{
                //             "someone:family":"[159429..., 572246...]",
                //             "someone:friend":"[114119..., 477210...]"
                //         },
                //         "privacyAttrMap":{},
                //         "APKMap":{},
                //         "ASKMap":{},
                //         "EGGAlpha":"[481866..., 536232...]",
                //         "Alpha":"126759...",
                //         "GAlpha":"[158540..., 620270...]",
                //         "Name":"someone2",
                //         "OPKMap":{},
                //         "OSKMap":{},
                //         "Password":"202cb962ac59075b964b07152d234b70",
                //         "UserType":"user",
                //         "Channel":"myc"
                //     }
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
     * 撤销属性
     * @param {*} _data 参数
     * @returns Promise
     */
    revoke: function ({
        userName,
        toUserName,
        attrName,
        remark,
    }) {
        // userName   用户名
        // toUserName 被撤销的用户名
        // attrName   属性名
        // remark     备注           可无
        const data = {
            userName,
            toUserName,
            attrName,
            remark,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr/revoke',
                method: 'post',
                data,
                params: data
            }).then(response => {
                // {
                //     "code":200,      //200，成功；其他，失败
                //     "msg":"success", //描述
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
     * 查询属性历史
     * @param {*} _data 参数
     * @returns Promise
     */
    history: function (userName) {
        // userName   用户名
        const data = {
            userName
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/user/attr/history',
                method: 'post',
                data,
                params: data
            }).then(response => {
                // {
                //     "code":200,
                //     "message":null,
                //     "data":[
                //         {
                //           "uid":"someone2",                // 当前用户
                //             "fromUid":"someone",           // 操作用户
                //             "attrName":"someone:family",   // 属性名称
                //             "operation":"agree"或"revoke", // 操作：授权或撤销
                //             "timeStamp":"xxx",             // 操作时间
                //         },
                //         ...
                //     ]
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