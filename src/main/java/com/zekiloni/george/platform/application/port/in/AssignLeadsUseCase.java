package com.zekiloni.george.platform.application.port.in;

import java.util.List;

public interface AssignLeadsUseCase {
    void handle(String serviceAccessId, List<String> leadIds);
}
