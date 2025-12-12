package com.team03.monew.user.mapper;

import com.team03.monew.user.domain.User;
import com.team03.monew.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-12T12:05:54+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.16 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String email = null;
        String nickname = null;
        LocalDateTime createdAt = null;

        id = user.getId();
        email = user.getEmail();
        nickname = user.getNickname();
        createdAt = user.getCreatedAt();

        UserDto userDto = new UserDto( id, email, nickname, createdAt );

        return userDto;
    }
}
