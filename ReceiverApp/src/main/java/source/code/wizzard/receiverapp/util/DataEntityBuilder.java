package source.code.wizzard.receiverapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import source.code.wizzard.receiverapp.Model.dto.DataRequestDto;

import java.io.IOException;

public class DataEntityBuilder {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static DataRequestDto buildFromArray(final byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, DataRequestDto.class);
    }
}
