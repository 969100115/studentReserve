//package reserve.common.redis;//package com.jimulian.tongban_service.application.config.redis;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationContext;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.params.SetParams;
//
//import java.util.Collections;
//
///**
// * @author Wenbo
// * @date 2019/12/31 11:47
// * @Email 969****15@qq.com
// * @phone 176****7037
// */
//@Slf4j
//public class RedisLock {
//
//    Locked locked;
//
//    Object object;
//
//    public static ApplicationContext applicationContext;
//
//    protected long internalLockLeaseTime = 30000;//锁过期时间
//
//    private long timeout = 999999; //获取锁的超时时间
//
//    //SET命令的参数
//    SetParams params = SetParams.setParams().nx().px(internalLockLeaseTime);
//
//    JedisPool jedisPool;
//
//
//
//    public RedisLock(Locked locked) {
//
//        this.jedisPool = (JedisPool) applicationContext.getBean("jedisPool");
//        this.locked = locked;
//
//    }
//
//    /**
//     * 加锁
//     *
//     * @param id
//     * @return
//     */
//    public boolean lock(String lock_key, String id) {
//
//        Jedis jedis = jedisPool.getResource();
//        try {
//            //SET命令返回OK ，则证明获取锁成功
//            String lock = jedis.set(lock_key+ "_locked", id , params);
//            if ("OK".equals(lock)) {
//                Object o = null;
//                try {
//                    o = locked.lock();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                setMessage(o);
//                return true;
//            } else {
//                return false;
//            }
//        } finally {
//            jedis.close();
//            unlock(lock_key, id);
//
//        }
//
//    }
//
//
//    /**
//     * 解锁
//     *
//     * @param id
//     * @return
//     */
//    public boolean unlock(String lock_key, String id) {
//        Jedis jedis = jedisPool.getResource();
//        String script =
//                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
//                        "   return redis.call('del',KEYS[1]) " +
//                        "else" +
//                        "   return 0 " +
//                        "end";
//        try {
//            Object result = jedis.eval(script, Collections.singletonList(lock_key+ "_locked"),
//                    Collections.singletonList(id));
//            if ("1".equals(result.toString())) {
//                return true;
//            }
//            return false;
//        } finally {
//            jedis.close();
//        }
//    }
//
//    public void setMessage(Object message) {
//        this.object = message;
//    }
//
//    public Object getMessage() {
//        return this.object;
//    }
//}