package com.studysync.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private String id;
    private String userId;
    private String groupId;
    private Instant startTime;
    private Instant endTime;
    private Long durationSeconds;
    private Long currentDuration;
    private String status;
    private String studySubject;
    private String location;
    private String mood;
    private String notes;
    private Integer productivity;
    private SessionValidationDto validation;
}



