package com.weiyan.atp.config;

import com.weiyan.atp.data.bean.LocalUser;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author : 魏延thor
 * @since : 2020/5/30
 */
@Configuration
public class DevModeChaincodeConfig {

    @Value("${atp.devMode.channelName}")
    private String channelName;
    @Value("${atp.devMode.peerName}")
    private String peerName;
    @Value("${atp.devMode.ordererName}")
    private String ordererName;
    @Value("${atp.devMode.peerUrl}")
    private String peerUrl;
    @Value("${atp.devMode.ordererUrl}")
    private String ordererUrl;

    @Bean
    public LocalUser devModeLocalUser() throws ClassNotFoundException, IllegalAccessException,
        InstantiationException, CryptoException, IOException {
        String keyFile = "src/main/resources/msp/keystore/key.pem";
        String certFile = "src/main/resources/msp/signcerts/peer.pem";
        return new LocalUser("admin", "DEFAULT", keyFile, certFile);
    }

    @Bean
    public HFClient devModeClient() throws IllegalAccessException, InvocationTargetException, InvalidArgumentException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException, IOException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(devModeLocalUser());
        return client;
    }

    @Bean
    public Channel devModeChannel() throws IllegalAccessException, InvalidArgumentException, InstantiationException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, TransactionException {
        HFClient client = devModeClient();
        Channel channel = client.newChannel(channelName);
        //修改grpc消息大小限制
        Properties properties = new Properties();
        properties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize",104857600);
        Peer peer = client.newPeer(peerName, peerUrl,properties);
        channel.addPeer(peer);
        Orderer orderer = client.newOrderer(ordererName, ordererUrl,properties);
        channel.addOrderer(orderer);
        channel.initialize();
        return channel;
    }
}
