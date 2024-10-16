package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class TestSpec {

    private List<Task> tasks;

    private long stopDuration;

    @Builder
    @Getter
    public static class Task {

        private String name;

        private long startTime;

        private long cycleDuration;

        private long priority;

        private String file;
    }
}
