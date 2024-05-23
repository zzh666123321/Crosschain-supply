package com.tanklab.supply.service.impl;
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

import org.web3j.protocol.core.methods.response.EthBlock;
import org.chainmaker.sdk.ChainClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>
 * 链信息表 服务实现类
 * </p>
 *
 * @author Bochen Hou
 * @since 2024-03-25
 */
/*public class ChainServiceImpl extends ServiceImpl<ChainMapper, Chain> implements ChainService {


}*/
@Service
public class ChainServiceImpl extends ServiceImpl<ChainMapper, Chain> implements ChainService {

    @Autowired
    private ChainMapper chainMapper;
    @Autowired
    public ChainServiceImpl ChainService;


    public CommonResp checkChainnewblock(ChainReq chainreq) {
        CommonResp chainresp = new CommonResp();

        try {
            // 创建 OkHttpClient 实例，并设置超时时间
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(1000, TimeUnit.SECONDS)
                    .readTimeout(1000, TimeUnit.SECONDS);

            // 创建自定义的 HttpService，并传入 OkHttpClient 实例
           HttpService httpService = new HttpService(String.valueOf(chainreq.getChainIP()), builder.build());
            //HttpService httpService = new HttpService(String.valueOf("http://116.204.36.31:10012"), builder.build());
            // 创建 Web3j 实例
            Web3j web3j = Web3j.build(httpService);

            // 查询该链的块高
            BigInteger blockHeight = web3j.ethBlockNumber().send().getBlockNumber();
            chainresp.setData(blockHeight);
        } catch (IOException e) {
            e.printStackTrace();
            chainresp.setData("Failed to connect to the blockchain node.");
        }

        return chainresp;
    }


    /**
     * 写一个结构体包括两个参数，blochhash和chainip
     **/
    public CommonResp checkBlockInfo(BlockhashReq blockhashreq) {
        CommonResp queryBlockInfoResp = new CommonResp();

        // 查询指定区块哈希的区块信息
        Web3j web3j = Web3j.build(new HttpService(blockhashreq.getChainIP())); // 替换为你的节点地址
        try {
            EthBlock.Block block = web3j.ethGetBlockByHash(blockhashreq.getBlockHASH(), false)
                    .send()
                    .getBlock();

            BigInteger timestamp = block.getTimestamp();
            BigInteger blockSize = block.getSize();
            //BigInteger transactionCount = BigInteger.valueOf(block.getTransactions().size());
            String previousBlockAddress = block.getParentHash();
            BigInteger gasLimit = block.getGasLimit();
            // 获取Gas消耗
            BigInteger gasUsed = block.getGasUsed();
            // 获取状态哈希
            String stateRoot = block.getStateRoot();
            // 获取总难度
            BigInteger totalDifficulty = block.getTotalDifficulty();
            // 获取难度
            BigInteger difficulty = block.getDifficulty();
            // 获取奖励
            BigInteger blockReward = block.getGasUsed().multiply(block.getGasLimit());
            // 获取矿工
            String minerAddress = block.getMiner();

            // 获取交易数量
            int transactionCount = block.getTransactions().size();

            JSONObject blockInfo = new JSONObject();
            blockInfo.put("blockHash", blockhashreq.getBlockHASH());
            blockInfo.put("timestamp", timestamp);
            blockInfo.put("blockSize", blockSize);
            blockInfo.put("transactionCount", transactionCount);//
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
            // queryBlockInfoResp.setRet(ResultCode.INTERNAL_SERVER_ERROR);
            queryBlockInfoResp.setData("Failed to connect to the blockchain node.");
        }

        return queryBlockInfoResp;
    }

    public CommonResp checkTxInfo(TxhashReq txhashreq) {//暂时用不上
        CommonResp queryTxInfoResp = new CommonResp();
        Web3j web3j = Web3j.build(new HttpService(txhashreq.getChainIP()));


        try {

            EthTransaction transaction = web3j.ethGetTransactionByHash(txhashreq.getTxHASH()).sendAsync().get();

            JSONObject txInfo = new JSONObject();
            txInfo.put("transaction", transaction.getTransaction());
            queryTxInfoResp.setRet(ResultCode.SUCCESS);
            queryTxInfoResp.setData(txInfo);
        } catch (InterruptedException | ExecutionException e) {
            queryTxInfoResp.setData("Failed to connect to the blockchain node.");
        }

        return queryTxInfoResp;
    }


