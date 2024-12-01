package net.ork.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import net.ork.wallet.model.WalletRequest;

@Service
@RequiredArgsConstructor
public class WalletQueueService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${wallet.queue.name}")
    private String queueName;

    public void sendOperationToQueue(WalletRequest request) {
        rabbitTemplate.convertAndSend(queueName, request);
    }
}
