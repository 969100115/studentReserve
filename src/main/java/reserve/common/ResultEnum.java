package reserve.common;


public enum ResultEnum{

	/*
    规范定义响应码    要求开发人员在遇到问题时，先自行处理好问题，然后抛出标准化异常

    200              操作成功

    1XXX             数据还没有开始处理，因请求不正确导致的问题，包括但不限于如下问题
    11XX             session相关的问题，登录异常
    12XX             请求参数错误（参数为空，参数结构不合法，参数问题的粒度可协商讨论）
    13XX             文件上传问题 （格式问题、大小问题等）

    2XXX             请求的数据格式没有问题，但是基于这些数据进行操作后遇到了问题，包括但不限于如下问题：
    21XX             数据库操作异常（主键冲突、 查询没有结果、查询超时、更新失败等等）
    22XX             接口操作异常（接口超时、接口请求失败，具体粒度可详细讨论如区分哪类业务接口等）
    23XX             其他数据交换操作发生的问题（如操作缓存、操作消息队列发生的异常）

    9999             未知错误，即因开发人员代码有bug引发的不可控的错误，
                     比如空指针、下标越界、方法不存在等等离奇错误，编译时没有报错但实际运行时发生的
     */

    SUCCESS("200","操作成功"),
    ERROR("-2","操作失败"),
    SERVER_ERROR("-500","服务异常"),
    LOGIN_ERROR("-402","登录异常"),
    OPEN_LOGIN_ERROR("-406","登录验证失败"),
    DATA_EXISTENT("-504","数据不存在"),
    PARAM_ISNULL("-400","参数为空"),
    REPEAT("-666","数据已存在"),
    HTTP_ERROR("-405","请求异常"),
    API_ERROR("-1001",""),
    ENUM_GATEWAY_API_ERROR("-500",""),
    HUI_DAO_ERROR("-501", ""),
    TEST("99999",""),


    FULL_RESERVE("1001","当前时间段预约已满"),
    USER_RESERVED("2001","您在该时间段已预约")

    ;
    private String code;
    private String message;

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    ResultEnum(String code, String decs) {
        this.code = code;
        this.message = decs;
    }

    @Override
    public String toString() {
        return this.name() + ":{" +
                "\"code\":\"" + code + '\"' +
                ", \"message\":\"" + message + '\"' +
                '}';
    }
}
