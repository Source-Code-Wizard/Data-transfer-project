package source.code.wizzard.senderapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import source.code.wizzard.senderapp.model.DataEntity;
import source.code.wizzard.senderapp.ws.WSDataTransferHandler;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WSDataTransferStrategy extends TextWebSocketHandler implements DataTransferStrategy {

    private final WSDataTransferHandler wsDataTransferHandler;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendDataInBatches(List<DataEntity> data) {
        final WebSocketSession session = wsDataTransferHandler.getSession();
        if ((session) != null && session.isOpen()) {
            data.forEach(dataEntity -> {
                try {
                    String jsonPayload = objectMapper.writeValueAsString(dataEntity);
                    session.sendMessage(new TextMessage(jsonPayload));
                } catch (JsonProcessingException e) {
                    log.error("Failed to convert DataEntity to JSON for entity ID: {}", dataEntity.getId(), e);
                } catch (IOException e) {
                    log.error("Failed to send message for entity ID: {}", dataEntity.getId(), e);
                }
            });
        } else {
            log.error("WebSocket session is not open!");
        }
    }

    @Override
    public String getKey() {
        return "WEBSOCKET";
    }
}
