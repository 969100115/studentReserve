package reserve.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reserve.bean.ReserveTime;
import reserve.dao.ReserveTimeMapper;
import reserve.service.ReserveTimeService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ReserveTimeServiceImpl implements ReserveTimeService {

    @Resource
    ReserveTimeMapper reserveTimeMapper;
    @Override
    public List<ReserveTime> listAllReserveTime() {
        List<ReserveTime> reserveTimeList = reserveTimeMapper.selectAll();
        return reserveTimeList;
    }

    @Override
    public List<String> getPlaceDiction() {
        List<String> placeList = reserveTimeMapper.getPlaceDiction();
        return placeList;
    }

    @Override
    public List<String> getTimesWithPlace(String place) {
        List<String> placeList = reserveTimeMapper.getTimes(place);
        return placeList;
    }
}
