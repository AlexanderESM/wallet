package net.ork.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ork.wallet.model.WalletRequest;
import net.ork.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();

    @Test
    void processOperation_ShouldReturnOk_WhenValidRequest() throws Exception {
        WalletRequest request = new WalletRequest(UUID.randomUUID(), "DEPOSIT", BigDecimal.TEN);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Operation successful"));

        // Verify that the service method was called
        verify(walletService, times(1)).processOperation(request);
    }

    @Test
    void getBalance_ShouldReturnBalance_WhenValidWalletId() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.TEN;

        when(walletService.getBalance(walletId)).thenReturn(balance);

        mockMvc.perform(get("/api/v1/wallet/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().json(balance.toString()));

        // Verify that the service method was called
        verify(walletService, times(1)).getBalance(walletId);
    }
}
