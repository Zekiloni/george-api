package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;

import java.util.List;

public interface ServiceAccessCreateUseCase {
    ServiceAccess create(ServiceAccess serviceAccess);

}
