import request from '@/utils/request'

/** 统计功能
 * 
 * 用户总数
 * 属性总数
 * 组织总数
 * 通道总数
 * 通道列表
**/

export const adminApi = {
    /**
     * 用户总数
     * @param {*} _data 来自前端的参数，用于发送请求，字段不一定和请求字段一致，需要转换一下
     * @returns Promise
     */
    usersNum: function (_data) {
        // 以下参数放在 headers 中
        // userName  用户名
        const data = {
            userName: _data.userName,
        }

        return request({
            url: '/statistic/usercnt',
            method: 'get',
            data
        })
        /**
         * 100       用户总数
         */
    },

    /**
     * 属性总数
     * @returns Promise
     */
    attrsNum: function () {
        return request({
            url: '/statistic/attrcnt',
            method: 'get'
        });
        /**
         * 100       属性总数
         */
    },

    /**
     * 组织总数
     * @returns Promise
     */
     orgsNum: function () {
        return request({
            url: '/statistic/orgcnt',
            method: 'get'
        })
        /**
         * 100       组织总数
         */
    },

    /**
     * 通道总数
     * @returns Promise
     */
     channelsNum: function () {
        return request({
            url: '/statistic/channelcnt',
            method: 'get'
        })
        /**
         * 100       通道总数
         */
    },

    /**
     * 通道列表
     * @returns Promise
     */
     channels: function () {
        return request({
            url: '/statistic/channlelist',
            method: 'get'
        })
        /**
         * []      返回通道列表
         */
    },

}