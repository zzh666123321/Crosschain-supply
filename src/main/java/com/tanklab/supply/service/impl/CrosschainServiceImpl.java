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
                resultObj.put("srcChainType", crosschain.getSrcChainType());
                resultObj.put("dstChainType", crosschain.getDstChainType());
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
        String[] srcIpAndPort = crossReq.getSrc().split(":");
        String srcIp = srcIpAndPort[0];
        int srcPort = Integer.parseInt(srcIpAndPort[1]);
        String[] dstIpAndPort = crossReq.getDst().split(":");
        String dstIp = dstIpAndPort[0];
        int dstPort = Integer.parseInt(dstIpAndPort[1]);
        Crosschain crosschain = new Crosschain().setSrcIp(srcIp).setDstIp(dstIp)
                .setSrcChainType(crossReq.getSrcChainType()).setDstChainType(crossReq.getDstChainType())
                .setSrcPort(srcPort).setDstPort(dstPort);
        String targetUrl = "http://192.168.0.44:8080/cross_chain?src-chain=" + crosschain.getSrcChainType()
                + "&dst-chain=" + crosschain.getDstChainType() + "&src-ip=" + srcIp + "&dst-ip=" + dstIp;
        // crosschain.setSrcPort(crossReq.getSrcPort());
        // crosschain.setDstPort(crossReq.getDstPort());
        // crosschain.setSrcIp(crossReq.getSrcIp());
        // crosschain.setDstIp(crossReq.getDstIp());
        System.out.println(crosschain);
        // crosschain.setDstIp("192.168.0.193");
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
            /// 提取sendToEth的所有字符串
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
            Matcher matcher_cmk = pattern_cmk.matcher(logs);
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
            if (crosschain.getSrcChainType().equals("eth") && crosschain.getDstChainType().equals("chainmaker")) {
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
            } else if ((crosschain.getSrcChainType().equals("h2chain")
                    && crosschain.getDstChainType().equals("chainmaker"))) {
                crosschain.setResponseHash(hashValues_h2c.get(0));
                crosschain.setDstHash(hashValues_cmk.get(0));
            } else if ((crosschain.getSrcChainType().equals("h2chain")
                    && crosschain.getDstChainType().equals("h2chain"))) {
                crosschain.setResponseHash(hashValues_h2c.get(0));
                crosschain.setDstHash(hashValues_h2c.get(1));
            } else if ((crosschain.getSrcChainType().equals("chainmaker")
                    && crosschain.getDstChainType().equals("eth"))) {
                crosschain.setResponseHash(hashValues_cmk.get(0));
                crosschain.setDstHash(hashValues_eth.get(0));
            } else if ((crosschain.getSrcChainType().equals("chainmaker")
                    && crosschain.getDstChainType().equals("h2chain"))) {
                crosschain.setResponseHash(hashValues_cmk.get(0));
                crosschain.setDstHash(hashValues_h2c.get(0));
            } else if ((crosschain.getSrcChainType().equals("chainmaker")
                    && crosschain.getDstChainType().equals("chainmaker"))) {
                crosschain.setResponseHash(hashValues_cmk.get(0));
                crosschain.setDstHash(hashValues_cmk.get(1));
            }

            // 提取源链响应哈希（获取第一个有效匹配）
            String h2cRespPattern = "\\[DEBG\\]:\\s+get resp txhash: ([a-fA-F0-9]+)";
            Pattern h2cRespRegex = Pattern.compile(h2cRespPattern);
            Matcher h2cRespMatcher = h2cRespRegex.matcher(logs);
            String h2cRespHash = h2cRespMatcher.find() ? h2cRespMatcher.group(1) : "";
            crosschain.setResponseHash(h2cRespHash);
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
        resultObj.put("srcHash", crosschain.getSrcHash());
        resultObj.put("dstHash", crosschain.getDstHash());
        resultObj.put("responseHash", crosschain.getResponseHash());
        // crosschain.setSrcHash(chainmakerTxHash);
        // crosschain.setDstHash(h2chainTxHash);
        // crosschain.setResponseHash(ethTxHash);

        // if(crosschain.getSrcChainType().equals("eth")){
        // resultObj.put("srcHash", ethTxHash);
        // resultObj.put("dstHash", chainmakerTxHash);
        // crosschain.setSrcHash(ethTxHash);
        // crosschain.setDstHash(chainmakerTxHash);
        // }else{
        // resultObj.put("srcHash", chainmakerTxHash);
        // resultObj.put("dstHash", ethTxHash);
        // crosschain.setSrcHash(chainmakerTxHash);
        // crosschain.setDstHash(ethTxHash);
        // }
        // 设置响应数据
        responseForF.setRet(ResultCode.SUCCESS);
        responseForF.setData(resultObj);
        try {
            // 尝试插入数据库
            crosschainMapper.insert(crosschain);
        } catch (Exception e) {
            // 数据库操作失败时只记录日志，不影响跨链操作的结果
            log.error("保存跨链记录到数据库失败: " + e.getMessage());
        }
        return responseForF;
    }

    @Override
    public CommonResp startGateways(String srcIp, String srcChainType, String dstIp, String dstChainType,
            String relayIp) {
        CommonResp response = new CommonResp();
        JSONObject resultObj = new JSONObject();
        
        try {
            // 1. 启动中继链网关
            startRelayChain(relayIp, resultObj);
            
            // 2. 启动源链网关
            startSourceChain(srcIp, srcChainType, dstIp, dstPort(dstChainType), dstChainType, resultObj);
            
            // 3. 启动目标链网关
            startDestinationChain(dstIp, dstChainType, srcIp, srcPort(srcChainType), srcChainType, resultObj);
            
            response.setRet(ResultCode.SUCCESS);
            response.setData(resultObj);
            
        } catch (Exception e) {
            response.setRet(ResultCode.FAILURE);
            response.setMessage("启动网关失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * 启动中继链网关
     */
    private void startRelayChain(String relayIp, JSONObject resultObj) throws Exception {
        SSHConfig.connect(relayIp); // 使用默认的用户名和密码

        // 确保脚本有执行权限
        String chmodCmd = "chmod +x /root/shell/relay_start.sh";
        SSHConfig.executeCMD(chmodCmd, "UTF-8");

        String startCmd = "source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/relay_start.sh > relay.log 2>&1 &";
        String result = SSHConfig.executeCMD(startCmd, "UTF-8");
        resultObj.put("relayStartResult", "中继链网关启动成功");
        resultObj.put("relayStartLog", result);
    }
    
    /**
     * 启动源链网关
     */
    private void startSourceChain(String srcIp, String srcChainType, String dstIp, int dstPort, 
                                String dstChainType, JSONObject resultObj) throws Exception {
        SSHConfig.connect(srcIp); // 使用默认的用户名和密码
        
        switch (srcChainType.toLowerCase()) {
            case "ethereum":
                String ethCmd = "source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/eth_start.sh > eth.log 2>&1 &";
                String ethResult = SSHConfig.executeCMD(ethCmd, "UTF-8");
                resultObj.put("ethereumStartResult_" + srcIp, "以太坊网关启动成功");
                resultObj.put("ethereumStartLog_" + srcIp, ethResult);
                break;
                
            case "chainmaker":
                // String chainId = String.valueOf(getChainId("chainmaker", srcIp));
                // todo: for local test
                String cmCmd = String.format(
                        "source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/chainmaker_start1.sh %s %s %d > chainmaker.log 2>&1 &",
                        "13002", "192.168.0.2", 8087);
                String cmResult = SSHConfig.executeCMD(cmCmd, "UTF-8");
                resultObj.put("chainmakerStartResult_" + srcIp, "长安链网关启动成功");
                resultObj.put("chainmakerStartLog_" + srcIp, cmResult); 
                break;
                
            case "h2chain":
                //for test
                String h2cCmd = String.format("source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/h2chain_start.sh %d > h2chain.log 2>&1 &", getChainId(dstChainType, dstIp));
                // String h2cCmd = String.format("source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/h2chain_start.sh %d > h2chain.log 2>&1 &", getChainId(dstChainType, dstIp));
                String h2cResult = SSHConfig.executeCMD(h2cCmd, "UTF-8");
                resultObj.put("h2chainStartResult_" + srcIp, "海河链网关启动成功");
                resultObj.put("h2chainStartLog_" + srcIp, h2cResult);
                break;
                
            default:
                throw new IllegalArgumentException("不支持的源链类型: " + srcChainType);
        }
    }
    
    /**
     * 启动目标链网关
     */
    private void startDestinationChain(String dstIp, String dstChainType, String srcIp, int srcPort,
                                     String srcChainType, JSONObject resultObj) throws Exception {
        SSHConfig.connect(dstIp); // 使用默认的用户名和密码
        
        switch (dstChainType.toLowerCase()) {
            case "ethereum":
                String ethCmd = "source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/eth_start.sh > eth.log 2>&1 &";
                String ethResult = SSHConfig.executeCMD(ethCmd, "UTF-8");
                resultObj.put("ethereumStartResult_" + dstIp, "以太坊网关启动成功");
                resultObj.put("ethereumStartLog_" + dstIp, ethResult);
                break;
                
            case "chainmaker":
                String chainId = String.valueOf(getChainId("chainmaker", dstIp));
                // todo: for local test
                String cmCmd = String.format(
                        "source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/chainmaker_start1.sh %s %s %d > chainmaker.log 2>&1 &",
                        "12002", "192.168.0.2", 8086);
                String cmResult = SSHConfig.executeCMD(cmCmd, "UTF-8");
                resultObj.put("chainmakerStartResult_" + dstIp, "长安链网关启动成功");
                resultObj.put("chainmakerStartLog_" + dstIp, cmResult);
                break;
                
            case "h2chain":
                // String h2cCmd = String.format("source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/h2chain_start.sh %d > h2chain.log 2>&1 &", getChainId(srcChainType, srcIp));
                //for test
                String h2cCmd = String.format("source /etc/profile && source ~/.bashrc && cd /root/shell && nohup /root/shell/h2chain_start.sh %d > h2chain.log 2>&1 &", getChainId(dstChainType, dstIp));
                String h2cResult = SSHConfig.executeCMD(h2cCmd, "UTF-8");
                resultObj.put("h2chainStartResult_" + dstIp, "海河链网关启动成功");
                resultObj.put("h2chainStartLog_" + dstIp, h2cResult);
                break;
                
            default:
                throw new IllegalArgumentException("不支持的目标链类型: " + dstChainType);
        }
    }
    
    /**
     * 获取链的默认端口
     */
    private int srcPort(String chainType) {
        switch (chainType.toLowerCase()) {
            case "ethereum":
                return 8086;
            case "chainmaker":
                return 8088;
            case "h2chain":
                return 8087;
            default:
                throw new IllegalArgumentException("不支持的链类型: " + chainType);
        }
    }
    
    /**
     * 获取链的默认端口
     */
    private int dstPort(String chainType) {
        return srcPort(chainType);
    }
    
    /**
     * 计算链ID
     */
    private int getChainId(String chainType, String ip) {
        // 从IP地址中提取最后一个数字
        String[] parts = ip.split("\\.");
        int lastNumber = Integer.parseInt(parts[3]);
        
        // 根据链类型计算chainId
        switch (chainType.toLowerCase()) {
            case "ethereum":
                return 12000 + lastNumber;
            case "chainmaker":
                return 11000 + lastNumber;
            case "h2chain":
                return 13000 + lastNumber;
            default:
                throw new IllegalArgumentException("不支持的链类型: " + chainType);
        }
    }

    /**
     * 执行跨链操作
     * 
     * @param srcIp        源链IP
     * @param srcChainType 源链类型
     * @param dstIp        目标链IP
     * @param dstChainType 目标链类型
     * @return 跨链操作结果
     */
    @Override
    public CommonResp executeCrossChain(String srcIp, String srcChainType, String dstIp, String dstChainType) {
        CommonResp response = new CommonResp();
        JSONObject resultObj = new JSONObject();

        try {
            SSHConfig.connect(srcIp);

            // 根据链类型执行不同的跨链命令
            switch (srcChainType.toLowerCase()) {
                case "ethereum":
                    // 计算源链和目标链的chainId
                    String getIpCmd = "ip -4 addr show eth0 | grep -oP '(?<=inet\\s)\\d+\\.\\d+\\.\\d+\\.\\d+' | cut -d. -f4";
                    String ipLastPart = SSHConfig.executeCMD(getIpCmd, "UTF-8").trim();
                    int srcChainId = 12000 + Integer.parseInt(ipLastPart);

                    // 目标链ID计算
                    String[] dstIpParts = dstIp.split("\\.");
                    int dstChainId = 12000;
                    if (dstChainType.equalsIgnoreCase("ethereum")) {
                        dstChainId = 12000 + Integer.parseInt(dstIpParts[3]);
                    } else if (dstChainType.equalsIgnoreCase("chainmaker")) {
                        dstChainId = 11000 + Integer.parseInt(dstIpParts[3]);
                    } else if (dstChainType.equalsIgnoreCase("h2chain")) {
                        dstChainId = 13000 + Integer.parseInt(dstIpParts[3]);
                    }

                    // 执行以太坊跨链命令
                    String ethCmd = String.format(
                            "source /etc/profile && source ~/.bashrc && cd ~/CIPS-Gemini-Ethereum && ./helper.sh SendCCMsg ws://127.0.0.1:10026 contract_addresses_%d.toml %d 1 1",
                            srcChainId, dstChainId);
                    String ethResult = SSHConfig.executeCMD(ethCmd, "UTF-8");

                    // 打印命令输出用于调试
                    System.out.println("命令完整输出：");
                    System.out.println(ethResult);

                    // 从命令输出中提取源链请求哈希
                    String ethReqPattern = "\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\s+\\[DEBG\\]:\\s+Send out on source chain succeed @ (0x[a-fA-F0-9]+)";
                    Pattern ethReqRegex = Pattern.compile(ethReqPattern);
                    Matcher ethReqMatcher = ethReqRegex.matcher(ethResult);

                    // 打印匹配结果用于调试
                    System.out.println("正在尝试匹配哈希值...");
                    String ethReqHash = "";
                    if (ethReqMatcher.find()) {
                        ethReqHash = ethReqMatcher.group(1);
                        System.out.println("成功提取到哈希值: " + ethReqHash);
                    } else {
                        System.out.println("未能匹配到哈希值，可能的原因：");
                        System.out.println("1. 输出格式不匹配");
                        System.out.println("2. 正则表达式不正确");
                        System.out.println("使用的正则表达式: " + ethReqPattern);
                        // 打印实际的输出内容，帮助调试
                        System.out.println("实际输出内容：");
                        System.out.println(ethResult);
                    }

                    // 等待15秒，确保日志已经生成
                    Thread.sleep(50000);

                    // 读取以太坊日志文件
                    String ethToH2cLogCmd = "cat /root/CIPS-Gemini-Ethereum/logs/eth.log";
                    String ethToH2cLogs = SSHConfig.executeCMD(ethToH2cLogCmd, "UTF-8");

                    // 连接目标链服务器并读取海河链日志
                    SSHConfig.connect(dstIp);
                    String h2cFromEthLogCmd = "cat /root/CIPS-Gemini-H2Chain/logs/h2chain.log";
                    String h2cFromEthLogs = SSHConfig.executeCMD(h2cFromEthLogCmd, "UTF-8");

                    // 提取源链响应哈希
                    String ethRespPattern = "get resp txhash: (0x[a-fA-F0-9]+)";
                    Pattern ethRespRegex = Pattern.compile(ethRespPattern);
                    Matcher ethRespMatcher = ethRespRegex.matcher(ethToH2cLogs);
                    String ethRespHash = "";
                    while (ethRespMatcher.find()) {
                        ethRespHash = ethRespMatcher.group(1);
                    }

                    // 提取目标链哈希
                    String ethToH2cDstPattern = "get req txhash: ([a-fA-F0-9]+)";
                    Pattern ethToH2cDstRegex = Pattern.compile(ethToH2cDstPattern);
                    Matcher ethToH2cDstMatcher = ethToH2cDstRegex.matcher(h2cFromEthLogs);
                    String ethToH2cDstHash = "";
                    while (ethToH2cDstMatcher.find()) {
                        ethToH2cDstHash = ethToH2cDstMatcher.group(1);
                    }

                    resultObj.put("dstHash", ethToH2cDstHash);
                    resultObj.put("srcRespHash", ethRespHash);
                    resultObj.put("srcReqHash", ethReqHash);
                    resultObj.put("crossChainResult", "以太坊跨链操作执行成功");
                    break;

                case "h2chain":
                    // 执行海河链跨链命令
                    String h2cCmd = "source /etc/profile && source ~/.bashrc && cd /root/CIPS-Gemini-H2Chain && ./crossH2C test";
                    String h2cResult = SSHConfig.executeCMD(h2cCmd, "UTF-8");

                    // 等待5秒，确保日志已经生成
                    Thread.sleep(35000);

                    // 读取海河链日志文件
                    String h2cSrcLogCmd = "cat /root/CIPS-Gemini-H2Chain/logs/h2chain.log";
                    String h2cSrcLogs = SSHConfig.executeCMD(h2cSrcLogCmd, "UTF-8");

                    // 连接目标链服务器
                    SSHConfig.connect(dstIp);

                    // 读取以太坊日志文件
                    String ethLogCmdH2c = "cat /root/CIPS-Gemini-Ethereum/logs/eth.log";
                    String ethLogsH2c = SSHConfig.executeCMD(ethLogCmdH2c, "UTF-8");

                    // 提取源链请求哈希
                    String h2cReqPattern = "Obtained request cmhash on the source chain\\(chainid: 13002, cmhash: ([a-fA-F0-9]+)\\)";
                    Pattern h2cReqRegex = Pattern.compile(h2cReqPattern);
                    Matcher h2cReqMatcher = h2cReqRegex.matcher(h2cSrcLogs);
                    String h2cReqHash = h2cReqMatcher.find() ? h2cReqMatcher.group(1) : "";

                    // 提取源链响应哈希（获取第一个有效匹配）
                    String h2cRespPattern = "\\[DEBG\\]:\\s+get resp txhash: ([a-fA-F0-9]+)";
                    Pattern h2cRespRegex = Pattern.compile(h2cRespPattern);
                    Matcher h2cRespMatcher = h2cRespRegex.matcher(h2cSrcLogs);
                    String h2cRespHash = h2cRespMatcher.find() ? h2cRespMatcher.group(1) : "";

                    // 提取目标链哈希
                    String h2cDstPattern = "get req txhash: (0x[a-fA-F0-9]+)";
                    Pattern h2cDstRegex = Pattern.compile(h2cDstPattern);
                    Matcher h2cDstMatcher = h2cDstRegex.matcher(ethLogsH2c);
                    String h2cDstHash = h2cDstMatcher.find() ? h2cDstMatcher.group(1) : "";

                    resultObj.put("dstHash", h2cDstHash);
                    resultObj.put("srcRespHash", h2cRespHash);
                    resultObj.put("srcReqHash", h2cReqHash);
                    resultObj.put("crossChainResult", "海河链跨链操作执行成功");
                    break;

                case "chainmaker":
                    // 执行长安链跨链命令
                    String cmCmd = "source /etc/profile && source ~/.bashrc && cd /root/CIPS-Gemini-ChainMaker && go run main.go send 1";
                    String cmResult = SSHConfig.executeCMD(cmCmd, "UTF-8");

                    // 等待5秒，确保日志已经生成
                    Thread.sleep(5000);

                    // 读取长安链日志文件
                    String cmLogCmd = "cat /root/CIPS-Gemini-ChainMaker/logs/chainmaker.log";
                    String cmLogs = SSHConfig.executeCMD(cmLogCmd, "UTF-8");

                    // 连接目标链服务器
                    SSHConfig.connect(dstIp);

                    String srcReqHash = "";
                    String srcRespHash = "";
                    String dstHash = "";

                    if (dstChainType.equalsIgnoreCase("ethereum")) {
                        // 读取以太坊日志文件
                        String ethLogCmd = "cat /root/CIPS-Gemini-Ethereum/logs/eth.log";
                        String ethLogs = SSHConfig.executeCMD(ethLogCmd, "UTF-8");

                        // 提取源链请求哈希
                        String srcReqPattern = "Obtained request cmhash on the source chain\\([0-9]+\\): ([a-fA-F0-9]+)";
                        Pattern srcReqRegex = Pattern.compile(srcReqPattern);
                        Matcher srcReqMatcher = srcReqRegex.matcher(cmLogs);
                        srcReqHash = srcReqMatcher.find() ? srcReqMatcher.group(1) : "";

                        // 提取源链响应哈希（获取第一个有效匹配）
                        String srcRespPattern = "\\[DEBG\\]:\\s+get resp txhash: ([a-fA-F0-9]+)";
                        Pattern srcRespRegex = Pattern.compile(srcRespPattern);
                        Matcher srcRespMatcher = srcRespRegex.matcher(cmLogs);
                        srcRespHash = srcRespMatcher.find() ? srcRespMatcher.group(1) : "";

                        // 提取目标链哈希
                        String dstPattern = "get req txhash: (0x[a-fA-F0-9]+)";
                        Pattern dstRegex = Pattern.compile(dstPattern);
                        Matcher dstMatcher = dstRegex.matcher(ethLogs);
                        dstHash = dstMatcher.find() ? dstMatcher.group(1) : "";

                    } else if (dstChainType.equalsIgnoreCase("h2chain")) {
                        // 读取海河链日志文件
                        String h2cLogCmd = "cat /root/CIPS-Gemini-H2Chain/logs/h2chain.log";
                        String h2cLogs = SSHConfig.executeCMD(h2cLogCmd, "UTF-8");

                        // 提取源链请求哈希
                        String srcReqPattern = "Obtained request cmhash on the source chain\\(11002\\): ([a-fA-F0-9]+)";
                        Pattern srcReqRegex = Pattern.compile(srcReqPattern);
                        Matcher srcReqMatcher = srcReqRegex.matcher(cmLogs);
                        srcReqHash = srcReqMatcher.find() ? srcReqMatcher.group(1) : "";

                        // 提取源链响应哈希（获取第一个有效匹配）
                        String srcRespPattern = "\\[DEBG\\]:\\s+get resp txhash: ([a-fA-F0-9]+)";
                        Pattern srcRespRegex = Pattern.compile(srcRespPattern);
                        Matcher srcRespMatcher = srcRespRegex.matcher(cmLogs);
                        srcRespHash = srcRespMatcher.find() ? srcRespMatcher.group(1) : "";

                        // 提取目标链哈希
                        String dstPattern = "Obtained response cmhash on the target chain\\(chainid: 13002, cmhash: ([a-fA-F0-9]+)\\)";
                        Pattern dstRegex = Pattern.compile(dstPattern);
                        Matcher dstMatcher = dstRegex.matcher(h2cLogs);
                        dstHash = dstMatcher.find() ? dstMatcher.group(1) : "";
                    }

                    resultObj.put("dstHash", dstHash);
                    resultObj.put("srcRespHash", srcRespHash);
                    resultObj.put("srcReqHash", srcReqHash);
                    resultObj.put("crossChainResult", "长安链跨链操作执行成功");
                    break;

                default:
                    throw new IllegalArgumentException("不支持的源链类型: " + srcChainType);
            }

            response.setRet(ResultCode.SUCCESS);
            response.setData(resultObj);

        } catch (Exception e) {
            response.setRet(ResultCode.FAILURE);
            response.setMessage("跨链操作失败: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    /**
     * 执行完整的跨链操作（包括启动网关和执行跨链）
     */
    @Override
    public CommonResp executeFullCrossChain(String srcIp, String srcChainType, String dstIp, String dstChainType,
            String relayIp) {
        CommonResp response = new CommonResp();

        try {
            // 第一步：启动网关
            CommonResp gatewayResponse = startGateways(srcIp, srcChainType, dstIp, dstChainType, relayIp);
            if (!ResultCode.SUCCESS.Code.equals(gatewayResponse.getCode())) {
                return gatewayResponse; // 如果网关启动失败，直接返回错误
            }

            // 等待网关启动完成（这里等待10秒，确保网关完全启动）
            Thread.sleep(10000);

            // 第二步：执行跨链操作
            CommonResp crossChainResponse = executeCrossChain(srcIp, srcChainType, dstIp, dstChainType);
            if (!ResultCode.SUCCESS.Code.equals(crossChainResponse.getCode())) {
                return crossChainResponse; // 如果跨链操作失败，直接返回错误
            }

            // 设置成功响应，只返回跨链执行结果
            response.setRet(ResultCode.SUCCESS);
            response.setData(crossChainResponse.getData());

        } catch (Exception e) {
            response.setRet(ResultCode.FAILURE);
            response.setMessage("完整跨链操作失败: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

}

// public CommonResp doCross(CrossReq crossreq) {
// int crossTYPE =
// crossreq.getCrossType();//from和to是区块链的编号，1，2是两条以太坊，3是chainmaker
// int crossFROM = crossreq.getCrossFrom();
// int crossTO = crossreq.getCrossTo();
// //int crossCONTRACT =crossreq.getCrossContract(); 合约选择，毕设中没有涉及到，可以在改进版本中补全
// int PARAM = crossreq.getParam();
// CommonResp queryBlockInfoResp = new CommonResp();
// String logs = null;
// try {
// SSHConfig.connect();
// if (crossTYPE == 2) {//中继跨链
// if (crossTO == 3) {
//
// logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private &&
// go run *.go -c eth2chainmaker -r true -ap beefAccess -ag " + PARAM, "UTF-8");
//
// } else {
// if (crossFROM == 1) {//Paris to Tianjin
// logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private &&
// go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 12",
// "UTF-8");
// }
// if (crossFROM == 2) {//Paris to Beijing
// logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private &&
// go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 21",
// "UTF-8");
//
// }
// }
// }
// if (crossTYPE == 1) {
// if (crossTO == 3) {
//
// logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private &&
// go run *.go -c eth2chainmaker -r true -ap beefAccess -ag " + PARAM, "UTF-8");
//
// } else {
// if (crossFROM == 1) {//Paris to Tianjin
// logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private &&
// go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 12",
// "UTF-8");
// }
// if (crossFROM == 2) {//Paris to Beijing
// logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private &&
// go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 21",
// "UTF-8");
//
// }
// }
//// System.out.println("cross-chain finished");
// } catch (Exception e) {
// System.out.println("SSH ERROR");
// }
/// *
// 交易哈希部分暂时毕设未实现
// String[] log = logs.split("\n");
// String txfrom = "";
// String txto = "";
// String txback = "";
// if (transport_cityblock == CityBlock.Tianjin){//Paris to Tianjin
// String m1 = "eth source TxHash is: ";
// String m2 = "eth target TxHash is: ";
// String m3 = "eth response TxHash is: ";
// for (int i=0; i<log.length; i++){
// if (log[i].indexOf(m1) != -1){
//// System.out.println(log[i]);
//// System.out.println(log[i].indexOf(m1));
//// System.out.println(log[i].length());
// txfrom =
// log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+66);
// break;
// }
// }
// for (int i=0; i<log.length; i++){
// if (log[i].indexOf(m2) != -1){
//// System.out.println(log[i]);
//// System.out.println(log[i].indexOf(m2));
//// System.out.println(log[i].length());
// txto =
// log[i].substring(log[i].indexOf(m2)+m2.length(),log[i].indexOf(m2)+m2.length()+66);
// break;
// }
// }
// for (int i=0; i<log.length; i++){
// if (log[i].indexOf(m3) != -1){
// txback =
// log[i].substring(log[i].indexOf(m3)+m3.length(),log[i].indexOf(m3)+m3.length()+66);
// break;
// }
// }
// }
// if (transport_cityblock == CityBlock.Beijing){//Paris to Beijing
// String m1 = "eth source TxHash is: ";
// String m2 = "chainmaker target TxId is: ";
// String m3 = "eth response TxHash is: ";
// for (int i=0; i<log.length; i++){
// if (log[i].indexOf(m1) != -1){
// txfrom =
// log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+66);
// break;
// }
// }
// for (int i=0; i<log.length; i++){
// if (log[i].indexOf(m2) != -1){
// txto =
// log[i].substring(log[i].indexOf(m2)+m2.length(),log[i].indexOf(m2)+m2.length()+64);
// break;
// }
// }
// for (int i=0; i<log.length; i++){
// if (log[i].indexOf(m3) != -1){
// txback =
// log[i].substring(log[i].indexOf(m3)+m3.length(),log[i].indexOf(m3)+m3.length()+66);
// break;
// }
// }
// }
// System.out.println("====Crosschain Key Messages====");
// System.out.println("[from id]"+crossFROM);
// System.out.println("[to id]"+crossTO);
// System.out.println("[param]"+PARAM);
// System.out.println("[type]"+crossTYPE);
// System.out.println("[Source TX]"+txfrom);
// System.out.println("[Target TX]"+txto);
// System.out.println("[Response TX]"+txback);
// System.out.println("===============================");
// Crosschain cross = new Crosschain()
//
// .setCrossFrom(String.valueOf(crossFROM))
// .setCrossTo(String.valueOf(crossTO))
// .setTxFrom(txfrom)
// .setTxTo(txto)
// .setTxBack(txback);
//
// */
// JSONObject Crosschaininfo = new JSONObject();
//
//
// Crosschaininfo.put("crossFrom", crossFROM);
// Crosschaininfo.put("crossTo", crossTO);
// Crosschaininfo.put("crosType", crossTYPE);
// //Crosschaininfo.put("crossContract", crossCONTRACT); 合约选择，毕设中暂时不需要
// Crosschaininfo.put("crossParam", PARAM);
// Crosschaininfo.put("crossResult", 1);
// //Crosschaininfo.put("txFrom",txfrom);哈希部分毕设暂时不需要
// // Crosschaininfo.put("txTo",txto);
// //Crosschaininfo.put("txBack",txback);
//
//
//
// Crosschain crosschain = new Crosschain()
// .setCrossFrom(crossreq.getCrossFrom())
// .setCrossTo(crossreq.getCrossTo())
// .setCrossType(crossreq.getCrossType())//改一下直连中继为布尔型
// .setCrossResult(true)
// .setCrossParam(crossreq.getParam())
// .setCrossTime(new Date());
//
// // 查询自增id下一个是多少
// QueryWrapper<Crosschain> wrapper = new QueryWrapper<>();
// wrapper.select("cross_id");
// List<Crosschain> cross = crosschainMapper.selectList(wrapper);
// Long maxId = Long.valueOf(0);
// for (int i = 0; i < cross.size(); i++) {
// Long perId = cross.get(i).getCrossId();
// if (perId > maxId) maxId = perId;
// }
// maxId = maxId + 1;
//
// Crosschaininfo.put("crossId", maxId);
// queryBlockInfoResp.setRet(ResultCode.SUCCESS);
// queryBlockInfoResp.setData(Crosschaininfo);
// int insert = this.crosschainMapper.insert(crosschain);
// return queryBlockInfoResp;
// }
