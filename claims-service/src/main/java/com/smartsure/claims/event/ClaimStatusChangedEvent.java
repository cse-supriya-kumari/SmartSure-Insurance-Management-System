package com.smartsure.claims.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimStatusChangedEvent {
    private Long claimId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime timestamp;
}
