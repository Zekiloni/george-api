package com.zekiloni.george.platform.application.port.in.page;

import com.zekiloni.george.platform.domain.model.page.Page;

public interface PageCreateUseCase {

    Page handle(Page command);
}
