package com.zekiloni.george.platform.application.port.in;

import com.zekiloni.george.platform.domain.page.Page;


public interface PageUpdateUseCase {

    Page handle(String id, Page command);
}

