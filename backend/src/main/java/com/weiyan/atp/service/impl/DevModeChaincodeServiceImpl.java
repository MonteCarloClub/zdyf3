package com.weiyan.atp.service.impl;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.ChaincodeResponse.Status;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.utils.JsonProviderHolder;

import lombok.extern.slf4j.Slf4j;

import org.hyperledger.fabric.protos.peer.FabricProposalResponse.Response;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/5/30
 */
@Service
@Slf4j
public class DevModeChaincodeServiceImpl implements ChaincodeService {
    private final HFClient client;
    private final Channel channel;

    public DevModeChaincodeServiceImpl(HFClient client, Channel channel) {
        this.client = client;
        this.channel = channel;
    }

    @Override
    public ChaincodeResponse invoke(ChaincodeTypeEnum ccType, String function, Object arg) {
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(ccType.getCcName()).build();
        request.setChaincodeID(cid);
        request.setFcn(function);
        request.setArgs(JsonProviderHolder.JACKSON.toJsonString(arg));
        return doInvoke(ccType, request);
    }

    @Override
    public ChaincodeResponse invoke(ChaincodeTypeEnum ccType, String function, List<String> args) {
        TransactionProposalRequest request = client.newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(ccType.getCcName()).build();
        request.setChaincodeID(cid);
        request.setFcn(function);
        request.setArgs((ArrayList<String>) args);
        return doInvoke(ccType, request);
    }

    @Override
    public ChaincodeResponse query(ChaincodeTypeEnum ccType, String function, Object arg) {
        QueryByChaincodeRequest request = client.newQueryProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(ccType.getCcName()).build();
        request.setChaincodeID(cid);
        request.setFcn(function);
        request.setArgs(JsonProviderHolder.JACKSON.toJsonString(arg));
        return doQuery(ccType, request);
    }

    @Override
    public ChaincodeResponse query(ChaincodeTypeEnum ccType, String function, List<String> args) {
        QueryByChaincodeRequest request = client.newQueryProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(ccType.getCcName()).build();
        request.setChaincodeID(cid);
        request.setFcn(function);
        request.setArgs((ArrayList<String>) args);
        System.out.println("1111111111111111111");
        System.out.println(request.toString());
        return doQuery(ccType, request);
    }

    private ChaincodeResponse doInvoke(ChaincodeTypeEnum ccType, TransactionProposalRequest request) {
        try {
            Collection<ProposalResponse> responses = channel.sendTransactionProposal(request);
            Response response = responses.toArray(new ProposalResponse[0])[0].getProposalResponse().getResponse();
            if (response.getStatus() == 200) {
                TransactionEvent event = channel.sendTransaction(responses).get();
                return ChaincodeResponse.builder()
                    .txId(event.getTransactionID())
                    .status(event.isValid() ? Status.SUCCESS : Status.FAIL)
                    .message(response.getPayload().toStringUtf8())
                    .build();
            } else {
                return ChaincodeResponse.builder()
                    .status(Status.FAIL)
                    .message(response.getMessage())
                    .build();
            }
        } catch (Exception e) {
            log.error("invoke chaincode error", e);
            throw new BaseException("query chaincode error in: " + ccType.getCcName());
        }
    }

    private ChaincodeResponse doQuery(ChaincodeTypeEnum ccType, QueryByChaincodeRequest request) {
        try {
            Collection<ProposalResponse> responses = channel.queryByChaincode(request);
            if (CollectionUtils.isEmpty(responses)) {
                throw new BaseException("no response");
            }
            if (responses.toArray(new ProposalResponse[0])[0].getProposalResponse().getResponse().getStatus() == 200) {
                return ChaincodeResponse.builder()
                    .status(Status.SUCCESS)
                    .message(responses.toArray(new ProposalResponse[0])[0].getProposalResponse().getResponse().getPayload().toStringUtf8())
                    .build();
            } else {
                return ChaincodeResponse.builder()
                    .status(Status.FAIL)
                    .message(responses.toArray(new ProposalResponse[0])[0].getProposalResponse().getResponse().getMessage())
                    .build();
            }
        } catch (Exception e) {
            log.error("query chaincode error", e);
            throw new BaseException("query chaincode error in: " + ccType.getCcName());
        }
    }
}
