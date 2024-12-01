package net.ork.wallet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ork.wallet.model.WalletRequest;
import net.ork.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<String> processOperation(@RequestBody @Valid WalletRequest request) {
        walletService.processOperation(request);
        return ResponseEntity.ok("Operation successful");
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }
}
