package com.team03.monew.subscribe.service;

import com.team03.monew.subscribe.dto.SubscribeDto;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

public interface SubscribeService {
    SubscribeDto subscribeCreate(UUID userId, UUID interestId) throws NoSuchObjectException;
}
