package com.zekiloni.george.platform.domain.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.zekiloni.george.platform.domain.model.PhoneResolutionResult;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PhoneDataResolver {

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private final PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
    private final PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

    /**
     * Resolves phone number into country, areaCode, regionCode and location.
     *
     * @param phoneNumber raw phone number (with or without +)
     * @return PhoneResolutionResult with resolved fields (null if cannot resolve)
     */
    public PhoneResolutionResult resolve(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        String normalized = phoneNumber.trim();
        if (!normalized.startsWith("+")) {
            normalized = "+" + normalized;
        }

        try {
            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse(normalized, "");

            String countryCode = phoneUtil.getRegionCodeForNumber(parsedNumber); // ISO2 country (e.g. "BA")
            String nationalNumber = String.valueOf(parsedNumber.getNationalNumber());
            String areaCode = extractAreaCode(countryCode, nationalNumber);
            String location = geocoder.getDescriptionForNumber(parsedNumber, Locale.ENGLISH);
            String carrier = null;

            PhoneNumberUtil.PhoneNumberType numberType = phoneUtil.getNumberType(parsedNumber);

            if (numberType == PhoneNumberUtil.PhoneNumberType.MOBILE ||
                numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE) {
                carrier = carrierMapper.getNameForNumber(parsedNumber, Locale.ENGLISH);
            }

            return PhoneResolutionResult.builder()
                    .country(countryCode)
                    .areaCode(areaCode)
                    .carrier(carrier)
                    .phoneNumber(phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164))
                    .regionCode(areaCode)
                    .location(location != null && !location.isEmpty() ? location : null)
                    .build();

        } catch (NumberParseException e) {
            return PhoneResolutionResult.builder()
                    .country(null)
                    .areaCode(null)
                    .phoneNumber(null)
                    .regionCode(null)
                    .location(null)
                    .build();
        }
    }

    /**
     * Simple area code extraction logic.
     * You can enhance this per country if needed.
     */
    private String extractAreaCode(String countryCode, String nationalNumber) {
        if (nationalNumber == null || nationalNumber.length() < 3) {
            return null;
        }

        // Common patterns:
        // Bosnia (BA): area codes are 2-3 digits (e.g. 32 for Sarajevo)
        // USA/Canada (+1): first 3 digits = area code
        if ("US".equals(countryCode) || "CA".equals(countryCode)) {
            return nationalNumber.length() >= 3 ? nationalNumber.substring(0, 3) : null;
        }

        // Default: take first 2 or 3 digits
        return nationalNumber.length() >= 3
                ? nationalNumber.substring(0, Math.min(3, nationalNumber.length()))
                : nationalNumber.substring(0, Math.min(2, nationalNumber.length()));
    }
}