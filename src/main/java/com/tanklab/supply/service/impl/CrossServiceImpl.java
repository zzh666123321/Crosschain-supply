package com.tanklab.supply.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanklab.supply.common.CityBlock;
import com.tanklab.supply.common.ResultCode;
import com.tanklab.supply.common.SSHConfig;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Beef;
import com.tanklab.supply.entity.Crosschain;
import com.tanklab.supply.entity.Ox;
import com.tanklab.supply.mapper.CrossMapper;
import com.tanklab.supply.service.CrossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrossServiceImpl extends ServiceImpl<CrossMapper, Crosschain> implements CrossService {


    @Autowired
    private CrossMapper crossMapper;
    @Override
    public void doCross(Long oxId, String beefKey, String crossFrom, String crossTo){
        CityBlock process_cityblock = CityBlock.valueOf(crossFrom);
        CityBlock transport_cityblock = CityBlock.valueOf(crossTo);
        String logs = null;
        try {
            SSHConfig.connect();
            if (transport_cityblock == CityBlock.Tianjin){//Paris to Tianjin
                logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag "+ beefKey +" -cd 12", "UTF-8");
            }
            if (transport_cityblock == CityBlock.Beijing){//Paris to Beijing
                logs = SSHConfig.executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth2chainmaker -r true -ap beefAccess -ag "+beefKey,"UTF-8");
            }
//            System.out.println("cross-chain finished");

        } catch (Exception e) {
            System.out.println("SSH ERROR");
        }
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
        System.out.println("[Beef key]"+beefKey);
        System.out.println("[Source TX]"+txfrom);
        System.out.println("[Target TX]"+txto);
        System.out.println("[Response TX]"+txback);
        System.out.println("===============================");
        Crosschain cross = new Crosschain()
                .setOxId(oxId)
                .setBeefKey(beefKey)
                .setCrossFrom(crossFrom)
                .setCrossTo(crossTo)
                .setTxFrom(txfrom)
                .setTxTo(txto)
                .setTxBack(txback);
        int insert = this.crossMapper.insert(cross);
//        System.out.println("Data Cross Chain Finished");
    }

    public CommonResp queryCrossRet(Long oxId){
        CommonResp queryCross = new CommonResp();
        QueryWrapper<Crosschain> wrapper = new QueryWrapper<>();
        wrapper.eq("ox_id",oxId);
        List<Crosschain> crosses = crossMapper.selectList(wrapper);

        JSONObject crossinfo = new JSONObject();
        JSONArray crossarr = new JSONArray();
        for (int i=0;i<crosses.size();i++){
            JSONObject perc = new JSONObject();
            perc.put("beefKey",crosses.get(i).getBeefKey());
            perc.put("crossFrom",crosses.get(i).getCrossFrom());
            perc.put("crossTo",crosses.get(i).getCrossTo());
            perc.put("txFrom",crosses.get(i).getTxFrom());
            perc.put("txTo",crosses.get(i).getTxTo());
            perc.put("txBack",crosses.get(i).getTxBack());
            crossarr.add(perc);
        }
        crossinfo.put("crossInfo",crossarr);
//        JSONObject crossinfo = (JSONObject) JSONObject.toJSON(cross);

        queryCross.setRet(ResultCode.SUCCESS);
        queryCross.setData(crossinfo);
        return queryCross;
    }
}
