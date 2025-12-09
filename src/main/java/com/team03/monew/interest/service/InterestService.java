package com.team03.monew.interest.service;


import com.team03.monew.interest.dto.*;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface InterestService {

    InterestDto interestCreate(InterestRegisterRequest request);

    InterestDto interestUpdate(UUID interest, InterestUpdateRequest request) throws NoSuchObjectException;

    void interestDelete(UUID interest) throws NoSuchObjectException;

    CursorPageResponseInterestDto interestList(UUID userId, InterestSearchRequest request);
}
