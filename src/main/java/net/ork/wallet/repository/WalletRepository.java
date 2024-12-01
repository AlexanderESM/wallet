package net.ork.wallet.repository;

import net.ork.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    // Использование пессимистичной блокировки для предотвращения гонок
    Optional<Wallet> findById(UUID id, LockModeType lockMode);
}
