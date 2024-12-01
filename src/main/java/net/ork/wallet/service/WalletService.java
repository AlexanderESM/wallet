package net.ork.wallet.service;

import lombok.RequiredArgsConstructor;
import net.ork.wallet.exception.InsufficientFundsException;
import net.ork.wallet.exception.InvalidOperationTypeException;
import net.ork.wallet.exception.WalletNotFoundException;
import net.ork.wallet.model.Wallet;
import net.ork.wallet.model.WalletRequest;
import net.ork.wallet.repository.WalletRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    // Аннотация @Retryable для повторных попыток в случае исключений
    @Retryable(
            value = {WalletNotFoundException.class, InsufficientFundsException.class}, // Исключения, для которых будет выполняться повторная попытка
            maxAttempts = 3, // Максимальное количество попыток
            backoff = @Backoff(delay = 1000) // Задержка между попытками (в миллисекундах)
    )
    @Transactional
    public void processOperation(WalletRequest request) {
        // Получаем кошелек из базы с использованием пессимистичной блокировки для предотвращения гонок
        Wallet wallet = walletRepository.findById(request.getWalletId(), LockModeType.PESSIMISTIC_WRITE)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        switch (request.getOperationType()) {
            case "WITHDRAW":
                // Проверка на наличие достаточных средств
                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new InsufficientFundsException("Not enough funds");
                }
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount())); // Снятие средств
                break;

            case "DEPOSIT":
                wallet.setBalance(wallet.getBalance().add(request.getAmount())); // Пополнение средств
                break;

            default:
                throw new InvalidOperationTypeException("Invalid operation type");
        }

        // Сохраняем обновленный кошелек в базе данных
        walletRepository.save(wallet);
    }

    // Метод для получения баланса кошелька
    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        return wallet.getBalance();
    }
}
