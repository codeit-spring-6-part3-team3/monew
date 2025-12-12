package com.team03.monew.interest.repository;

import com.team03.monew.article.config.JpaQueryFactoryTestConfig;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestSearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaQueryFactoryTestConfig.class)
public class InterestRepositoryTest {
    @Autowired
    private InterestRepository interestRepository;

    @BeforeEach
    public void setup() {
        Interest interest1 = Interest.builder()
                .name("오늘의 뉴스")
                .keywords(List.of("경재","주식"))
                .build();
        Interest interest2 = Interest.builder()
                .name("오늘의 뉴스 경재")
                .keywords(List.of("환율","부동산"))
                .build();
        interestRepository.save(interest1);
        interestRepository.save(interest2);
    }

    @Test
    @DisplayName("관심사 커서 없을떄 목록 조회")
    public void search(){
        //given
        InterestSearchRequest req = InterestSearchRequest.builder()
                .keyword("오늘에 뉴스")
                .orderBy("name")
                .direction("ASC")
                .cursor(null)
                .after(null)
                .limit(1)
                .build();
        //when
        List<Interest> interests = interestRepository.search(req);
        //then
        assertThat(interests).isNotNull();

    }
    @Test
    @DisplayName("관심사 커서 있을떄 목록 조회")
    public void searchCursor(){
        //given
        InterestSearchRequest req = InterestSearchRequest.builder()
                .keyword("오늘에 뉴스")
                .orderBy("name")
                .direction("ASC")
                .cursor("오늘에 뉴스")
                .after(LocalDateTime.now().toString())
                .limit(1)
                .build();
        //when
        List<Interest> interests = interestRepository.search(req);
        //then
        assertThat(interests).isNotNull();

    }

    @Test
    @DisplayName("관심사 조건에 맞는 개수 조회")
    public void totalElements(){
        InterestSearchRequest req = InterestSearchRequest.builder()
                .keyword("오늘에 뉴스")
                .orderBy("name")
                .direction("ASC")
                .cursor("오늘에 뉴스")
                .after(LocalDateTime.now().toString())
                .limit(1)
                .build();

        Long totalElements = interestRepository.totalElements(req);
        assertThat(totalElements).isNotNull();
    }
}
