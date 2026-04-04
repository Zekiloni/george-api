package com.zekiloni.george.infrastructure.input.web.billing;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path:/api/v1}/plan")
@RequiredArgsConstructor
public class PlanApiController {
}
