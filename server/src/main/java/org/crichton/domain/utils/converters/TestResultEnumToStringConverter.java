package org.crichton.domain.utils.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;
import org.crichton.domain.utils.enums.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Converter(autoApply = true)
public class TestResultEnumToStringConverter implements AttributeConverter<TestResult, String> {

    private static final Logger logger = LoggerFactory.getLogger(TestResultEnumToStringConverter.class);

    @Override
    public String convertToDatabaseColumn(TestResult testResult) {
        return testResult == null ? null : testResult.name();
    }

    @Override
    public TestResult convertToEntityAttribute(String testResult) {
        try {
            return StringUtils.isBlank(testResult) ? TestResult.None : TestResult.valueOf(testResult);
        }
        catch (Exception e) {
            logger.warn("Failed to convert database value to TestResult enum: {}", testResult, e);
            return TestResult.None;
        }
    }
}
