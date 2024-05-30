package com.tanklab.supply.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanklab.supply.common.*;
import com.tanklab.supply.ds.req.*;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Chain;
import com.tanklab.supply.mapper.ChainMapper;
import com.tanklab.supply.service.ChainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chainmaker.pb.common.ChainmakerTransaction;
import org.chainmaker.pb.config.ChainConfigOuterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.TimeUnit;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.chainmaker.sdk.ChainClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * <p>
 * 链信息表 服务实现类
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
/*
 * public class ChainServiceImpl extends ServiceImpl<ChainMapper, Chain>
 * implements ChainService {
 * 
 * 
 * }
 */
@Service
public class ChainServiceImpl extends ServiceImpl<ChainMapper, Chain> implements ChainService {
    // 这边的post格式用编号是不是比链IP更方便前端开发？如果是的话可以修改
    @Autowired
    private ChainMapper chainMapper;
    @Autowired
    public ChainServiceImpl ChainService;

    @Override
    public CommonResp querychainInfo() {
        CommonResp querychainresp = new CommonResp();

        QueryWrapper<Chain> wrapper = new QueryWrapper<>();
        wrapper.select("chain_id", "ip_address", "port", "chain_type");
        List<Chain> chains = chainMapper.selectList(wrapper);

        JSONObject chainsinfo = new JSONObject();
        JSONArray chainsarr = new JSONArray();
        for (int i = 0; i < chains.size(); i++) {
            JSONObject perchaininfo = new JSONObject();
            perchaininfo.put("chainId", chains.get(i).getChainId());
            perchaininfo.put("ipAddress", chains.get(i).getIpAddress());
            perchaininfo.put("port", chains.get(i).getPort());
            perchaininfo.put("chainType", chains.get(i).getChainType());
            chainsarr.add(perchaininfo);
        }
        chainsinfo.put("chainsinfo", chainsarr);
        querychainresp.setRet(ResultCode.SUCCESS);
        querychainresp.setData(chainsinfo);
        System.out.println(chainsinfo);
        return querychainresp;
    }

