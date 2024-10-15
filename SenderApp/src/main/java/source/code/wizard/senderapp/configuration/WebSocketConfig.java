package source.code.wizard.senderapp.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import source.code.wizard.senderapp.ws.WSDataTransferHandler;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final WSDataTransferHandler wsDataTransferHandler;

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, wsDataTransferHandler, "ws://localhost:8080/data-transfer-websocket-channel");
        manager.setAutoStartup(true); // Automatically start the connection on application startup
        return manager;
    }
}

