package reserve.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import reserve.bean.ReserveRecord;
import reserve.bean.ReserveTime;
import reserve.common.BaseMapper;

import java.util.List;

@Repository
public interface ReserveTimeMapper extends BaseMapper<ReserveTime> {
    List<String> getPlaceDiction();
    List<String> getTimes(@Param("place")String place);

}
