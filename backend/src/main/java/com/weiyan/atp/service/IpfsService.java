package com.weiyan.atp.service;

import java.io.IOException;

public interface IpfsService {
    /**
     * 指定path+文件名称,上传至ipfs
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    String uploadToIpfs(String filePath) throws IOException;

    /**
     * 将byte格式的数据,上传至ipfs
     *
     * @param data
     * @return
     * @throws IOException
     */
    String uploadToIpfs(byte[] data) throws IOException;

    /**
     * 根据Hash值,从ipfs下载内容,返回byte数据格式
     *
     * @param hash
     * @return
     */
    byte[] downFromIpfs(String hash);

    /**
     * 根据Hash值,从ipfs下载内容,并写入指定文件destFilePath
     *
     * @param hash
     * @param destFilePath
     */
    void downFromIpfs(String hash, String destFilePath);
}
