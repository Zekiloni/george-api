package com.zekiloni.george.platform.application.port.in.lead;


import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface LeadImportUseCase {
    void handle(LeadImportCommand command) throws IOException;

    record LeadImportCommand(InputStream inputStream, Optional<String> serviceAccessId) {
    }
}
