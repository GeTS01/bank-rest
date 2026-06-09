package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, UUID> {

    boolean existsByNumberHash(String numberHash);

    Optional<Card> findByIdAndOwnerUsername(UUID id, String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c join fetch c.owner where c.id = :id")
    Optional<Card> findByIdForUpdate(@Param("id") UUID id);

    @Query("""
            select c from Card c
            join c.owner o
            where (:ownerId is null or o.id = :ownerId)
              and (:status is null or c.status = :status)
              and (:search is null
                   or c.lastFour like concat('%', :search, '%')
                   or lower(o.username) like lower(concat('%', :search, '%'))
                   or lower(o.fullName) like lower(concat('%', :search, '%')))
            """)
    Page<Card> searchAll(@Param("ownerId") UUID ownerId,
                         @Param("status") CardStatus status,
                         @Param("search") String search,
                         Pageable pageable);

    @Query("""
            select c from Card c
            join c.owner o
            where o.username = :username
              and (:status is null or c.status = :status)
              and (:search is null or c.lastFour like concat('%', :search, '%'))
            """)
    Page<Card> searchOwn(@Param("username") String username,
                         @Param("status") CardStatus status,
                         @Param("search") String search,
                         Pageable pageable);
}
