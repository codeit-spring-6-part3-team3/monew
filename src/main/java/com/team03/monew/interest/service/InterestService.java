package com.team03.monew.interest.service;


import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestRegisterRequest;

public interface InterestService {

    InterestDto interestCreate(InterestRegisterRequest request);
}
