package com.studysync.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionValidationDto {
    private double anomalyScore;
    private List<String> flags;
    private boolean isValid;
    private Instant validatedAt;
}



