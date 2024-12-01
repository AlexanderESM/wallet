package net.ork.wallet.exception;

import net.ork.wallet.controller.WalletController;
import net.ork.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(walletController)
            .setControllerAdvice(new GlobalExceptionHandler()) // Устанавливаем GlobalExceptionHandler
            .build();

    @Test
    void handleWalletNotFound_ShouldReturnNotFound_WhenExceptionThrown() throws Exception {
        // Вызываем контроллер с ошибкой WalletNotFoundException
        mockMvc.perform(get("/api/v1/wallet/{walletId}", "invalid-id"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found"));
    }

    @Test
    void handleInsufficientFunds_ShouldReturnBadRequest_WhenExceptionThrown() throws Exception {
        // Вызываем контроллер с ошибкой InsufficientFundsException
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"valid-id\", \"operationType\":\"WITHDRAW\", \"amount\":1000}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough funds"));
    }

    @Test
    void handleInvalidOperation_ShouldReturnBadRequest_WhenExceptionThrown() throws Exception {
        // Вызываем контроллер с ошибкой InvalidOperationTypeException
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"walletId\":\"valid-id\", \"operationType\":\"INVALID\", \"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid operation type"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError_WhenExceptionThrown() throws Exception {
        // Вызываем контроллер с общим исключением
        mockMvc.perform(get("/api/v1/wallet/{walletId}", "invalid-id"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred: Wallet not found"));
    }
}
