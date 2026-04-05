package com.zekiloni.george.platform.domain.gsm.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing different GSM Box types/models.
 */
@Getter
@RequiredArgsConstructor
public enum GSMBoxType {
    QUAD_BAND("quad_band", "Quad Band GSM Box"),
    DUAL_BAND("dual_band", "Dual Band GSM Box"),
    SINGLE_BAND("single_band", "Single Band GSM Box"),
    LTE_MODEM("lte_modem", "LTE Modem"),
    ROUTER_4G("4g_router", "4G Router"),
    MODEM_5G("5g_modem", "5G Modem"),
    HYBRID("hybrid", "Hybrid GSM/VoIP Box");

    private final String value;
    private final String displayName;
}

