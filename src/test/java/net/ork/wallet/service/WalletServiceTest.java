package net.ork.wallet.service;

import net.ork.wallet.exception.InsufficientFundsException;
import net.ork.wallet.exception.InvalidOperationTypeException;
import net.ork.wallet.exception.WalletNotFoundException;
import net.ork.wallet.model.Wallet;
import net.ork.wallet.model.WalletRequest;
import net.ork.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private WalletRequest request;

    @BeforeEach
    public void setup() {
        wallet = new Wallet(UUID.randomUUID(), BigDecimal.valueOf(1000));
        request = WalletRequest.builder()
                .walletId(wallet.getId())
                .operationType("WITHDRAW")
                .amount(BigDecimal.valueOf(500))
                .build();
    }

    @Test
    public void testProcessOperationWithdraw() {
        // Arrange
        when(walletRepository.findById(wallet.getId(), LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(Optional.of(wallet));

        // Act
        walletService.processOperation(request);

        // Assert
        assertEquals(BigDecimal.valueOf(500), wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    public void testProcessOperationInsufficientFunds() {
        // Arrange
        request.setAmount(BigDecimal.valueOf(1500)); // Слишком большая сумма для снятия
        when(walletRepository.findById(wallet.getId(), LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(Optional.of(wallet));

        // Act & Assert
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            walletService.processOperation(request);
        });
        assertEquals("Not enough funds", exception.getMessage());
    }

    @Test
    public void testProcessOperationInvalidOperationType() {
        // Arrange
        request.setOperationType("TRANSFER"); // Недопустимый тип операции
        when(walletRepository.findById(wallet.getId(), LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(Optional.of(wallet));

        // Act & Assert
        InvalidOperationTypeException exception = assertThrows(InvalidOperationTypeException.class, () -> {
            walletService.processOperation(request);
        });
        assertEquals("Invalid operation type", exception.getMessage());
    }

    @Test
    public void testGetBalance() {
        // Arrange
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        // Act
        BigDecimal balance = walletService.getBalance(wallet.getId());

        // Assert
        assertEquals(BigDecimal.valueOf(1000), balance);
    }

    @Test
    public void testGetBalanceWalletNotFound() {
        // Arrange
        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.empty());

        // Act & Assert
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(wallet.getId());
        });
        assertEquals("Wallet not found", exception.getMessage());
    }
}
