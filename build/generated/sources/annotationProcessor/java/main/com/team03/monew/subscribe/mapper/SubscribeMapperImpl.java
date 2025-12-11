package com.team03.monew.subscribe.mapper;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-12T07:05:20+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class SubscribeMapperImpl implements SubscribeMapper {

    @Override
    public SubscribeDto toDto(Subscribe subscribe, Interest interest) {
        if ( subscribe == null && interest == null ) {
            return null;
        }

        SubscribeDto.SubscribeDtoBuilder subscribeDto = SubscribeDto.builder();

        if ( subscribe != null ) {
            subscribeDto.id( subscribe.getId() );
            subscribeDto.createdAt( subscribe.getCreatedAt() );
        }
        if ( interest != null ) {
            subscribeDto.interestId( interest.getId() );
            subscribeDto.interestName( interest.getName() );
            List<String> list = interest.getKeywords();
            if ( list != null ) {
                subscribeDto.interestKeywords( new ArrayList<String>( list ) );
            }
            if ( interest.getSubscribeCount() != null ) {
                subscribeDto.interestSubscriberCount( interest.getSubscribeCount() );
            }
        }

        return subscribeDto.build();
    }
}
