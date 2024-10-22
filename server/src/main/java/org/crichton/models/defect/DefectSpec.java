package org.crichton.models.defect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

public record DefectSpec(int id, long trigger, long cycle, String target, @JsonProperty("defect") Spec spec) {
}
