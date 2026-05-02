package com.zekiloni.george.commerce.application.port.in;

public interface ServiceAccessRenewalControlUseCase {
    /** Set or clear the cancelAtPeriodEnd flag for a ServiceAccess. */
    void setCancelAtPeriodEnd(String serviceAccessId, boolean cancelAtPeriodEnd);
}
