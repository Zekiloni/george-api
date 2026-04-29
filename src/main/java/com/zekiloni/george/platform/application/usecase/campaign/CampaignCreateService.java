package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignDispatcherPort;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.util.PhoneNumberFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignCreateService implements CampaignCreateUseCase {
    private final CampaignRepositoryPort repository;
    private final OutreachRepositoryPort outreachRepository;
    private final CampaignDispatcherPort dispatcher;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public Campaign handle(Campaign campaignCreate, InputStream file) {
        try {
            campaignCreate.setBaseUrl(baseUrl);
            Campaign campaign = repository.save(campaignCreate);
            handleOutreach(file, campaign);
            dispatcher.dispatch(campaign.getId(), campaign.getServiceAccess().getId());
            return campaign;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleOutreach(InputStream file, Campaign campaign) throws IOException {
        // TODO: Later make generic resolver, for now just read phone numbers and create outreach
        outreachRepository.saveAll(getPhoneNumbers(file).stream()
                .map(recipient -> buildOutreach(campaign, recipient))
                .toList());
    }

    private Outreach buildOutreach(Campaign campaign, String recipient) {
        String token = campaign.getTokenStrategy().generate(campaign.getTokenLength());

        return Outreach.builder()
                .campaignId(campaign.getId())
                .recipient(recipient)
                .message(buildMessage(campaign, token))
                .sessionToken(token)
                .status(OutreachStatus.SCHEDULED)
                .scheduledAt(OffsetDateTime.now())
                .build();
    }

    private String buildMessage(Campaign campaign, String token) {
        String url = String.format("%s/%s", campaign.getBaseUrl(), token);
        return campaign.getMessageTemplate().replace("{token}", url);
    }

    private Set<String> getPhoneNumbers(InputStream file) throws IOException {
        return PhoneNumberFileReader.streamPhoneNumbers(file).collect(Collectors.toSet());
    }
}
