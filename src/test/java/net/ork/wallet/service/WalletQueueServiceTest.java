package net.ork.wallet.service;

import net.ork.wallet.model.WalletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
public class WalletQueueServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private WalletQueueService walletQueueService;

    @Value("${wallet.queue.name}")
    private String queueName;

    @Test
    public void testSendOperationToQueue() {
        // Создаем тестовый объект WalletRequest
        WalletRequest request = WalletRequest.builder()
                .walletId(UUID.randomUUID())
                .operationType("DEPOSIT")
                .amount(BigDecimal.valueOf(500))
                .build();

        // Вызываем метод, который мы тестируем
        walletQueueService.sendOperationToQueue(request);

        // Проверяем, что метод convertAndSend был вызван с правильными аргументами
        verify(rabbitTemplate, times(1)).convertAndSend(queueName, request);
    }
}
