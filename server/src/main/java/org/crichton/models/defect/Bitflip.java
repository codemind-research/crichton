package org.crichton.models.defect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@AllArgsConstructor
@Jacksonized
public class Bitflip {

    @JsonProperty("var")
    private final String variable;

    private final String type;

    private final String value;

    @Builder.Default
    private final String pattern = "random";

}
