package source.code.wizard.senderapp.service.strategy;

import source.code.wizard.senderapp.service.DataTransferService;

public interface DataTransferStrategy extends DataTransferService {
    String getKey();
}
