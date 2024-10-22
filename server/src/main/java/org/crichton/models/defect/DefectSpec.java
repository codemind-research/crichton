package org.crichton.models.defect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.crichton.models.safe.SafeSpec;

@Getter
public class DefectSpec {

    private String filePath;

    private SafeSpec spec;

}