    public CommonResp checkChainnewblock(ChainReq chainreq) {////////////
        CommonResp chainresp = new CommonResp();

        if (String.valueOf(chainreq.getChainIP()) == "ETH") {
            try {
                // 创建 OkHttpClient 实例，并设置超时时间
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(1000, TimeUnit.SECONDS)
                        .readTimeout(1000, TimeUnit.SECONDS);

                // 创建自定义的 HttpService，并传入 OkHttpClient 实例
                HttpService httpService = new HttpService(String.valueOf(chainreq.getChainIP()), builder.build());
                // HttpService httpService = new
                // HttpService(String.valueOf("http://116.204.36.31:10012"), builder.build());
                // 创建 Web3j 实例
                Web3j web3j = Web3j.build(httpService);

                // 查询该链的块高
                BigInteger blockHeight = web3j.ethBlockNumber().send().getBlockNumber();
                JSONObject heightinfo = new JSONObject();
                heightinfo.put("heightinfo", blockHeight);
                chainresp.setData(heightinfo);
            } catch (IOException e) {
                e.printStackTrace();
                chainresp.setData("Failed to connect to the blockchain node.");
            }
        } else if (String.valueOf(chainreq.getChainIP()) == "ChainMaker") {
            String logs = "";
            try {
                SSHConfig.connect();
                logs = SSHConfig.executeCMD(
                        "cd ~/ChainMaker/chainmaker-go/tools/cmc && ./cmc query block-by-height --chain-id=chain1 --sdk-conf-path=./testdata/sdk_config.yml",
                        "UTF-8");
            } catch (Exception e) {
                System.out.println("SSH ERROR");
            }
            BigInteger blockHeight = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("block")
                    .getAsJsonObject("header")
                    .get("block_height").getAsBigInteger();
            JSONObject heightinfo = new JSONObject();
            heightinfo.put("heightinfo", blockHeight);
            chainresp.setData(heightinfo);
        } else {
            String targetUrl = "http://116.204.36.31:8000/api/blockChain/blockHeight";
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
                BigInteger blockHeight = BigInteger.valueOf(Long.parseLong(response.toString()));
                JSONObject heightinfo = new JSONObject();
                heightinfo.put("heightinfo", blockHeight);
                chainresp.setData(heightinfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return chainresp;
    }

    public CommonResp checkHeightInfo(BlockheightReq blockheightReq) { /////////////////
        CommonResp queryBlockInfoResp = new CommonResp();
        // 用块高来查询
        // 查询指定块高的区块信息
        if (String.valueOf(blockheightReq.getChainIP()) == "ETH") {
            Web3j web3j = Web3j.build(new HttpService(blockheightReq.getChainIP())); // 替换为你的节点地址
            try {
                DefaultBlockParameter blockParameter = new DefaultBlockParameterNumber(
                        Long.parseLong(blockheightReq.getBlockHEIGHT()));
                EthBlock.Block block = web3j.ethGetBlockByNumber(blockParameter, false)
                        .send()
                        .getBlock();

                BigInteger timestamp = block.getTimestamp();
                BigInteger blockSize = block.getSize();
                String previousBlockAddress = block.getParentHash();
                BigInteger gasLimit = block.getGasLimit();
                BigInteger gasUsed = block.getGasUsed();
                String stateRoot = block.getStateRoot();
                BigInteger totalDifficulty = block.getTotalDifficulty();
                BigInteger difficulty = block.getDifficulty();
                BigInteger blockReward = block.getGasUsed().multiply(block.getGasLimit());
                String minerAddress = block.getMiner();
                int transactionCount = block.getTransactions().size();

                JSONObject blockInfo = new JSONObject();
                blockInfo.put("blockHeight", blockheightReq.getBlockHEIGHT());
                blockInfo.put("timestamp", timestamp);
                blockInfo.put("blockSize", blockSize);
                blockInfo.put("transactionCount", transactionCount);
                blockInfo.put("previousBlockAddress", previousBlockAddress);
                blockInfo.put("gasLimit", gasLimit);
                blockInfo.put("gasUsed", gasUsed);
                blockInfo.put("stateRoot", stateRoot);
                blockInfo.put("totalDifficulty", totalDifficulty);
                blockInfo.put("difficulty", difficulty);
                blockInfo.put("blockReward", blockReward);
                blockInfo.put("minerAddress", minerAddress);

                queryBlockInfoResp.setRet(ResultCode.SUCCESS);
                queryBlockInfoResp.setData(blockInfo);
            } catch (IOException e) {
                queryBlockInfoResp.setData("Failed to connect to the blockchain node.");
            }
        } else if (String.valueOf(blockheightReq.getChainIP()) == "ChainMaker") {
            String logs = "";
            try {
                SSHConfig.connect();
                String cmd = "cd ~/ChainMaker/chainmaker-go/tools/cmc && ./cmc query block-by-height "
                        + blockheightReq.getBlockHEIGHT()
                        + " --chain-id=chain1 --sdk-conf-path=./testdata/sdk_config.yml";
                logs = SSHConfig.executeCMD(cmd, "UTF-8");
            } catch (Exception e) {
                // System.out.println("SSH ERROR");
                queryBlockInfoResp.setData("Failed to connect to the blockchain node.");
            }

            // System.out.println("ChainMaker查询结束...");
            JsonObject headerJsonObj = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("block")
                    .getAsJsonObject("header");
            BigInteger blockHeight = headerJsonObj.get("block_height").getAsBigInteger();
            String blockHash = headerJsonObj.get("block_hash").getAsString();
            BigInteger timeStamp = headerJsonObj.get("block_timestamp").getAsBigInteger();
            String dagHash = headerJsonObj.get("dag_hash").getAsString();
            BigInteger transactionCount = headerJsonObj.get("tx_count").getAsBigInteger();
            String previousBlockHash = headerJsonObj.get("pre_block_hash").getAsString();
            String rwSetRoot = headerJsonObj.get("rw_set_root").getAsString();
            String proposerMemberInfo = headerJsonObj.getAsJsonObject("proposer").get("member_info").getAsString();
            String signature = headerJsonObj.get("signature").getAsString();
            String txRoot = headerJsonObj.get("tx_root").getAsString();

            JSONObject blockInfo = new JSONObject();
            blockInfo.put("blockHeight", blockHeight);
            blockInfo.put("blockHash", blockHash);
            blockInfo.put("timeStamp", timeStamp);
            blockInfo.put("dagHash", dagHash);
            blockInfo.put("transactionCount", transactionCount);
            blockInfo.put("previousBlockHash", previousBlockHash);
            blockInfo.put("rwSetRoot", rwSetRoot);
            blockInfo.put("proposerMemberInfo", proposerMemberInfo);
            blockInfo.put("signature", signature);
            blockInfo.put("txRoot", txRoot);

            queryBlockInfoResp.setRet(ResultCode.SUCCESS);
            queryBlockInfoResp.setData(blockInfo);

        } else {
            String targetUrl = "http://116.204.36.31:8000/api/blockChain/blockByHeight?blockHeight="
                    + blockheightReq.getBlockHEIGHT() + "&includeTransactions=true";
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

            // System.out.println("H2hain查询结束...");
            JsonObject headerJsonObj = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("Header");
            BigInteger blockHeight = headerJsonObj.get("Height").getAsBigInteger();
            String blockHash = JsonParser.parseString(logs).getAsJsonObject().get("BlockHash").getAsString();
            BigInteger timeStamp = headerJsonObj.get("Time").getAsBigInteger();
            BigInteger blockSize = JsonParser.parseString(logs).getAsJsonObject().get("BlockSize").getAsBigInteger();
            BigInteger transactionCount = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("Body")
                    .get("TransactionsCount").getAsBigInteger();
            String previousBlockHash = headerJsonObj.get("PreviousBlockHash").getAsString();
            String merkleTreeRootOfWorldState = headerJsonObj.get("MerkleTreeRootOfWorldState").getAsString();
            String merkleTreeRootOfTransactions = headerJsonObj.get("MerkleTreeRootOfTransactions").getAsString();
            String merkleTreeRootOfTransactionState = headerJsonObj.get("MerkleTreeRootOfTransactionState")
                    .getAsString();
            String signerPubkey = headerJsonObj.get("SignerPubkey").getAsString();

            JSONObject blockInfo = new JSONObject();
            blockInfo.put("blockHeight", blockHeight);
            blockInfo.put("blockHash", blockHash);
            blockInfo.put("timeStamp", timeStamp);
            blockInfo.put("blockSize", blockSize);
            blockInfo.put("transactionCount", transactionCount);
            blockInfo.put("previousBlockHash", previousBlockHash);
            blockInfo.put("merkleTreeRootOfWorldState", merkleTreeRootOfWorldState);
            blockInfo.put("merkleTreeRootOfTransactions", merkleTreeRootOfTransactions);
            blockInfo.put("merkleTreeRootOfTransactionState", merkleTreeRootOfTransactionState);
            blockInfo.put("signerPubkey", signerPubkey);

            queryBlockInfoResp.setRet(ResultCode.SUCCESS);
            queryBlockInfoResp.setData(blockInfo);
        }

        return queryBlockInfoResp;
    }

    public CommonResp checkNewBlock(ChainReq chainreq) {//////////////////
        CommonResp queryNewBlock = new CommonResp();
        JSONArray blocks = new JSONArray();
        if (String.valueOf(chainreq.getChainIP()) == "ETH") {
            BigInteger blockHeight = new BigInteger("0");
            try {
                // 创建 OkHttpClient 实例，并设置超时时间
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .connectTimeout(1000, TimeUnit.SECONDS)
                        .readTimeout(1000, TimeUnit.SECONDS);

                // 创建自定义的 HttpService，并传入 OkHttpClient 实例
                HttpService httpService = new HttpService(String.valueOf(chainreq.getChainIP()), builder.build());
                // HttpService httpService = new
                // HttpService(String.valueOf("http://116.204.36.31:10012"), builder.build());
                // 创建 Web3j 实例
                Web3j web3j = Web3j.build(httpService);

                // 查询该链的块高
                blockHeight = web3j.ethBlockNumber().send().getBlockNumber();

            } catch (IOException e) {
                e.printStackTrace();
            }
            int j = 0;
            for (BigInteger i = blockHeight; i.compareTo(blockHeight) >= 0; i = i.subtract(new BigInteger("1"))) {
                Web3j web3j = Web3j.build(new HttpService(chainreq.getChainIP())); // 替换为你的节点地址
                try {
                    DefaultBlockParameter blockParameter = new DefaultBlockParameterNumber(
                            Long.parseLong(i.toString()));
                    EthBlock.Block block = web3j.ethGetBlockByNumber(blockParameter, false)
                            .send()
                            .getBlock();

                    BigInteger timestamp = block.getTimestamp();
                    BigInteger blockSize = block.getSize();
                    String previousBlockAddress = block.getParentHash();
                    BigInteger gasLimit = block.getGasLimit();
                    BigInteger gasUsed = block.getGasUsed();
                    String stateRoot = block.getStateRoot();
                    BigInteger totalDifficulty = block.getTotalDifficulty();
                    BigInteger difficulty = block.getDifficulty();
                    BigInteger blockReward = block.getGasUsed().multiply(block.getGasLimit());
                    String minerAddress = block.getMiner();
                    int transactionCount = block.getTransactions().size();

                    JSONObject blockInfo = new JSONObject();
                    blockInfo.put("blockHeight", i);
                    blockInfo.put("timestamp", timestamp);
                    blockInfo.put("blockSize", blockSize);
                    blockInfo.put("transactionCount", transactionCount);
                    blockInfo.put("previousBlockAddress", previousBlockAddress);
                    blockInfo.put("gasLimit", gasLimit);
                    blockInfo.put("gasUsed", gasUsed);
                    blockInfo.put("stateRoot", stateRoot);
                    blockInfo.put("totalDifficulty", totalDifficulty);
                    blockInfo.put("difficulty", difficulty);
                    blockInfo.put("blockReward", blockReward);
                    blockInfo.put("minerAddress", minerAddress);

                    j++;
                    blocks.add(blockInfo);

                    if (j == 10)
                        break;
                } catch (IOException e) {
                    queryNewBlock.setData("Failed to connect to the blockchain node.");
                }
            }
        } else if (String.valueOf(chainreq.getChainIP()) == "ChainMaker") {
            String logs = "";
            try {
                SSHConfig.connect();
                logs = SSHConfig.executeCMD(
                        "cd ~/ChainMaker/chainmaker-go/tools/cmc && ./cmc query block-by-height --chain-id=chain1 --sdk-conf-path=./testdata/sdk_config.yml",
                        "UTF-8");
            } catch (Exception e) {
                System.out.println("SSH ERROR");
            }
            BigInteger height = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("block")
                    .getAsJsonObject("header")
                    .get("block_height").getAsBigInteger();
            int j = 0;
            for (BigInteger i = height; i.compareTo(height) >= 0; i = i.subtract(new BigInteger("1"))) {
                String blocklog = "";
                try {
                    SSHConfig.connect();
                    String cmd = "cd ~/ChainMaker/chainmaker-go/tools/cmc && ./cmc query block-by-height "
                            + i.toString()
                            + " --chain-id=chain1 --sdk-conf-path=./testdata/sdk_config.yml";
                    blocklog = SSHConfig.executeCMD(cmd, "UTF-8");
                } catch (Exception e) {
                    // System.out.println("SSH ERROR");
                    queryNewBlock.setData("Failed to connect to the blockchain node.");
                }

                // System.out.println("ChainMaker查询结束...");
                JsonObject headerJsonObj = JsonParser.parseString(blocklog).getAsJsonObject().getAsJsonObject("block")
                        .getAsJsonObject("header");
                BigInteger blockHeight = headerJsonObj.get("block_height").getAsBigInteger();
                String blockHash = headerJsonObj.get("block_hash").getAsString();
                BigInteger timeStamp = headerJsonObj.get("block_timestamp").getAsBigInteger();
                String dagHash = headerJsonObj.get("dag_hash").getAsString();
                BigInteger transactionCount = headerJsonObj.get("tx_count").getAsBigInteger();
                String previousBlockHash = headerJsonObj.get("pre_block_hash").getAsString();
                String rwSetRoot = headerJsonObj.get("rw_set_root").getAsString();
                String proposerMemberInfo = headerJsonObj.getAsJsonObject("proposer").get("member_info").getAsString();
                String signature = headerJsonObj.get("signature").getAsString();
                String txRoot = headerJsonObj.get("tx_root").getAsString();

                JSONObject blockInfo = new JSONObject();
                blockInfo.put("blockHeight", blockHeight);
                blockInfo.put("blockHash", blockHash);
                blockInfo.put("timeStamp", timeStamp);
                blockInfo.put("dagHash", dagHash);
                blockInfo.put("transactionCount", transactionCount);
                blockInfo.put("previousBlockHash", previousBlockHash);
                blockInfo.put("rwSetRoot", rwSetRoot);
                blockInfo.put("proposerMemberInfo", proposerMemberInfo);
                blockInfo.put("signature", signature);
                blockInfo.put("txRoot", txRoot);

                j++;
                blocks.add(blockInfo);

                if (j == 10)
                    break;
            }
        } else {
            // String targetUrl = "http://116.204.36.31:8000/api/blockChain/blockHeight";
            BigInteger height = new BigInteger("0");
            try {
                URL url = new URL("http://116.204.36.31:8000/api/blockChain/blockHeight");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                height = BigInteger.valueOf(Long.parseLong(response.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int j = 0;
            for (BigInteger i = height; i.compareTo(height) >= 0; i = i.subtract(new BigInteger("1"))) {
                String targetUrl = "http://116.204.36.31:8000/api/blockChain/blockByHeight?blockHeight="
                        + i.toString() + "&includeTransactions=true";
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

                // System.out.println("H2hain查询结束...");
                JsonObject headerJsonObj = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("Header");
                BigInteger blockHeight = headerJsonObj.get("Height").getAsBigInteger();
                String blockHash = JsonParser.parseString(logs).getAsJsonObject().get("BlockHash").getAsString();
                BigInteger timeStamp = headerJsonObj.get("Time").getAsBigInteger();
                BigInteger blockSize = JsonParser.parseString(logs).getAsJsonObject().get("BlockSize")
                        .getAsBigInteger();
                BigInteger transactionCount = JsonParser.parseString(logs).getAsJsonObject().getAsJsonObject("Body")
                        .get("TransactionsCount").getAsBigInteger();
                String previousBlockHash = headerJsonObj.get("PreviousBlockHash").getAsString();
                String merkleTreeRootOfWorldState = headerJsonObj.get("MerkleTreeRootOfWorldState").getAsString();
                String merkleTreeRootOfTransactions = headerJsonObj.get("MerkleTreeRootOfTransactions").getAsString();
                String merkleTreeRootOfTransactionState = headerJsonObj.get("MerkleTreeRootOfTransactionState")
                        .getAsString();
                String signerPubkey = headerJsonObj.get("SignerPubkey").getAsString();

                JSONObject blockInfo = new JSONObject();
                blockInfo.put("blockHeight", blockHeight);
                blockInfo.put("blockHash", blockHash);
                blockInfo.put("timeStamp", timeStamp);
                blockInfo.put("blockSize", blockSize);
                blockInfo.put("transactionCount", transactionCount);
                blockInfo.put("previousBlockHash", previousBlockHash);
                blockInfo.put("merkleTreeRootOfWorldState", merkleTreeRootOfWorldState);
                blockInfo.put("merkleTreeRootOfTransactions", merkleTreeRootOfTransactions);
                blockInfo.put("merkleTreeRootOfTransactionState", merkleTreeRootOfTransactionState);
                blockInfo.put("signerPubkey", signerPubkey);

                j++;
                blocks.add(blockInfo);

                if (j == 10)
                    break;
            }
        }
        JSONObject tenblocks = new JSONObject();
        tenblocks.put("tenBlocksInfo", blocks);
        queryNewBlock.setRet(ResultCode.SUCCESS);
        queryNewBlock.setData(tenblocks);

        return queryNewBlock;
    }

    public CommonResp checkTxInfo(TxhashReq txhashreq) { ///////////////
        CommonResp queryTxInfoResp = new CommonResp();

        if (txhashreq.getChainIP() == "ETH") {
            Web3j web3j = Web3j.build(new HttpService(txhashreq.getChainIP()));
            try {

                EthTransaction transaction = web3j.ethGetTransactionByHash(txhashreq.getTxHASH()).sendAsync().get();

                String txHash = txhashreq.getTxHASH();
                String blockHash = transaction.getTransaction().get().getBlockHash();
                BigInteger blockNumber = transaction.getTransaction().get().getBlockNumber();
                String from = transaction.getTransaction().get().getFrom();
                BigInteger gas = transaction.getTransaction().get().getGas();
                BigInteger gasPrice = transaction.getTransaction().get().getGasPrice();
                BigInteger nonce = transaction.getTransaction().get().getNonce();
                String to = transaction.getTransaction().get().getTo();
                BigInteger value = transaction.getTransaction().get().getValue();
                String input = transaction.getTransaction().get().getInput();

                JSONObject txInfo = new JSONObject();
                txInfo.put("txHash", txHash);
                txInfo.put("blockHash", blockHash);
                txInfo.put("blockNumber", blockNumber);
                txInfo.put("from", from);
                txInfo.put("gas", gas);
                txInfo.put("gasPrice", gasPrice);
                txInfo.put("nonce", nonce);
                txInfo.put("to", to);
                txInfo.put("value", value);
                txInfo.put("input", input);

                queryTxInfoResp.setRet(ResultCode.SUCCESS);
                queryTxInfoResp.setData(txInfo);
            } catch (InterruptedException | ExecutionException e) {
                queryTxInfoResp.setData("Failed to connect to the blockchain node.");
            }
        } else if (txhashreq.getChainIP() == "ChainMaker") {
            String logs = "";
            try {
                SSHConfig.connect();
                String cmd = "cd ~/ChainMaker/chainmaker-go/tools/cmc && ./cmc query tx " + txhashreq.getTxHASH()
                        + " --chain-id=chain1 --sdk-conf-path=./testdata/sdk_config.yml";
                logs = SSHConfig.executeCMD(cmd, "UTF-8");
            } catch (Exception e) {
                System.out.println("SSH ERROR");
            }

            // System.out.println("ChainMaker查询结束...");
            JsonObject tx = JsonParser.parseString(logs).getAsJsonObject().get("transaction").getAsJsonObject();
            JsonObject result = tx.getAsJsonObject("result");
            JsonObject sender = tx.getAsJsonObject("sender");

            BigInteger blockHeight = JsonParser.parseString(logs).getAsJsonObject().get("block_height")
                    .getAsBigInteger();
            BigInteger blockTimestamp = JsonParser.parseString(logs).getAsJsonObject().get("block_timestamp")
                    .getAsBigInteger();
            BigInteger timestamp = tx.getAsJsonObject("payload").get("timestamp").getAsBigInteger();
            String rwSetHash = result.get("rw_set_hash").getAsString();
            String signature = sender.get("signature").getAsString();
            String signer = sender.getAsJsonObject("signer").get("member_info").getAsString();
            String contractName = tx.getAsJsonObject("payload").get("contract_name").getAsString();
            String method = tx.getAsJsonObject("payload").get("method").getAsString();

            JSONObject txInfo = new JSONObject();
            txInfo.put("txHash", txhashreq.getTxHASH());
            txInfo.put("blockHeight", blockHeight);
            txInfo.put("blockTimestamp", blockTimestamp);
            txInfo.put("timestamp", timestamp);
            txInfo.put("rwSetHash", rwSetHash);
            txInfo.put("signature", signature);
            txInfo.put("signer", signer);
            txInfo.put("contractName", contractName);
            txInfo.put("method", method);

            queryTxInfoResp.setRet(ResultCode.SUCCESS);
            queryTxInfoResp.setData(txInfo);

        } else {
            String targetUrl = "http://116.204.36.31:8000/api/blockChain/transactionResult?transactionId="
                    + txhashreq.getTxHASH();

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

            System.out.println("H2hain查询结束...");
            JsonObject jsonObj = JsonParser.parseString(logs).getAsJsonObject();
            JsonObject txObj = jsonObj.getAsJsonObject("Transaction");

            BigInteger transactionId = jsonObj.get("TransactionId").getAsBigInteger();
            String status = jsonObj.get("Status").getAsString();
            BigInteger blockNumber = jsonObj.get("BlockNumber").getAsBigInteger();
            // String blockHash = jsonObj.get("BlockHash").getAsString();
            String returnValue = jsonObj.get("ReturnValue").getAsString();
            // String error = jsonObj.get("Error").getAsString();
            BigInteger transactionSize = jsonObj.get("TransactionSize").getAsBigInteger();
            String from = txObj.get("From").getAsString();
            String to = txObj.get("To").getAsString();
            BigInteger refBlockNumber = txObj.get("RefBlockNumber").getAsBigInteger();
            String refBlockPrefix = txObj.get("RefBlockPrefix").getAsString();
            String methodName = txObj.get("MethodName").getAsString();
            String params = txObj.get("Params").getAsString();
            String signature = txObj.get("Signature").getAsString();

            JSONObject txInfo = new JSONObject();
            txInfo.put("txHash", txhashreq.getTxHASH());
            txInfo.put("status", status);
            txInfo.put("blockNumber", blockNumber);
            txInfo.put("returnValue", returnValue);
            txInfo.put("transactionSize", transactionSize);
            txInfo.put("from", from);
            txInfo.put("to", to);
            txInfo.put("refBlockNumber", refBlockNumber);
            txInfo.put("refBlockPrefix", refBlockPrefix);
            txInfo.put("methodName", methodName);
            txInfo.put("params", params);
            txInfo.put("signature", signature);

            queryTxInfoResp.setRet(ResultCode.SUCCESS);
            queryTxInfoResp.setData(txInfo);
        }

        return queryTxInfoResp;
    }

    //
    // /**
    // * 写一个结构体包括两个参数，blochhash和chainip
    // **/
    // public CommonResp checkBlockInfo(BlockhashReq blockhashreq) {
    // CommonResp queryBlockInfoResp = new CommonResp();
    //// 用哈希查询
    // // 查询指定区块哈希的区块信息
    // Web3j web3j = Web3j.build(new HttpService(blockhashreq.getChainIP())); //
    // 替换为你的节点地址
    // try {
    // EthBlock.Block block = web3j.ethGetBlockByHash(blockhashreq.getBlockHASH(),
    // false)
    // .send()
    // .getBlock();
    //
    // BigInteger timestamp = block.getTimestamp();
    // BigInteger blockSize = block.getSize();
    // //BigInteger transactionCount =
    // BigInteger.valueOf(block.getTransactions().size());
    // String previousBlockAddress = block.getParentHash();
    // BigInteger gasLimit = block.getGasLimit();
    // // 获取Gas消耗
    // BigInteger gasUsed = block.getGasUsed();
    // // 获取状态哈希
    // String stateRoot = block.getStateRoot();
    // // 获取总难度
    // BigInteger totalDifficulty = block.getTotalDifficulty();
    // // 获取难度
    // BigInteger difficulty = block.getDifficulty();
    // // 获取奖励
    // BigInteger blockReward = block.getGasUsed().multiply(block.getGasLimit());
    // // 获取矿工
    // String minerAddress = block.getMiner();
    //
    // // 获取交易数量
    // int transactionCount = block.getTransactions().size();
    //
    // JSONObject blockInfo = new JSONObject();
    // blockInfo.put("blockHash", blockhashreq.getBlockHASH());
    // blockInfo.put("timestamp", timestamp);
    // blockInfo.put("blockSize", blockSize);
    // blockInfo.put("transactionCount", transactionCount);//
    // blockInfo.put("previousBlockAddress", previousBlockAddress);
    // blockInfo.put("gasLimit", gasLimit);
    // blockInfo.put("gasUsed", gasUsed);
    // blockInfo.put("stateRoot", stateRoot);
    // blockInfo.put("totalDifficulty", totalDifficulty);
    // blockInfo.put("difficulty", difficulty);
    // blockInfo.put("blockReward", blockReward);
    // blockInfo.put("minerAddress", minerAddress);
    //
    // queryBlockInfoResp.setRet(ResultCode.SUCCESS);
    // queryBlockInfoResp.setData(blockInfo);
    // } catch (IOException e) {
    // // queryBlockInfoResp.setRet(ResultCode.INTERNAL_SERVER_ERROR);
    // queryBlockInfoResp.setData("Failed to connect to the blockchain node.");
    // }
    //
    // return queryBlockInfoResp;
    // }

    // public CommonResp addChain(AddChainReq addchainreq) { //暂时不需要
    // CommonResp addchainresp = new CommonResp();
    // // 将填入的信息添加到新的对象中
    // Chain chain = new Chain()
    // .setChainIp(addchainreq.getUploadchainIP())
    // .setChainType(addchainreq.getChainTYPE())
    // .setOwnerId(addchainreq.getOwnerID());
    // //.setIsProcessed(false);
    // // 查询自增id下一个是多少
    // QueryWrapper<Chain> wrapper = new QueryWrapper<>();
    // wrapper.select("chain_id");
    // List<Chain> blockchain = chainMapper.selectList(wrapper);
    // Long maxId = Long.valueOf(0);
    // for (int i = 0; i < blockchain.size(); i++) {
    // Long perId = blockchain.get(i).getChainId();
    // if (perId > maxId) maxId = perId;
    // }
    // maxId = maxId + 1;
    //
    // // 构造json
    // JSONObject chaininfo = new JSONObject();
    // chaininfo.put("UploadchainIP", chain.getChainIp());
    // chaininfo.put("getChainTYPE", chain.getChainType());
    // chaininfo.put("getOwnerID", chain.getOwnerId());
    //
    //
    // chaininfo.put("chainId", maxId);
    //
    // int insert = this.chainMapper.insert(chain);
    //
    // addchainresp.setRet(ResultCode.SUCCESS);
    // addchainresp.setData("add successfully!");
    // return addchainresp;
    // }

    /*
     * try {
     * 
     * QueryWrapper<Chain> wrapper = new QueryWrapper<>();
     * wrapper.eq("chain_id", chainId);
     * List<Chain> chains = chainMapper.selectList(wrapper);
     * Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));
     * 
     * /**先把链url与web3j相连接——在通过对创造的web3j实例对私连接点进行交互
     * 
     * 
     * } catch (Exception e) {
     * System.out.println("SSH ERROR");
     * }
     */

    /*
     * public CommonResp checkChain(){
     * ChainClient chainClient = new ChainClient();
     * CommonResp querychainresp = new CommonResp();
     * QueryWrapper<Chain> wrapper = new QueryWrapper<>();
     * wrapper.select("chain_ip");
     * List<Chain> chain = chainMapper.selectList(wrapper);
     * 
     * 
     * Discovery.ChainInfo chaininfo = chainClient.getChainInfo(10000);
     * querychainresp.setRet(ResultCode.SUCCESS);
     * querychainresp.setData(chaininfo);
     * return querychainresp;
     * }
     */
    //
    // public CommonResp checkChainmaker(ChainMakerReq chainmakerreq)
    // {
    //
    // ChainClient chainClient =new ChainClient();
    // ChainConfigOuterClass.ChainConfig chainConfig = null;
    // CommonResp chainmakerkInfoResp = new CommonResp();
    // try {
    // chainConfig = chainClient.getChainConfig(20000);
    //
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // chainmakerkInfoResp.setData(chainConfig);
    // return chainmakerkInfoResp;
    // }
    /// **链跨链尝试**/
    //
    // public CommonResp ChainmakerTxInfo(ChainMakerReq chainmakerreq)
    // {
    // ChainClient chainClient =new ChainClient();
    //
    // ChainmakerTransaction.TransactionInfo chainmakertx=null;
    // CommonResp chainmakerkTxResp = new CommonResp();
    //
    // try {
    // chainmakertx = chainClient.getTxByTxId(chainmakerreq.getTxId(),20000);
    //
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // chainmakerkTxResp.setData(chainmakertx);
    // return chainmakerkTxResp;
    // }
    // public CommonResp ChainmakerBlockInfo(ChainMakerReq chainmakerreq)
    // {
    // ChainClient chainClient =new ChainClient();
    //
    // long chainmakerblockhash = 0;
    // CommonResp chainmakerkTxResp = new CommonResp();
    // try {
    // chainmakerblockhash=chainClient.getCurrentBlockHeight(20000);
    // }catch (Exception e) {
    // e.printStackTrace();
    // }
    // chainmakerkTxResp.setData(chainmakerblockhash);
    // return chainmakerkTxResp;
    // }
    // public CommonResp ChainmakerNewBlock(ChainMakerReq chainmakerreq)
    // {
    // ChainClient chainClient =new ChainClient();
    //
    // //long chainmakerblockheight = 0;
    // CommonResp chainmakerkTxResp = new CommonResp();
    // try {
    // //chainmakerblockheight=chainClient.getBlockHeightByBlockHash(chainmakerreq.getBlockHash(),20000);
    // chainmakerkTxResp.setData(chainClient.getBlockHeightByBlockHash(chainmakerreq.getBlockHash(),20000));
    // }catch (Exception e) {
    // e.printStackTrace();
    // }
    // //chainmakerkTxResp.setData(chainmakerblockheight);
    // return chainmakerkTxResp;
    // }

    // 清空数据库中的内容

}
