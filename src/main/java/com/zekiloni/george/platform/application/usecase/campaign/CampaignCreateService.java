package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignDispatcherPort;
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
    private final CampaignDispatcherPort dispatcher;
    private final CampaignStatusTransitionService statusTransitionService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public Campaign handle(Campaign campaignCreate, InputStream file) {
        try {
            campaignCreate.setBaseUrl(baseUrl);
            campaignCreate.setStatus(CampaignStatus.SCHEDULED);
            Campaign campaign = repository.save(campaignCreate);
            saveOutreachesInBatches(buildOutreaches(file, campaign));

            if (isImmediateDispatch(campaign)) {
                statusTransitionService.transitionTo(campaign.getId(), CampaignStatus.ACTIVE);
                dispatcher.dispatch(campaign.getId(), campaign.getServiceAccess().getId());
            }

            return campaign;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Outreach> buildOutreaches(InputStream file, Campaign campaign) throws IOException {
        Set<String> phoneNumbers = PhoneNumberFileReader.streamPhoneNumbers(file)
                .collect(Collectors.toSet());

        // Stamp country/carrier from the customer's lead pool — used by the SMTP
        // dispatcher to pick the correct mail2sms domain. Phones not in the pool
        // get null fields and fall back to the gateway default at dispatch.
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

    private boolean isImmediateDispatch(Campaign campaign) {
        return campaign.getScheduledAt() == null ||
                !campaign.getScheduledAt().isAfter(OffsetDateTime.now());
    }
}
