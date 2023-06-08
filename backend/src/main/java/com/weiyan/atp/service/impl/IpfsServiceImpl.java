package com.weiyan.atp.service.impl;


import com.weiyan.atp.service.IpfsService;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;

import io.ipfs.multihash.Multihash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class IpfsServiceImpl implements IpfsService {

    // ipfs 的服务器地址和端口，与yaml文件中的配置对应
    @Value("${ipfs.config.multi-addr}")
    private String multiAddr;

    private IPFS ipfs;

    //@PostConstruct
    public  IpfsServiceImpl() {
        this.ipfs = new IPFS(multiAddr);
    }

    @Override
    public String uploadToIpfs(String filePath) throws IOException {
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(filePath));

        MerkleNode addResult = ipfs.add(file).get(0);
        return addResult.hash.toString();
    }

    @Override
    public String uploadToIpfs(byte[] data) throws IOException {
        NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(data);
        MerkleNode addResult = ipfs.add(file).get(0);
        return addResult.hash.toString();
    }

    @Override
    public byte[] downFromIpfs(String hash) {
        byte[] data = null;
        try {
            data = ipfs.cat(Multihash.fromBase58(hash));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void downFromIpfs(String hash, String destFile) {
        byte[] data = null;
        try {
            data = ipfs.cat(Multihash.fromBase58(hash));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data != null && data.length > 0) {
            File file = new File(destFile);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

