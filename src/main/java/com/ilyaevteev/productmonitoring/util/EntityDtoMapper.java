package com.ilyaevteev.productmonitoring.util;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EntityDtoMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public EntityDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <Entity, Dto> Dto toDto(Entity category, Class<Dto> dtoClass) {
        return Objects.isNull(category) ? null : modelMapper.map(category, dtoClass);
    }

    public <Entity, Dto> Entity toEntity(Dto dto, Class<Entity> entityClass) {
        return Objects.isNull(dto) ? null : modelMapper.map(dto, entityClass);

    }
}
