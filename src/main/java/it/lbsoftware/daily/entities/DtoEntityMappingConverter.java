package it.lbsoftware.daily.entities;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DtoEntityMappingConverter<DTO extends BaseDto, ENTITY extends BaseEntity> {

    private final ModelMapper modelMapper;

    public DTO convertToDto(Object entity, Class<DTO> dtoType) {
        return modelMapper.map(entity, dtoType);
    }

    public ENTITY convertToEntity(Object dto, Class<ENTITY> entityType) {
        return modelMapper.map(dto, entityType);
    }

}
