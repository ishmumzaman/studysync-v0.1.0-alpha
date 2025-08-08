package com.studysync.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionStartRequest {
    private String groupId;
    private String studySubject;
    private String location;
    private Integer plannedDuration;
    private String platform;
    private String appVersion;
    private String deviceId;
}



