package reserve.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reserve.bean.ReserveRecord;
import reserve.bean.ReserveTime;
import reserve.common.*;
import reserve.common.redis.RedisKeyPrefixEnum;
import reserve.common.redis.RedisUtil;
import reserve.dto.ReserveRecordDTO;
import reserve.params.ReserveRecordParams;
import reserve.service.ReserveRecordService;
import reserve.service.ReserveTimeService;
import reserve.vo.ReserveVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("reserve")
@Slf4j
public class ReserveController {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ReserveTimeService reserveTimeService;
    @Autowired
    ReserveRecordService reserveRecordService;
    @Autowired
    WechatUtils wechatUtils;

    @Value("${test.port}")
    String port;

    @PostMapping("getPlaceDiction")
    public ResultBean getPlaceDiction() {
        List<String> place = reserveTimeService.getPlaceDiction();
        JSONArray  jsonArray = new JSONArray();
        jsonArray.addAll(place);
        return new ResultBean(jsonArray, ResultEnum.SUCCESS);
    }

    @PostMapping("getSupportReserve")
    public ResultBean getSupportReserve(@RequestBody JSONObject params) {
        String place = params.getString("place");
        String dateKey = RedisKeyPrefixEnum.RESERVE_DATA_.getKey("");
        String dateValue = (String) redisUtil.get(dateKey);

        String reserveTimeKey = RedisKeyPrefixEnum.RESERVE_NUM_.getKey(dateValue+"_"+place+"_");

        List<String> times = reserveTimeService.getTimesWithPlace(place);
        JSONArray  jsonArray = new JSONArray();
        for (String time:times) {
            JSONObject supportReserve = new JSONObject();
            supportReserve.put("time",time.replace(reserveTimeKey,""));
            Integer number = (Integer) redisUtil.get(reserveTimeKey+time);
            if(number<1){
                supportReserve.put("number",0);
            }else {
                supportReserve.put("number",number);
            }
            jsonArray.add(supportReserve);
        }

        return new ResultBean(jsonArray, ResultEnum.SUCCESS);
    }

    @PostMapping("reserve")
    public ResultBean reserve(@RequestBody ReserveRecordParams params) {
        String dateKey = RedisKeyPrefixEnum.RESERVE_DATA_.getKey("");
        String dateValue = (String) redisUtil.get(dateKey);

        if(StringUtils.isBlank(params.getOpenId())){
            return new ResultBean("", ResultEnum.UN_AUTHORIZATION);
        }

        String reserveKey = RedisKeyPrefixEnum.RESERVE_RECORD_.getKey(params.getOpenId()+"_"+dateValue+"_"+params.getTime()+"_*");
        if(!redisUtil.getKeys(reserveKey).isEmpty()) {
            return new ResultBean("", ResultEnum.USER_RESERVED);
        }

        String reserveTimeKey = RedisKeyPrefixEnum.RESERVE_NUM_.getKey(dateValue+"_"+params.getPlace()+"_"+params.getTime());
        Long number =  redisUtil.decr(reserveTimeKey,1);
        if(number<1){
            return new ResultBean("", ResultEnum.FULL_RESERVE);
        }

        ReserveRecordDTO reserveRecordDTO = new ReserveRecordDTO();
        BeanUtils.copyProperties(params,reserveRecordDTO);
        reserveRecordDTO.setDate(dateValue);
        reserveRecordDTO.setLicence(number.toString());
        ReserveVO reserveVO = reserveRecordService.addReserveRecord(reserveRecordDTO);
        reserveVO.setDate(formatDate(reserveVO.getDate()));

        return new ResultBean(reserveVO, ResultEnum.SUCCESS);
    }


    @PostMapping("refreshTodayReserve")
    public ResultBean queryByFlightNo() {
        String dateKey = RedisKeyPrefixEnum.RESERVE_DATA_.getKey( "");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyMMdd");
        String time = simpleDateFormat.format(System.currentTimeMillis()+24*60*60*1000);
//        String time = simpleDateFormat.format(System.currentTimeMillis());
        //查询当天可预约时间段
        List<ReserveTime> reservelist = reserveTimeService.listAllReserveTime();
        for(ReserveTime reserveTime:reservelist){
            //设置可预约redisKey，格式为：RESERVE_NUM_当天(RESERVE_DATA)_地点_可预约时间段
            String reserveKey = RedisKeyPrefixEnum.RESERVE_NUM_.getKey(time+"_"+reserveTime.getPlace()+"_"+reserveTime.getTime());
            boolean a = redisUtil.set(reserveKey,reserveTime.getNumber(),24*60*60);
            System.out.println(a);
        }
        boolean b = redisUtil.set(dateKey,time,24*60*60);
        return new ResultBean(b, ResultEnum.SUCCESS);
    }

    @PostMapping("userLogin")
    public ResultBean userLogin(@RequestBody JSONObject params) {
        JSONObject jsonObject = wechatUtils.getResultByCode(params.getString("code"));
        return new ResultBean(jsonObject,ResultEnum.SUCCESS);
    }

    @PostMapping("getRecentlyReserve")
    public ResultBean getRecentlyReserve(@RequestBody JSONObject params) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyMMdd-HH:mm");
        String countTime = simpleDateFormat.format(System.currentTimeMillis());
        String date = countTime.split("-")[0];
        String time = countTime.split("-")[1];

