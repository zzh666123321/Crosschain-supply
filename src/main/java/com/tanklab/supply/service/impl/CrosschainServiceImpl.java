package com.tanklab.supply.service.impl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                resultObj.put("srcIp", crosschain.getSrcIp());
                resultObj.put("srcPort", crosschain.getSrcPort());
                resultObj.put("dstIp", crosschain.getDstIp());
                resultObj.put("dstPort", crosschain.getDstPort());
                resultObj.put("srcChainType",crosschain.getSrcChainType());
                resultObj.put("dstChainType",crosschain.getDstChainType());
                resultObj.put("srcHash", crosschain.getSrcHash());
                resultObj.put("dstHash", crosschain.getDstHash());
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
                    resultObj.put("srcIp", crosschain.getSrcIp());
                    resultObj.put("srcPort", crosschain.getSrcPort());
                    resultObj.put("dstIp", crosschain.getDstIp());
                    resultObj.put("dstPort", crosschain.getDstPort());
                    resultObj.put("srcHash", crosschain.getSrcHash());
                    resultObj.put("dstHash", crosschain.getDstHash());

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
        String ethTxHash = new String();
        String chainmakerTxHash = new String();
        String h2chainTxHash = new String();
        CommonResp responseForF = new CommonResp();
        String []srcIpAndPort = crossReq.getSrcIp().split(":");
        String srcIp = srcIpAndPort[0];
        int  srcPort= Integer.parseInt(srcIpAndPort[1]);
        String []dstIpAndPort = crossReq.getDstIp().split(":");
        String dstIp = dstIpAndPort[0];
        int  dstPort= Integer.parseInt(dstIpAndPort[1]);
        Crosschain crosschain = new Crosschain().setSrcIp(srcIp).setDstIp(dstIp).setSrcChainType(crossReq.getSrcChainType()).setDstChainType(crossReq.getDstChainType()).setSrcPort(srcPort).setDstPort(dstPort);
        String targetUrl = "http://192.168.0.193/cross_chain?src-chain="+crosschain.getSrcChainType()+"&dst-chain="+crosschain.getDstChainType()+"&src-ip="+srcIp+"&dst-ip="+dstIp;
//        crosschain.setSrcPort(crossReq.getSrcPort());
//        crosschain.setDstPort(crossReq.getDstPort());
//        crosschain.setSrcIp(crossReq.getSrcIp());
//        crosschain.setDstIp(crossReq.getDstIp());
        System.out.println(crosschain);
