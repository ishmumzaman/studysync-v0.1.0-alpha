package com.studysync.dto.session;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEndRequest {
    private String mood;
    private String notes;
    
    @Min(1)
    @Max(5)
    private Integer productivity;
}



