package source.code.wizard.receiverapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.wizard.receiverapp.Model.dto.DataRequestDto;
import source.code.wizard.receiverapp.service.DataEntityService;

@Slf4j
@RestController
@RequestMapping("/data/api/v1")
@RequiredArgsConstructor
public class DataController {

    private final DataEntityService dataEntityService;

    @PostMapping("/save")
    public void save(@RequestBody final DataRequestDto dataRequestDto) {
        dataEntityService.save(dataRequestDto);
    }
}
