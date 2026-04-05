package com.zekiloni.george.platform.application.port.in;


import java.io.InputStream;

public interface LeadImportUseCase {
    void handle(InputStream inputStream);
}
