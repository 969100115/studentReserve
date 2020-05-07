package reserve.common;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WechatUtils {

    public static String appId = "wx34f7a4867d27d397";

    @Value("${tencent.xcx.appId}")
    public void setAppId(String appId){
        WechatUtils.appId = appId;
    }

    public static String appSecret = "a31187246b2b40296d7545aa10f188d7";

    @Value("${tencent.xcx.appSecret}")
    public void setAppSecret(String appSecret){
        WechatUtils.appSecret = appSecret;
    }

//    public static String getAccesstokenByCode(){
//
//
//    }
//
//    public static String getOpenIdByCode(){
//
//    }

    public JSONObject getResultByCode(String code){
        return authorizationCodeRequest(code);
    }

    private static JSONObject authorizationCodeRequest(String code){

        HttpRequest get = HttpRequest.get("https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code&appid=" + appId + "&secret=" + appSecret+"&code="+code);
        HttpResponse execute = get.execute();
        String body = execute.body();
        JSONObject object = JSON.parseObject(body);
        String access_token = object.getString("access_token");
        Long expires_in = object.getLong("expires_in");

        if(access_token != null){
            return object;
        }else{
            log.error("getAccess_token 获取wechat 小程序 access_token 返回{}", object);
            return null;
        }
    }
}
