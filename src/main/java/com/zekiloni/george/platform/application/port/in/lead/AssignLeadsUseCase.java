package com.zekiloni.george.platform.application.port.in.lead;

import java.util.List;

public interface AssignLeadsUseCase {
    void handle(String serviceAccessId, List<String> leadIds);
}