//        crosschain.setDstIp("192.168.0.193");
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
                // 正则表达式模式
                String src_hash_rex = "SOURCE_CHAIN_TX.*?(?:交易哈希|txHash):\\s?(0x[a-fA-F0-9]{64}|[a-fA-F0-9]{64})";

                // 编译正则表达式
                Pattern pattern = Pattern.compile(src_hash_rex);

                // 创建匹配器对象
                Matcher matcher = pattern.matcher(logs);

                // 检查是否找到匹配
                if (matcher.find()) {
                    // 提取哈希值
                    String src_hash = matcher.group(1);
                    System.out.println("提取源链上的哈希值: " + src_hash);
                    crosschain.setSrcHash(src_hash);
                } else {
                    System.out.println("未找到匹配的哈希值");
                }
                ///提取sendToEth的所有字符串
                String regex_eth = "sendToEth.*?(?:交易哈希|txHash)：?\\s?(0x[a-fA-F0-9]{64}|[a-fA-F0-9]{64})";
                Pattern pattern_eth = Pattern.compile(regex_eth);
                Matcher matcher_eth = pattern_eth.matcher(logs);
                ArrayList<String> hashValues_eth = new ArrayList<>();

                // Iterate over all matches and add them to the list
                while (matcher_eth.find()) {
                    hashValues_eth.add(matcher_eth.group(1));
                }
                System.out.println(hashValues_eth);

                String regex_cmk = "sendToChainmaker.*?(?:交易哈希|txHash):?\\s?([a-fA-F0-9]{64})";
                Pattern pattern_cmk = Pattern.compile(regex_cmk);
                Matcher matcher_cmk= pattern_cmk.matcher(logs);
                ArrayList<String> hashValues_cmk = new ArrayList<>();

                // Iterate over all matches and add them to the list
                while (matcher_cmk.find()) {
                    hashValues_cmk.add(matcher_cmk.group(1));
                }
                System.out.println(hashValues_cmk);

                String regex_h2c = "sendToH2Chain.*?(?:交易哈希|txHash):\\s?([a-fA-F0-9]{64})";
                Pattern pattern_h2c = Pattern.compile(regex_h2c);
                Matcher matcher_h2c = pattern_h2c.matcher(logs);
                ArrayList<String> hashValues_h2c = new ArrayList<>();
            System.out.println(logs);
                // Iterate over all matches and add them to the list
                while (matcher_h2c.find()) {
                    hashValues_h2c.add(matcher_h2c.group(1));
                }
                System.out.println(hashValues_h2c);
                if(crosschain.getSrcChainType().equals("eth") && crosschain.getDstChainType().equals("chainmaker")){
                        crosschain.setResponseHash(hashValues_eth.get(0));
                        crosschain.setDstHash(hashValues_cmk.get(0));
                } else if (crosschain.getSrcChainType().equals("eth") && crosschain.getDstChainType().equals("h2chain")) {
                        crosschain.setResponseHash(hashValues_eth.get(0));
                        crosschain.setDstHash(hashValues_h2c.get(0));
                } else if ((crosschain.getSrcChainType().equals("eth") && crosschain.getDstChainType().equals("eth"))) {
                        crosschain.setResponseHash(hashValues_eth.get(0));
                        crosschain.setDstHash(hashValues_eth.get(1));
                } else if ((crosschain.getSrcChainType().equals("h2chain") && crosschain.getDstChainType().equals("eth"))) {
                        crosschain.setResponseHash(hashValues_h2c.get(0));
                        crosschain.setDstHash(hashValues_eth.get(0));
                } else if ((crosschain.getSrcChainType().equals("h2chain") && crosschain.getDstChainType().equals("chainmaker"))) {
                        crosschain.setResponseHash(hashValues_h2c.get(0));
                        crosschain.setDstHash(hashValues_cmk.get(0));
                }else if ((crosschain.getSrcChainType().equals("h2chain") && crosschain.getDstChainType().equals("h2chain"))) {
                        crosschain.setResponseHash(hashValues_h2c.get(0));
                        crosschain.setDstHash(hashValues_h2c.get(1));
                }else if ((crosschain.getSrcChainType().equals("chainmaker") && crosschain.getDstChainType().equals("eth"))) {
                        crosschain.setResponseHash(hashValues_cmk.get(0));
                        crosschain.setDstHash(hashValues_eth.get(0));
                }else if ((crosschain.getSrcChainType().equals("chainmaker") && crosschain.getDstChainType().equals("h2chain"))) {
                    crosschain.setResponseHash(hashValues_cmk.get(0));
                    crosschain.setDstHash(hashValues_h2c.get(0));
                }else if ((crosschain.getSrcChainType().equals("chainmaker") && crosschain.getDstChainType().equals("chainmaker"))) {
                    crosschain.setResponseHash(hashValues_cmk.get(0));
                    crosschain.setDstHash(hashValues_cmk.get(1));
                }


