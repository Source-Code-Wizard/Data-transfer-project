package source.code.wizard.receiverapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import source.code.wizard.receiverapp.Model.dto.DataRequestDto;
import source.code.wizard.receiverapp.Model.entity.DataEntity;
import source.code.wizard.receiverapp.repository.DataEntityRepository;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataEntityServiceImpl implements DataEntityService {

    private final DataEntityRepository dataEntityRepository;

    @Transactional
    @Override
    public void save(DataRequestDto dataRequestDto) throws Exception {
        log.info("Data with ID: " + dataRequestDto.id() + " received...");

        // Let's throw an exception in order for the sender to implement a simple error mechanism.
        Random rand = new Random();
        if (rand.nextInt(100) == 1)
            throw new Exception("Data: " + dataRequestDto.id() + " was not saved!");

        dataEntityRepository.save(toEntity(dataRequestDto));
        log.info("Data with ID: " + dataRequestDto.id() + " was saved!");
    }

    private DataEntity toEntity(final DataRequestDto dataRequestDto) {
        return DataEntity.builder()
                .id(dataRequestDto.id())
                .fieldFour(dataRequestDto.fieldFour())
                .fieldThree(dataRequestDto.fieldThree())
                .fieldTwo(dataRequestDto.fieldTwo())
                .fieldOne(dataRequestDto.fieldOne())
                .build();
    }
}
