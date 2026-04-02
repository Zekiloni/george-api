package com.zekiloni.george.domain.gsm.entity;

/**
 * Enum representing GSM protocols supported by GSM boxes.
 */
public enum GSMProtocol {
    GSM("gsm", "GSM"),
    GPRS("gprs", "GPRS"),
    EDGE("edge", "EDGE"),
    UMTS("umts", "UMTS/3G"),
    HSPA("hspa", "HSPA"),
    LTE("lte", "LTE/4G"),
    LTE_ADVANCED("lte_advanced", "LTE-Advanced (4G+)"),
    NR("nr", "5G NR"),
    VOIP_SIP("voip_sip", "VoIP SIP"),
    VOIP_IAX("voip_iax", "VoIP IAX"),
    VOIP_H323("voip_h323", "VoIP H.323"),
    SS7("ss7", "SS7 Protocol");

    private final String value;
    private final String displayName;

    GSMProtocol(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}

