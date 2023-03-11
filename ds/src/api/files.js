import request from '@/utils/request'
import axios from 'axios'

/** 文件管理
 * 
 * 加密共享文件
 * 查询共享文件
 * 解密共享文件
**/

export const fileApi = {
    /**
     * 加密共享文件
     * @param {*} _data 来自前端的参数，用于发送请求，字段不一定和请求字段一致，需要转换一下
     * @returns Promise
     */
    encrypt: function ({ userName, tags, file, policy }) {
        // file           用户名 String 
        // tags           标签 List 
        // file           明文文件 File 
        // policy         共享策略 String (A AND B AND (C OR D))
        const data = new FormData();
        data.append('fileName', userName);
        data.append('file', file);
        data.append('tags', tags);
        data.append('policy', policy);

        return new Promise((resolve, reject) => {
            request({
                url: '/content/upload',
                method: 'post',
                headers: { 'Content-Type': 'multipart/form-data' },
                data,
                timeout: 0,
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
     * 查询共享文件
     * @param {*} _data 参数
     * @returns Promise
     */
    files: function ({ userName, tag, size, bookmark }) {
        const _data = { userName, tag, size, bookmark }
        // fromUserName 分享人 String ，为 "" 空表示查询所有人的文件
        // tag          标签   String 
        // pageSize     每页显示条数 Int 默认为 10
        // bookmark     下一页的起点   String 默认为空
        const data = {
            fromUserName: _data.userName,
            tag: _data.tag,
            pageSize: _data.size || 10,
            bookmark: _data.bookmark,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/content/list',
                method: 'get',
                data,
                params: data
            }).then(response => {
                // {
                //     "code":200   200, 成功; 其他，失败
                //     "msg":"success",  描述
                //     "data":{
                //          "contents":[
                //              {
                //                  "fileName":"test.txt",
                //                  "policy":"(someone:friend AND someone:family)",
                //                  "cipher":"xxx",
                //                  "tags":[
                //                      "shanghai",
                //                      "myc",
                //                      "edu",
                //                      "test"
                //                  ],
                //                  "sharedUser":"someone"
                //              }
                //          ],
                //          "bookmark":"g1AAAA...",
                //          "pageSize":10,
                //          "count":1
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
     * 解密共享文件
     * @param {*} _data 参数
     * @returns Promise
     */
    decrypt: function ({ user, cipher, sharedUser, fileName, tags }) {
        // userName   解密用户
        // fileName   解密文件名
        // cipher     密文
        // sharedUser 文件共享者
        const data = {
            userName: user,
            fileName,
            cipher,
            tags,
            sharedUser,
        }

        return new Promise((resolve, reject) => {
            request({
                url: '/content/decryption',
                method: 'post',
                data
            }).then(response => {
                // {
                //     "code":200     200, 成功; 其他，失败
                //     "msg":null,    描述
                //     "data": content 共享文件内容
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
     * 下载共享文件
     * @param {*} _data 参数
     * @returns Promise
     */
    download: function ({ fileName, sharedUser }) {
        // fileName   解密文件名
        // sharedUser 文件共享者
        const data = {
            fileName,
            sharedUser,
        }

        return new Promise((resolve, reject) => {
            axios.request({
                baseURL: process.env.NODE_ENV === "development" ? process.env.VUE_APP_DEV_URL : process.env.VUE_APP_PRO_URL,
                url: '/content/download',
                method: 'get',
                data,
                params: data,
                responseType: 'blob', // important
            }).then(response => {
                // {
                //     "code":200     200, 成功; 其他，失败
                //     "msg":null,    描述
                //     "data":content 共享文件内容
                // }
                if (response.status === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    },

    /**
     * 下载密文哈希
     * @param {*} _data 参数
     * @returns Promise
     */
    downloadCipher: function ({ userName, fileName, sharedUser }) {
        // userName   用户名
        // fileName   解密文件名
        // sharedUser 文件共享者
        const data = {
            userName,
            fileName,
            sharedUser,
        }

        return new Promise((resolve, reject) => {
            axios.request({
                baseURL: process.env.NODE_ENV === "development" ? process.env.VUE_APP_DEV_URL : process.env.VUE_APP_PRO_URL,
                url: '/content/cipher',
                method: 'get',
                data,
                params: data,
                responseType: 'blob', // important
            }).then(response => {
                // {
                //     "code":200     200, 成功; 其他，失败
                //     "msg":null,    描述
                //     "data":content 共享文件内容
                // }
                if (response.status === 200) {
                    resolve(response.data)
                }
                else {
                    reject(response)
                }
            }).catch(reject)
        })
    }
}