//                // 检查是否找到匹配
//                if (matcher_eth.find()) {
//                    // 提取哈希值
//                    String hash_eth = matcher_eth.group(1);
//                    System.out.println("eth链提取的哈希值: " + hash_eth);
//                    crosschain.setResponseHash(hash_eth);
//                } else {
//                    System.out.println("未找到匹配的哈希值");
//                }
//                if(crosschain.getSrcChainType().equals("eth") && crosschain.getDstChainType().equals("chainmaker")){
//                        String regex_eth = "sendToEth.*?(?:交易哈希|txHash)：?\\s?(0x[a-fA-F0-9]{64}|[a-fA-F0-9]{64})";
//                        Pattern pattern_eth = Pattern.compile(regex_eth);
//                        Matcher matcher_eth = pattern_eth.matcher(logs);
//                        // 检查是否找到匹配
//                        if (matcher_eth.find()) {
//                            // 提取哈希值
//                            String hash_eth = matcher_eth.group(1);
//                            System.out.println("eth链提取的哈希值: " + hash_eth);
//                            crosschain.setResponseHash(hash_eth);
//                        } else {
//                            System.out.println("未找到匹配的哈希值");
//                        }
//                        String regex_cmk = "sendToChainmaker.*?(?:交易哈希|txHash):?\\s?([a-fA-F0-9]{64})";
//                        Pattern pattern_cmk = Pattern.compile(regex_cmk);
//                        Matcher matcher_cmk= pattern_cmk.matcher(logs);
//                        // 检查是否找到匹配
//                        if (matcher_cmk.find()) {
//                            // 提取哈希值
//                            String hash_cmk = matcher_cmk.group(1);
//                            System.out.println("cmk链提取的哈希值: " + hash_cmk);
//                            crosschain.setDstHash(hash_cmk);
//                        } else {
//                            System.out.println("未找到匹配的哈希值");
//                        }
//                        crosschain.setSrcIp("192.168.0.2");
//                        crosschain.setDstIp("192.168.0.2");
//                        crosschain.setSrcPort(10012);
//                        crosschain.setDstPort(1000);
//                }else if(crosschain.getSrcChainType().equals("chainmaker") && crosschain.getDstChainType().equals("h2chain")){
//                    // 正则表达式模式
//                    String regex_h2c = "sendToH2Chain.*?(?:交易哈希|txHash):\\s?([a-fA-F0-9]{64})";
//
//                    // 编译正则表达式
//                    Pattern pattern_h2c = Pattern.compile(regex_h2c);
//
//                    // 创建匹配器对象
//                    Matcher matcher_h2c = pattern_h2c.matcher(logs);
//
//                    // 检查是否找到匹配
//                    if (matcher_h2c.find()) {
//                        // 提取哈希值
//                        String hash_h2c = matcher_h2c.group(1);
//                        System.out.println("h2chain提取的哈希值: " + hash_h2c);
//                        crosschain.setDstHash(hash_h2c);
//                    } else {
//                        System.out.println("未找到匹配的哈希值");
//                    }
//
//                    String regex_cmk = "sendToChainmaker.*?(?:交易哈希|txHash):?\\s?([a-fA-F0-9]{64})";
//                    Pattern pattern_cmk = Pattern.compile(regex_cmk);
//                    Matcher matcher_cmk= pattern_cmk.matcher(logs);
//                    // 检查是否找到匹配
//                    if (matcher_cmk.find()) {
//                        // 提取哈希值
//                        String hash_cmk = matcher_cmk.group(1);
//                        System.out.println("cmk链提取的哈希值: " + hash_cmk);
//                        crosschain.setResponseHash(hash_cmk);
//                    } else {
//                        System.out.println("未找到匹配的哈希值");
//                    }
//                    crosschain.setSrcIp("192.168.0.2");
//                    crosschain.setDstIp("192.168.0.193");
//                    crosschain.setSrcPort(1000);
//                    crosschain.setDstPort(8000);
//                }else if(crosschain.getSrcChainType().equals("h2chain") && crosschain.getDstChainType().equals("eth")){
//                        // 正则表达式模式
//                        String regex_h2c = "sendToH2Chain.*?(?:交易哈希|txHash):\\s?([a-fA-F0-9]{64})";
//
//                        // 编译正则表达式
//                        Pattern pattern_h2c = Pattern.compile(regex_h2c);
//
//                        // 创建匹配器对象
//                        Matcher matcher_h2c = pattern_h2c.matcher(logs);
//
//                        // 检查是否找到匹配
//                        if (matcher_h2c.find()) {
//                            // 提取哈希值
//                            String hash_h2c = matcher_h2c.group(1);
//                            System.out.println("h2chain提取的哈希值: " + hash_h2c);
//                            crosschain.setResponseHash(hash_h2c);
//                        } else {
//                            System.out.println("未找到匹配的哈希值");
//                        }
//                        String regex_eth = "sendToEth.*?(?:交易哈希|txHash)：?\\s?(0x[a-fA-F0-9]{64}|[a-fA-F0-9]{64})";
//                        Pattern pattern_eth = Pattern.compile(regex_eth);
//                        Matcher matcher_eth = pattern_eth.matcher(logs);
//                        // 检查是否找到匹配
//                        if (matcher_eth.find()) {
//                            // 提取哈希值
//                            String hash_eth = matcher_eth.group(1);
//                            System.out.println("eth链提取的哈希值: " + hash_eth);
//                            crosschain.setDstHash(hash_eth);
//                        } else {
//                            System.out.println("未找到匹配的哈希值");
//                        }
//                    crosschain.setSrcIp("192.168.0.193");
//                    crosschain.setDstIp("192.168.0.2");
//                    crosschain.setSrcPort(8000);
//                    crosschain.setDstPort(10012);
//                }
//                // 创建正则表达式模式
//                Pattern pattern = Pattern.compile("RunChainmaker2H2Chain] 调用长安链成功, 交易哈希: ([a-zA-z0-9]+)");
//                Pattern pattern1 = Pattern.compile("H2Chain发送成功,交易哈希:([a-zA-z0-9]+)");
//                Pattern pattern2 = Pattern.compile("sendToChainmaker] 调用长安链成功, 交易哈希: ([a-zA-z0-9]+)");
//
//
//                Matcher matcher = pattern.matcher(logs);
//                Matcher matcher1 = pattern1.matcher(logs);
//                Matcher matcher2 = pattern2.matcher(logs);
//                // 查找匹配的子字符串
//                if (matcher.find()) {
//                    // 提取哈希内容
//                    chainmakerTxHash = matcher.group(1);
////                    chainmakerTxHash = chainmakerTxHash.substring(0, chainmakerTxHash.length() - 1);
//                    // 打印哈希内容
//                    System.out.println("长安链哈希内容：" + chainmakerTxHash);
//                } else {
//                    // 如果未找到匹配的子字符串，则打印提示
//                    System.out.println("未找到长安链匹配的哈希内容。");
//                }
//                if (matcher1.find()) {
//                    // 提取哈希内容
//                    h2chainTxHash = matcher1.group(1);
////                    h2chainTxHash = h2chainTxHash.substring(0, h2chainTxHash.length() - 1);
//                    // 打印哈希内容
//                    System.out.println("海河哈希内容：" + h2chainTxHash);
//                } else {
//                    // 如果未找到匹配的子字符串，则打印提示
//                    System.out.println("未找到海河匹配的哈希内容。");
//                }
//
//            if (matcher2.find()) {
//                // 提取哈希内容
//                ethTxHash = matcher2.group(1);
////                    h2chainTxHash = h2chainTxHash.substring(0, h2chainTxHash.length() - 1);
//                // 打印哈希内容
//                System.out.println("response哈希内容：" + ethTxHash);
//            } else {
//                // 如果未找到匹配的子字符串，则打印提示
//                System.out.println("未找到response的哈希内容。");
//            }
                System.out.println(logs);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject resultObj = new JSONObject();
            resultObj.put("txId", crosschain.getTxId());
            resultObj.put("srcIp", crosschain.getSrcIp());
            resultObj.put("srcPort", crosschain.getSrcPort());
            resultObj.put("dstIp", crosschain.getDstIp());
            resultObj.put("dstPort", crosschain.getDstPort());
            resultObj.put("srcHash",crosschain.getSrcHash());
            resultObj.put("dstHash",crosschain.getDstHash());
            resultObj.put("responseHash",crosschain.getResponseHash());
            crosschain.setSrcHash(chainmakerTxHash);
            crosschain.setDstHash(h2chainTxHash);
            crosschain.setResponseHash(ethTxHash);

//            if(crosschain.getSrcChainType().equals("eth")){
//                resultObj.put("srcHash", ethTxHash);
//                resultObj.put("dstHash", chainmakerTxHash);
//                crosschain.setSrcHash(ethTxHash);
//                crosschain.setDstHash(chainmakerTxHash);
//            }else{
//                resultObj.put("srcHash", chainmakerTxHash);
//                resultObj.put("dstHash", ethTxHash);
//                crosschain.setSrcHash(chainmakerTxHash);
//                crosschain.setDstHash(ethTxHash);
//            }
            // 设置响应数据
            responseForF.setRet(ResultCode.SUCCESS);
            responseForF.setData(resultObj);
//            int insert = this.crosschainMapper.insert(crosschain);
        return responseForF;
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

