package com.zekiloni.george.platform.application.usecase;

import com.zekiloni.george.platform.application.port.in.LeadImportUseCase;
import com.zekiloni.george.platform.application.port.out.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.lead.model.Lead;
import com.zekiloni.george.platform.domain.lead.model.PhoneResolutionResult;
import com.zekiloni.george.platform.domain.lead.service.CountryCodeResolver;
import com.zekiloni.george.platform.domain.lead.util.PhoneNumberFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeadImportService implements LeadImportUseCase {
    private final CountryCodeResolver countryCodeResolver;
    private final LeadRepositoryPort repository;

    @Override
    public void handle(InputStream inputStream) {
        try {
            List<Lead> leads = PhoneNumberFileReader.readPhoneNumbers(inputStream)
                    .stream()
                    .map(countryCodeResolver::resolve)
                    .map(this::mapToLead)
                    .toList();

            repository.saveAll(leads);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import leads from file", e);
        }
    }

    private Lead mapToLead(PhoneResolutionResult result) {
        return Lead.builder()
                .country(result.getCountry())
                .areaCode(result.getAreaCode())
                .regionCode(result.getRegionCode())
                .phoneNumber(result.getPhoneNumber())
                .location(result.getLocation())
                .build();
    }
}
