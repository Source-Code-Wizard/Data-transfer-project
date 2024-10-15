package source.code.wizard.receiverapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import source.code.wizard.receiverapp.Model.dto.DataRequestDto;
import source.code.wizard.receiverapp.Model.entity.DataEntity;
import source.code.wizard.receiverapp.repository.DataEntityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataEntityServiceImpl implements DataEntityService {

    private final DataEntityRepository dataEntityRepository;
    @Transactional
    @Override
    public void save(DataRequestDto dataRequestDto) {
        log.info("Data with ID: "+dataRequestDto.id()+" received...");
        dataEntityRepository.save(toEntity(dataRequestDto));
        log.info("Data with ID: "+dataRequestDto.id()+" was saved!");
    }

    private DataEntity toEntity(final DataRequestDto dataRequestDto){
        return DataEntity.builder()
                .id(dataRequestDto.id())
                .fieldFour(dataRequestDto.fieldFour())
                .fieldThree(dataRequestDto.fieldThree())
                .fieldTwo(dataRequestDto.fieldTwo())
                .fieldOne(dataRequestDto.fieldOne())
                .build();
    }
}
