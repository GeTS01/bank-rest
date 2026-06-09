package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public Card getFromCard() {
        return fromCard;
    }

    public void setFromCard(Card fromCard) {
        this.fromCard = fromCard;
    }

    public Card getToCard() {
        return toCard;
    }

    public void setToCard(Card toCard) {
        this.toCard = toCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
