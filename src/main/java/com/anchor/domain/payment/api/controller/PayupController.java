package com.anchor.domain.payment.api.controller;

import com.anchor.domain.payment.api.service.PayupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payup")
public class PayupController {

    private final PayupService payupService;

    // 정산 현황 조회
    // 한달에 한번 정산

    //정산 방식
    // 계좌이체 api가 없으므로, mentor의 계좌번호, 계좌이름 조회
    // payment의 정산

    @PostMapping("/month")
    public void payupProcess() {
        // 정산일자는 매달

    }

}
