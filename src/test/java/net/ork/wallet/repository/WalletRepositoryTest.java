package net.ork.wallet.repository;

import net.ork.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest  // Аннотация для тестирования репозитория
public class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    private UUID walletId;

    @BeforeEach
    public void setUp() {
        // Создаем и сохраняем тестовый кошелек
        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .balance(BigDecimal.valueOf(1000))
                .build();
        walletId = wallet.getId();
        walletRepository.save(wallet);
    }

    @Test
    public void testFindByIdWithLock() {
        // Попытка получить кошелек с пессимистичной блокировкой
        Optional<Wallet> walletOptional = walletRepository.findById(walletId, LockModeType.PESSIMISTIC_WRITE);

        // Проверяем, что кошелек найден
        assertTrue(walletOptional.isPresent(), "Wallet should be found");

        Wallet wallet = walletOptional.get();

        // Проверяем, что кошелек имеет правильный баланс
        assertEquals(BigDecimal.valueOf(1000), wallet.getBalance(), "Balance should be 1000");
    }

    @Test
    public void testFindByIdWithLock_NotFound() {
        // Используем несуществующий ID
        UUID nonExistentId = UUID.randomUUID();

        // Проверяем, что кошелек с таким ID не найден
        Optional<Wallet> walletOptional = walletRepository.findById(nonExistentId, LockModeType.PESSIMISTIC_WRITE);

        assertFalse(walletOptional.isPresent(), "Wallet should not be found");
    }

    @Test
    public void testFindByIdWithLock_Exception() {
        // Используем null в качестве ID
        assertThrows(IllegalArgumentException.class, () -> walletRepository.findById(null, LockModeType.PESSIMISTIC_WRITE),
                "Should throw IllegalArgumentException when ID is null");
    }
}
