package org.crichton.domain.utils.mapper;

import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.entities.TestSpec;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestSpecMapper {

    TestSpec testSpecDtoToTestSpecEntity(TestSpecDto dto);

    TestSpec.Task taskDtoToTaskEntity(TestSpecDto.TaskDto dto);

}
