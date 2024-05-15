package com.tanklab.supply.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanklab.supply.common.*;
import com.tanklab.supply.ds.req.AddOxReq;
import com.tanklab.supply.ds.resp.CommonResp;
import com.tanklab.supply.entity.Ox;
import com.tanklab.supply.mapper.OxMapper;
import com.tanklab.supply.service.OxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 牛信息表 服务实现类
 * </p>
 *
 * @author Zhiyuan Zheng
 * @since 2023-07-25
 */
@Service
public class OxServiceImpl extends ServiceImpl<OxMapper, Ox> implements OxService {
    @Autowired
    private OxMapper oxMapper;

    public CommonResp addOx(AddOxReq addoxreq){
        CommonResp addoxresp = new CommonResp();
        // 将填入的信息添加到新的Ox对象中
        Ox ox = new Ox()
                .setBreed(addoxreq.getBreed())
                .setEndTime(addoxreq.getEndTime())
                .setFeedPerson(addoxreq.getFeedPerson())
                .setLocation(addoxreq.getLocation())
                .setWeight(addoxreq.getWeight())
                .setFeedingPeriod(addoxreq.getFeedingPeriod())
                .setIsProcessed(false);
        // 查询自增id下一个是多少
        QueryWrapper<Ox> wrapper = new QueryWrapper<>();
        wrapper.select("ox_id");
        List<Ox> oxen = oxMapper.selectList(wrapper);
        Long maxId = Long.valueOf(0);
        for (int i = 0; i < oxen.size();i++){
            Long perId = oxen.get(i).getOxId();
            if (perId > maxId) maxId = perId;
        }
        maxId = maxId + 1;

        // 构造json
        JSONObject oxinfo = new JSONObject();
        oxinfo.put("breed",ox.getBreed());
        oxinfo.put("endTime",ox.getEndTime());
        oxinfo.put("feedPerson",ox.getFeedPerson());
        oxinfo.put("location",ox.getLocation());
        oxinfo.put("weight",ox.getWeight());
        oxinfo.put("feedingPeriod",ox.getFeedingPeriod());
        oxinfo.put("oxId",maxId);
        // 计算牛的唯一key hash
        String newoxkey = HashUtil.hash_func(oxinfo.toString(),"MD5");
        ox.setOxKey(newoxkey);

        // TODO: 以太坊信息上链（已完成）
        if (BlockchainConfig.do_update_blockchain){
            //NEW: 判断是哪条链
            CityBlock cityblock = CityBlock.valueOf(ox.getLocation());
//            String chain = cityblock.chain;

            String transactionId=BlockchainOperation.addOxBlockchain(
                    cityblock,
                    ox.getOxKey(),
                    new BigInteger(String.valueOf(maxId)),
                    ox.getBreed(),
                    String.valueOf(ox.getEndTime()),
                    new BigInteger(String.valueOf(ox.getFeedingPeriod())),
                    new BigInteger(String.valueOf(ox.getWeight())),
                    ox.getLocation(),
                    ox.getFeedPerson()
            );
            ox.setTransactionId(transactionId);
        } else {
            ox.setTransactionId("Not on chain");
        }

        System.out.println("[New Ox Key] "+newoxkey);
        // 更新数据库
        int insert = this.oxMapper.insert(ox);
//        System.out.println(maxId);
        // 返回表单
        addoxresp.setRet(ResultCode.SUCCESS);
        addoxresp.setData("add successfully!");
        return addoxresp;
    }

    public CommonResp queryOxen(){
        CommonResp queryoxenresp = new CommonResp();
        QueryWrapper<Ox> wrapper = new QueryWrapper<>();
        wrapper.select("ox_id","ox_key","end_time","is_processed");
        List<Ox> oxen = oxMapper.selectList(wrapper);

        JSONObject oxeninfo = new JSONObject();
        JSONArray oxenarr = new JSONArray();
        for (int i=0;i<oxen.size();i++){
            JSONObject peroxinfo = new JSONObject();
            peroxinfo.put("oxId",oxen.get(i).getOxId());
            peroxinfo.put("oxKey",oxen.get(i).getOxKey());
            peroxinfo.put("endTime",oxen.get(i).getEndTime());
            peroxinfo.put("isProcessed",oxen.get(i).getIsProcessed());
            oxenarr.add(peroxinfo);
        }
        oxeninfo.put("oxenInfo",oxenarr);
        queryoxenresp.setRet(ResultCode.SUCCESS);
        queryoxenresp.setData(oxeninfo);
        return queryoxenresp;
    }
    public CommonResp queryOneOx(Long oxId){
        CommonResp queryoneoxresp = new CommonResp();
        Ox  ox = oxMapper.selectById(oxId);

        if (ox == null){
            queryoneoxresp.setRet(ResultCode.NOT_MATCH_ERROR);
            return queryoneoxresp;
        }

        JSONObject oxinfo = new JSONObject();
        oxinfo.put("breed",ox.getBreed());
        oxinfo.put("endTime",ox.getEndTime());
        oxinfo.put("feedPerson",ox.getFeedPerson());
        oxinfo.put("location",ox.getLocation());
        oxinfo.put("weight",ox.getWeight());
        oxinfo.put("feedingPeriod",ox.getFeedingPeriod());
        oxinfo.put("oxKey",ox.getOxKey());
        oxinfo.put("transactionId",ox.getTransactionId());
        oxinfo.put("isProcessed",ox.getIsProcessed());

        queryoneoxresp.setRet(ResultCode.SUCCESS);
        queryoneoxresp.setData(oxinfo);
        return queryoneoxresp;
    }
}
