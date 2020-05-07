package reserve.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class ReserveRecordDTO {
    String date;

    String openId;

    String place;

    String time;

    String licence;
}
