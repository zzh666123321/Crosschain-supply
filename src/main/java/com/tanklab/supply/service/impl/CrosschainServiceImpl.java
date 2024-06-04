package com.tanklab.supply.service.impl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanklab.supply.common.ResultCode;
import com.tanklab.supply.common.SSHConfig;
import com.tanklab.supply.ds.req.CrossReq;
import com.tanklab.supply.ds.resp.CommonResp;

import com.tanklab.supply.entity.Crosschain;
import com.tanklab.supply.mapper.CrosschainMapper;
import com.tanklab.supply.service.CrosschainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;

/**
 * <p>
 * 跨链信息表 进行跨链操作返回结果，还没有写与数据库的交互
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
@Service
public class CrosschainServiceImpl extends ServiceImpl<CrosschainMapper, Crosschain> implements CrosschainService {

    @Autowired
    private CrosschainMapper crosschainMapper;
    @Autowired
    public CrosschainServiceImpl CrosschainService;

    @Override
    public CommonResp queryCrossTx() {
        CommonResp response = new CommonResp();

        try {
            // 使用QueryWrapper构建查询条件
            QueryWrapper<Crosschain> wrapper = new QueryWrapper<>();
            // 可以根据需要添加更多的查询条件
            // wrapper.eq("status", "success");

            // 执行查询
            List<Crosschain> crosschainList = crosschainMapper.selectList(wrapper);

            // 构建响应数据
            JSONArray resultArray = new JSONArray();
            for (Crosschain crosschain : crosschainList) {
                JSONObject resultObj = new JSONObject();
                resultObj.put("txId", crosschain.getTxId());
                resultObj.put("username", crosschain.getUsername());
                resultObj.put("status", crosschain.getStatus());
                resultObj.put("contract", crosschain.getContract());
                resultObj.put("crossFrom", crosschain.getCrossFrom());
                resultObj.put("crossTo", crosschain.getCrossTo());
                resultObj.put("crossType", crosschain.getCrossType());
                resultObj.put("txHash", crosschain.getTxHash());
                resultArray.add(resultObj);
            }

            // 设置响应数据
            JSONObject responseData = new JSONObject();
            responseData.put("crossTransactions", resultArray);
            response.setRet(ResultCode.SUCCESS);
            response.setData(responseData);

        } catch (Exception e) {
            // 错误处理
            response.setRet(ResultCode.FAILURE);
            response.setMessage("Failed to query cross-chain transactions. Error: " + e.getMessage());
        }

        return response;
    }

    @Override
    public CommonResp queryTxInfo(String txHash, Integer txId) {

                CommonResp response = new CommonResp();

                try {
                    // 使用QueryWrapper构建查询条件
                    QueryWrapper<Crosschain> wrapper = new QueryWrapper<>();
                    if (txHash != null && !txHash.isEmpty()) {
                        wrapper.eq("tx_hash", txHash);
                    } else if (txId != null) {
                        wrapper.eq("tx_id", txId);
                    } else {
                        // 如果没有提供有效的txHash或txId，返回错误信息
                        response.setRet(ResultCode.FAILURE);
                        response.setMessage("Please provide either txHash or txId");
                        return response;
                    }

                    // 执行查询
                    Crosschain crosschain = crosschainMapper.selectOne(wrapper);
                    if (crosschain == null) {
                        response.setRet(ResultCode.FAILURE);
                        response.setMessage("No transaction found with the provided txHash or txId");
                        return response;
                    }

                    // 构建响应数据
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("txId", crosschain.getTxId());
                    resultObj.put("username", crosschain.getUsername());
                    resultObj.put("status", crosschain.getStatus());
                    resultObj.put("contract", crosschain.getContract());
                    resultObj.put("crossFrom", crosschain.getCrossFrom());
                    resultObj.put("crossTo", crosschain.getCrossTo());
                    resultObj.put("crossType", crosschain.getCrossType());
                    resultObj.put("txHash", crosschain.getTxHash());

                    // 设置响应数据
                    response.setRet(ResultCode.SUCCESS);
                    response.setData(resultObj);

                } catch (Exception e) {
                    // 错误处理
                    response.setRet(ResultCode.FAILURE);
                    response.setMessage("Failed to query transaction info. Error: " + e.getMessage());
                }

                return response;
            }

    @Override
    public CommonResp addCrossTx(CrossReq crossReq) {
        String targetUrl = "http://<server_ip>:8090/cross_chain";
        String logs = "";
        try {
                URL url = new URL(targetUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                logs = response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
        }


}

//    public CommonResp doCross(CrossReq crossreq) {
//        int crossTYPE = crossreq.getCrossType();//from和to是区块链的编号，1，2是两条以太坊，3是chainmaker
//        int crossFROM = crossreq.getCrossFrom();
//        int crossTO = crossreq.getCrossTo();
//        //int crossCONTRACT =crossreq.getCrossContract(); 合约选择，毕设中没有涉及到，可以在改进版本中补全
//        int PARAM = crossreq.getParam();
//        CommonResp queryBlockInfoResp = new CommonResp();
//        String logs = null;
//        try {
//            SSHConfig.connect();
//            if (crossTYPE == 2) {//中继跨链
//                if (crossTO == 3) {
//
//                    logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth2chainmaker -r true -ap beefAccess -ag " + PARAM, "UTF-8");
//
//                } else {
//                    if (crossFROM == 1) {//Paris to Tianjin
//                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 12", "UTF-8");
//                    }
//                    if (crossFROM == 2) {//Paris to Beijing
//                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 21", "UTF-8");
//
//                    }
//                }
//            }
//            if (crossTYPE == 1) {
//                if (crossTO == 3) {
//
//                    logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth2chainmaker -r true -ap beefAccess -ag " + PARAM, "UTF-8");
//
//                } else {
//                    if (crossFROM == 1) {//Paris to Tianjin
//                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 12", "UTF-8");
//                    }
//                    if (crossFROM == 2) {//Paris to Beijing
//                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 21", "UTF-8");
//
//                    }
//                }
//            }
////            System.out.println("cross-chain finished");
//        } catch (Exception e) {
//            System.out.println("SSH ERROR");
//        }
///*
//交易哈希部分暂时毕设未实现
//        String[] log = logs.split("\n");
//        String txfrom = "";
//        String txto = "";
//        String txback = "";
//        if (transport_cityblock == CityBlock.Tianjin){//Paris to Tianjin
//            String m1 = "eth source TxHash is:  ";
//            String m2 = "eth target TxHash is:  ";
//            String m3 = "eth response TxHash is:  ";
//            for (int i=0; i<log.length; i++){
//                if (log[i].indexOf(m1) != -1){
////                    System.out.println(log[i]);
////                    System.out.println(log[i].indexOf(m1));
////                    System.out.println(log[i].length());
//                    txfrom = log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+66);
//                    break;
//                }
//            }
//            for (int i=0; i<log.length; i++){
//                if (log[i].indexOf(m2) != -1){
////                    System.out.println(log[i]);
////                    System.out.println(log[i].indexOf(m2));
////                    System.out.println(log[i].length());
//                    txto = log[i].substring(log[i].indexOf(m2)+m2.length(),log[i].indexOf(m2)+m2.length()+66);
//                    break;
//                }
//            }
//            for (int i=0; i<log.length; i++){
//                if (log[i].indexOf(m3) != -1){
//                    txback = log[i].substring(log[i].indexOf(m3)+m3.length(),log[i].indexOf(m3)+m3.length()+66);
//                    break;
//                }
//            }
//        }
//        if (transport_cityblock == CityBlock.Beijing){//Paris to Beijing
//            String m1 = "eth source TxHash is:  ";
//            String m2 = "chainmaker target TxId is:  ";
//            String m3 = "eth response TxHash is:  ";
//            for (int i=0; i<log.length; i++){
//                if (log[i].indexOf(m1) != -1){
//                    txfrom = log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+66);
//                    break;
//                }
//            }
//            for (int i=0; i<log.length; i++){
//                if (log[i].indexOf(m2) != -1){
//                    txto = log[i].substring(log[i].indexOf(m2)+m2.length(),log[i].indexOf(m2)+m2.length()+64);
//                    break;
//                }
//            }
//            for (int i=0; i<log.length; i++){
//                if (log[i].indexOf(m3) != -1){
//                    txback = log[i].substring(log[i].indexOf(m3)+m3.length(),log[i].indexOf(m3)+m3.length()+66);
//                    break;
//                }
//            }
//        }
//        System.out.println("====Crosschain Key Messages====");
//        System.out.println("[from id]"+crossFROM);
//        System.out.println("[to id]"+crossTO);
//        System.out.println("[param]"+PARAM);
//        System.out.println("[type]"+crossTYPE);
//        System.out.println("[Source TX]"+txfrom);
//        System.out.println("[Target TX]"+txto);
//        System.out.println("[Response TX]"+txback);
//        System.out.println("===============================");
//        Crosschain cross = new Crosschain()
//
//                .setCrossFrom(String.valueOf(crossFROM))
//                .setCrossTo(String.valueOf(crossTO))
//                .setTxFrom(txfrom)
//                .setTxTo(txto)
//                .setTxBack(txback);
//
//                */
//        JSONObject Crosschaininfo = new JSONObject();
//
//
//        Crosschaininfo.put("crossFrom", crossFROM);
//        Crosschaininfo.put("crossTo", crossTO);
//        Crosschaininfo.put("crosType", crossTYPE);
//        //Crosschaininfo.put("crossContract", crossCONTRACT); 合约选择，毕设中暂时不需要
//        Crosschaininfo.put("crossParam", PARAM);
//        Crosschaininfo.put("crossResult", 1);
//        //Crosschaininfo.put("txFrom",txfrom);哈希部分毕设暂时不需要
//        // Crosschaininfo.put("txTo",txto);
//        //Crosschaininfo.put("txBack",txback);
//
//
//
//        Crosschain crosschain = new Crosschain()
//                .setCrossFrom(crossreq.getCrossFrom())
//                .setCrossTo(crossreq.getCrossTo())
//                .setCrossType(crossreq.getCrossType())//改一下直连中继为布尔型
//                .setCrossResult(true)
//                .setCrossParam(crossreq.getParam())
//                .setCrossTime(new Date());
//
//        // 查询自增id下一个是多少
//        QueryWrapper<Crosschain> wrapper = new QueryWrapper<>();
//        wrapper.select("cross_id");
//        List<Crosschain> cross = crosschainMapper.selectList(wrapper);
//        Long maxId = Long.valueOf(0);
//        for (int i = 0; i < cross.size(); i++) {
//            Long perId = cross.get(i).getCrossId();
//            if (perId > maxId) maxId = perId;
//        }
//        maxId = maxId + 1;
//
//        Crosschaininfo.put("crossId", maxId);
//        queryBlockInfoResp.setRet(ResultCode.SUCCESS);
//        queryBlockInfoResp.setData(Crosschaininfo);
//        int insert = this.crosschainMapper.insert(crosschain);
//        return queryBlockInfoResp;
//    }

