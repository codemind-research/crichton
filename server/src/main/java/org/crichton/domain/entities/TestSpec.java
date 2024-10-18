package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public record TestSpec(List<Task> tasks, long stopDuration) {

    public record Task(String name, long startTime, long cycleDuration, long priority, String file) {
    }
}
