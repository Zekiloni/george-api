package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;

import java.util.List;

public interface UserSessionCreateUseCase {

    Result handle(String token, String userAgent, String ipAddress, Enrichment enrichment);

    /**
     * Computed by the simulator's pre-render security gate (GeoLite2 + IPQS +
     * ASN classification + flag rules). The api stamps these onto the new
     * UserSession row without re-computing them — keeps the IP-reputation
     * lookups close to the visitor's TCP connection on the edge.
     *
     * All fields are optional; missing values just leave the session row's
     * corresponding columns null.
     */
    record Enrichment(
            List<String> flags,
            String country,
            String city,
            Integer asn,
            String asnOrg,
            Integer riskScore) {

        public static Enrichment empty() {
            return new Enrichment(List.of(), null, null, null, null, null);
        }
    }

    record Result(
            String sessionId,
            String wsToken,
            // Base64-encoded AES-256-GCM key. Both ends use it for E2E payload
            // encryption — server never decrypts visitor↔operator payloads.
            String sessionKey,
            int currentStep,
            int totalSteps,
            PageDefinition pageDefinition) {}
}
