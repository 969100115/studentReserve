package reserve.service;

import reserve.bean.ReserveRecord;
import reserve.dto.ReserveRecordDTO;
import reserve.vo.ReserveVO;

public interface ReserveRecordService {
    ReserveVO addReserveRecord(ReserveRecordDTO reserveRecordDTO);
}
