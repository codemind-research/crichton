package org.crichton.domain.utils.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Converter(autoApply = true)
public class UUIDToStringConverter implements AttributeConverter<UUID, String> {


    /**
     * UUID를 문자열로 변환하여 데이터베이스에 저장합니다.
     * @param uuid  uuid 객체
     * @return 객체가 null이면 null 그외, UUID의 문자열
     */
    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return Objects.isNull(uuid) ? null : uuid.toString();
    }

    /**
     * 데이터베이스에 저장된 UUID 문자열을 UUID 객체로 만듭니다.
     * @param uuid 데이터베이스에 저장된 UUID의 문자열 형태
     * @return uuid가 null이나 빈 값이라면 null 그외, UUID 객체
     */
    @Override
    public UUID convertToEntityAttribute(String uuid) {
        return StringUtils.isBlank(uuid) ? null : UUID.fromString(uuid);
    }
}
