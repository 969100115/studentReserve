package reserve.service;

import reserve.bean.ReserveTime;

import java.util.List;

public interface ReserveTimeService {

    List<ReserveTime> listAllReserveTime();
    List<String> getPlaceDiction();
    List<String> getTimesWithPlace(String place);
}
