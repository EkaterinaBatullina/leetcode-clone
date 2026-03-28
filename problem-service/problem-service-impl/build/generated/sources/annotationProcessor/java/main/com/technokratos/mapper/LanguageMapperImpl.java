package com.technokratos.mapper;

import com.technokratos.dto.request.LanguageRequest;
import com.technokratos.dto.response.LanguageResponse;
import com.technokratos.entity.Language;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-28T16:06:12+0300",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class LanguageMapperImpl implements LanguageMapper {

    @Override
    public LanguageResponse toResponse(Language language) {
        if ( language == null ) {
            return null;
        }

        int id = 0;
        String name = null;

        id = language.getId();
        name = language.getName();

        LanguageResponse languageResponse = new LanguageResponse( id, name );

        return languageResponse;
    }

    @Override
    public Language toEntity(LanguageRequest languageRequest) {
        if ( languageRequest == null ) {
            return null;
        }

        Language.LanguageBuilder language = Language.builder();

        language.id( languageRequest.id() );
        language.name( languageRequest.name() );

        return language.build();
    }

    @Override
    public List<LanguageResponse> toResponse(List<Language> languages) {
        if ( languages == null ) {
            return null;
        }

        List<LanguageResponse> list = new ArrayList<LanguageResponse>( languages.size() );
        for ( Language language : languages ) {
            list.add( toResponse( language ) );
        }

        return list;
    }

    @Override
    public List<Language> toEntity(List<LanguageRequest> languageRequest) {
        if ( languageRequest == null ) {
            return null;
        }

        List<Language> list = new ArrayList<Language>( languageRequest.size() );
        for ( LanguageRequest languageRequest1 : languageRequest ) {
            list.add( toEntity( languageRequest1 ) );
        }

        return list;
    }
}
