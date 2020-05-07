package reserve.common.redis;

/**
 * @author Wenbo
 * @date 2020/1/3 9:44
 * @Email 969****15@qq.com
 * @phone 176****7037
 */
public interface Locked {
   public Object lock() throws InterruptedException;
}
