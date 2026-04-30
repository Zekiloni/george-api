package com.zekiloni.george.commerce.application.port.out.gateway;

public interface SmtpProvisioningPort {

    void createAccount(String gatewayId, String username, String password, String email);

    void deleteAccount(String gatewayId, String username);
}
