package com.tanklab.supply.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanklab.supply.common.*;
import com.tanklab.supply.ds.req.AddBeefReq;
import com.tanklab.supply.ds.req.AddSellerReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Beef;
import com.tanklab.supply.entity.Ox;
import com.tanklab.supply.mapper.BeefMapper;
import com.tanklab.supply.mapper.OxMapper;
import com.tanklab.supply.service.BeefService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.tanklab.supply.service.impl.CrossServiceImpl;
import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 牛肉信息表 服务实现类
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@Service
public class BeefServiceImpl extends ServiceImpl<BeefMapper, Beef> implements BeefService {
    @Autowired
    private BeefMapper beefMapper;

    @Autowired
    private OxMapper oxMapper;

    @Autowired
    public CrossServiceImpl crossService;

    public CommonResp addBeef(AddBeefReq addbeefreq) {
        CommonResp addbeefresp = new CommonResp();

        Ox ox = oxMapper.selectById(addbeefreq.getOxId());
        if (ox.getIsProcessed() == true) {
            addbeefresp.setRet(ResultCode.PROCESS_ERROR);
            return addbeefresp;
        }

        if (addbeefreq.getNumberOfBeef() == 0) {
            addbeefresp.setRet(ResultCode.ERROR);
            return addbeefresp;
        }
        // 查询自增id下一个是多少
        QueryWrapper<Beef> wrapper = new QueryWrapper<>();
        wrapper.select("beef_id");
        List<Beef> beefs = beefMapper.selectList(wrapper);
        Long maxId = Long.valueOf(0);
        for (int i = 0; i < beefs.size(); i++) {
            Long perId = beefs.get(i).getBeefId();
            if (perId > maxId) maxId = perId;
        }
        maxId = maxId + 1;

        JSONObject txhashgroup = null;
        for (Long i = maxId; i < maxId + addbeefreq.getNumberOfBeef(); i++) {
            Beef beef = new Beef()
                    .setBreed(addbeefreq.getBreed())
                    .setProcessTime(addbeefreq.getProcessTime())
                    .setProcessPlace(addbeefreq.getProcessPlace())
                    .setProcessPerson(addbeefreq.getProcessPerson())
                    .setTransportPlace(addbeefreq.getTransportPlace())
                    .setTransportTime(addbeefreq.getTransportTime())
                    .setTransportPerson(addbeefreq.getTransportPerson())
                    .setOxId(addbeefreq.getOxId());

            JSONObject perbeef = new JSONObject();
            perbeef.put("breed", beef.getBreed());
            perbeef.put("processTime", beef.getProcessTime());
            perbeef.put("processPlace", beef.getProcessPlace());
            perbeef.put("processPerson", beef.getProcessPerson());
            perbeef.put("transportPlace", beef.getTransportPlace());
            perbeef.put("transportTime", beef.getTransportTime());
            perbeef.put("transportPerson", beef.getTransportPerson());
            perbeef.put("oxId", beef.getOxId());
            perbeef.put("beefId", i);
            // 计算牛肉的唯一key hash
            String newbeefkey = HashUtil.hash_func(perbeef.toString(), "MD5");
            beef.setBeefKey(newbeefkey);

            txhashgroup = new JSONObject();

            // TODO: 以太坊信息上链（已完成）
            if (BlockchainConfig.do_update_blockchain) {

                String transactionId = BlockchainOperation.addBeefBlockchain(
                        CityBlock.Paris,
                        beef.getBeefKey(),
                        new BigInteger(String.valueOf(i)),
                        beef.getBreed(),
                        String.valueOf(beef.getProcessTime()),
                        beef.getProcessPlace(),
                        beef.getProcessPerson(),
                        String.valueOf(beef.getTransportTime()),
                        beef.getTransportPlace(),
                        beef.getTransportPerson(),
                        new BigInteger(String.valueOf(beef.getOxId()))
                );
                beef.setTransactionIdC0(transactionId);
                txhashgroup.put("AbroadTx", beef.getTransactionIdC0());//TIP：这里是国外上链交易号
                if (SSHConfig.do_crosschain) { // TODO：跨链调用（测试中）
                    //old version
//                    try {
//                        SSHConfig.connect();
//                        SSHConfig.executeCMD("cd CIPS-main && pwd && go run *.go -t 2 -i 0 -n [\"" + beef.getBeefKey() + "\"] -c Internal -m transferToCHN", "UTF-8");
//                        //TODO：这里需要获取远程终端里的输出信息里的内容
//                    } catch (Exception e) {
//                        System.out.println("SSH ERROR");
//                    }
                    crossService.doCross(beef.getOxId(),newbeefkey,beef.getProcessPlace(),beef.getTransportPlace());

                }
            } else {
                beef.setTransactionIdC0("Not on chain");
            }


            // 更新数据库
            int insert = this.beefMapper.insert(beef);
            System.out.println("[New Beef Key] "+newbeefkey);
        }

        ox.setIsProcessed(true);
        oxMapper.updateById(ox);

        addbeefresp.setRet(ResultCode.SUCCESS);
        addbeefresp.setData(txhashgroup);
        return addbeefresp;
    }

