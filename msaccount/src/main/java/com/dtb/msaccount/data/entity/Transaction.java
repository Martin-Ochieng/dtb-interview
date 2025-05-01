package com.dtb.msaccount.data.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column("account_id")
    private UUID accountId;

    private String type;

    private BigDecimal amount;

    @Column("related_account")
    private UUID relatedAccount;

    @Column("created_at")
    private OffsetDateTime createdAt;
}

