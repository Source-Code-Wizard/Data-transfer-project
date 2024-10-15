package source.code.wizzard.senderapp.service;

import source.code.wizzard.senderapp.model.DataEntity;

import java.util.List;

public interface DataTransferService {
    void sendDataInBatches( List<DataEntity> data);
}
