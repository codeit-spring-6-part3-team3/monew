package com.team03.monew.subscribe.service;

import com.team03.monew.subscribe.dto.SubscribeDto;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.UUID;

public interface SubscribeService {
    SubscribeDto subscribeCreate(UUID userId, UUID interestId);
    void subscribeDelete(UUID userId, UUID interestId);
    List<SubscribeDto> subscribeUser(UUID userId);
}
