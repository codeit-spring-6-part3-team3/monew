package com.team03.monew.interest.service;


import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestRegisterRequest;
import com.team03.monew.interest.dto.InterestUpdateRequest;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface InterestService {

    InterestDto interestCreate(InterestRegisterRequest request);

    InterestDto interestUpdate(UUID interest, InterestUpdateRequest request) throws NoSuchObjectException;
}
