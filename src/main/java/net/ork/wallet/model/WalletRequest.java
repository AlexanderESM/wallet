package net.ork.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletRequest {
    private UUID walletId;
    private String operationType; // DEPOSIT or WITHDRAW
    private BigDecimal amount;
}
