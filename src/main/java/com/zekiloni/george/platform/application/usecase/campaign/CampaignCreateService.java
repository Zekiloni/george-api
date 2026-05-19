package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessQueryUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.in.page.PageQueryUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.lead.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.platform.domain.util.PhoneNumberFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignCreateService implements CampaignCreateUseCase {
    private static final int BATCH_SIZE = 500;

    private final CampaignRepositoryPort repository;
    private final OutreachRepositoryPort outreachRepository;
    private final LeadRepositoryPort leadRepository;
    private final PageQueryUseCase pageQueryUseCase;
    private final ServiceAccessQueryUseCase serviceAccessQueryUseCase;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public Campaign handle(Campaign campaignCreate, InputStream file) {
        try {
            return createCampaign(campaignCreate, file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read phone number file", e);
        }
    }

    private Campaign createCampaign(Campaign campaignCreate, InputStream file) throws IOException {
        validateReferences(campaignCreate);
        campaignCreate.setBaseUrl(baseUrl);
        campaignCreate.setStatus(CampaignStatus.DRAFT);

        List<Outreach> outreaches = buildOutreaches(file, campaignCreate);

        return persistCampaignAndOutreaches(campaignCreate, outreaches);
    }

    @Transactional
    protected Campaign persistCampaignAndOutreaches(Campaign campaignCreate, List<Outreach> outreaches) {
        Campaign campaign = repository.save(campaignCreate);
        for (Outreach o : outreaches) {
            o.setCampaignId(campaign.getId());
            o.setTenantId(campaign.getTenantId());
        }
        saveOutreachesInBatches(outreaches);
        return campaign;
    }

    private List<Outreach> buildOutreaches(InputStream file, Campaign campaign) throws IOException {
        Set<String> phoneNumbers = PhoneNumberFileReader.streamPhoneNumbers(file)
                .collect(Collectors.toSet());

        Map<String, Lead> leadByPhone = leadRepository.findByPhoneNumberIn(phoneNumbers).stream()
                .collect(Collectors.toMap(Lead::getPhoneNumber, Function.identity(), (a, b) -> a));

        List<Outreach> outreaches = new ArrayList<>(phoneNumbers.size());
        for (String recipient : phoneNumbers) {
            String token = campaign.getTokenStrategy().generate(campaign.getTokenLength());
            Lead lead = leadByPhone.get(recipient);
            outreaches.add(Outreach.builder()
                    .campaignId(campaign.getId())
                    .recipient(recipient)
                    .message(buildMessage(campaign, token))
                    .sessionToken(token)
                    .status(OutreachStatus.SCHEDULED)
                    .scheduledAt(OffsetDateTime.now())
                    .country(lead != null ? lead.getCountry() : null)
                    .carrier(lead != null ? lead.getCarrier() : null)
                    .build());
        }
        return outreaches;
    }

    private void saveOutreachesInBatches(List<Outreach> outreaches) {
        for (int i = 0; i < outreaches.size(); i += BATCH_SIZE) {
            List<Outreach> batch = outreaches.subList(i, Math.min(i + BATCH_SIZE, outreaches.size()));
            outreachRepository.saveAll(batch);
        }
    }

    private String buildMessage(Campaign campaign, String token) {
        String url = String.format("%s/%s", campaign.getBaseUrl(), token);
        return campaign.getMessageTemplate().replace("{token}", url);
    }

    private void validateReferences(Campaign campaign) {
        if (campaign.getFlow() == null || campaign.getFlow().isEmpty()) {
            throw new IllegalArgumentException("flow must contain at least one page");
        }
        for (var pageRef : campaign.getFlow()) {
            if (pageRef == null || pageRef.getId() == null) {
                throw new IllegalArgumentException("flow contains a null page reference");
            }
            pageQueryUseCase.findById(pageRef.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Page not found: " + pageRef.getId()));
        }

        if (campaign.getServiceAccess() == null || campaign.getServiceAccess().getId() == null) {
            throw new IllegalArgumentException("serviceAccess is required");
        }
        ServiceAccess access = serviceAccessQueryUseCase.getById(campaign.getServiceAccess().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ServiceAccess not found: " + campaign.getServiceAccess().getId()));
        if (access.getStatus() != ServiceStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "ServiceAccess must be ACTIVE (was " + access.getStatus() + ")");
        }
        ServiceSpecification spec = access.getServiceSpecification();
        if (spec != ServiceSpecification.SMTP && spec != ServiceSpecification.GSM) {
            throw new IllegalArgumentException(
                    "ServiceAccess must be SMTP or GSM (was " + spec + ")");
        }
    }
}
