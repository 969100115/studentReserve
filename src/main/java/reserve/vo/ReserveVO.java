package reserve.vo;

import lombok.Data;

import javax.persistence.Column;

@Data
public class ReserveVO {

    String openId;
    String place;
    String time;
    String date;
    String licence;
    String describe;
    String color;

    public void red(){
        this.color = "red";
        this.describe = "您还未有预约";
    }

    public void yellow(){
        this.color = "yellow";
        this.describe = "还未到达您预约的时间段";
    }

    public void green(){
        this.color = "green";
        this.describe = "您已预约，祝您用餐愉快";
    }


}
