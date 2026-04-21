package com.zekiloni.george.platform.application.port.in;

import com.zekiloni.george.platform.domain.page.Page;

public interface PageCreateUseCase {

    Page handle(Page command);
}
