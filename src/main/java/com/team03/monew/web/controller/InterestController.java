package com.team03.monew.web.controller;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.*;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.dto.SubscribeDto;
import com.team03.monew.subscribe.service.SubscribeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Mutability;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;
    private final SubscribeService subscribeService;

    //1 관심사 컨트롤러 생성 추가
    @PostMapping
    public ResponseEntity<InterestDto> interestCreate(
            @RequestBody
            @Valid
            InterestRegisterRequest request
    ) {
        InterestDto response = interestService.interestCreate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
