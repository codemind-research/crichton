package org.crichton.models.defect;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public record Spec(Bitflip bitflip) {
}
