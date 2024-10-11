package org.crichton.domain.utils.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Converter(autoApply = true)
public class ProjectStatusEnumToStringConverter implements AttributeConverter<ProjectStatus, String> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectStatusEnumToStringConverter.class);

    @Override
    public String convertToDatabaseColumn(ProjectStatus projectStatus) {
        return projectStatus == null ? null : projectStatus.name();
    }

    @Override
    public ProjectStatus convertToEntityAttribute(String projectStatus) {
        try {
            return StringUtils.isBlank(projectStatus) ? ProjectStatus.None : ProjectStatus.valueOf(projectStatus);
        }
        catch (Exception e) {
            logger.warn("Failed to convert database value to ProjectStatus enum: {}", projectStatus, e);
            return ProjectStatus.None;
        }
    }
}
