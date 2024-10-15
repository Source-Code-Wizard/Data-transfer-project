package source.code.wizard.senderapp;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class SenderAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SenderAppApplication.class, args);
    }
}
