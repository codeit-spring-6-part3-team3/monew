package com.team03.monew.interest.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestSearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.team03.monew.interest.domain.QInterest.interest;

//2 관심사 목록 조회
@Repository
public class InterestRepositoryCustomImpl  implements InterestRepositoryCustom {

    //QueryDSL 공용 설정 파일 생성 예정
    //@PersistenceContext
    //private EntityManager em;

    private final JPAQueryFactory queryFactory;

    public InterestRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Interest> search(InterestSearchRequest request) {
        return queryFactory
                .selectFrom(interest)
                .where(
                        containsKeyword(request.keyword()),
                        cursorCondition(request)
                )
                .orderBy(
                        getOrderSpecifier(request.orderBy(), request.direction()),
                        interest.id.desc()

                )
                .limit(request.limit()+1)
                .fetch();
    }

    @Override
    public Long totalElements(InterestSearchRequest request) {
        return queryFactory
                .select(interest.count())
                .from(interest)
                .where(
                        containsKeyword(request.keyword())
                )
                .fetchOne();
    }


    // 관심사 이름 or 키워드 부분일치
    private BooleanExpression containsKeyword(String keyword) {
        if(keyword == null || keyword.isEmpty()) return null;

        return interest.name.contains(keyword)
                .or(
                        //QueryDSL의 .contains() 대신 SQL 함수 직접 호출
                        //Hibernate 6부터 'array_contains' 함수가 표준으로 지원함
                        Expressions.booleanTemplate(
                                "function('array_contains', {0}, {1}) = true",
                                interest.keywords,
                                keyword
                        )
                );
    }

    // 커서 조건절
    private BooleanExpression cursorCondition(InterestSearchRequest req) {
        if(req.cursor() == null || req.after() == null) return null;

        String cursor =req.cursor();
        String after = req.after();
        boolean isDesc = "DESC".equalsIgnoreCase(req.direction());

        // 조건 1 구독자 수 정렬
        if("subscriberCount".equals(req.orderBy())){
            long lastSubscriber = Long.parseLong(cursor);
            if(isDesc){
                // DESC: 값 < last OR (값 == last AND id < lastId)
                return interest.subscribeCount.lt(lastSubscriber)
                        .or(interest.subscribeCount.eq(lastSubscriber)
                                .and(interest.createdAt.lt(LocalDateTime.parse(after)))
                        );
            }else {
                // ASC: 값 > last OR (값 == last AND id < lastId)
                return interest.subscribeCount.gt(lastSubscriber)
                        .or(interest.subscribeCount.lt(lastSubscriber)
                                .and(interest.createdAt.lt(LocalDateTime.parse(after)))
                        );
            }
        }
        else {  // 조건 2 이름 정렬

            if(isDesc){
                return interest.name.lt(cursor)
                        .or(interest.name.eq(cursor)
                                .and(interest.createdAt.lt(LocalDateTime.parse(after)))
                        );
            }else {
                return interest.name.gt(cursor)
                        .or(interest.name.eq(cursor)
                                .and(interest.createdAt.lt(LocalDateTime.parse(after)))
                        );
            }
        }
    }

    // 동적 이름 or 구독자 수 정렬 기준
    private OrderSpecifier<?> getOrderSpecifier(String sortBy, String sortOrder) {
        Order direction = "desc".equalsIgnoreCase(sortOrder) ? Order.DESC : Order.ASC;

        if("subscriberCount".equalsIgnoreCase(sortBy)) {
            return new OrderSpecifier<>(direction, interest.subscribeCount);
        }

        return new OrderSpecifier<>(direction, interest.name);
    }

}
