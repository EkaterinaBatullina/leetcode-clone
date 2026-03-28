package com.technokratos.mapper;

import com.technokratos.dto.request.TagRequest;
import com.technokratos.dto.response.TagResponse;
import com.technokratos.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse(Tag tag);

    Tag toEntity(TagRequest tagRequest);

    List<TagResponse> toResponse(List<Tag> tags);

    List<Tag> toEntity(List<TagRequest> tagRequest);

}
