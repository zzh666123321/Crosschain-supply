package com.tanklab.supply.common;


import com.tanklab.supply.eth.contract.Internal;
import com.tanklab.supply.eth.contract.Safemath;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import com.tanklab.supply.eth.contract.Abroad;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * TODO:区块链操作
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-28
 */
public class BlockchainOperation {

    public static String addOxBlockchain(
            CityBlock cb,
            String ox_key,
            BigInteger ox_id,
            String breed,
            String end_time,
            BigInteger feeding_period,
            BigInteger weight,
            String location,
            String feed_person
    ){

        String netWorkUrl = cb.netWorkUrl;
        String walletKey = cb.walletKey;
        String contractAddress = BlockchainConfig.ox_contractAddress;
        long chainId = cb.chainId;
        try {
            //连接对应节点
            System.out.println("Uploading data to blockchain...");
            Web3j web3 = Web3j.build(new HttpService(netWorkUrl));
            Web3ClientVersion web3clientversion = web3.web3ClientVersion().send();
            Credentials credentials = Credentials.create(walletKey);
            TransactionManager transactionManager = new RawTransactionManager(
                    web3, credentials,chainId);
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            Abroad contract = Abroad.load(contractAddress,web3,
                    transactionManager,new StaticGasProvider(gasPrice, Contract.GAS_LIMIT));
            //调用合约方法
            RemoteCall<TransactionReceipt> setWord = contract.addCow(ox_key, ox_id, breed, end_time, feeding_period, weight, location, feed_person);

            TransactionReceipt transactionReceipt = setWord.send();
            String transactionHash = transactionReceipt.getTransactionHash();
            System.out.println("[Successfully Upload]\n"+transactionHash);
            return transactionHash;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public static String addBeefBlockchain(
            CityBlock cb,
            String beef_key,
            BigInteger beef_id,
            String breed,
            String process_time,
            String process_place,
            String process_person,
            String transport_time,
            String transport_place,
            String transport_person,
            BigInteger ox_id
    ){
        String netWorkUrl = cb.netWorkUrl;
        String walletKey = cb.walletKey;
        String contractAddress = BlockchainConfig.ox_contractAddress;
        long chainId = cb.chainId;
        try {
            //连接对应节点
            System.out.println("Uploading data to blockchain...");
            Web3j web3 = Web3j.build(new HttpService(netWorkUrl));
            Web3ClientVersion web3clientversion = web3.web3ClientVersion().send();
            Credentials credentials = Credentials.create(walletKey);
            TransactionManager transactionManager = new RawTransactionManager(
                    web3, credentials,chainId);
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            Abroad contract = Abroad.load(contractAddress,web3,
                    transactionManager,new StaticGasProvider(gasPrice, Contract.GAS_LIMIT));
            //调用合约方法
            RemoteCall<TransactionReceipt> setWord = contract.addBeef(beef_key, beef_id, breed, process_time, process_place, process_person, transport_time, transport_place, transport_person, ox_id);

            TransactionReceipt transactionReceipt = setWord.send();
            String transactionHash = transactionReceipt.getTransactionHash();
            System.out.println("[Successfully Upload]\n"+transactionHash);
            return transactionHash;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public static String sellBeefBlockchain(
            CityBlock cb,
            String beef_key,
            BigInteger beef_id,
            String transfer_time,
            String transfer_place,
            String transfer_person,
            String sell_place,
            BigInteger price,
            BigInteger weight,
            String quality_guarantee_time,
            BigInteger ox_id
    ){
        String netWorkUrl = cb.netWorkUrl;
        String walletKey = cb.walletKey;
        String contractAddress = BlockchainConfig.beef_contractAddress;
        long chainId = cb.chainId;
        try {
            //连接对应节点
            System.out.println("Uploading data to blockchain...");
            Web3j web3 = Web3j.build(new HttpService(netWorkUrl));
            Web3ClientVersion web3clientversion = web3.web3ClientVersion().send();
            Credentials credentials = Credentials.create(walletKey);
            TransactionManager transactionManager = new RawTransactionManager(
                    web3, credentials,chainId);
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            Safemath contract = Safemath.load(contractAddress,web3,
                    transactionManager,new StaticGasProvider(gasPrice, Contract.GAS_LIMIT));
            //调用合约方法
            RemoteCall<TransactionReceipt> setWord = contract.sellBeef(beef_key, beef_id, transfer_time, transfer_place, transfer_person, sell_place, price, weight, quality_guarantee_time, ox_id);

            TransactionReceipt transactionReceipt = setWord.send();
            String transactionHash = transactionReceipt.getTransactionHash();
            System.out.println("[Successfully Upload]\n"+transactionHash);
            return transactionHash;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public static void queryCowBlockchain(
            CityBlock cb,
            String ox_key
    ){
        String netWorkUrl = cb.netWorkUrl;
        String walletKey = cb.walletKey;
        String contractAddress = BlockchainConfig.ox_contractAddress;
        long chainId = cb.chainId;
        try {
            //连接对应节点
            Web3j web3 = Web3j.build(new HttpService(netWorkUrl));
            Web3ClientVersion web3clientversion = web3.web3ClientVersion().send();
            Credentials credentials = Credentials.create(walletKey);
            TransactionManager transactionManager = new RawTransactionManager(
                    web3, credentials,chainId);
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            Abroad contract = Abroad.load(contractAddress,web3,
                    transactionManager,new StaticGasProvider(gasPrice, Contract.GAS_LIMIT));
            //调用合约方法
            System.out.println(contract.queryCow(ox_key).send().component1());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);
        String txid = addBeefBlockchain(CityBlock.Paris,"12312313",new BigInteger(String.valueOf(100)),"111","2023-12-12","Paris","zzy","2023-12-12","Tianjin","zzy",new BigInteger(String.valueOf(220)));
//        String transactionId = addOxBlockchain(CityBlock.Paris,"12345621372138190", new BigInteger(String.valueOf(100)),"Yak","2023-07-26",new BigInteger(String.valueOf(40)),new BigInteger(String.valueOf(220)),"Paris","zzy");
//        System.out.println(transactionId);
        System.out.println(txid);
        Date date1 = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDate1 = sdf.format(date1);
        System.out.println(formattedDate1);
        long delta = date1.getTime()-date.getTime();
        System.out.println(delta);

        //        queryCowBlockchain("3e66b6bafeb7c3f5b551ba3dbb888f87e0726cc357c33bed328390a303f2cde1");
    }
}
