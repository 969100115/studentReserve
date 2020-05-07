package reserve.common.redis;

/**
 * @author zengkang
 * @date 2019/9/27 16:32
 */
public enum RedisKeyPrefixEnum {

    // 预约数 缓存前缀
    RESERVE_NUM_,

    // 预约日期 缓存前缀
    RESERVE_DATA_,

    RESERVE_RECORD_,


    ;

    public String getKey(String suffix){
        return this.name()+suffix;
    }

}
