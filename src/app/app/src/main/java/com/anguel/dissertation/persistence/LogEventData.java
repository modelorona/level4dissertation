package com.anguel.dissertation.persistence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogEventData {
    private String packageName;
    private long lastTimeVisible;
    private long lastTimeUsed;
    private long lastTimeInForeground;
    private long totalTimeForegroundUsed;
    private long totalTimeInForeground;
    private long totalTimeVisible;
}
