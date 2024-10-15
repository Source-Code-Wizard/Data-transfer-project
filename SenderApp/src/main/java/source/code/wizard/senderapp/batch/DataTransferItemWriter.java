package source.code.wizard.senderapp.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import source.code.wizard.senderapp.model.DataEntity;
import source.code.wizard.senderapp.service.DataTransferService;

import java.util.List;


@Slf4j
public class DataTransferItemWriter implements ItemWriter<DataEntity> {

    @Qualifier("DataTransferService")
    private final DataTransferService dataTransferService;

    public DataTransferItemWriter(@Qualifier("DataTransferService") DataTransferService dataTransferService) {
        this.dataTransferService = dataTransferService;
    }

    @Override
    public void write(Chunk<? extends DataEntity> chunk) {
        final List<? extends DataEntity> itemsToBeTransfered = chunk.getItems();
        dataTransferService.sendDataInBatches((List<DataEntity>) itemsToBeTransfered);
    }

}
