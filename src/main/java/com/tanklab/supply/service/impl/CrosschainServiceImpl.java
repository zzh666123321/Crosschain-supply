package com.tanklab.supply.service.impl;
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

    public CommonResp doCross(CrossReq crossreq) {
        int crossTYPE = crossreq.getCrossType();//from和to是区块链的编号，1，2是两条以太坊，3是chainmaker
        int crossFROM = crossreq.getCrossFrom();
        int crossTO = crossreq.getCrossTo();
        //int crossCONTRACT =crossreq.getCrossContract(); 合约选择，毕设中没有涉及到，可以在改进版本中补全
        int PARAM = crossreq.getParam();
        CommonResp queryBlockInfoResp = new CommonResp();
        String logs = null;
        try {
            SSHConfig.connect();
            if (crossTYPE == 2) {//中继跨链
                if (crossTO == 3) {

                    logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth2chainmaker -r true -ap beefAccess -ag " + PARAM, "UTF-8");

                } else {
                    if (crossFROM == 1) {//Paris to Tianjin
                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 12", "UTF-8");
                    }
                    if (crossFROM == 2) {//Paris to Beijing
                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 21", "UTF-8");

                    }
                }
            }
            if (crossTYPE == 1) {
                if (crossTO == 3) {

                    logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth2chainmaker -r true -ap beefAccess -ag " + PARAM, "UTF-8");

                } else {
                    if (crossFROM == 1) {//Paris to Tianjin
                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 12", "UTF-8");
                    }
                    if (crossFROM == 2) {//Paris to Beijing
                        logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag " + PARAM + " -cd 21", "UTF-8");

                    }
                }
            }
//            System.out.println("cross-chain finished");
        } catch (Exception e) {
            System.out.println("SSH ERROR");
        }
/*
交易哈希部分暂时毕设未实现
        String[] log = logs.split("\n");
        String txfrom = "";
        String txto = "";
        String txback = "";
        if (transport_cityblock == CityBlock.Tianjin){//Paris to Tianjin
            String m1 = "eth source TxHash is:  ";
            String m2 = "eth target TxHash is:  ";
            String m3 = "eth response TxHash is:  ";
            for (int i=0; i<log.length; i++){
                if (log[i].indexOf(m1) != -1){
//                    System.out.println(log[i]);
//                    System.out.println(log[i].indexOf(m1));
//                    System.out.println(log[i].length());
                    txfrom = log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+66);
                    break;
                }
            }
            for (int i=0; i<log.length; i++){
                if (log[i].indexOf(m2) != -1){
//                    System.out.println(log[i]);
//                    System.out.println(log[i].indexOf(m2));
//                    System.out.println(log[i].length());
                    txto = log[i].substring(log[i].indexOf(m2)+m2.length(),log[i].indexOf(m2)+m2.length()+66);
                    break;
                }
            }
            for (int i=0; i<log.length; i++){
                if (log[i].indexOf(m3) != -1){
                    txback = log[i].substring(log[i].indexOf(m3)+m3.length(),log[i].indexOf(m3)+m3.length()+66);
                    break;
                }
            }
        }
        if (transport_cityblock == CityBlock.Beijing){//Paris to Beijing
            String m1 = "eth source TxHash is:  ";
            String m2 = "chainmaker target TxId is:  ";
            String m3 = "eth response TxHash is:  ";
            for (int i=0; i<log.length; i++){
                if (log[i].indexOf(m1) != -1){
                    txfrom = log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+66);
                    break;
                }
            }
            for (int i=0; i<log.length; i++){
                if (log[i].indexOf(m2) != -1){
                    txto = log[i].substring(log[i].indexOf(m2)+m2.length(),log[i].indexOf(m2)+m2.length()+64);
                    break;
                }
            }
            for (int i=0; i<log.length; i++){
                if (log[i].indexOf(m3) != -1){
                    txback = log[i].substring(log[i].indexOf(m3)+m3.length(),log[i].indexOf(m3)+m3.length()+66);
                    break;
                }
            }
        }
        System.out.println("====Crosschain Key Messages====");
        System.out.println("[from id]"+crossFROM);
        System.out.println("[to id]"+crossTO);
        System.out.println("[param]"+PARAM);
        System.out.println("[type]"+crossTYPE);
        System.out.println("[Source TX]"+txfrom);
        System.out.println("[Target TX]"+txto);
        System.out.println("[Response TX]"+txback);
        System.out.println("===============================");
        Crosschain cross = new Crosschain()

                .setCrossFrom(String.valueOf(crossFROM))
                .setCrossTo(String.valueOf(crossTO))
                .setTxFrom(txfrom)
                .setTxTo(txto)
                .setTxBack(txback);

                */
        JSONObject Crosschaininfo = new JSONObject();


        Crosschaininfo.put("crossFrom", crossFROM);
        Crosschaininfo.put("crossTo", crossTO);
        Crosschaininfo.put("crosType", crossTYPE);
        //Crosschaininfo.put("crossContract", crossCONTRACT); 合约选择，毕设中暂时不需要
        Crosschaininfo.put("crossParam", PARAM);
        Crosschaininfo.put("crossResult", 1);
        //Crosschaininfo.put("txFrom",txfrom);哈希部分毕设暂时不需要
        // Crosschaininfo.put("txTo",txto);
        //Crosschaininfo.put("txBack",txback);



        Crosschain crosschain = new Crosschain()
                .setCrossFrom(crossreq.getCrossFrom())
                .setCrossTo(crossreq.getCrossTo())
                .setCrossType(crossreq.getCrossType())//改一下直连中继为布尔型
                .setCrossResult(true)
                .setCrossParam(crossreq.getParam())
                .setCrossTime(new Date());

        // 查询自增id下一个是多少
        QueryWrapper<Crosschain> wrapper = new QueryWrapper<>();
        wrapper.select("cross_id");
        List<Crosschain> cross = crosschainMapper.selectList(wrapper);
        Long maxId = Long.valueOf(0);
        for (int i = 0; i < cross.size(); i++) {
            Long perId = cross.get(i).getCrossId();
            if (perId > maxId) maxId = perId;
        }
        maxId = maxId + 1;

        Crosschaininfo.put("crossId", maxId);
        queryBlockInfoResp.setRet(ResultCode.SUCCESS);
        queryBlockInfoResp.setData(Crosschaininfo);
        int insert = this.crosschainMapper.insert(crosschain);
        return queryBlockInfoResp;
    }

}