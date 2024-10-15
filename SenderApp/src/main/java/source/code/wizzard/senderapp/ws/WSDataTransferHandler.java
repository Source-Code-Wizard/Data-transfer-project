package source.code.wizzard.senderapp.ws;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Getter
@Slf4j
@Component
public class WSDataTransferHandler extends TextWebSocketHandler {
    private WebSocketSession session;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established with session ID: {}", session.getId());
        this.session = session; // Save the session to use later for sending messages
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received response from receiver: {}", message.getPayload());
        // Here we can receive a validation response dto that contains all the info about the result of the procedure and act accordingly
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Transport error in WebSocket session ID: {}", session.getId(), exception);
        this.session = null; // Reset the session on error
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket connection closed with session ID: {}, status: {}", session.getId(), status);
        this.session = null; // Reset the session on close
    }

}
