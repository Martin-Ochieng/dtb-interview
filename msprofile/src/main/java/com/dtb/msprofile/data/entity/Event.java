package com.dtb.msprofile.data.entity;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("events")
public class Event {

    @Id
    private UUID id;

    @Column("transaction_id")
    private UUID transactionId;

    @Column("profile_id")
    private UUID profileId;

    @Column("event_type")
    private String eventType;

    private JsonNode payload; // JSONB support using Jackson's JsonNode

    @Column("created_at")
    private OffsetDateTime createdAt;
}
