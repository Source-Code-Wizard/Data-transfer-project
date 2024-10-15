package source.code.wizzard.senderapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import source.code.wizzard.senderapp.model.DataEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service("DataTransferService")
public class DataTransferServiceImpl implements DataTransferService {

    @Value("${app.integration.type}")
    private String integrationType;

    private final Map<String, DataTransferStrategy> strategiesMap;

    public DataTransferServiceImpl(Set<DataTransferStrategy> strategies) {
        strategiesMap = new HashMap<>();
        strategies.forEach(strategy -> strategiesMap.put(strategy.getKey(), strategy));
    }

    @Override
    public void sendDataInBatches(List<DataEntity> data) {
        strategiesMap.get(integrationType).sendDataInBatches(data);
    }
}
