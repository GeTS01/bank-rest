package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
