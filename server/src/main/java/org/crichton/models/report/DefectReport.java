package org.crichton.models.report;

import lombok.Builder;
import org.crichton.models.safe.SafeSpec;

import java.util.List;

@Builder
public record DefectReport(int defectId, String file, List<SafeSpec> safeSpecs) {

}
