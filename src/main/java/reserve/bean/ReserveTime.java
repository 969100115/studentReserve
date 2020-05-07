package reserve.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Data
@Table(name = "reserve_time")
public class ReserveTime {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer Id;
    @Column(name = "time")
    String time;
    @Column(name = "place")
    String place;
    @Column(name = "number")
    Integer number;

}
