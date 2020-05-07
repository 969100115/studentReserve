package reserve.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Setter
@Getter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultBean<T> implements Serializable {

    private String code;
    private String desc;
    private T data;
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public T getData() {
        return data;
    }

    public ResultBean(T data,ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.desc = resultEnum.getMessage();
        this.data = data;
    }

    public ResultBean(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.desc = resultEnum.getMessage();
    }

    public ResultBean(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public ResultBean setResultEnum(ResultEnum result){
        this.code = result.getCode();
        this.desc = result.getMessage();
        return this;
    }

    public ResultBean(T data,ResultEnum resultEnum,Integer count) {
        this.code = resultEnum.getCode();
        this.desc = resultEnum.getMessage();
        this.data = data;
        this.count =count;
    }


}