        List<ReserveVO> list = new ArrayList<>();
        String reserveKey = RedisKeyPrefixEnum.RESERVE_RECORD_.getKey(params.getString("openId")+"_");
        Set<String> keys = redisUtil.getKeys(reserveKey+"*");
        if(keys.isEmpty()||keys.size()<1){
            ReserveVO reserveVO = new ReserveVO();
            reserveVO.setDate(formatDate(date));
            reserveVO.red();
            return new ResultBean(reserveVO,ResultEnum.SUCCESS);
        }
           for(String key:keys){
            String value = (String) redisUtil.get(key);
//            JSONObject jsonObject = JSONObject.parseObject(value);
            ReserveVO reserveVO = JSONObject.parseObject(value,ReserveVO.class);
            list.add(reserveVO);
        }

           list.sort(new Comparator<ReserveVO>() {
               @SneakyThrows
               @Override
               public int compare(ReserveVO o1, ReserveVO o2) {
                   Date o1Date = simpleDateFormat.parse(o1.getDate()+"-"+o1.getTime().split("-")[0]);
                   Date o2Date = simpleDateFormat.parse(o2.getDate()+"-"+o2.getTime().split("-")[0]);
                   if(o1Date.getTime()>o2Date.getTime()){
                       return 1;
                   }else {
                       return -1;
                   }
               }
           });


        ReserveVO resultVO = selectRecently(list);

        return new ResultBean(setColor(resultVO),ResultEnum.SUCCESS);

    }

    private ReserveVO selectRecently(List<ReserveVO> reserveVOList) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm");
        String countTime = simpleDateFormat.format(System.currentTimeMillis());
        Date countDate = simpleDateFormat.parse(countTime);
        for (ReserveVO reserveVO:reserveVOList){
            String reserveTime = reserveVO.getDate()+"-"+reserveVO.getTime().split("-")[1];
            Date reserveDate = simpleDateFormat.parse(reserveTime);

            if(reserveDate.getTime()<countDate.getTime()){
                continue;
            }else {
                return reserveVO;
            }
        }
        return reserveVOList.get(reserveVOList.size()-1);
    }

    private ReserveVO setColor(ReserveVO reserveVO) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm");
        String countTime = simpleDateFormat.format(System.currentTimeMillis());
        Date countDate = simpleDateFormat.parse(countTime);
        String date = countTime.split("-")[0];
        String time = countTime.split("-")[1];
        String reserveStartTime = reserveVO.getDate()+"-"+reserveVO.getTime().split("-")[0];
        String reserveEndTime = reserveVO.getDate()+"-"+reserveVO.getTime().split("-")[1];
        Date reserveStartDate = simpleDateFormat.parse(reserveStartTime);
        Date reserveEndDate = simpleDateFormat.parse(reserveEndTime);




        if(Integer.parseInt(reserveVO.getDate())-Integer.parseInt(date)>0){
            reserveVO.yellow();
        }else if(reserveEndDate.getTime()-countDate.getTime()<30*60*1000&&reserveEndDate.getTime()-countDate.getTime()>0) {
            reserveVO.green();
        }else if (reserveEndDate.getTime()-countDate.getTime()>30*60*1000){
            reserveVO.yellow();
        }else {
            reserveVO.setTime("");
            reserveVO.setLicence("");
            reserveVO.setPlace("");
            reserveVO.red();
        }
        reserveVO.setDate(formatDate(reserveVO.getDate()));
        return reserveVO;
    }

    private String formatDate(String date){

        StringBuffer stringBuilder1=new StringBuffer(date);

        stringBuilder1.insert(6,"-");

        stringBuilder1.insert(4,"-");
        return stringBuilder1.toString();
    }

    @PostMapping("reserveTest")
    public ResultBean reserveTest(@RequestAttribute ReserveRecordParams params) {
        String dateKey = RedisKeyPrefixEnum.RESERVE_DATA_.getKey("");
        String dateValue = (String) redisUtil.get(dateKey);


        String reserveTimeKey = RedisKeyPrefixEnum.RESERVE_NUM_.getKey(dateValue+"_"+params.getPlace()+"_"+params.getTime());
        Long number =  redisUtil.decr(reserveTimeKey,1);
        if(number<1){
            return new ResultBean("", ResultEnum.FULL_RESERVE);
        }

        ReserveRecordDTO reserveRecordDTO = new ReserveRecordDTO();
        BeanUtils.copyProperties(params,reserveRecordDTO);
        reserveRecordDTO.setDate(dateValue);
        reserveRecordDTO.setLicence(number.toString());
        ReserveVO reserveVO = reserveRecordService.addReserveRecord(reserveRecordDTO);
        reserveVO.setDate(formatDate(reserveVO.getDate()));

        return new ResultBean(reserveVO, ResultEnum.SUCCESS);
    }

    @PostMapping("delReserve")
    public ResultBean delReserve(@RequestBody JSONObject params) {

        String dateKey = RedisKeyPrefixEnum.RESERVE_RECORD_.getKey("");
        String key = dateKey+"*_"+params.getString("data")+"_*";
        Set reserveSet =  redisUtil.getKeys(key);
        Iterator iterator = reserveSet.iterator();
        while (iterator.hasNext()){
            redisUtil.del((String) iterator.next());
        }
        return new ResultBean("true",ResultEnum.SUCCESS);

    }





}
