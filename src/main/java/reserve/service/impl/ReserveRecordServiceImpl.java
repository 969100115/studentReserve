package reserve.service.impl;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import reserve.bean.ReserveRecord;
import reserve.common.redis.RedisKeyPrefixEnum;
import reserve.common.redis.RedisUtil;
import reserve.dao.ReserveRecordMapper;
import reserve.dto.ReserveRecordDTO;
import reserve.service.ReserveRecordService;
import reserve.vo.ReserveVO;

import javax.annotation.Resource;

@Service
public class ReserveRecordServiceImpl implements ReserveRecordService {

    @Resource
    RedisUtil redisUtil;
    @Resource
    ReserveRecordMapper reserveRecordMapper;

    @Override
    public ReserveVO addReserveRecord(ReserveRecordDTO reserveRecordDTO) {
        //设置可预约redisKey，格式为：RESERVE_RECORD_用户openId_当天(RESERVE_DATA)_地点_可预约时间段
        String reserveKey = RedisKeyPrefixEnum.RESERVE_RECORD_.getKey(reserveRecordDTO.getOpenId()+"_"+reserveRecordDTO.getDate()+"_"+reserveRecordDTO.getTime()+"_"+reserveRecordDTO.getPlace());

        ReserveRecord reserveRecord = new ReserveRecord();
        BeanUtils.copyProperties(reserveRecordDTO,reserveRecord);
        reserveRecordMapper.insert(reserveRecord);

        redisUtil.set(reserveKey, JSONObject.toJSONString(reserveRecord));
        ReserveVO reserveVO = new ReserveVO();
        BeanUtils.copyProperties(reserveRecord,reserveVO);
        reserveVO = setColor(reserveVO);
        return reserveVO;
    }

    private static ReserveVO setColor(ReserveVO reserveVO){

        return reserveVO;
    }
}
