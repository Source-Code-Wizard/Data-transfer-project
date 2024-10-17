package source.code.wizard.receiverapp.websocket.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import source.code.wizard.receiverapp.Model.dto.DataRequestDto;
import source.code.wizard.receiverapp.Model.entity.DataEntity;
import source.code.wizard.receiverapp.repository.DataEntityRepository;
import source.code.wizard.receiverapp.util.DataEntityBuilder;

import java.io.IOException;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(DataWebSocketHandler.class);
    private final DataEntityRepository dataEntityRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("WebSocket connection established with session ID: {}", session.getId());
        // dataTransferService.setSession(session); // Set the session in the service
    }

    @Override
    @Transactional
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        logger.info("Received message from client: {}", message.getPayload());
        Long messageId = 0L;
        try {
            final DataRequestDto dataRequestDto = DataEntityBuilder.buildFromArray(message.asBytes());
            messageId = dataRequestDto.id();

            Random rand = new Random();
            if (rand.nextInt(100) < 3)
                throw new Exception("Data: " + messageId + " was not saved!");

            dataEntityRepository.save(toDataEntity(dataRequestDto));

            // Send success acknowledgment back to the sender
           // session.sendMessage(new TextMessage("Data successfully saved for ID: " + dataRequestDto.id()));
        } catch (IOException e) {
            logger.error("Failed to convert message to DataEntity object", e);
            // Notify the sender about the error
            sendMessageThroughWS(session, "Error: Failed to convert message to DataEntity object. Error details: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to save data to the database", e);
            // Notify the sender about the error
            sendMessageThroughWS(session, "Error: Failed to save data" + messageId + " to the database. Error details: " + e.getMessage());
        }
    }

    private static DataEntity toDataEntity(final DataRequestDto dataRequest) {
        return DataEntity.builder()
                .id(dataRequest.id())
                .fieldOne(dataRequest.fieldOne())
                .fieldTwo(dataRequest.fieldTwo())
                .fieldThree(dataRequest.fieldThree())
                .fieldFour(dataRequest.fieldFour())
                .build();
    }

    private static void sendMessageThroughWS(final WebSocketSession session, final String e) {
        try {
            session.sendMessage(new TextMessage(e));
        } catch (Exception sendError) {
            logger.error("Failed to send error message to sender", sendError);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("Transport error in WebSocket session ID: {}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("WebSocket connection closed with session ID: {}, status: {}", session.getId(), status);
    }

}

