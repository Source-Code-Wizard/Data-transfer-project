package source.code.wizard.senderapp.service;

import source.code.wizard.senderapp.model.DataEntity;

import java.util.List;

public interface DataTransferService {
    void sendDataInBatches( List<DataEntity> data);
}