    public CommonResp queryOxBeef(Long oxId){
        CommonResp queryoxbeefresp = new CommonResp();

        QueryWrapper<Beef> wrapper = new QueryWrapper<>();
        wrapper.eq("ox_id",oxId);
        List<Beef> beefs = beefMapper.selectList(wrapper);

        JSONObject beefsinfo = new JSONObject();
        JSONArray beefsarr = new JSONArray();
        for (int i=0;i<beefs.size();i++){
            JSONObject perbeefinfo = new JSONObject();
            perbeefinfo.put("beefId",beefs.get(i).getBeefId());
            perbeefinfo.put("beefKey",beefs.get(i).getBeefKey());
            perbeefinfo.put("processTime",beefs.get(i).getProcessTime());
            beefsarr.add(perbeefinfo);
        }
        beefsinfo.put("beefsInfo",beefsarr);
        queryoxbeefresp.setRet(ResultCode.SUCCESS);
        queryoxbeefresp.setData(beefsinfo);
        return queryoxbeefresp;
    }
    public CommonResp queryBeef(){
        CommonResp querybeefresp = new CommonResp();

        QueryWrapper<Beef> wrapper = new QueryWrapper<>();
        wrapper.select("beef_id","beef_key","process_time","transfer_place");
        List<Beef> beefs = beefMapper.selectList(wrapper);

        JSONObject beefsinfo = new JSONObject();
        JSONArray beefsarr = new JSONArray();
        for (int i=0;i<beefs.size();i++){
            JSONObject perbeefinfo = new JSONObject();
            perbeefinfo.put("beefId",beefs.get(i).getBeefId());
            perbeefinfo.put("beefKey",beefs.get(i).getBeefKey());
            perbeefinfo.put("processTime",beefs.get(i).getProcessTime());
            perbeefinfo.put("transferPlace",beefs.get(i).getTransferPlace());
            beefsarr.add(perbeefinfo);
        }
        beefsinfo.put("beefsInfo",beefsarr);
        querybeefresp.setRet(ResultCode.SUCCESS);
        querybeefresp.setData(beefsinfo);

        return querybeefresp;
    }
    public CommonResp queryOneBeef(String beefKey){
        CommonResp queryonebeefresp = new CommonResp();

        QueryWrapper<Beef> wrapper = new QueryWrapper<>();
        wrapper.eq("beef_key",beefKey);
        Beef beef = beefMapper.selectOne(wrapper);
        JSONObject beefinfo = (JSONObject) JSONObject.toJSON(beef);
        if (beefinfo.containsKey("transferTime")) beefinfo.put("transferTime",beef.getTransferTime());
        if (beefinfo.containsKey("transportTime")) beefinfo.put("transportTime",beef.getTransportTime());
        if (beefinfo.containsKey("qualityGuaranteeTime")) beefinfo.put("qualityGuaranteeTime",beef.getQualityGuaranteeTime());
        if (beefinfo.containsKey("processTime")) beefinfo.put("processTime",beef.getProcessTime());
        queryonebeefresp.setRet(ResultCode.SUCCESS);
        queryonebeefresp.setData(beefinfo);
        return queryonebeefresp;
    }

    public CommonResp addSeller(AddSellerReq addsellerreq){
        CommonResp addsellerresp = new CommonResp();
        QueryWrapper<Beef> wrapper = new QueryWrapper<>();
        wrapper.eq("beef_key",addsellerreq.getBeefKey());
        Beef beef = beefMapper.selectOne(wrapper);

        beef.setTransferTime(addsellerreq.getTransferTime())
                .setTransferPlace(addsellerreq.getTransferPlace())
                .setTransferPerson(addsellerreq.getTransferPerson())
                .setSellPlace(addsellerreq.getSellPlace())
                .setPrice(addsellerreq.getPrice())
                .setWeight(addsellerreq.getWeight())
                .setQualityGuaranteeTime(addsellerreq.getQualityGuaranteeTime());

        // TODO: 信息上链（已完成）
        if (BlockchainConfig.do_update_blockchain){
            CityBlock cityblock = CityBlock.valueOf(beef.getTransferPlace());
            if (cityblock == CityBlock.Tianjin){
                String transactionId = BlockchainOperation.sellBeefBlockchain(
                        cityblock,
                        beef.getBeefKey(),
                        new BigInteger(String.valueOf(beef.getBeefId())),
                        String.valueOf(beef.getTransferTime()),
                        beef.getTransferPlace(),
                        beef.getTransferPerson(),
                        beef.getSellPlace(),
                        new BigInteger(String.valueOf(beef.getPrice())),
                        new BigInteger(String.valueOf(beef.getWeight())),
                        String.valueOf(beef.getQualityGuaranteeTime()),
                        new BigInteger(String.valueOf(beef.getOxId()))
                );
                beef.setTransactionIdC1(transactionId);
            }
            if (cityblock == CityBlock.Beijing){
                String logs = "";
                try{
                    SSHConfig.connect();
                    String content = String.valueOf(beef.getTransferTime()) +
                            beef.getTransferPlace() +
                            beef.getTransferPerson() +
                            beef.getSellPlace() +
                            String.valueOf(beef.getPrice()) +
                            String.valueOf(beef.getWeight()) +
                            String.valueOf(beef.getQualityGuaranteeTime())+
                            String.valueOf(beef.getOxId());
                    logs = SSHConfig.executeCMD("source ~/.profile && cd /root/CIPS-ETHEREUM-private && go run chainmakercontract/contractInvoke.go -cn BeefKing -cm sellbeef -ag "+beef.getBeefKey()+"+"+content,"utf-8");
                } catch (Exception e) {
                    System.out.println("SSH ERROR");
                }
                String[] log = logs.split("\n");
                String m1 = "TxId is: ";
                String txid = "";
                for (int i=0; i<log.length; i++){
                    if (log[i].indexOf(m1) != -1){
                        txid = log[i].substring(log[i].indexOf(m1)+m1.length(),log[i].indexOf(m1)+m1.length()+64);
                        break;
                    }
                }
                beef.setTransactionIdC1(txid);
            }
        }else {
            beef.setTransactionIdC1("Not on chain");
        }

        beefMapper.updateById(beef);
        System.out.println("[Sell Beef Key] "+beef.getBeefKey());
        addsellerresp.setRet(ResultCode.SUCCESS);
        return addsellerresp;
    }
}
