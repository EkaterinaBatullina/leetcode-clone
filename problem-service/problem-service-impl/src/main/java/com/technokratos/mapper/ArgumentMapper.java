package com.technokratos.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.dto.ArgumentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArgumentMapper {

    default List<ArgumentDto> map(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize arguments JSON", e);
        }
    }

    default String map(List<ArgumentDto> arguments) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(arguments);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize arguments", e);
        }
    }
}
