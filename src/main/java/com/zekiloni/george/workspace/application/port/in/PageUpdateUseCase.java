package com.zekiloni.george.workspace.application.port.in;

import com.zekiloni.george.workspace.domain.page.Page;


public interface PageUpdateUseCase {

    Page handle(String id, Page command);
}