    public CommonResp addChain(AddChainReq addchainreq) {//暂时不需要
        CommonResp addchainresp = new CommonResp();
        // 将填入的信息添加到新的Ox对象中
        Chain chain = new Chain()
                .setChainIp(addchainreq.getUploadchainIP())
                .setChainType(addchainreq.getChainTYPE())
                .setOwnerId(addchainreq.getOwnerID());
        //.setIsProcessed(false);
        // 查询自增id下一个是多少
        QueryWrapper<Chain> wrapper = new QueryWrapper<>();
        wrapper.select("chain_id");
        List<Chain> blockchain = chainMapper.selectList(wrapper);
        Long maxId = Long.valueOf(0);
        for (int i = 0; i < blockchain.size(); i++) {
            Long perId = blockchain.get(i).getChainId();
            if (perId > maxId) maxId = perId;
        }
        maxId = maxId + 1;

        // 构造json
        JSONObject chaininfo = new JSONObject();
        chaininfo.put("UploadchainIP", chain.getChainIp());
        chaininfo.put("getChainTYPE", chain.getChainType());
        chaininfo.put("getOwnerID", chain.getOwnerId());


        chaininfo.put("chainId", maxId);

        int insert = this.chainMapper.insert(chain);

        addchainresp.setRet(ResultCode.SUCCESS);
        addchainresp.setData("add successfully!");
        return addchainresp;
    }



        /*try {

            QueryWrapper<Chain> wrapper = new QueryWrapper<>();
            wrapper.eq("chain_id", chainId);
            List<Chain> chains = chainMapper.selectList(wrapper);
            Web3j web3j = Web3j.build(new HttpService("http://localhost:8545"));

/**先把链url与web3j相连接——在通过对创造的web3j实例对私连接点进行交互


        } catch (Exception e) {
            System.out.println("SSH ERROR");
        }*/


    /*public CommonResp checkChain(){
        ChainClient chainClient = new ChainClient();
        CommonResp querychainresp = new CommonResp();
        QueryWrapper<Chain> wrapper = new QueryWrapper<>();
        wrapper.select("chain_ip");
        List<Chain> chain = chainMapper.selectList(wrapper);


        Discovery.ChainInfo chaininfo = chainClient.getChainInfo(10000);
        querychainresp.setRet(ResultCode.SUCCESS);
        querychainresp.setData(chaininfo);
        return querychainresp;
    }*/

    public CommonResp checkChainmaker(ChainMakerReq chainmakerreq)
    {

        ChainClient chainClient =new ChainClient();
        ChainConfigOuterClass.ChainConfig chainConfig = null;
        CommonResp chainmakerkInfoResp = new CommonResp();
        try {
            chainConfig = chainClient.getChainConfig(20000);


        } catch (Exception e) {
            e.printStackTrace();
        }
        chainmakerkInfoResp.setData(chainConfig);
        return chainmakerkInfoResp;
    }
/**链跨链尝试**/

    public CommonResp ChainmakerTxInfo(ChainMakerReq chainmakerreq)
    {
        ChainClient chainClient =new ChainClient();

        ChainmakerTransaction.TransactionInfo chainmakertx=null;
        CommonResp chainmakerkTxResp = new CommonResp();

        try {
            chainmakertx = chainClient.getTxByTxId(chainmakerreq.getTxId(),20000);


        } catch (Exception e) {
            e.printStackTrace();
        }
        chainmakerkTxResp.setData(chainmakertx);
        return chainmakerkTxResp;
    }
    public CommonResp ChainmakerBlockInfo(ChainMakerReq chainmakerreq)
    {
        ChainClient chainClient =new ChainClient();

        long chainmakerblockhash = 0;
        CommonResp chainmakerkTxResp = new CommonResp();
        try {
            chainmakerblockhash=chainClient.getCurrentBlockHeight(20000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        chainmakerkTxResp.setData(chainmakerblockhash);
        return chainmakerkTxResp;
    }
    public CommonResp ChainmakerNewBlock(ChainMakerReq chainmakerreq)
    {
        ChainClient chainClient =new ChainClient();

        //long chainmakerblockheight = 0;
        CommonResp chainmakerkTxResp = new CommonResp();
        try {
            //chainmakerblockheight=chainClient.getBlockHeightByBlockHash(chainmakerreq.getBlockHash(),20000);
            chainmakerkTxResp.setData(chainClient.getBlockHeightByBlockHash(chainmakerreq.getBlockHash(),20000));
        }catch (Exception e) {
            e.printStackTrace();
        }
        //chainmakerkTxResp.setData(chainmakerblockheight);
        return chainmakerkTxResp;
    }


    // 清空数据库中的内容

    }







