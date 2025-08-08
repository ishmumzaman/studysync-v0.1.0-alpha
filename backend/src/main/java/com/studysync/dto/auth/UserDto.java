package com.studysync.dto.auth;

import com.studysync.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String timezone;
    private User.UserPreferences preferences;
    private User.UserAnalytics analytics;
    private Instant createdAt;
    private Instant updatedAt;
}



