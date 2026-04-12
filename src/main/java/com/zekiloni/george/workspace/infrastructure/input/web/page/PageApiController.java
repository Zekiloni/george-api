package com.zekiloni.george.workspace.infrastructure.input.web.page;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path:/api/v1}/page")
@RequiredArgsConstructor
public class PageApiController {
}
