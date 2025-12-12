package com.team03.monew.interest.mapper;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-12T12:05:54+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class InterestMapperImpl implements InterestMapper {

    @Override
    public InterestDto toDto(Interest interest, Boolean subscribedByMe) {
        if ( interest == null && subscribedByMe == null ) {
            return null;
        }

        InterestDto.InterestDtoBuilder interestDto = InterestDto.builder();

        if ( interest != null ) {
            interestDto.subscriberCount( interest.getSubscribeCount() );
            interestDto.id( interest.getId() );
            interestDto.name( interest.getName() );
            List<String> list = interest.getKeywords();
            if ( list != null ) {
                interestDto.keywords( new ArrayList<String>( list ) );
            }
        }
        interestDto.subscribedByMe( subscribedByMe );

        return interestDto.build();
    }
}
