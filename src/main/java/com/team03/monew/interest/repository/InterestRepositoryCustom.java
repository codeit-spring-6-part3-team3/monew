package com.team03.monew.interest.repository;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestSearchRequest;

import java.util.List;

//2 관심사 목록 조회
public interface InterestRepositoryCustom {

    List<Interest> search(InterestSearchRequest request);

    Long totalElements(InterestSearchRequest request);
}
