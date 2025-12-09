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

    //2 관심사 컨트롤러 업데이트 추가
    @PatchMapping("/{interestId}")
    public ResponseEntity<InterestDto> interestUpdate(
            @NotNull
            @PathVariable(name = "interestId")
            UUID interestId,
            @Valid
            @RequestBody
            InterestUpdateRequest request
    ) throws NoSuchObjectException {
        InterestDto response = interestService.interestUpdate(interestId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //3 관심사 목록 조회 추가
    @GetMapping
    public ResponseEntity<CursorPageResponseInterestDto> interestList(
            @NotNull
            @RequestParam(name = "Monew-Request-User-ID")
            UUID userId,
            @Valid
            @ModelAttribute
            InterestSearchRequest request
    ){
        CursorPageResponseInterestDto response = interestService.interestList(userId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //4 관심사 구독 생성 추가
    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<SubscribeDto> subscribeCreate(
            @PathVariable
            UUID interestId,
            @RequestParam(name = "Monew-Request-User-ID")
            UUID userId
    ) throws NoSuchObjectException {
        SubscribeDto response = subscribeService.subscribeCreate(userId,interestId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //5 관심사 구독 삭제 추가
    @DeleteMapping("/{interestId}/subscriptions")
    public ResponseEntity<Void> subscribeDelete(
            @PathVariable
            UUID interestId,
            @RequestParam(name = "Monew-Request-User-ID")
            UUID userId
    ) throws NoSuchObjectException {
        subscribeService.subscribeDelete(userId,interestId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //6 관심사 물리 삭제 추가
    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> interestDelete(
            @PathVariable
            UUID interestId
    ) throws NoSuchObjectException {
        interestService.interestDelete(interestId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
