package org.crichton.domain.utils.mapper;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Mapper(componentModel = "spring")
public interface InjectorPluginResultMapper {

    default List<Object> mapToObject(Map<String, Object> map) {
        return map.values().stream()
                .filter(value -> value instanceof Map)
                .collect(Collectors.toUnmodifiableList());
    }


}
