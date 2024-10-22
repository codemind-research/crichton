package org.crichton.models.defect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Bitflip {

    @JsonProperty("var")
    private final String variable;

    private final String type;

    private final String value;

    @Builder.Default
    private final String pattern = "random";

}
