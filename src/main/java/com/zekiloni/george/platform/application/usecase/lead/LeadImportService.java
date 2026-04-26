package com.zekiloni.george.platform.application.usecase.lead;

import com.zekiloni.george.platform.application.port.in.lead.AssignLeadsUseCase;
import com.zekiloni.george.platform.application.port.in.lead.LeadImportUseCase;
import com.zekiloni.george.platform.application.port.out.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.platform.domain.model.lead.PhoneResolutionResult;
import com.zekiloni.george.platform.domain.service.PhoneDataResolver;
import com.zekiloni.george.platform.domain.util.PhoneNumberFileReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeadImportService implements LeadImportUseCase {
    private final PhoneDataResolver phoneDataResolver;
    private final LeadRepositoryPort repository;
    private final AssignLeadsUseCase assignLeadsUseCase;

    @Override
    @Transactional
    public void handle(LeadImportCommand command) throws IOException {
        List<Lead> leads = repository.saveAll(buildLeads(command.inputStream()));
        command.serviceAccessId()
                .ifPresent(id -> assignLeadsUseCase.handle(id, leads.stream().map(Lead::getId).toList()));
    }

    private @NonNull List<Lead> buildLeads(InputStream inputStream) throws IOException {
        return PhoneNumberFileReader.streamPhoneNumbers(inputStream)
                .map(phoneDataResolver::resolve)
                .map(this::mapToLead)
                .toList();
    }

    private Lead mapToLead(PhoneResolutionResult result) {
        return Lead.builder()
                .country(result.getCountry())
                .areaCode(result.getAreaCode())
                .carrier(result.getCarrier())
                .regionCode(result.getRegionCode())
                .phoneNumber(result.getPhoneNumber())
                .location(result.getLocation())
                .build();
    }
}
