package reserve.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;


@Data
@Table(name = "reserve_record")
public class ReserveRecord {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer Id;
    @Column(name = "open_id")
    String openId;
    @Column(name = "place")
    String place;
    @Column(name = "date")
    String date;
    @Column(name = "time")
    String time;
    @Column(name = "licence")
    String licence;

    public ReserveRecord(String openId, String place, String time,String date) {
        this.openId = openId;
        this.place = place;
        this.time = time;
        this.date =date;
    }

    public ReserveRecord() {
    }
}
